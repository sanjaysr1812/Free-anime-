package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.data.local.LibraryEntryEntity
import com.example.data.local.WatchedEpisodeEntity
import com.example.data.model.AnimeItem
import com.example.ui.MainTab
import com.example.ui.components.AnimeArtworkImage
import com.example.ui.components.AnimePosterCard
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.GradientPlayButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.pressScaleClickable
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GlassCardHighlight
import com.example.ui.theme.GlassCardSurface
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.OrchidPink
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SubtleBorderGlow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    catalog: List<AnimeItem>,
    continueWatching: List<LibraryEntryEntity>,
    watchlist: List<LibraryEntryEntity>,
    watchedEpisodes: List<WatchedEpisodeEntity>,
    reducedMotion: Boolean,
    onAnimeClick: (Int) -> Unit,
    onPlayClick: (Int, Int) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit,
    onNavigateTab: (MainTab) -> Unit,
    onRandomAnimeClick: () -> Unit,
    onFilterHiddenGemsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val watchlistIds = remember(watchlist) { watchlist.map { it.animeId }.toSet() }
    val featuredList = remember(catalog) { catalog.take(5) }
    val trendingList = remember(catalog) { catalog.sortedBy { it.popularity }.take(10) }
    val seasonalList = remember(catalog) {
        catalog.filter { it.year >= 2022 || it.status.contains("Airing", ignoreCase = true) }
            .ifEmpty { catalog }
    }
    val newEpisodesList = remember(catalog) {
        catalog.filter { it.hasNewVerifiedEpisode || it.hasVerifiedFreeStream }
    }
    val recentlyAddedList = remember(catalog) {
        catalog.filter { it.isRecentlyAdded }.ifEmpty { catalog.reversed().take(6) }
    }
    val allTimePopularList = remember(catalog) {
        catalog.sortedByDescending { it.scoredBy }.take(10)
    }
    val hiddenGemsList = remember(catalog) {
        catalog.filter { it.isHiddenGem || it.popularity > 100 }.ifEmpty { catalog.takeLast(6) }
    }

    val recommendedList = remember(catalog, watchlist) {
        val preferredGenres = watchlist
            .flatMap { it.genresCsv.split(",") }
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .toSet()
        if (preferredGenres.isEmpty()) {
            catalog.sortedByDescending { it.score }.take(8)
        } else {
            catalog.sortedByDescending { anime ->
                anime.allTags.count { preferredGenres.contains(it.lowercase()) } * 10 + anime.score
            }.take(8)
        }
    }

    val recentlyWatchedAnime = remember(catalog, watchedEpisodes, continueWatching) {
        val watchedIds = (watchedEpisodes.map { it.animeId } + continueWatching.map { it.animeId }).distinct()
        watchedIds.mapNotNull { id -> catalog.find { it.id == id } }
    }

    var selectedGenre by remember { mutableStateOf<String?>(null) }
    val homeGenres = remember {
        listOf("Action", "Romance", "Sci-Fi", "Fantasy", "Comedy", "Drama", "Supernatural", "Shounen")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 1. Top App Bar
        item {
            HomeTopBar(
                onSearchClick = { onNavigateTab(MainTab.SEARCH) },
                onRandomClick = onRandomAnimeClick,
                onProfileClick = { onNavigateTab(MainTab.PROFILE) }
            )
        }

        // 2. Cinematic Featured Anime Hero Section
        if (featuredList.isNotEmpty()) {
            item {
                FeaturedHeroCarousel(
                    featuredAnime = featuredList,
                    watchlistIds = watchlistIds,
                    reducedMotion = reducedMotion,
                    onPlayClick = { anime -> onPlayClick(anime.id, 1) },
                    onDetailsClick = { anime -> onAnimeClick(anime.id) },
                    onToggleWatchlist = onToggleWatchlist
                )
            }
        }

        // 2.5 Horizontal Chip-based Genre Filter Component
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalGenreFilterChips(
                genres = homeGenres,
                selectedGenre = selectedGenre,
                onGenreSelected = { genre ->
                    selectedGenre = if (selectedGenre == genre) null else genre
                }
            )
        }

        // If a specific genre is selected, display an instant Spotlight Carousel for that genre
        if (selectedGenre != null) {
            val genreFilteredList = catalog.filter { anime ->
                val target = selectedGenre!!.lowercase()
                anime.genres.any { it.lowercase().contains(target) } ||
                anime.allTags.any { it.lowercase().contains(target) }
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    title = "$selectedGenre Hits",
                    subtitle = "Showing top rated $selectedGenre anime",
                    onActionClick = { onNavigateTab(MainTab.DISCOVER) }
                )
                if (genreFilteredList.isNotEmpty()) {
                    HorizontalAnimeRow(
                        items = genreFilteredList,
                        watchlistIds = watchlistIds,
                        onAnimeClick = onAnimeClick,
                        onToggleWatchlist = onToggleWatchlist
                    )
                } else {
                    Surface(
                        color = GlassCardSurface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .border(1.dp, SubtleBorderGlow, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "No $selectedGenre titles currently in the instant spotlight.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Continue Watching (backed by Room RecentlyWatched & library_entries)
        if (continueWatching.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(
                    title = "Continue Watching",
                    onActionClick = { onNavigateTab(MainTab.LIBRARY) }
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    continueWatching.take(2).forEach { entry ->
                        ContinueWatchingCard(
                            entry = entry,
                            onResumeClick = { onPlayClick(entry.animeId, entry.lastWatchedEpisode.coerceAtLeast(1)) },
                            onDetailsClick = { onAnimeClick(entry.animeId) }
                        )
                    }
                }
            }
        }

        // 4. Trending Now
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Trending Now",
                onActionClick = { onNavigateTab(MainTab.DISCOVER) }
            )
            HorizontalAnimeRow(
                items = trendingList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 5. Popular This Season
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Popular This Season",
                subtitle = "Simulcast hits & seasonal favorites",
                onActionClick = { onNavigateTab(MainTab.DISCOVER) }
            )
            HorizontalAnimeRow(
                items = seasonalList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 6. New & Verified Free Episodes
        if (newEpisodesList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    title = "New & Verified Free Episodes",
                    subtitle = "Authorized free streams & official YouTube channels",
                    onActionClick = { onNavigateTab(MainTab.DISCOVER) }
                )
                HorizontalAnimeRow(
                    items = newEpisodesList,
                    watchlistIds = watchlistIds,
                    onAnimeClick = onAnimeClick,
                    onToggleWatchlist = onToggleWatchlist
                )
            }
        }

        // 7. Random Anime Discovery Banner
        item {
            Spacer(modifier = Modifier.height(20.dp))
            RandomDiscoveryBanner(
                onRandomClick = onRandomAnimeClick,
                onHiddenGemsClick = onFilterHiddenGemsClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 8. Hidden Gems & Underrated Anime
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Hidden Gems & Underrated",
                subtitle = "Critically acclaimed masterpieces off the beaten path",
                onActionClick = onFilterHiddenGemsClick
            )
            HorizontalAnimeRow(
                items = hiddenGemsList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 9. Recommended For You
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Recommended For You",
                subtitle = "Curated from your watchlist & favorite genres",
                onActionClick = { onNavigateTab(MainTab.DISCOVER) }
            )
            HorizontalAnimeRow(
                items = recommendedList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 10. My Watchlist
        if (watchlist.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    title = "My Watchlist",
                    onActionClick = { onNavigateTab(MainTab.LIBRARY) }
                )
                val watchlistAnime = watchlist.mapNotNull { entry ->
                    catalog.find { it.id == entry.animeId }
                }
                HorizontalAnimeRow(
                    items = watchlistAnime,
                    watchlistIds = watchlistIds,
                    onAnimeClick = onAnimeClick,
                    onToggleWatchlist = onToggleWatchlist
                )
            }
        }

        // 11. Recently Added Titles
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Recently Added Titles",
                onActionClick = { onNavigateTab(MainTab.DISCOVER) }
            )
            HorizontalAnimeRow(
                items = recentlyAddedList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 12. Most Popular of All Time
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Most Popular of All Time",
                onActionClick = { onNavigateTab(MainTab.DISCOVER) }
            )
            HorizontalAnimeRow(
                items = allTimePopularList,
                watchlistIds = watchlistIds,
                onAnimeClick = onAnimeClick,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        // 13. Recently Watched
        if (recentlyWatchedAnime.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    title = "Recently Watched",
                    onActionClick = { onNavigateTab(MainTab.LIBRARY) }
                )
                HorizontalAnimeRow(
                    items = recentlyWatchedAnime,
                    watchlistIds = watchlistIds,
                    onAnimeClick = onAnimeClick,
                    onToggleWatchlist = onToggleWatchlist
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onSearchClick: () -> Unit,
    onRandomClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                    append("Anime")
                }
                withStyle(SpanStyle(color = LuminousPurple, fontWeight = FontWeight.Bold)) {
                    append("Flix")
                }
            },
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onRandomClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassCardSurface)
                    .border(1.dp, SubtleBorderGlow, CircleShape)
                    .testTag("home_random_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Random Anime Discovery",
                    tint = SoftLavender,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassCardSurface)
                    .border(1.dp, SubtleBorderGlow, CircleShape)
                    .testTag("home_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Anime",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(ElectricViolet, OrchidPink))
                    )
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(GlassCardSurface)
                    .testTag("home_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = SoftLavender,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturedHeroCarousel(
    featuredAnime: List<AnimeItem>,
    watchlistIds: Set<Int>,
    reducedMotion: Boolean,
    onPlayClick: (AnimeItem) -> Unit,
    onDetailsClick: (AnimeItem) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit
) {
    var activeIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(featuredAnime.size, reducedMotion) {
        if (!reducedMotion && featuredAnime.size > 1) {
            while (true) {
                delay(6000)
                activeIndex = (activeIndex + 1) % featuredAnime.size
            }
        }
    }

    val currentAnime = featuredAnime.getOrElse(activeIndex) { featuredAnime.first() }
    val inWatchlist = watchlistIds.contains(currentAnime.id)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(242.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(SoftLavender.copy(alpha = 0.55f), SubtleBorderGlow.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .pressScaleClickable(pressedScale = 0.98f) { onDetailsClick(currentAnime) }
                .testTag("featured_hero_card")
        ) {
            AnimatedContent(
                targetState = currentAnime,
                transitionSpec = {
                    if (reducedMotion) {
                        fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                    } else {
                        (fadeIn(tween(420)) + scaleIn(initialScale = 1.04f, animationSpec = tween(420)))
                            .togetherWith(fadeOut(tween(320)))
                            .using(SizeTransform(clip = false))
                    }
                },
                label = "heroAnimatedContent"
            ) { anime ->
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimeArtworkImage(
                        imageUrl = anime.backdropUrl.ifBlank { anime.posterUrl },
                        contentDescription = anime.title,
                        accentSeed = anime.id,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic multi-stop vignette overlay matching Screen 2
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x22070810),
                                        Color(0x88090B16),
                                        Color(0xF50A0C1A)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = anime.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (anime.genres + anime.demographics).distinct().take(3).joinToString(" • "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftLavender.copy(alpha = 0.92f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GradientPlayButton(
                                    text = "Play",
                                    onClick = { onPlayClick(anime) },
                                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                                    modifier = Modifier.testTag("hero_play_button")
                                )

                                IconButton(
                                    onClick = { onToggleWatchlist(anime) },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xAA171A30))
                                        .border(1.dp, SubtleBorderGlow, CircleShape)
                                        .testTag("hero_watchlist_button")
                                ) {
                                    Icon(
                                        imageVector = if (inWatchlist) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = "Toggle Watchlist",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Clickable indicator pill (e.g. 1/5) to cycle hero items
                            Surface(
                                color = Color(0xCC131629),
                                shape = CircleShape,
                                modifier = Modifier
                                    .border(1.dp, SubtleBorderGlow, CircleShape)
                                    .clip(CircleShape)
                                    .clickable {
                                        activeIndex = (activeIndex + 1) % featuredAnime.size
                                    }
                                    .testTag("hero_cycle_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${activeIndex + 1}/${featuredAnime.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Featured Anime",
                                        tint = SoftLavender,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated pagination indicator pills underneath hero card
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            featuredAnime.forEachIndexed { idx, _ ->
                val isSelected = idx == activeIndex
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 22.dp else 7.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "heroDotWidth"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(LuminousPurple, OrchidPink))
                            } else {
                                Brush.horizontalGradient(listOf(SubtleBorderGlow, SubtleBorderGlow))
                            }
                        )
                        .clickable { activeIndex = idx }
                )
            }
        }
    }
}

@Composable
private fun HorizontalAnimeRow(
    items: List<AnimeItem>,
    watchlistIds: Set<Int>,
    onAnimeClick: (Int) -> Unit,
    onToggleWatchlist: (AnimeItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { anime ->
            AnimePosterCard(
                anime = anime,
                isBookmarked = watchlistIds.contains(anime.id),
                onClick = { onAnimeClick(anime.id) },
                onBookmarkToggle = { onToggleWatchlist(anime) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun RandomDiscoveryBanner(
    onRandomClick: () -> Unit,
    onHiddenGemsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF281852),
                        Color(0xFF171A36),
                        Color(0xFF2B1442)
                    )
                )
            )
            .border(1.dp, ElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SoftLavender,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Can't decide what to watch?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Spin a random anime or explore underrated hidden gems.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = ElectricViolet,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .pressScaleClickable(pressedScale = 0.94f, onClick = onRandomClick)
                            .testTag("banner_random_anime_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Random Anime",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = GlassCardHighlight,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, SubtleBorderGlow, RoundedCornerShape(50))
                            .pressScaleClickable(pressedScale = 0.94f, onClick = onHiddenGemsClick)
                    ) {
                        Text(
                            text = "Hidden Gems",
                            style = MaterialTheme.typography.labelLarge,
                            color = SoftLavender,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalGenreFilterChips(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_genre_filter_chips_row"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" chip to reset or clear selection
        item {
            val isAllSelected = selectedGenre == null
            FilterChip(
                selected = isAllSelected,
                onClick = {
                    if (!isAllSelected) {
                        onGenreSelected("")
                    }
                },
                label = {
                    Text(
                        text = "All Genres",
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0x331E2138),
                    labelColor = TextSecondary,
                    selectedContainerColor = ElectricViolet,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isAllSelected,
                    borderColor = SubtleBorderGlow,
                    selectedBorderColor = SoftLavender
                ),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("genre_chip_all")
            )
        }

        items(genres, key = { it }) { genre ->
            val isSelected = selectedGenre == genre
            FilterChip(
                selected = isSelected,
                onClick = { onGenreSelected(genre) },
                label = {
                    Text(
                        text = genre,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0x331E2138),
                    labelColor = TextSecondary,
                    selectedContainerColor = ElectricViolet,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = SubtleBorderGlow,
                    selectedBorderColor = SoftLavender
                ),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("genre_chip_${genre.lowercase().replace(" ", "_")}")
            )
        }
    }
}

