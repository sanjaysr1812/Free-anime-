package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnimeDatabase
import com.example.data.local.CachedAnimeEntity
import com.example.data.local.CustomListEntity
import com.example.data.local.FavoriteAnimeEntity
import com.example.data.local.LibraryEntryEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.RecentlyWatchedEntity
import com.example.data.local.UserPreferencesManager
import com.example.data.local.UserSettings
import com.example.data.local.WatchedEpisodeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.AnimeGenre
import com.example.data.model.AnimeItem
import com.example.data.model.FilterState
import com.example.data.model.SortOption
import com.example.data.repository.AnimeRepository
import com.example.ui.theme.AppearanceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab(val label: String, val testTag: String) {
    HOME("Home", "nav_tab_home"),
    DISCOVER("Discover", "nav_tab_discover"),
    SEARCH("Search", "nav_tab_search"),
    LIBRARY("Library", "nav_tab_library"),
    PROFILE("Profile", "nav_tab_profile")
}

sealed class ScreenDestination {
    data object WelcomeOnboarding : ScreenDestination()
    data class Main(val tab: MainTab = MainTab.HOME) : ScreenDestination()
    data class Details(val animeId: Int) : ScreenDestination()
    data class Watch(val animeId: Int, val episodeNumber: Int = 1) : ScreenDestination()
}

data class CatalogUiState(
    val isLoading: Boolean = false,
    val isRefreshingRemote: Boolean = false,
    val isOfflineFallback: Boolean = false,
    val errorMessage: String? = null,
    val catalog: List<AnimeItem> = emptyList(),
    val filteredResults: List<AnimeItem> = emptyList(),
    val searchSuggestions: List<String> = emptyList(),
    val filterState: FilterState = FilterState(),
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val isLoadingMore: Boolean = false,
    val activeAnimeDetail: AnimeItem? = null,
    val isDetailLoading: Boolean = false,
    val snackbarMessage: String? = null
)

class AnimeFlixViewModel(
    private val repository: AnimeRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _navigationStack = MutableStateFlow<List<ScreenDestination>>(
        listOf(ScreenDestination.Main(MainTab.HOME))
    )
    val navigationStack: StateFlow<List<ScreenDestination>> = _navigationStack.asStateFlow()

    val currentDestination: ScreenDestination
        get() = _navigationStack.value.lastOrNull() ?: ScreenDestination.Main(MainTab.HOME)

    // State machine to manage anime filtering by genre and search queries
    val filterStateMachine = AnimeFilterStateMachine(repository, viewModelScope)

    /**
     * StateFlow exposing the Filter and Search state machine to the UI.
     */
    val filterSearchState: StateFlow<FilterSearchState> = filterStateMachine.state

    private val _uiState = MutableStateFlow(
        CatalogUiState(
            catalog = repository.getAllCatalogAnime(),
            filteredResults = repository.getAllCatalogAnime()
        )
    )
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    val allGenres: List<AnimeGenre> = repository.allGenres

    // Dedicated Room Table StateFlows: Watchlist, FavoriteAnime, RecentlyWatched
    val watchlistTable: StateFlow<List<WatchlistEntity>> = repository.watchlistTableFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteAnimeTable: StateFlow<List<FavoriteAnimeEntity>> = repository.favoriteAnimeTableFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyWatchedTable: StateFlow<List<RecentlyWatchedEntity>> = repository.recentlyWatchedTableFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unified Library StateFlows
    val watchlist: StateFlow<List<LibraryEntryEntity>> = repository.watchlistFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<LibraryEntryEntity>> = repository.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueWatching: StateFlow<List<LibraryEntryEntity>> = repository.continueWatchingFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLibraryEntries: StateFlow<List<LibraryEntryEntity>> = repository.allLibraryEntriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchedEpisodes: StateFlow<List<WatchedEpisodeEntity>> = repository.allWatchedEpisodesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customLists: StateFlow<List<CustomListEntity>> = repository.customListsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<RecentSearchEntity>> = repository.recentSearchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedAnime: StateFlow<List<CachedAnimeEntity>> = repository.cachedAnimeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    init {
        // Collect state machine emissions and synchronize CatalogUiState
        viewModelScope.launch {
            filterStateMachine.state.collect { machineState ->
                val suggestions = when (machineState) {
                    is FilterSearchState.Empty -> machineState.suggestions
                    else -> computeSuggestions(machineState.filterState.query)
                }
                _uiState.update { current ->
                    current.copy(
                        filterState = machineState.filterState,
                        filteredResults = machineState.results,
                        searchSuggestions = suggestions,
                        isLoadingMore = (machineState as? FilterSearchState.Filtering)?.let { it.page > 1 } ?: false,
                        errorMessage = (machineState as? FilterSearchState.Error)?.message
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.ensureInitialSeedData()
            refreshCatalog()
        }
    }

    fun refreshCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingRemote = true, errorMessage = null) }
            val result = repository.refreshCatalogFromRemote()
            val allItems = result.getOrDefault(repository.getAllCatalogAnime())
            filterStateMachine.dispatch(FilterSearchIntent.RefreshCatalog(allItems))
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshingRemote = false,
                    catalog = allItems,
                    activeAnimeDetail = it.activeAnimeDetail?.let { detail ->
                        repository.getAnimeById(detail.id) ?: detail
                    }
                )
            }
        }
    }

    /**
     * Dispatch an intent to the Filter and Search state machine.
     */
    fun dispatchFilterIntent(intent: FilterSearchIntent) {
        filterStateMachine.dispatch(intent)
    }

    fun selectTab(tab: MainTab) {
        _navigationStack.value = listOf(ScreenDestination.Main(tab))
    }

    fun openWelcomeScreen() {
        _navigationStack.update { it + ScreenDestination.WelcomeOnboarding }
    }

    fun openAnimeDetails(animeId: Int) {
        val immediate = repository.getAnimeById(animeId)
        _uiState.update { it.copy(activeAnimeDetail = immediate, isDetailLoading = true) }
        _navigationStack.update { stack -> stack + ScreenDestination.Details(animeId) }
        viewModelScope.launch {
            val full = repository.fetchAnimeDetailsWithExtras(animeId)
            _uiState.update {
                it.copy(
                    activeAnimeDetail = full,
                    isDetailLoading = false,
                    catalog = repository.getAllCatalogAnime()
                )
            }
        }
    }

    fun openVideoPlayer(animeId: Int, episodeNumber: Int = 1) {
        val immediate = repository.getAnimeById(animeId)
        if (immediate != null) {
            _uiState.update { it.copy(activeAnimeDetail = immediate) }
            val existingRecent = recentlyWatchedTable.value.find { it.animeId == animeId }
            val existingEntry = allLibraryEntries.value.find { it.animeId == animeId }
            val resumePos = when {
                existingRecent != null && existingRecent.episodeNumber == episodeNumber && existingRecent.playbackPositionMs > 0L ->
                    existingRecent.playbackPositionMs
                existingEntry != null && existingEntry.lastWatchedEpisode == episodeNumber && existingEntry.lastPlaybackPositionMs > 0L ->
                    existingEntry.lastPlaybackPositionMs
                else -> 180_000L
            }
            viewModelScope.launch {
                repository.savePlaybackProgress(
                    anime = immediate,
                    episodeNumber = episodeNumber,
                    positionMs = resumePos,
                    durationMs = 1_440_000L
                )
            }
        }
        _navigationStack.update { stack -> stack + ScreenDestination.Watch(animeId, episodeNumber) }
    }

    fun openRandomAnime() {
        val pool = _uiState.value.catalog.ifEmpty { repository.getAllCatalogAnime() }
        if (pool.isNotEmpty()) {
            val currentId = _uiState.value.activeAnimeDetail?.id
            val candidates = pool.filter { it.id != currentId }.ifEmpty { pool }
            val picked = candidates.random()
            showSnackbar("Random Discovery: ${picked.title}")
            openAnimeDetails(picked.id)
        }
    }

    fun navigateBack(): Boolean {
        val current = _navigationStack.value
        return if (current.size > 1) {
            _navigationStack.value = current.dropLast(1)
            true
        } else {
            val top = current.firstOrNull()
            if (top is ScreenDestination.Main && top.tab != MainTab.HOME) {
                _navigationStack.value = listOf(ScreenDestination.Main(MainTab.HOME))
                true
            } else {
                false
            }
        }
    }

    // State machine intent dispatchers for search and genre discovery
    fun updateSearchQuery(query: String) {
        filterStateMachine.dispatch(FilterSearchIntent.SetQuery(query))
    }

    fun toggleIncludeGenre(genreName: String) {
        filterStateMachine.dispatch(FilterSearchIntent.ToggleIncludeGenre(genreName))
    }

    fun toggleExcludeGenre(genreName: String) {
        filterStateMachine.dispatch(FilterSearchIntent.ToggleExcludeGenre(genreName))
    }

    fun setSortOption(sortOption: SortOption) {
        filterStateMachine.dispatch(FilterSearchIntent.SetSort(sortOption))
    }

    fun setYearFilter(year: Int?) {
        filterStateMachine.dispatch(FilterSearchIntent.SetYear(year))
    }

    fun setSeasonFilter(season: String?) {
        filterStateMachine.dispatch(FilterSearchIntent.SetSeason(season))
    }

    fun setStatusFilter(status: String?) {
        filterStateMachine.dispatch(FilterSearchIntent.SetStatus(status))
    }

    fun setFormatFilter(format: String?) {
        filterStateMachine.dispatch(FilterSearchIntent.SetFormat(format))
    }

    fun setOnlyFreeVerified(enabled: Boolean) {
        filterStateMachine.dispatch(FilterSearchIntent.SetOnlyFreeVerified(enabled))
    }

    fun setOnlyHiddenGems(enabled: Boolean) {
        filterStateMachine.dispatch(FilterSearchIntent.SetOnlyHiddenGems(enabled))
    }

    fun clearAllFilters() {
        filterStateMachine.dispatch(FilterSearchIntent.ClearAllFilters)
    }

    fun jumpToGenreDiscover(genreName: String) {
        filterStateMachine.dispatch(FilterSearchIntent.JumpToGenre(genreName))
        selectTab(MainTab.DISCOVER)
    }

    fun loadMoreResults() {
        filterStateMachine.dispatch(FilterSearchIntent.LoadNextPage)
    }

    fun retryFiltering() {
        filterStateMachine.dispatch(FilterSearchIntent.Retry)
    }

    private fun computeSuggestions(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.length < 2) return emptyList()
        val titleMatches = _uiState.value.catalog
            .filter {
                it.title.lowercase().contains(q) ||
                    it.titleJapanese.lowercase().contains(q) ||
                    it.alternativeTitles.any { alt -> alt.lowercase().contains(q) }
            }
            .map { it.title }
        val genreMatches = allGenres
            .filter { it.name.lowercase().contains(q) }
            .map { "Genre: ${it.name}" }
        return (titleMatches + genreMatches).distinct().take(6)
    }

    fun toggleWatchlist(anime: AnimeItem) {
        viewModelScope.launch {
            val added = repository.toggleWatchlist(anime)
            showSnackbar(if (added) "Saved '${anime.title}' to Watchlist" else "Removed '${anime.title}' from Watchlist")
        }
    }

    fun toggleFavorite(anime: AnimeItem) {
        viewModelScope.launch {
            val added = repository.toggleFavorite(anime)
            showSnackbar(if (added) "Saved '${anime.title}' to FavoriteAnime" else "Removed '${anime.title}' from FavoriteAnime")
        }
    }

    fun assignToCustomList(anime: AnimeItem, listName: String?) {
        viewModelScope.launch {
            repository.assignToCustomList(anime, listName)
            showSnackbar(if (listName != null) "Saved '${anime.title}' to $listName" else "Removed from custom list")
        }
    }

    fun createCustomList(name: String, description: String) {
        viewModelScope.launch {
            repository.createCustomList(name, description)
            showSnackbar("Created list '$name'")
        }
    }

    fun deleteCustomList(name: String) {
        viewModelScope.launch {
            repository.deleteCustomList(name)
            showSnackbar("Deleted list '$name'")
        }
    }

    fun savePlaybackProgress(
        anime: AnimeItem,
        episodeNumber: Int,
        positionMs: Long,
        durationMs: Long
    ) {
        viewModelScope.launch {
            repository.savePlaybackProgress(anime, episodeNumber, positionMs, durationMs)
        }
    }

    fun removeFromLibrary(animeId: Int) {
        viewModelScope.launch {
            repository.removeFromLibrary(animeId)
            showSnackbar("Removed from My Library")
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch { repository.removeRecentSearch(query) }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { repository.clearRecentSearches() }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
            showSnackbar("RecentlyWatched history cleared")
        }
    }

    fun setAppearanceMode(mode: AppearanceMode) {
        viewModelScope.launch {
            preferencesManager.setAppearanceMode(mode)
            showSnackbar("Appearance updated to ${mode.label}")
        }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setReducedMotion(enabled) }
    }

    fun setDefaultQuality(quality: String) {
        viewModelScope.launch { preferencesManager.setDefaultQuality(quality) }
    }

    fun setDefaultSubtitle(subtitle: String) {
        viewModelScope.launch { preferencesManager.setDefaultSubtitle(subtitle) }
    }

    fun setAutoPlayNext(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setAutoPlayNext(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setNotificationsEnabled(enabled) }
    }

    fun updateProfile(name: String, bio: String) {
        viewModelScope.launch {
            preferencesManager.updateProfile(name, bio)
            showSnackbar("Profile updated")
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AnimeDatabase.getInstance(context)
                    val repo = AnimeRepository(db.animeDao())
                    val prefs = UserPreferencesManager(context)
                    return AnimeFlixViewModel(repo, prefs) as T
                }
            }
        }
    }
}
