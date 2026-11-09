package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.LibraryEntryEntity
import com.example.data.model.AnimeGenre
import com.example.data.model.AnimeItem
import com.example.data.model.FilterState
import com.example.data.model.SortOption
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.components.AnimePosterCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CoralWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextMuted
import com.example.ui.FilterSearchState
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(
    allGenres: List<AnimeGenre>,
    filterState: FilterState,
    filteredResults: List<AnimeItem>,
    watchlist: List<LibraryEntryEntity>,
    isLoadingMore: Boolean,
    filterSearchState: FilterSearchState? = null,
    onRetry: (() -> Unit)? = null,
    onQueryChange: (String) -> Unit,
    onToggleIncludeGenre: (String) -> Unit,
    onToggleExcludeGenre: (String) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onYearChange: (Int?) -> Unit,
    onSeasonChange: (String?) -> Unit,
    onStatusChange: (String?) -> Unit,
    onFormatChange: (String?) -> Unit,
    onOnlyFreeChange: (Boolean) -> Unit,
    onOnlyHiddenGemsChange: (Boolean) -> Unit,
    onClearFilters: () -> Unit,
    onLoadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit,
    onRandomAnimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdvancedFilterPanel by remember { mutableStateOf(false) }
    var showAllGenreCards by remember { mutableStateOf(false) }
    var genreSelectionModeExclude by remember { mutableStateOf(false) }

    val watchlistIds = remember(watchlist) { watchlist.map { it.animeId }.toSet() }
    val quickPills = remember {
        listOf("Action", "Adventure", "Romance", "Comedy", "Fantasy", "Science fiction", "Drama")
    }
    val displayedGenreCards = remember(allGenres, showAllGenreCards) {
        if (showAllGenreCards) allGenres else allGenres.take(4)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("discover_screen_list"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Header + Random Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = GlassCardSurface,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onRandomAnimeClick)
                            .testTag("discover_random_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Random Anime",
                                tint = SoftLavender,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Surprise Me",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = if (filterState.activeFilterCount > 0 || showAdvancedFilterPanel) {
                            ElectricViolet.copy(alpha = 0.3f)
                        } else {
                            GlassCardSurface
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (filterState.activeFilterCount > 0) ElectricViolet else SubtleBorderGlow,
                                RoundedCornerShape(50)
                            )
                            .clip(RoundedCornerShape(50))
                            .clickable { showAdvancedFilterPanel = !showAdvancedFilterPanel }
                            .testTag("discover_filters_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = SoftLavender,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (filterState.activeFilterCount > 0) {
                                    "Filters (${filterState.activeFilterCount})"
                                } else {
                                    "Filters"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Search input on Discover matching Screen 3
        item {
            TextField(
                value = filterState.query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search anime, genres, or titles...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (filterState.query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextSecondary,
                            modifier = Modifier.clickable { onQueryChange("") }
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GlassCardSurface,
                    unfocusedContainerColor = GlassCardSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, SubtleBorderGlow, RoundedCornerShape(24.dp))
                    .testTag("discover_search_input")
            )
        }

        // Quick Genre Pill Cloud matching Screen 3 ("Action", "Adventure", "Romance", "Comedy", "Fantasy", "Sci-Fi", "Drama", "More ->")
        item {
            Spacer(modifier = Modifier.height(14.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPills.forEach { genreName ->
                    val isIncluded = filterState.includedGenres.contains(genreName)
                    val isExcluded = filterState.excludedGenres.contains(genreName)
                    val displayLabel = if (genreName == "Science fiction") "Sci-Fi" else genreName

                    Surface(
                        color = when {
                            isIncluded -> ElectricViolet.copy(alpha = 0.35f)
                            isExcluded -> CoralWarning.copy(alpha = 0.25f)
                            else -> GlassCardSurface
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = when {
                                    isIncluded -> SoftLavender
                                    isExcluded -> CoralWarning
                                    else -> SubtleBorderGlow
                                },
                                shape = RoundedCornerShape(50)
                            )
                            .clip(RoundedCornerShape(50))
                            .clickable { onToggleIncludeGenre(genreName) }
                            .testTag("quick_genre_${genreName.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = displayLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // "More ->" pill matching Screen 3
                Surface(
                    color = GlassCardHighlight,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .border(1.dp, SoftLavender.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .clickable { showAdvancedFilterPanel = !showAdvancedFilterPanel }
                        .testTag("discover_more_genres_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (showAdvancedFilterPanel) "Less" else "More",
                            style = MaterialTheme.typography.labelLarge,
                            color = SoftLavender
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = SoftLavender,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Expandable Full 26-Genre Combiner + Exclusion + Sort/Year/Season/Status/Format Panel
        item {
            AnimatedVisibility(visible = showAdvancedFilterPanel) {
                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(22.dp))
                        .testTag("advanced_filter_panel")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Multi-Genre & Advanced Filters",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (filterState.activeFilterCount > 0) {
                                Text(
                                    text = "Reset All",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CoralWarning,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = onClearFilters)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("reset_filters_button")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Include vs Exclude mode toggle
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !genreSelectionModeExclude,
                                onClick = { genreSelectionModeExclude = false },
                                label = { Text("Include Genres (+)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            FilterChip(
                                selected = genreSelectionModeExclude,
                                onClick = { genreSelectionModeExclude = true },
                                label = { Text("Exclude Genres (-)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CoralWarning.copy(alpha = 0.28f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All 26 Genres, Themes & Demographics:",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allGenres.forEach { genre ->
                                val isInc = filterState.includedGenres.contains(genre.name)
                                val isExc = filterState.excludedGenres.contains(genre.name)
                                Surface(
                                    color = when {
                                        isInc -> ElectricViolet.copy(alpha = 0.38f)
                                        isExc -> CoralWarning.copy(alpha = 0.35f)
                                        else -> Color(0xFF111426)
                                    },
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier
                                        .border(
                                            1.dp,
                                            when {
                                                isInc -> SoftLavender
                                                isExc -> CoralWarning
                                                else -> SubtleBorderGlow
                                            },
                                            RoundedCornerShape(50)
                                        )
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            if (genreSelectionModeExclude) {
                                                onToggleExcludeGenre(genre.name)
                                            } else {
                                                onToggleIncludeGenre(genre.name)
                                            }
                                        }
                                ) {
                                    val prefix = when {
                                        isInc -> "+ "
                                        isExc -> "✕ "
                                        else -> ""
                                    }
                                    Text(
                                        text = "$prefix${genre.name}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Sort By", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(SortOption.entries) { option ->
                                FilterChip(
                                    selected = filterState.sortBy == option,
                                    onClick = { onSortChange(option) },
                                    label = { Text(option.label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Format & Discovery Modes", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = filterState.onlyFreeVerified,
                                    onClick = { onOnlyFreeChange(!filterState.onlyFreeVerified) },
                                    label = { Text("Official Free Streams Only") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = filterState.onlyHiddenGems,
                                    onClick = { onOnlyHiddenGemsChange(!filterState.onlyHiddenGems) },
                                    label = { Text("Hidden Gems Only") }
                                )
                            }
                            items(listOf("TV", "Movie")) { fmt ->
                                FilterChip(
                                    selected = filterState.selectedFormat == fmt,
                                    onClick = {
                                        onFormatChange(if (filterState.selectedFormat == fmt) null else fmt)
                                    },
                                    label = { Text(fmt) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Season & Status", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("Winter", "Spring", "Summer", "Fall")) { season ->
                                FilterChip(
                                    selected = filterState.selectedSeason == season,
                                    onClick = {
                                        onSeasonChange(if (filterState.selectedSeason == season) null else season)
                                    },
                                    label = { Text(season) }
                                )
                            }
                            items(listOf("Currently Airing", "Finished Airing")) { st ->
                                FilterChip(
                                    selected = filterState.selectedStatus == st,
                                    onClick = {
                                        onStatusChange(if (filterState.selectedStatus == st) null else st)
                                    },
                                    label = { Text(st) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Release Year", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf(2024, 2023, 2022, 2021, 2020, 2019, 2016)) { yr ->
                                FilterChip(
                                    selected = filterState.selectedYear == yr,
                                    onClick = {
                                        onYearChange(if (filterState.selectedYear == yr) null else yr)
                                    },
                                    label = { Text(yr.toString()) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Trending Genres 2-column rich visual cards matching Screen 3
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "Trending Genres",
                actionLabel = if (showAllGenreCards) "Show top 4" else "View all (${allGenres.size})",
                onActionClick = { showAllGenreCards = !showAllGenreCards }
            )
            val rows = displayedGenreCards.chunked(2)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { genre ->
                            val isSelected = filterState.includedGenres.contains(genre.name)
                            TrendingGenreCard(
                                genre = genre,
                                isSelected = isSelected,
                                onClick = { onToggleIncludeGenre(genre.name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Filtered / This Season Section matching Screen 3
        item {
            Spacer(modifier = Modifier.height(20.dp))
            val headerTitle = when {
                filterState.onlyHiddenGems -> "Underrated & Hidden Gems (${filteredResults.size})"
                filterState.activeFilterCount > 0 || filterState.query.isNotBlank() ->
                    "Matching Anime (${filteredResults.size})"
                else -> "This Season"
            }
            SectionHeader(
                title = headerTitle,
                actionLabel = if (filterState.activeFilterCount > 0) "Clear Filters" else "View all",
                onActionClick = {
                    if (filterState.activeFilterCount > 0) onClearFilters() else showAdvancedFilterPanel = true
                }
            )

            // Filtering State Machine progress indicator
            if (filterSearchState is FilterSearchState.Filtering) {
                Surface(
                    color = ElectricViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.dp, SoftLavender.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Refining anime catalog by genre filters...",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftLavender
                        )
                    }
                }
            }

            // Error State with Retry
            if (filterSearchState is FilterSearchState.Error) {
                Surface(
                    color = CoralWarning.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, CoralWarning, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Filter Error",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Text(
                                text = filterSearchState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        if (onRetry != null) {
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = CoralWarning)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }

        if (filteredResults.isEmpty()) {
            item {
                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        val emptyMsg = (filterSearchState as? FilterSearchState.Empty)?.message
                            ?: "No anime matched those exact filters"
                        Text(
                            text = emptyMsg,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try removing a genre exclusion or resetting the active filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onClearFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            val gridRows = filteredResults.chunked(3)
            items(gridRows) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { anime ->
                        Box(modifier = Modifier.weight(1f)) {
                            AnimePosterCard(
                                anime = anime,
                                isBookmarked = watchlistIds.contains(anime.id),
                                onClick = { onAnimeClick(anime.id) },
                                onBookmarkToggle = { onToggleWatchlist(anime) },
                                width = 120.dp,
                                height = 162.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Pagination button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = onLoadMore,
                        enabled = !isLoadingMore,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.testTag("discover_load_more_button")
                    ) {
                        Text(
                            text = if (isLoadingMore) "Fetching from Jikan API..." else "Load More Anime",
                            color = SoftLavender
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendingGenreCard(
    genre: AnimeGenre,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(106.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SoftLavender else SubtleBorderGlow.copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .testTag("genre_card_${genre.name.lowercase().replace(" ", "_")}")
    ) {
        AnimeArtworkImage(
            imageUrl = genre.representativeImageUrl,
            contentDescription = genre.name,
            accentSeed = genre.malId,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(genre.accentHex).copy(alpha = 0.55f),
                            Color(0xDD0A0C18)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = genre.countLabel,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.78f)
            )
        }

        if (isSelected) {
            Surface(
                color = EmeraldVerified,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.padding(3.dp)
                )
            }
        }
    }
}
