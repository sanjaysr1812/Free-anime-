package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.RecentSearchEntity
import com.example.data.model.AnimeItem
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StarAmber
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.FilterSearchState
import com.example.ui.theme.CoralWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    query: String,
    suggestions: List<String>,
    recentSearches: List<RecentSearchEntity>,
    searchResults: List<AnimeItem>,
    onQueryChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    onPlayClick: (Int) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit,
    onToggleFavorite: (AnimeItem) -> Unit,
    modifier: Modifier = Modifier,
    filterSearchState: FilterSearchState? = null,
    onRetry: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_list"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Top Search Bar + Cancel button matching Screen 6
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = query,
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
                            tint = SoftLavender
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear query",
                                    tint = TextSecondary
                                )
                            }
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
                        .weight(1f)
                        .border(1.dp, ElectricViolet.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .testTag("search_input_field")
                )

                if (query.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onQueryChange("") }
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Instant Search Suggestions
        if (suggestions.isNotEmpty()) {
            item {
                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        suggestions.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val clean = suggestion.removePrefix("Genre: ")
                                        onSuggestionClick(clean)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = SoftLavender,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Searches section matching Screen 6
        if (recentSearches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelMedium,
                        color = SoftLavender,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onClearRecentSearches)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSearches.forEach { recent ->
                        Surface(
                            color = GlassCardSurface,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                                .clip(RoundedCornerShape(50))
                                .clickable { onQueryChange(recent.query) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = recent.query,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove ${recent.query}",
                                    tint = TextMuted,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onRemoveRecentSearch(recent.query) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Popular Searches / Matching Results header
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = if (query.isBlank()) "Popular Searches" else "Search Results (${searchResults.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            if (filterSearchState is FilterSearchState.Filtering) {
                Surface(
                    color = ElectricViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.dp, SoftLavender.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Searching titles and genres...",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftLavender,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            if (filterSearchState is FilterSearchState.Error) {
                Surface(
                    color = CoralWarning.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.dp, CoralWarning, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = filterSearchState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (onRetry != null) {
                            Text(
                                text = "Retry",
                                style = MaterialTheme.typography.labelLarge,
                                color = SoftLavender,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable(onClick = onRetry)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val emptyMsg = (filterSearchState as? FilterSearchState.Empty)?.message
                            ?: if (query.isNotBlank()) "No anime found for \"$query\"" else "No matching anime"
                        Text(
                            text = emptyMsg,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching by English title, Japanese title, or genre (e.g., Isekai, Romance, Dark fantasy).",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(searchResults, key = { it.id }) { anime ->
                SearchAnimeRowItem(
                    anime = anime,
                    onClick = { onAnimeClick(anime.id) },
                    onPlayClick = { onPlayClick(anime.id) },
                    onToggleWatchlist = { onToggleWatchlist(anime) },
                    onToggleFavorite = { onToggleFavorite(anime) }
                )
            }
        }
    }
}

@Composable
private fun SearchAnimeRowItem(
    anime: AnimeItem,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("search_result_${anime.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimeArtworkImage(
            imageUrl = anime.posterUrl,
            contentDescription = anime.title,
            accentSeed = anime.id,
            modifier = Modifier
                .size(width = 62.dp, height = 62.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(14.dp))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarAmber,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = String.format("%.1f", anime.score),
                    style = MaterialTheme.typography.labelSmall,
                    color = StarAmber
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = anime.allTags.take(3).joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (anime.hasVerifiedFreeStream) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Official Free Stream Available",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldVerified
                )
            }
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options for ${anime.title}",
                    tint = TextSecondary
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(GlassCardSurface)
            ) {
                DropdownMenuItem(
                    text = { Text("Watch Now", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onPlayClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Toggle Watchlist", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onToggleWatchlist()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Toggle Favorite", color = Color.White) },
                    onClick = {
                        menuExpanded = false
                        onToggleFavorite()
                    }
                )
            }
        }
    }
}
