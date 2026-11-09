package com.example.ui

import com.example.data.model.AnimeGenre
import com.example.data.model.AnimeItem
import com.example.data.model.FilterState
import com.example.data.model.SortOption
import com.example.data.repository.AnimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State machine states representing anime filtering by genre and search queries.
 */
sealed interface FilterSearchState {
    val filterState: FilterState
    val results: List<AnimeItem>

    /**
     * Initial or idle state with baseline catalog items.
     */
    data class Idle(
        override val filterState: FilterState = FilterState(),
        override val results: List<AnimeItem> = emptyList(),
        val allGenres: List<AnimeGenre> = emptyList()
    ) : FilterSearchState

    /**
     * Active state when query debounce or filtering computation is in progress.
     * Keeps previous results to avoid UI flicker during typing or filter switching.
     */
    data class Filtering(
        override val filterState: FilterState,
        override val results: List<AnimeItem>,
        val isDebouncing: Boolean = false,
        val page: Int = 1
    ) : FilterSearchState

    /**
     * Resolved state when matching anime have been found.
     */
    data class Success(
        override val filterState: FilterState,
        override val results: List<AnimeItem>,
        val totalMatches: Int,
        val page: Int = 1,
        val hasMorePages: Boolean = true,
        val isOfflineFallback: Boolean = false
    ) : FilterSearchState

    /**
     * Empty state when no anime matches the current query and genre filter criteria.
     */
    data class Empty(
        override val filterState: FilterState,
        val suggestions: List<String> = emptyList(),
        val message: String = "No anime matches the current search and genre filters."
    ) : FilterSearchState {
        override val results: List<AnimeItem> get() = emptyList()
    }

    /**
     * Error state when search or filtering execution fails.
     */
    data class Error(
        val message: String,
        val lastValidResults: List<AnimeItem> = emptyList(),
        override val results: List<AnimeItem> = lastValidResults,
        override val filterState: FilterState = FilterState()
    ) : FilterSearchState
}

/**
 * Explicit user/UI intents dispatched to the filter state machine.
 */
sealed interface FilterSearchIntent {
    data class SetQuery(val query: String) : FilterSearchIntent
    data object ClearQuery : FilterSearchIntent
    data class ToggleIncludeGenre(val genreName: String) : FilterSearchIntent
    data class ToggleExcludeGenre(val genreName: String) : FilterSearchIntent
    data object ClearGenreFilters : FilterSearchIntent
    data class SetSort(val sort: SortOption) : FilterSearchIntent
    data class SetFormat(val format: String?) : FilterSearchIntent
    data class SetYear(val year: Int?) : FilterSearchIntent
    data class SetSeason(val season: String?) : FilterSearchIntent
    data class SetStatus(val status: String?) : FilterSearchIntent
    data class SetOnlyFreeVerified(val enabled: Boolean) : FilterSearchIntent
    data class SetOnlyHiddenGems(val enabled: Boolean) : FilterSearchIntent
    data object ClearAllFilters : FilterSearchIntent
    data class JumpToGenre(val genreName: String) : FilterSearchIntent
    data object LoadNextPage : FilterSearchIntent
    data object Retry : FilterSearchIntent
    data class RefreshCatalog(val newCatalog: List<AnimeItem>) : FilterSearchIntent
}

/**
 * ViewModel State Machine to manage anime filtering by genre and search queries.
 * Exposes state transitions to Jetpack Compose via StateFlow.
 */
class AnimeFilterStateMachine(
    private val repository: AnimeRepository,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow<FilterSearchState>(
        FilterSearchState.Idle(
            filterState = FilterState(),
            results = repository.getAllCatalogAnime(),
            allGenres = repository.allGenres
        )
    )

    /**
     * Exposes the current state to the UI via StateFlow.
     */
    val state: StateFlow<FilterSearchState> = _state.asStateFlow()

    private var debounceJob: Job? = null
    private var currentPage: Int = 1

    init {
        val initialCatalog = repository.getAllCatalogAnime()
        if (initialCatalog.isNotEmpty()) {
            _state.value = FilterSearchState.Success(
                filterState = FilterState(),
                results = initialCatalog,
                totalMatches = initialCatalog.size,
                page = 1,
                hasMorePages = true
            )
        }
    }

    /**
     * Dispatch an intent into the state machine to trigger a state transition.
     */
    fun dispatch(intent: FilterSearchIntent) {
        when (intent) {
            is FilterSearchIntent.SetQuery -> handleSetQuery(intent.query)
            is FilterSearchIntent.ClearQuery -> handleSetQuery("")
            is FilterSearchIntent.ToggleIncludeGenre -> handleToggleIncludeGenre(intent.genreName)
            is FilterSearchIntent.ToggleExcludeGenre -> handleToggleExcludeGenre(intent.genreName)
            is FilterSearchIntent.ClearGenreFilters -> handleClearGenreFilters()
            is FilterSearchIntent.SetSort -> handleUpdateFilter { it.copy(sortBy = intent.sort) }
            is FilterSearchIntent.SetFormat -> handleUpdateFilter { it.copy(selectedFormat = intent.format) }
            is FilterSearchIntent.SetYear -> handleUpdateFilter { it.copy(selectedYear = intent.year) }
            is FilterSearchIntent.SetSeason -> handleUpdateFilter { it.copy(selectedSeason = intent.season) }
            is FilterSearchIntent.SetStatus -> handleUpdateFilter { it.copy(selectedStatus = intent.status) }
            is FilterSearchIntent.SetOnlyFreeVerified -> handleUpdateFilter { it.copy(onlyFreeVerified = intent.enabled) }
            is FilterSearchIntent.SetOnlyHiddenGems -> handleUpdateFilter { it.copy(onlyHiddenGems = intent.enabled) }
            is FilterSearchIntent.ClearAllFilters -> handleClearAllFilters()
            is FilterSearchIntent.JumpToGenre -> handleJumpToGenre(intent.genreName)
            is FilterSearchIntent.LoadNextPage -> handleLoadNextPage()
            is FilterSearchIntent.Retry -> handleRetry()
            is FilterSearchIntent.RefreshCatalog -> handleRefreshCatalog(intent.newCatalog)
        }
    }

    private fun handleSetQuery(query: String) {
        val currentFilter = _state.value.filterState
        val newFilter = currentFilter.copy(query = query)
        currentPage = 1

        // Instant local filter for responsive 60fps UI
        val catalog = repository.getAllCatalogAnime()
        val instantResults = repository.filterCatalogLocally(catalog, newFilter)
        val suggestions = computeSuggestions(query, catalog)

        _state.update {
            FilterSearchState.Filtering(
                filterState = newFilter,
                results = instantResults,
                isDebouncing = true,
                page = 1
            )
        }

        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(320)
            if (query.trim().length >= 3) {
                repository.addRecentSearch(query.trim())
            }
            executeFiltering(newFilter, page = 1, suggestions = suggestions)
        }
    }

    private fun handleToggleIncludeGenre(genreName: String) {
        val current = _state.value.filterState
        val newIncluded = if (current.includedGenres.contains(genreName)) {
            current.includedGenres - genreName
        } else {
            current.includedGenres + genreName
        }
        val newExcluded = current.excludedGenres - genreName
        val updated = current.copy(includedGenres = newIncluded, excludedGenres = newExcluded)
        applyDirectFilter(updated)
    }

    private fun handleToggleExcludeGenre(genreName: String) {
        val current = _state.value.filterState
        val newExcluded = if (current.excludedGenres.contains(genreName)) {
            current.excludedGenres - genreName
        } else {
            current.excludedGenres + genreName
        }
        val newIncluded = current.includedGenres - genreName
        val updated = current.copy(includedGenres = newIncluded, excludedGenres = newExcluded)
        applyDirectFilter(updated)
    }

    private fun handleClearGenreFilters() {
        val current = _state.value.filterState
        val updated = current.copy(includedGenres = emptySet(), excludedGenres = emptySet())
        applyDirectFilter(updated)
    }

    private fun handleClearAllFilters() {
        val current = _state.value.filterState
        val updated = FilterState(query = current.query)
        applyDirectFilter(updated)
    }

    private fun handleJumpToGenre(genreName: String) {
        val updated = FilterState(includedGenres = setOf(genreName))
        applyDirectFilter(updated)
    }

    private fun handleUpdateFilter(transform: (FilterState) -> FilterState) {
        val updated = transform(_state.value.filterState)
        applyDirectFilter(updated)
    }

    private fun applyDirectFilter(newFilter: FilterState) {
        currentPage = 1
        debounceJob?.cancel()

        val catalog = repository.getAllCatalogAnime()
        val instantResults = repository.filterCatalogLocally(catalog, newFilter)

        _state.update {
            FilterSearchState.Filtering(
                filterState = newFilter,
                results = instantResults,
                isDebouncing = false,
                page = 1
            )
        }

        scope.launch {
            executeFiltering(newFilter, page = 1, suggestions = emptyList())
        }
    }

    private fun handleLoadNextPage() {
        val current = _state.value
        if (current is FilterSearchState.Filtering) return

        val nextPage = currentPage + 1
        currentPage = nextPage

        _state.update {
            FilterSearchState.Filtering(
                filterState = it.filterState,
                results = it.results,
                isDebouncing = false,
                page = nextPage
            )
        }

        scope.launch {
            executeFiltering(current.filterState, page = nextPage, suggestions = emptyList())
        }
    }

    private fun handleRetry() {
        val currentFilter = _state.value.filterState
        applyDirectFilter(currentFilter)
    }

    private fun handleRefreshCatalog(newCatalog: List<AnimeItem>) {
        val filter = _state.value.filterState
        val localFiltered = repository.filterCatalogLocally(newCatalog, filter)
        if (localFiltered.isEmpty()) {
            _state.value = FilterSearchState.Empty(
                filterState = filter,
                suggestions = computeSuggestions(filter.query, newCatalog),
                message = "No anime matches the current search and genre filters."
            )
        } else {
            _state.value = FilterSearchState.Success(
                filterState = filter,
                results = localFiltered,
                totalMatches = localFiltered.size,
                page = currentPage,
                hasMorePages = true
            )
        }
    }

    private suspend fun executeFiltering(
        filter: FilterState,
        page: Int,
        suggestions: List<String>
    ) {
        try {
            val result = repository.searchAndFilterAnime(filter, page)
            result.onSuccess { resolvedList ->
                if (resolvedList.isEmpty()) {
                    _state.value = FilterSearchState.Empty(
                        filterState = filter,
                        suggestions = suggestions.ifEmpty {
                            computeSuggestions(filter.query, repository.getAllCatalogAnime())
                        },
                        message = if (filter.query.isNotBlank()) {
                            "No anime found matching '${filter.query}' with active genre filters."
                        } else {
                            "No anime found matching selected genres."
                        }
                    )
                } else {
                    _state.value = FilterSearchState.Success(
                        filterState = filter,
                        results = resolvedList,
                        totalMatches = resolvedList.size,
                        page = page,
                        hasMorePages = resolvedList.size >= 12
                    )
                }
            }.onFailure { error ->
                _state.value = FilterSearchState.Error(
                    message = error.localizedMessage ?: "Failed to filter anime",
                    lastValidResults = _state.value.results,
                    filterState = filter
                )
            }
        } catch (e: Exception) {
            _state.value = FilterSearchState.Error(
                message = e.localizedMessage ?: "Unexpected error during filtering",
                lastValidResults = _state.value.results,
                filterState = filter
            )
        }
    }

    private fun computeSuggestions(query: String, catalog: List<AnimeItem>): List<String> {
        val q = query.trim().lowercase()
        if (q.length < 2) return emptyList()

        val titleMatches = catalog
            .filter {
                it.title.lowercase().contains(q) ||
                    it.titleJapanese.lowercase().contains(q) ||
                    it.alternativeTitles.any { alt -> alt.lowercase().contains(q) }
            }
            .map { it.title }

        val genreMatches = repository.allGenres
            .filter { it.name.lowercase().contains(q) }
            .map { "Genre: ${it.name}" }

        return (titleMatches + genreMatches).distinct().take(6)
    }
}
