package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Dedicated Room table 'Watchlist' for storing user's bookmarked/planned anime locally.
 */
@Entity(tableName = "Watchlist")
data class WatchlistEntity(
    @PrimaryKey val animeId: Int,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val episodesCount: Int,
    val score: Double,
    val genresCsv: String,
    val format: String,
    val year: Int,
    val customListName: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Dedicated Room table 'FavoriteAnime' for storing user's favorite anime locally.
 */
@Entity(tableName = "FavoriteAnime")
data class FavoriteAnimeEntity(
    @PrimaryKey val animeId: Int,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val episodesCount: Int,
    val score: Double,
    val genresCsv: String,
    val format: String,
    val year: Int,
    val favoritedAt: Long = System.currentTimeMillis()
)

/**
 * Dedicated Room table 'RecentlyWatched' for storing user's episode playback progress locally
 * without needing a backend server.
 */
@Entity(tableName = "RecentlyWatched")
data class RecentlyWatchedEntity(
    @PrimaryKey val animeId: Int,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val episodesCount: Int,
    val score: Double,
    val genresCsv: String,
    val format: String,
    val year: Int,
    val episodeNumber: Int,
    val episodeTitle: String = "",
    val playbackPositionMs: Long,
    val durationMs: Long,
    val isCompleted: Boolean = false,
    val lastWatchedAt: Long = System.currentTimeMillis()
) {
    val progressFraction: Float
        get() = (playbackPositionMs.toFloat() / durationMs.coerceAtLeast(1L).toFloat())
            .coerceIn(0.05f, 1f)
}

@Entity(tableName = "library_entries")
data class LibraryEntryEntity(
    @PrimaryKey val animeId: Int,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val episodesCount: Int,
    val score: Double,
    val genresCsv: String,
    val format: String,
    val year: Int,
    val inWatchlist: Boolean = false,
    val isFavorite: Boolean = false,
    val isCompleted: Boolean = false,
    val customListName: String? = null,
    val lastWatchedEpisode: Int = 0,
    val lastPlaybackPositionMs: Long = 0L,
    val lastDurationMs: Long = 1440000L,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watched_episodes")
data class WatchedEpisodeEntity(
    @PrimaryKey val id: String, // "${animeId}_${episodeNumber}"
    val animeId: Int,
    val episodeNumber: Int,
    val positionMs: Long,
    val durationMs: Long,
    val isCompleted: Boolean,
    val watchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_lists")
data class CustomListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_anime")
data class CachedAnimeEntity(
    @PrimaryKey val animeId: Int,
    val title: String,
    val titleEnglish: String,
    val titleJapanese: String,
    val synopsis: String,
    val posterUrl: String,
    val backdropUrl: String,
    val score: Double,
    val scoredBy: Int,
    val episodesCount: Int,
    val format: String,
    val status: String,
    val season: String,
    val year: Int,
    val rating: String,
    val genresCsv: String,
    val themesCsv: String,
    val demographicsCsv: String,
    val studiosCsv: String,
    val trailerYoutubeId: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

@Dao
interface AnimeDao {
    // --- Dedicated 'Watchlist' Table Queries ---
    @Query("SELECT * FROM Watchlist ORDER BY addedAt DESC")
    fun observeWatchlistTable(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM Watchlist WHERE animeId = :animeId LIMIT 1")
    suspend fun getWatchlistItem(animeId: Int): WatchlistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistEntity)

    @Query("DELETE FROM Watchlist WHERE animeId = :animeId")
    suspend fun deleteWatchlistItem(animeId: Int)

    // --- Dedicated 'FavoriteAnime' Table Queries ---
    @Query("SELECT * FROM FavoriteAnime ORDER BY favoritedAt DESC")
    fun observeFavoriteAnimeTable(): Flow<List<FavoriteAnimeEntity>>

    @Query("SELECT * FROM FavoriteAnime WHERE animeId = :animeId LIMIT 1")
    suspend fun getFavoriteAnimeItem(animeId: Int): FavoriteAnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteAnimeItem(item: FavoriteAnimeEntity)

    @Query("DELETE FROM FavoriteAnime WHERE animeId = :animeId")
    suspend fun deleteFavoriteAnimeItem(animeId: Int)

    // --- Dedicated 'RecentlyWatched' Table Queries ---
    @Query("SELECT * FROM RecentlyWatched ORDER BY lastWatchedAt DESC")
    fun observeRecentlyWatchedTable(): Flow<List<RecentlyWatchedEntity>>

    @Query("SELECT * FROM RecentlyWatched WHERE animeId = :animeId LIMIT 1")
    suspend fun getRecentlyWatchedItem(animeId: Int): RecentlyWatchedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentlyWatchedItem(item: RecentlyWatchedEntity)

    @Query("DELETE FROM RecentlyWatched WHERE animeId = :animeId")
    suspend fun deleteRecentlyWatchedItem(animeId: Int)

    @Query("DELETE FROM RecentlyWatched")
    suspend fun clearRecentlyWatchedTable()

    // --- Unified Library Entries Queries ---
    @Query("SELECT * FROM library_entries ORDER BY updatedAt DESC")
    fun observeAllLibraryEntries(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library_entries WHERE inWatchlist = 1 ORDER BY updatedAt DESC")
    fun observeWatchlist(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library_entries WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun observeFavorites(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library_entries WHERE lastWatchedEpisode > 0 ORDER BY updatedAt DESC")
    fun observeContinueWatching(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library_entries WHERE animeId = :animeId LIMIT 1")
    suspend fun getLibraryEntry(animeId: Int): LibraryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLibraryEntry(entry: LibraryEntryEntity)

    @Query("DELETE FROM library_entries WHERE animeId = :animeId")
    suspend fun deleteLibraryEntry(animeId: Int)

    @Query("SELECT * FROM watched_episodes ORDER BY watchedAt DESC")
    fun observeAllWatchedEpisodes(): Flow<List<WatchedEpisodeEntity>>

    @Query("SELECT * FROM watched_episodes WHERE animeId = :animeId ORDER BY episodeNumber ASC")
    fun observeWatchedEpisodesForAnime(animeId: Int): Flow<List<WatchedEpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchedEpisode(episode: WatchedEpisodeEntity)

    @Query("SELECT * FROM custom_lists ORDER BY createdAt DESC")
    fun observeCustomLists(): Flow<List<CustomListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomList(customList: CustomListEntity)

    @Query("DELETE FROM custom_lists WHERE name = :name")
    suspend fun deleteCustomListByName(name: String)

    @Query("SELECT * FROM recent_searches ORDER BY searchedAt DESC LIMIT 12")
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteRecentSearch(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()

    @Query("SELECT * FROM cached_anime ORDER BY score DESC")
    fun observeCachedAnime(): Flow<List<CachedAnimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedAnimeList(items: List<CachedAnimeEntity>)

    @Query("DELETE FROM watched_episodes")
    suspend fun clearWatchHistory()
}

@Database(
    entities = [
        WatchlistEntity::class,
        FavoriteAnimeEntity::class,
        RecentlyWatchedEntity::class,
        LibraryEntryEntity::class,
        WatchedEpisodeEntity::class,
        CustomListEntity::class,
        RecentSearchEntity::class,
        CachedAnimeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        fun getInstance(context: Context): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "animeflix_db"
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
        }
    }
}
