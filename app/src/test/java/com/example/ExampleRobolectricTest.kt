package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AnimeDatabase
import com.example.data.local.FavoriteAnimeEntity
import com.example.data.local.RecentlyWatchedEntity
import com.example.data.local.WatchlistEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun animeFlix_launchesHomeAndNavigatesAcrossTabsAndDetails() {
        // Verify Home Screen loads with Featured Hero
        composeTestRule.onNodeWithTag("home_screen_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("featured_hero_card").assertIsDisplayed()

        // Navigate to Discover tab and verify filter panel toggle
        composeTestRule.onNodeWithTag("nav_tab_discover").performClick()
        composeTestRule.onNodeWithTag("discover_screen_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discover_filters_toggle").performClick()
        composeTestRule.onNodeWithTag("advanced_filter_panel").assertIsDisplayed()

        // Navigate to Search tab
        composeTestRule.onNodeWithTag("nav_tab_search").performClick()
        composeTestRule.onNodeWithTag("search_input_field").assertIsDisplayed()

        // Navigate to Library tab
        composeTestRule.onNodeWithTag("nav_tab_library").performClick()
        composeTestRule.onNodeWithTag("library_screen_list").assertIsDisplayed()

        // Navigate to Profile tab
        composeTestRule.onNodeWithTag("nav_tab_profile").performClick()
        composeTestRule.onNodeWithTag("profile_settings_row").assertIsDisplayed()
    }

    @Test
    fun roomDatabase_storesWatchlistFavoriteAnimeAndRecentlyWatchedLocally() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AnimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.animeDao()

        // 1. Test Watchlist table
        dao.insertWatchlistItem(
            WatchlistEntity(
                animeId = 40748,
                title = "Jujutsu Kaisen",
                posterUrl = "https://example.com/jjk.jpg",
                backdropUrl = "https://example.com/jjk_bg.jpg",
                episodesCount = 24,
                score = 8.63,
                genresCsv = "Action,Supernatural",
                format = "TV",
                year = 2020
            )
        )
        val watchlistItems = dao.observeWatchlistTable().first()
        assertEquals(1, watchlistItems.size)
        assertEquals("Jujutsu Kaisen", watchlistItems.first().title)

        // 2. Test FavoriteAnime table
        dao.insertFavoriteAnimeItem(
            FavoriteAnimeEntity(
                animeId = 52991,
                title = "Frieren",
                posterUrl = "https://example.com/frieren.jpg",
                backdropUrl = "https://example.com/frieren_bg.jpg",
                episodesCount = 28,
                score = 9.32,
                genresCsv = "Adventure,Fantasy",
                format = "TV",
                year = 2023
            )
        )
        val favoriteItems = dao.observeFavoriteAnimeTable().first()
        assertEquals(1, favoriteItems.size)
        assertEquals("Frieren", favoriteItems.first().title)

        // 3. Test RecentlyWatched table
        dao.upsertRecentlyWatchedItem(
            RecentlyWatchedEntity(
                animeId = 1735,
                title = "Naruto Shippuden",
                posterUrl = "https://example.com/naruto.jpg",
                backdropUrl = "https://example.com/naruto_bg.jpg",
                episodesCount = 500,
                score = 8.27,
                genresCsv = "Action,Adventure",
                format = "TV",
                year = 2007,
                episodeNumber = 133,
                episodeTitle = "The Tale of Jiraiya the Gallant",
                playbackPositionMs = 720_000L,
                durationMs = 1_440_000L
            )
        )
        val recentlyWatched = dao.observeRecentlyWatchedTable().first()
        assertEquals(1, recentlyWatched.size)
        assertEquals(133, recentlyWatched.first().episodeNumber)
        assertTrue(recentlyWatched.first().progressFraction >= 0.49f)

        db.close()
    }

    @Test
    fun animeFilterStateMachine_managesGenreAndQueryTransitions() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AnimeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = com.example.data.repository.AnimeRepository(db.animeDao())
        repo.ensureInitialSeedData()

        val stateMachine = com.example.ui.AnimeFilterStateMachine(repo, this)

        // 1. Initial State
        val initialState = stateMachine.state.value
        assertTrue(initialState is com.example.ui.FilterSearchState.Success || initialState is com.example.ui.FilterSearchState.Idle)
        assertTrue(initialState.results.isNotEmpty())

        // 2. Query Intent
        stateMachine.dispatch(com.example.ui.FilterSearchIntent.SetQuery("Demon"))
        val filteringState = stateMachine.state.value
        assertTrue(filteringState is com.example.ui.FilterSearchState.Filtering)
        assertEquals("Demon", filteringState.filterState.query)

        // 3. Toggle Include Genre
        stateMachine.dispatch(com.example.ui.FilterSearchIntent.ToggleIncludeGenre("Action"))
        val genreState = stateMachine.state.value
        assertTrue(genreState.filterState.includedGenres.contains("Action"))

        // 4. Toggle Exclude Genre
        stateMachine.dispatch(com.example.ui.FilterSearchIntent.ToggleExcludeGenre("Horror"))
        val excludeState = stateMachine.state.value
        assertTrue(excludeState.filterState.excludedGenres.contains("Horror"))

        // 5. Clear All Filters
        stateMachine.dispatch(com.example.ui.FilterSearchIntent.ClearAllFilters)
        val clearedState = stateMachine.state.value
        assertTrue(clearedState.filterState.includedGenres.isEmpty())
        assertTrue(clearedState.filterState.excludedGenres.isEmpty())

        db.close()
    }
}
