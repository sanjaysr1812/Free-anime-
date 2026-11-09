package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AnimeFlixViewModel
import com.example.ui.MainTab
import com.example.ui.ScreenDestination
import com.example.ui.components.AdaptiveSideNavigationRail
import com.example.ui.components.AmbientNebulaBackground
import com.example.ui.components.FloatingGlassBottomBar
import com.example.ui.screens.AnimeDetailsScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.WelcomeOnboardingScreen
import com.example.ui.theme.AnimeFlixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel: AnimeFlixViewModel = viewModel(
                factory = AnimeFlixViewModel.provideFactory(context)
            )
            val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

            AnimeFlixTheme(appearanceMode = userSettings.appearanceMode) {
                AnimeFlixApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AnimeFlixApp(
    viewModel: AnimeFlixViewModel
) {
    val navStack by viewModel.navigationStack.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()
    val allLibraryEntries by viewModel.allLibraryEntries.collectAsStateWithLifecycle()
    val watchedEpisodes by viewModel.watchedEpisodes.collectAsStateWithLifecycle()
    val customLists by viewModel.customLists.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val cachedAnime by viewModel.cachedAnime.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val filterSearchState by viewModel.filterSearchState.collectAsStateWithLifecycle()

    val currentDestination = navStack.lastOrNull() ?: ScreenDestination.Main(MainTab.HOME)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    // Handle system back navigation across tabs and detail/player stack
    BackHandler(
        enabled = navStack.size > 1 ||
            (currentDestination is ScreenDestination.Main && currentDestination.tab != MainTab.HOME)
    ) {
        viewModel.navigateBack()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        AmbientNebulaBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isExpandedScreen = maxWidth >= 600.dp

                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = {
                        if (userSettings.reducedMotion) {
                            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                        } else {
                            val init = initialState
                            val target = targetState
                            val isPushingDetailOrWatch = target is ScreenDestination.Details ||
                                target is ScreenDestination.Watch ||
                                target is ScreenDestination.WelcomeOnboarding
                            val isPoppingDetailOrWatch = (init is ScreenDestination.Details ||
                                init is ScreenDestination.Watch ||
                                init is ScreenDestination.WelcomeOnboarding) &&
                                target is ScreenDestination.Main

                            when {
                                isPushingDetailOrWatch -> {
                                    (slideInHorizontally(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        initialOffsetX = { fullWidth -> fullWidth / 4 }
                                    ) + fadeIn(tween(220)) + scaleIn(
                                        initialScale = 0.95f,
                                        animationSpec = tween(240)
                                    )) togetherWith (fadeOut(tween(180)) + scaleOut(targetScale = 0.96f))
                                }

                                isPoppingDetailOrWatch -> {
                                    (fadeIn(tween(220)) + scaleIn(initialScale = 0.96f)) togetherWith
                                        (slideOutHorizontally(
                                            animationSpec = tween(220),
                                            targetOffsetX = { fullWidth -> fullWidth / 4 }
                                        ) + fadeOut(tween(180)) + scaleOut(targetScale = 0.95f))
                                }

                                init is ScreenDestination.Main && target is ScreenDestination.Main -> {
                                    val forward = target.tab.ordinal >= init.tab.ordinal
                                    val dir = if (forward) 1 else -1
                                    (slideInHorizontally(
                                        animationSpec = tween(230),
                                        initialOffsetX = { w -> dir * (w / 7) }
                                    ) + fadeIn(tween(220))) togetherWith (slideOutHorizontally(
                                        animationSpec = tween(200),
                                        targetOffsetX = { w -> -dir * (w / 7) }
                                    ) + fadeOut(tween(170)))
                                }

                                else -> {
                                    fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                                }
                            }
                        }
                    },
                    label = "rootNavigation"
                ) { destination ->
                    when (destination) {
                        is ScreenDestination.WelcomeOnboarding -> {
                            WelcomeOnboardingScreen(
                                onGetStartedClick = { viewModel.navigateBack() }
                            )
                        }

                        is ScreenDestination.Details -> {
                            val anime = uiState.activeAnimeDetail
                                ?: uiState.catalog.find { it.id == destination.animeId }
                                ?: uiState.catalog.firstOrNull()
                            if (anime != null) {
                                val isInWatchlist = watchlist.any { it.animeId == anime.id }
                                val isFav = favorites.any { it.animeId == anime.id }
                                AnimeDetailsScreen(
                                    anime = anime,
                                    allCatalog = uiState.catalog,
                                    isInWatchlist = isInWatchlist,
                                    isFavorite = isFav,
                                    watchedEpisodes = watchedEpisodes,
                                    onBackClick = { viewModel.navigateBack() },
                                    onPlayEpisodeClick = { epNum ->
                                        viewModel.openVideoPlayer(anime.id, epNum)
                                    },
                                    onToggleWatchlist = { viewModel.toggleWatchlist(anime) },
                                    onToggleFavorite = { viewModel.toggleFavorite(anime) },
                                    onGenreClick = { genreName ->
                                        viewModel.jumpToGenreDiscover(genreName)
                                    },
                                    onRelatedAnimeClick = { relatedId ->
                                        viewModel.openAnimeDetails(relatedId)
                                    }
                                )
                            }
                        }

                        is ScreenDestination.Watch -> {
                            val anime = uiState.activeAnimeDetail
                                ?: uiState.catalog.find { it.id == destination.animeId }
                                ?: uiState.catalog.firstOrNull()
                            if (anime != null) {
                                val libEntry = allLibraryEntries.find { it.animeId == anime.id }
                                VideoPlayerScreen(
                                    anime = anime,
                                    episodeNumber = destination.episodeNumber,
                                    libraryEntry = libEntry,
                                    defaultQuality = userSettings.defaultQuality,
                                    defaultSubtitle = userSettings.defaultSubtitle,
                                    onBackClick = { viewModel.navigateBack() },
                                    onSelectEpisode = { newEp ->
                                        viewModel.navigateBack()
                                        viewModel.openVideoPlayer(anime.id, newEp)
                                    },
                                    onSaveProgress = { epNum, posMs, durMs ->
                                        viewModel.savePlaybackProgress(anime, epNum, posMs, durMs)
                                    }
                                )
                            }
                        }

                        is ScreenDestination.Main -> {
                            val activeTab = destination.tab
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (isExpandedScreen) {
                                    AdaptiveSideNavigationRail(
                                        selectedTab = activeTab,
                                        onSelectTab = { viewModel.selectTab(it) }
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .windowInsetsPadding(WindowInsets.statusBars)
                                ) {
                                    when (activeTab) {
                                        MainTab.HOME -> {
                                            HomeScreen(
                                                catalog = uiState.catalog,
                                                continueWatching = continueWatching,
                                                watchlist = watchlist,
                                                watchedEpisodes = watchedEpisodes,
                                                reducedMotion = userSettings.reducedMotion,
                                                onAnimeClick = { viewModel.openAnimeDetails(it) },
                                                onPlayClick = { id, ep -> viewModel.openVideoPlayer(id, ep) },
                                                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                                                onNavigateTab = { viewModel.selectTab(it) },
                                                onRandomAnimeClick = { viewModel.openRandomAnime() },
                                                onFilterHiddenGemsClick = {
                                                    viewModel.setOnlyHiddenGems(true)
                                                    viewModel.selectTab(MainTab.DISCOVER)
                                                }
                                            )
                                        }

                                        MainTab.DISCOVER -> {
                                            DiscoverScreen(
                                                allGenres = viewModel.allGenres,
                                                filterState = uiState.filterState,
                                                filteredResults = uiState.filteredResults,
                                                watchlist = watchlist,
                                                isLoadingMore = uiState.isLoadingMore,
                                                filterSearchState = filterSearchState,
                                                onRetry = { viewModel.retryFiltering() },
                                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                                onToggleIncludeGenre = { viewModel.toggleIncludeGenre(it) },
                                                onToggleExcludeGenre = { viewModel.toggleExcludeGenre(it) },
                                                onSortChange = { viewModel.setSortOption(it) },
                                                onYearChange = { viewModel.setYearFilter(it) },
                                                onSeasonChange = { viewModel.setSeasonFilter(it) },
                                                onStatusChange = { viewModel.setStatusFilter(it) },
                                                onFormatChange = { viewModel.setFormatFilter(it) },
                                                onOnlyFreeChange = { viewModel.setOnlyFreeVerified(it) },
                                                onOnlyHiddenGemsChange = { viewModel.setOnlyHiddenGems(it) },
                                                onClearFilters = { viewModel.clearAllFilters() },
                                                onLoadMore = { viewModel.loadMoreResults() },
                                                onAnimeClick = { viewModel.openAnimeDetails(it) },
                                                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                                                onRandomAnimeClick = { viewModel.openRandomAnime() }
                                            )
                                        }

                                        MainTab.SEARCH -> {
                                            SearchScreen(
                                                query = uiState.filterState.query,
                                                suggestions = uiState.searchSuggestions,
                                                recentSearches = recentSearches,
                                                searchResults = uiState.filteredResults,
                                                filterSearchState = filterSearchState,
                                                onRetry = { viewModel.retryFiltering() },
                                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                                onSuggestionClick = { viewModel.updateSearchQuery(it) },
                                                onRemoveRecentSearch = { viewModel.removeRecentSearch(it) },
                                                onClearRecentSearches = { viewModel.clearRecentSearches() },
                                                onAnimeClick = { viewModel.openAnimeDetails(it) },
                                                onPlayClick = { viewModel.openVideoPlayer(it, 1) },
                                                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                                            )
                                        }

                                        MainTab.LIBRARY -> {
                                            LibraryScreen(
                                                watchlist = watchlist,
                                                continueWatching = continueWatching,
                                                favorites = favorites,
                                                allLibraryEntries = allLibraryEntries,
                                                watchedEpisodes = watchedEpisodes,
                                                customLists = customLists,
                                                catalog = uiState.catalog,
                                                onAnimeClick = { viewModel.openAnimeDetails(it) },
                                                onPlayClick = { id, ep -> viewModel.openVideoPlayer(id, ep) },
                                                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                                onAssignCustomList = { anime, listName ->
                                                    viewModel.assignToCustomList(anime, listName)
                                                },
                                                onCreateCustomList = { name, desc ->
                                                    viewModel.createCustomList(name, desc)
                                                },
                                                onDeleteCustomList = { viewModel.deleteCustomList(it) },
                                                onRemoveEntry = { viewModel.removeFromLibrary(it) },
                                                onClearHistory = { viewModel.clearWatchHistory() }
                                            )
                                        }

                                        MainTab.PROFILE -> {
                                            val completedCount = allLibraryEntries.count { it.isCompleted }
                                            ProfileScreen(
                                                userSettings = userSettings,
                                                watchlistCount = watchlist.size,
                                                completedCount = completedCount,
                                                favoritesCount = favorites.size,
                                                cachedAnimeCount = cachedAnime.size,
                                                onUpdateProfile = { name, bio ->
                                                    viewModel.updateProfile(name, bio)
                                                },
                                                onCycleAppearance = { viewModel.setAppearanceMode(it) },
                                                onToggleReducedMotion = { viewModel.setReducedMotion(it) },
                                                onDefaultQualityChange = { viewModel.setDefaultQuality(it) },
                                                onDefaultSubtitleChange = { viewModel.setDefaultSubtitle(it) },
                                                onToggleAutoPlay = { viewModel.setAutoPlayNext(it) },
                                                onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                                                onOpenWelcomeShowcase = { viewModel.openWelcomeScreen() }
                                            )
                                        }
                                    }

                                    // Floating Glass Bottom Bar on compact/mobile screens
                                    if (!isExpandedScreen) {
                                        FloatingGlassBottomBar(
                                            selectedTab = activeTab,
                                            onSelectTab = { viewModel.selectTab(it) },
                                            modifier = Modifier.align(Alignment.BottomCenter)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
