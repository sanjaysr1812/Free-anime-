package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.WatchedEpisodeEntity
import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeEpisode
import com.example.data.model.AnimeItem
import com.example.data.model.OfficialStreamSource
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.components.AnimePosterCard
import com.example.ui.components.GradientPlayButton
import com.example.ui.theme.CoralWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StarAmber
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailsScreen(
    anime: AnimeItem,
    allCatalog: List<AnimeItem>,
    isInWatchlist: Boolean,
    isFavorite: Boolean,
    watchedEpisodes: List<WatchedEpisodeEntity>,
    onBackClick: () -> Unit,
    onPlayEpisodeClick: (Int) -> Unit,
    onToggleWatchlist: () -> Unit,
    onToggleFavorite: () -> Unit,
    onGenreClick: (String) -> Unit,
    onRelatedAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var synopsisExpanded by remember { mutableStateOf(false) }
    val tabs = listOf("About", "Episodes", "Characters", "More")

    val watchedEpNumbers = remember(watchedEpisodes, anime.id) {
        watchedEpisodes.filter { it.animeId == anime.id }.map { it.episodeNumber }.toSet()
    }

    val relatedAnimeList = remember(anime, allCatalog) {
        val byRelatedIds = anime.relatedIds.mapNotNull { id -> allCatalog.find { it.id == id } }
        val bySharedGenres = allCatalog.filter { candidate ->
            candidate.id != anime.id && candidate.genres.any { anime.genres.contains(it) }
        }
        (byRelatedIds + bySharedGenres).distinctBy { it.id }.take(8)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .testTag("anime_details_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Full-bleed Backdrop + Top Floating Actions + Title & Metadata (matching Screen 4)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                AnimeArtworkImage(
                    imageUrl = anime.backdropUrl.ifBlank { anime.posterUrl },
                    contentDescription = anime.title,
                    accentSeed = anime.id,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient fade to MidnightNavy
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x55070810),
                                    Color(0x66090B16),
                                    MidnightNavy
                                )
                            )
                        )
                )

                // Top Bar with Back, Share, Watchlist Bookmark
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xAA101326))
                            .border(1.dp, SubtleBorderGlow, CircleShape)
                            .testTag("details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out ${anime.title} (${anime.titleJapanese}) on AnimeFlix! Score: ★ ${anime.score}"
                                    )
                                }
                                runCatching {
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Anime"))
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xAA101326))
                                .border(1.dp, SubtleBorderGlow, CircleShape)
                                .testTag("details_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share ${anime.title}",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleWatchlist,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xAA101326))
                                .border(1.dp, SubtleBorderGlow, CircleShape)
                                .testTag("details_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark ${anime.title}",
                                tint = if (isInWatchlist) SoftLavender else Color.White
                            )
                        }
                    }
                }

                // Bottom of hero: Title, Japanese Title, Year/Format/Duration/Rating
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = anime.title,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (anime.titleJapanese.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = anime.titleJapanese,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${anime.year} • ${anime.format} • ${anime.duration} • ${anime.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Score + Genre Badges + Synopsis + Play & Watchlist Buttons (matching Screen 4)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Star score pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 6.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", anime.score),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${anime.scoredBy / 1000}k)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    anime.allTags.take(4).forEach { tag ->
                        Surface(
                            color = GlassCardSurface,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                                .clip(RoundedCornerShape(50))
                                .clickable { onGenreClick(tag) }
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                color = SoftLavender,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = anime.synopsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary.copy(alpha = 0.9f),
                    maxLines = if (synopsisExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { synopsisExpanded = !synopsisExpanded }
                )
                if (anime.synopsis.length > 180) {
                    Text(
                        text = if (synopsisExpanded) "Show less" else "Read more",
                        style = MaterialTheme.typography.labelMedium,
                        color = SoftLavender,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { synopsisExpanded = !synopsisExpanded }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Play Button + Plus (Watchlist) Button + Favorite Heart Button matching Screen 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientPlayButton(
                        text = if (anime.hasVerifiedFreeStream) "Play" else "Check Availability / Preview",
                        onClick = { onPlayEpisodeClick(1) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("details_play_button")
                    )

                    IconButton(
                        onClick = onToggleWatchlist,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GlassCardSurface)
                            .border(1.dp, SubtleBorderGlow, CircleShape)
                            .testTag("details_add_watchlist_button")
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Add to Watchlist",
                            tint = if (isInWatchlist) EmeraldVerified else Color.White
                        )
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GlassCardSurface)
                            .border(1.dp, SubtleBorderGlow, CircleShape)
                            .testTag("details_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFF43F5E) else Color.White
                        )
                    }
                }
            }
        }

        // 3. Tabs: About | Episodes | Characters | More (matching Screen 4)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = LuminousPurple
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTabIndex == idx,
                        onClick = { selectedTabIndex = idx },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (selectedTabIndex == idx) Color.White else TextSecondary,
                                fontWeight = if (selectedTabIndex == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("details_tab_${title.lowercase()}")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Tab Content
        when (selectedTabIndex) {
            0 -> {
                // ABOUT TAB (Genres + Where to watch Official Free Sources + Metadata)
                item {
                    AboutTabContent(
                        anime = anime,
                        onGenreClick = onGenreClick,
                        onWatchFreeClick = { onPlayEpisodeClick(1) },
                        onOpenExternalSource = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                    )
                }
            }

            1 -> {
                // EPISODES TAB
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Season 1 (${anime.episodes.size} Episodes)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        val freeCount = anime.episodes.count { it.isAvailableFree }
                        Text(
                            text = "$freeCount Verified Free",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (freeCount > 0) EmeraldVerified else CoralWarning
                        )
                    }
                }
                items(anime.episodes, key = { it.number }) { ep ->
                    EpisodeRowItem(
                        episode = ep,
                        isWatched = watchedEpNumbers.contains(ep.number),
                        onClick = { onPlayEpisodeClick(ep.number) }
                    )
                }
            }

            2 -> {
                // CHARACTERS TAB
                if (anime.characters.isEmpty()) {
                    item {
                        Text(
                            text = "Character & staff credits are being synced from Jikan API...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                } else {
                    items(anime.characters, key = { it.id }) { character ->
                        CharacterRowCard(character = character)
                    }
                }
            }

            3 -> {
                // MORE TAB (Related & Recommended + Studios & Alternative Titles)
                item {
                    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Text(
                            text = "Recommended & Related Anime",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedAnimeList, key = { it.id }) { related ->
                            AnimePosterCard(
                                anime = related,
                                isBookmarked = false,
                                onClick = { onRelatedAnimeClick(related.id) },
                                onBookmarkToggle = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutTabContent(
    anime: AnimeItem,
    onGenreClick: (String) -> Unit,
    onWatchFreeClick: () -> Unit,
    onOpenExternalSource: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Text(
            text = "Genres & Themes",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            anime.allTags.forEach { genre ->
                Surface(
                    color = GlassCardSurface,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .clickable { onGenreClick(genre) }
                ) {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Where to watch - Official Free Sources Card matching Screen 4
        Surface(
            color = GlassCardSurface,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SubtleBorderGlow, RoundedCornerShape(20.dp))
                .testTag("where_to_watch_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Where to watch",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Official Free Sources & Authorized Distributors",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                anime.officialSources.forEach { source ->
                    OfficialSourceItem(
                        source = source,
                        onPlayInApp = onWatchFreeClick,
                        onOpenExternal = { onOpenExternalSource(source.url) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Detailed Metadata Grid
        Surface(
            color = GlassCardSurface.copy(alpha = 0.7f),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SubtleBorderGlow.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetadataLine("Status", anime.status)
                MetadataLine("Season & Year", "${anime.season} ${anime.year}")
                MetadataLine("Format & Episodes", "${anime.format} • ${anime.episodesCount} episodes")
                if (anime.studios.isNotEmpty()) {
                    MetadataLine("Studios", anime.studios.joinToString(", "))
                }
                if (anime.alternativeTitles.isNotEmpty()) {
                    MetadataLine("Alternative Titles", anime.alternativeTitles.joinToString(", "))
                }
            }
        }
    }
}

@Composable
private fun OfficialSourceItem(
    source: OfficialStreamSource,
    onPlayInApp: () -> Unit,
    onOpenExternal: () -> Unit
) {
    Surface(
        color = GlassCardHighlight.copy(alpha = 0.75f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SubtleBorderGlow, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                if (source.isFreeVerified && source.directStreamUrl != null) {
                    onPlayInApp()
                } else {
                    onOpenExternal()
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (source.providerName.contains("YouTube", ignoreCase = true)) {
                    Color(0xFFEF4444)
                } else {
                    ElectricViolet
                },
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.providerName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = source.badgeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (source.isFreeVerified) EmeraldVerified else TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open ${source.providerName}",
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun MetadataLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun EpisodeRowItem(
    episode: AnimeEpisode,
    isWatched: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = GlassCardSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 5.dp)
            .border(
                1.dp,
                if (episode.isAvailableFree) SubtleBorderGlow else SubtleBorderGlow.copy(alpha = 0.35f),
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("episode_item_${episode.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 76.dp, height = 52.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AnimeArtworkImage(
                    imageUrl = episode.thumbnailUrl,
                    contentDescription = episode.title,
                    accentSeed = episode.number,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x66000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (episode.isAvailableFree) Icons.Default.PlayCircleFilled else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (episode.isAvailableFree) Color.White else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ep ${episode.number} • ${episode.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isWatched) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Watched",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftLavender
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (episode.isAvailableFree) {
                        "${episode.durationMinutes}m • ${episode.sourceLabel}"
                    } else {
                        "No official free stream available"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (episode.isAvailableFree) EmeraldVerified else CoralWarning
                )
            }

            if (!episode.isAvailableFree) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unavailable Free",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Play Episode ${episode.number}",
                    tint = SoftLavender
                )
            }
        }
    }
}

@Composable
private fun CharacterRowCard(character: AnimeCharacter) {
    Surface(
        color = GlassCardSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 5.dp)
            .border(1.dp, SubtleBorderGlow, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimeArtworkImage(
                imageUrl = character.imageUrl,
                contentDescription = character.name,
                accentSeed = character.id,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = character.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftLavender
                )
            }
            if (character.voiceActorName.isNotBlank()) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = character.voiceActorName,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "CV (${character.voiceActorLanguage})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
