package com.example.data.repository

import com.example.data.local.AnimeDao
import com.example.data.local.CachedAnimeEntity
import com.example.data.local.CustomListEntity
import com.example.data.local.FavoriteAnimeEntity
import com.example.data.local.LibraryEntryEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.RecentlyWatchedEntity
import com.example.data.local.WatchedEpisodeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeEpisode
import com.example.data.model.AnimeGenre
import com.example.data.model.AnimeItem
import com.example.data.model.FilterState
import com.example.data.model.OfficialStreamSource
import com.example.data.model.SortOption
import com.example.data.remote.JikanAnimeDto
import com.example.data.remote.JikanApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AnimeRepository(
    private val animeDao: AnimeDao,
    private val apiService: JikanApiService = createDefaultApiService()
) {
    private val inMemoryCatalog = LinkedHashMap<Int, AnimeItem>().apply {
        CatalogSeedData.curatedAnimeCatalog.forEach { put(it.id, it) }
    }

    val allGenres: List<AnimeGenre> = CatalogSeedData.allGenres

    // Dedicated table flows for Watchlist, FavoriteAnime, and RecentlyWatched
    val watchlistTableFlow: Flow<List<WatchlistEntity>> = animeDao.observeWatchlistTable()
    val favoriteAnimeTableFlow: Flow<List<FavoriteAnimeEntity>> = animeDao.observeFavoriteAnimeTable()
    val recentlyWatchedTableFlow: Flow<List<RecentlyWatchedEntity>> = animeDao.observeRecentlyWatchedTable()

    // Unified library flows
    val watchlistFlow: Flow<List<LibraryEntryEntity>> = animeDao.observeWatchlist()
    val favoritesFlow: Flow<List<LibraryEntryEntity>> = animeDao.observeFavorites()
    val continueWatchingFlow: Flow<List<LibraryEntryEntity>> = animeDao.observeContinueWatching()
    val allLibraryEntriesFlow: Flow<List<LibraryEntryEntity>> = animeDao.observeAllLibraryEntries()
    val allWatchedEpisodesFlow: Flow<List<WatchedEpisodeEntity>> = animeDao.observeAllWatchedEpisodes()
    val customListsFlow: Flow<List<CustomListEntity>> = animeDao.observeCustomLists()
    val recentSearchesFlow: Flow<List<RecentSearchEntity>> = animeDao.observeRecentSearches()
    val cachedAnimeFlow: Flow<List<CachedAnimeEntity>> = animeDao.observeCachedAnime()

    suspend fun ensureInitialSeedData() = withContext(Dispatchers.IO) {
        val existingEntries = animeDao.observeAllLibraryEntries().first()
        if (existingEntries.isEmpty()) {
            // Seed Continue Watching / RecentlyWatched (Naruto Shippuden Ep 133 & Your Name)
            val naruto = CatalogSeedData.curatedAnimeCatalog.find { it.id == 1735 }
            if (naruto != null) {
                animeDao.upsertLibraryEntry(
                    naruto.toLibraryEntry(
                        inWatchlist = true,
                        lastWatchedEpisode = 133,
                        lastPlaybackPositionMs = 920_000L,
                        lastDurationMs = 1_440_000L
                    )
                )
                animeDao.insertWatchlistItem(naruto.toWatchlistEntity())
                animeDao.upsertRecentlyWatchedItem(
                    naruto.toRecentlyWatchedEntity(
                        episodeNumber = 133,
                        episodeTitle = "The Tale of Jiraiya the Gallant",
                        playbackPositionMs = 920_000L,
                        durationMs = 1_440_000L
                    )
                )
            }

            // Seed initial Watchlist & FavoriteAnime tables matching Screen 7
            val initialWatchlistIds = listOf(40748, 52991, 44511, 52299, 50265)
            initialWatchlistIds.forEachIndexed { index, id ->
                val item = CatalogSeedData.curatedAnimeCatalog.find { it.id == id }
                if (item != null) {
                    val ts = System.currentTimeMillis() - (index * 60_000L)
                    val isFav = index < 2
                    animeDao.upsertLibraryEntry(
                        item.toLibraryEntry(
                            inWatchlist = true,
                            isFavorite = isFav,
                            updatedAt = ts
                        )
                    )
                    animeDao.insertWatchlistItem(item.toWatchlistEntity(addedAt = ts))
                    if (isFav) {
                        animeDao.insertFavoriteAnimeItem(item.toFavoriteAnimeEntity(favoritedAt = ts))
                    }
                }
            }
            animeDao.insertCustomList(CustomListEntity(name = "Weekend Binge", description = "High-octane action and thrillers"))
            animeDao.insertCustomList(CustomListEntity(name = "Cozy & Wholesome", description = "Relaxing slice of life and comedy"))
        }

        val existingSearches = animeDao.observeRecentSearches().first()
        if (existingSearches.isEmpty()) {
            listOf("solo leveling", "one piece", "spy x family", "naruto").forEachIndexed { idx, q ->
                animeDao.insertRecentSearch(RecentSearchEntity(query = q, searchedAt = System.currentTimeMillis() + idx))
            }
        }

        val existingCache = animeDao.observeCachedAnime().first()
        if (existingCache.isEmpty()) {
            animeDao.upsertCachedAnimeList(CatalogSeedData.curatedAnimeCatalog.map { it.toCachedEntity() })
        }
    }

    suspend fun refreshCatalogFromRemote(): Result<List<AnimeItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val topResponse = apiService.getTopAnime(limit = 20)
            val seasonalResponse = apiService.getSeasonNow(limit = 15)
            val remoteItems = (topResponse.data + seasonalResponse.data)
                .distinctBy { it.malId }
                .map { dto -> mapDtoToAnimeItem(dto) }

            remoteItems.forEach { item ->
                val existing = inMemoryCatalog[item.id]
                inMemoryCatalog[item.id] = if (existing != null) {
                    existing.copy(
                        score = if (item.score > 0) item.score else existing.score,
                        scoredBy = if (item.scoredBy > 0) item.scoredBy else existing.scoredBy,
                        rank = if (item.rank > 0) item.rank else existing.rank
                    )
                } else {
                    item
                }
            }
            animeDao.upsertCachedAnimeList(inMemoryCatalog.values.map { it.toCachedEntity() })
            inMemoryCatalog.values.toList()
        }.recoverCatching {
            val cached = animeDao.observeCachedAnime().first().map { it.toDomainModel() }
            cached.forEach { if (!inMemoryCatalog.containsKey(it.id)) inMemoryCatalog[it.id] = it }
            inMemoryCatalog.values.toList()
        }
    }

    fun getAllCatalogAnime(): List<AnimeItem> = inMemoryCatalog.values.toList()

    fun getAnimeById(animeId: Int): AnimeItem? = inMemoryCatalog[animeId]

    suspend fun fetchAnimeDetailsWithExtras(animeId: Int): AnimeItem = withContext(Dispatchers.IO) {
        val current = inMemoryCatalog[animeId]
        if (current != null && current.characters.isNotEmpty() && current.episodes.isNotEmpty()) {
            return@withContext current
        }

        val enriched = runCatching {
            val baseItem = current ?: apiService.getAnimeFullById(animeId).data?.let { mapDtoToAnimeItem(it) }
            val charactersResp = runCatching { apiService.getAnimeCharacters(animeId) }.getOrNull()
            val episodesResp = runCatching { apiService.getAnimeEpisodes(animeId, page = 1) }.getOrNull()

            val mappedCharacters = charactersResp?.data?.take(10)?.mapNotNull { entry ->
                val charInfo = entry.character ?: return@mapNotNull null
                val va = entry.voiceActors?.firstOrNull { it.language == "Japanese" } ?: entry.voiceActors?.firstOrNull()
                AnimeCharacter(
                    id = charInfo.malId,
                    name = charInfo.name,
                    role = entry.role ?: "Supporting",
                    imageUrl = charInfo.images?.jpg?.imageUrl ?: "",
                    voiceActorName = va?.person?.name ?: "",
                    voiceActorLanguage = va?.language ?: "Japanese"
                )
            }.orEmpty()

            val mappedEpisodes = episodesResp?.data?.mapIndexed { idx, epDto ->
                val epNum = idx + 1
                val isFree = epNum <= 3
                AnimeEpisode(
                    number = epNum,
                    seasonNumber = 1,
                    title = epDto.title ?: "Episode $epNum",
                    titleJapanese = epDto.titleJapanese ?: "第${epNum}話",
                    durationMinutes = 24,
                    airedDate = epDto.aired?.take(10) ?: "",
                    synopsis = if (isFree) {
                        "Official authorized free episode stream for ${baseItem?.title ?: "Anime"}."
                    } else {
                        "Episode $epNum requires an official regional subscription."
                    },
                    thumbnailUrl = baseItem?.posterUrl ?: "",
                    isAvailableFree = isFree,
                    directStreamUrl = if (isFree) CatalogSeedData.OPEN_STREAM_SPRITE_FRIGHT else null,
                    officialEmbedUrl = baseItem?.trailerEmbedUrl,
                    sourceLabel = if (isFree) "Official Free Stream • Subtitles" else "No Free Stream"
                )
            }.orEmpty()

            baseItem?.copy(
                characters = mappedCharacters.ifEmpty { baseItem.characters },
                episodes = mappedEpisodes.ifEmpty { baseItem.episodes }
            )
        }.getOrNull() ?: current ?: CatalogSeedData.curatedAnimeCatalog.first()

        inMemoryCatalog[enriched.id] = enriched
        enriched
    }

    suspend fun searchAndFilterAnime(
        filterState: FilterState,
        page: Int = 1
    ): Result<List<AnimeItem>> = withContext(Dispatchers.IO) {
        val localFiltered = filterCatalogLocally(inMemoryCatalog.values.toList(), filterState)

        if (filterState.query.length >= 3 || page > 1) {
            runCatching {
                val includedIds = filterState.includedGenres.mapNotNull { name ->
                    allGenres.find { it.name.equals(name, ignoreCase = true) && it.malId < 100 }?.malId
                }.joinToString(",").ifEmpty { null }

                val excludedIds = filterState.excludedGenres.mapNotNull { name ->
                    allGenres.find { it.name.equals(name, ignoreCase = true) && it.malId < 100 }?.malId
                }.joinToString(",").ifEmpty { null }

                val response = apiService.searchAnime(
                    query = filterState.query.trim().ifEmpty { null },
                    genres = includedIds,
                    genresExclude = excludedIds,
                    orderBy = filterState.sortBy.jikanOrderBy,
                    sort = if (filterState.sortBy == SortOption.TITLE) "asc" else "desc",
                    type = filterState.selectedFormat?.lowercase(),
                    page = page,
                    limit = 16
                )
                val remoteMapped = response.data.map { mapDtoToAnimeItem(it) }
                remoteMapped.forEach { item ->
                    if (!inMemoryCatalog.containsKey(item.id)) {
                        inMemoryCatalog[item.id] = item
                    }
                }
                val combined = (localFiltered + filterCatalogLocally(remoteMapped, filterState))
                    .distinctBy { it.id }
                sortAnimeList(combined, filterState.sortBy)
            }.recover {
                localFiltered
            }
        } else {
            Result.success(localFiltered)
        }
    }

    fun filterCatalogLocally(
        items: List<AnimeItem>,
        filter: FilterState
    ): List<AnimeItem> {
        val q = filter.query.trim().lowercase()
        val filtered = items.filter { anime ->
            val matchesQuery = if (q.isEmpty()) {
                true
            } else {
                anime.title.lowercase().contains(q) ||
                    anime.titleEnglish.lowercase().contains(q) ||
                    anime.titleJapanese.lowercase().contains(q) ||
                    anime.alternativeTitles.any { it.lowercase().contains(q) } ||
                    anime.synopsis.lowercase().contains(q) ||
                    anime.allTags.any { it.lowercase().contains(q) }
            }

            val allAnimeTagsLower = anime.allTags.map { it.lowercase() }.toSet()
            val matchesIncluded = filter.includedGenres.all { inc ->
                allAnimeTagsLower.contains(inc.lowercase())
            }
            val matchesExcluded = filter.excludedGenres.none { exc ->
                allAnimeTagsLower.contains(exc.lowercase())
            }
            val matchesYear = filter.selectedYear == null || anime.year == filter.selectedYear
            val matchesSeason = filter.selectedSeason == null ||
                anime.season.equals(filter.selectedSeason, ignoreCase = true)
            val matchesStatus = filter.selectedStatus == null ||
                anime.status.equals(filter.selectedStatus, ignoreCase = true)
            val matchesFormat = filter.selectedFormat == null ||
                anime.format.equals(filter.selectedFormat, ignoreCase = true)
            val matchesFree = !filter.onlyFreeVerified || anime.hasVerifiedFreeStream
            val matchesHiddenGem = !filter.onlyHiddenGems || anime.isHiddenGem || anime.popularity > 120

            matchesQuery && matchesIncluded && matchesExcluded &&
                matchesYear && matchesSeason && matchesStatus &&
                matchesFormat && matchesFree && matchesHiddenGem
        }
        return sortAnimeList(filtered, filter.sortBy)
    }

    private fun sortAnimeList(items: List<AnimeItem>, sortBy: SortOption): List<AnimeItem> {
        return when (sortBy) {
            SortOption.POPULARITY -> items.sortedByDescending { it.scoredBy }
            SortOption.RATING -> items.sortedByDescending { it.score }
            SortOption.RELEASE_DATE -> items.sortedByDescending { it.year }
            SortOption.TITLE -> items.sortedBy { it.title.lowercase() }
        }
    }

    suspend fun toggleWatchlist(anime: AnimeItem): Boolean = withContext(Dispatchers.IO) {
        val existingWatchlist = animeDao.getWatchlistItem(anime.id)
        val existingEntry = animeDao.getLibraryEntry(anime.id)
        val newState = existingWatchlist == null && !(existingEntry?.inWatchlist ?: false)

        if (newState) {
            animeDao.insertWatchlistItem(anime.toWatchlistEntity())
        } else {
            animeDao.deleteWatchlistItem(anime.id)
        }

        val updated = (existingEntry ?: anime.toLibraryEntry()).copy(
            inWatchlist = newState,
            updatedAt = System.currentTimeMillis()
        )
        animeDao.upsertLibraryEntry(updated)
        newState
    }

    suspend fun toggleFavorite(anime: AnimeItem): Boolean = withContext(Dispatchers.IO) {
        val existingFavorite = animeDao.getFavoriteAnimeItem(anime.id)
        val existingEntry = animeDao.getLibraryEntry(anime.id)
        val newState = existingFavorite == null && !(existingEntry?.isFavorite ?: false)

        if (newState) {
            animeDao.insertFavoriteAnimeItem(anime.toFavoriteAnimeEntity())
        } else {
            animeDao.deleteFavoriteAnimeItem(anime.id)
        }

        val updated = (existingEntry ?: anime.toLibraryEntry()).copy(
            isFavorite = newState,
            updatedAt = System.currentTimeMillis()
        )
        animeDao.upsertLibraryEntry(updated)
        newState
    }

    suspend fun assignToCustomList(anime: AnimeItem, listName: String?) = withContext(Dispatchers.IO) {
        val existing = animeDao.getLibraryEntry(anime.id)
        val updated = (existing ?: anime.toLibraryEntry()).copy(
            customListName = listName,
            inWatchlist = true,
            updatedAt = System.currentTimeMillis()
        )
        animeDao.upsertLibraryEntry(updated)
        animeDao.insertWatchlistItem(anime.toWatchlistEntity(customListName = listName))
    }

    suspend fun createCustomList(name: String, description: String) = withContext(Dispatchers.IO) {
        if (name.isNotBlank()) {
            animeDao.insertCustomList(CustomListEntity(name = name.trim(), description = description.trim()))
        }
    }

    suspend fun deleteCustomList(name: String) = withContext(Dispatchers.IO) {
        animeDao.deleteCustomListByName(name)
    }

    suspend fun savePlaybackProgress(
        anime: AnimeItem,
        episodeNumber: Int,
        positionMs: Long,
        durationMs: Long
    ) = withContext(Dispatchers.IO) {
        val safeDuration = durationMs.coerceAtLeast(60_000L)
        val isEpisodeCompleted = positionMs >= (safeDuration * 0.9f).toLong()
        val existing = animeDao.getLibraryEntry(anime.id)
        val isSeriesCompleted = episodeNumber >= anime.episodesCount && isEpisodeCompleted
        val epTitle = anime.episodes.find { it.number == episodeNumber }?.title ?: "Episode $episodeNumber"

        // 1. Update RecentlyWatched table
        animeDao.upsertRecentlyWatchedItem(
            anime.toRecentlyWatchedEntity(
                episodeNumber = episodeNumber,
                episodeTitle = epTitle,
                playbackPositionMs = positionMs,
                durationMs = safeDuration,
                isCompleted = isEpisodeCompleted
            )
        )

        // 2. Update unified library_entries
        val updatedEntry = (existing ?: anime.toLibraryEntry()).copy(
            lastWatchedEpisode = episodeNumber,
            lastPlaybackPositionMs = positionMs,
            lastDurationMs = safeDuration,
            isCompleted = isSeriesCompleted || (existing?.isCompleted == true),
            updatedAt = System.currentTimeMillis()
        )
        animeDao.upsertLibraryEntry(updatedEntry)

        // 3. Update per-episode history
        animeDao.upsertWatchedEpisode(
            WatchedEpisodeEntity(
                id = "${anime.id}_$episodeNumber",
                animeId = anime.id,
                episodeNumber = episodeNumber,
                positionMs = positionMs,
                durationMs = safeDuration,
                isCompleted = isEpisodeCompleted,
                watchedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromLibrary(animeId: Int) = withContext(Dispatchers.IO) {
        animeDao.deleteWatchlistItem(animeId)
        animeDao.deleteFavoriteAnimeItem(animeId)
        animeDao.deleteRecentlyWatchedItem(animeId)
        animeDao.deleteLibraryEntry(animeId)
    }

    suspend fun addRecentSearch(query: String) = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length >= 2) {
            animeDao.insertRecentSearch(RecentSearchEntity(query = trimmed))
        }
    }

    suspend fun removeRecentSearch(query: String) = withContext(Dispatchers.IO) {
        animeDao.deleteRecentSearch(query)
    }

    suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        animeDao.clearRecentSearches()
    }

    suspend fun clearWatchHistory() = withContext(Dispatchers.IO) {
        animeDao.clearWatchHistory()
        animeDao.clearRecentlyWatchedTable()
    }

    private fun mapDtoToAnimeItem(dto: JikanAnimeDto): AnimeItem {
        val curatedMatch = CatalogSeedData.curatedAnimeCatalog.find { it.id == dto.malId }
        if (curatedMatch != null) return curatedMatch

        val poster = dto.images?.jpg?.largeImageUrl
            ?: dto.images?.jpg?.imageUrl
            ?: "https://cdn.myanimelist.net/images/anime/1171/109222l.jpg"
        val backdrop = dto.trailer?.images?.maximumImageUrl
            ?: dto.trailer?.images?.largeImageUrl
            ?: poster
        val epCount = (dto.episodes ?: 12).coerceAtLeast(1)
        val youtubeId = dto.trailer?.youtubeId

        val officialLinks = mutableListOf<OfficialStreamSource>()
        if (!youtubeId.isNullOrBlank()) {
            officialLinks.add(
                OfficialStreamSource(
                    providerName = "YouTube Official Trailer & Preview",
                    url = "https://www.youtube.com/watch?v=$youtubeId",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = CatalogSeedData.OPEN_STREAM_SPRITE_FRIGHT,
                    badgeText = "Free • Subtitles"
                )
            )
        }
        dto.streaming?.forEach { ext ->
            officialLinks.add(
                OfficialStreamSource(
                    providerName = ext.name,
                    url = ext.url,
                    isFreeVerified = ext.name.contains("YouTube", ignoreCase = true) ||
                        ext.name.contains("Muse", ignoreCase = true) ||
                        ext.name.contains("Ani-One", ignoreCase = true),
                    hasSubtitles = true,
                    badgeText = "Official Distributor"
                )
            )
        }
        if (officialLinks.isEmpty()) {
            officialLinks.add(
                OfficialStreamSource(
                    providerName = "YouTube (Official Channel Search)",
                    url = "https://www.youtube.com/results?search_query=${dto.title}+official+anime",
                    isFreeVerified = false,
                    hasSubtitles = true,
                    badgeText = "Check Regional Availability"
                )
            )
        }

        return AnimeItem(
            id = dto.malId,
            title = dto.titleEnglish?.takeIf { it.isNotBlank() } ?: dto.title,
            titleEnglish = dto.titleEnglish ?: dto.title,
            titleJapanese = dto.titleJapanese ?: "",
            alternativeTitles = dto.titleSynonyms.orEmpty(),
            synopsis = dto.synopsis ?: "Discover this official anime series on AnimeFlix.",
            posterUrl = poster,
            backdropUrl = backdrop,
            score = dto.score ?: 7.8,
            scoredBy = dto.scoredBy ?: 45000,
            rank = dto.rank ?: 500,
            popularity = dto.popularity ?: 300,
            episodesCount = epCount,
            format = dto.type ?: "TV",
            status = dto.status ?: "Finished Airing",
            season = dto.season?.replaceFirstChar { it.uppercase() } ?: "Fall",
            year = dto.year ?: 2023,
            rating = dto.rating?.take(10) ?: "PG-13",
            duration = dto.duration ?: "24m per ep",
            genres = dto.genres?.map { it.name }.orEmpty().ifEmpty { listOf("Action", "Fantasy") },
            themes = dto.themes?.map { it.name }.orEmpty(),
            demographics = dto.demographics?.map { it.name }.orEmpty(),
            studios = dto.studios?.map { it.name }.orEmpty(),
            trailerYoutubeId = youtubeId,
            trailerEmbedUrl = dto.trailer?.embedUrl,
            officialSources = officialLinks,
            episodes = CatalogSeedData.buildEpisodesForAnime(
                animeTitle = dto.title,
                totalCount = epCount.coerceAtMost(24),
                thumbnail = poster,
                freeEpisodeCount = if (!youtubeId.isNullOrBlank()) 2 else 0,
                streamUrl = if (!youtubeId.isNullOrBlank()) CatalogSeedData.OPEN_STREAM_SPRITE_FRIGHT else null,
                youtubeId = youtubeId
            ),
            isHiddenGem = (dto.score ?: 0.0) >= 8.0 && (dto.popularity ?: 0) > 200
        )
    }

    private fun AnimeItem.toWatchlistEntity(
        customListName: String? = null,
        addedAt: Long = System.currentTimeMillis()
    ) = WatchlistEntity(
        animeId = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        episodesCount = episodesCount,
        score = score,
        genresCsv = genres.joinToString(","),
        format = format,
        year = year,
        customListName = customListName,
        addedAt = addedAt
    )

    private fun AnimeItem.toFavoriteAnimeEntity(
        favoritedAt: Long = System.currentTimeMillis()
    ) = FavoriteAnimeEntity(
        animeId = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        episodesCount = episodesCount,
        score = score,
        genresCsv = genres.joinToString(","),
        format = format,
        year = year,
        favoritedAt = favoritedAt
    )

    private fun AnimeItem.toRecentlyWatchedEntity(
        episodeNumber: Int,
        episodeTitle: String,
        playbackPositionMs: Long,
        durationMs: Long,
        isCompleted: Boolean = false
    ) = RecentlyWatchedEntity(
        animeId = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        episodesCount = episodesCount,
        score = score,
        genresCsv = genres.joinToString(","),
        format = format,
        year = year,
        episodeNumber = episodeNumber,
        episodeTitle = episodeTitle,
        playbackPositionMs = playbackPositionMs,
        durationMs = durationMs,
        isCompleted = isCompleted,
        lastWatchedAt = System.currentTimeMillis()
    )

    private fun AnimeItem.toLibraryEntry(
        inWatchlist: Boolean = false,
        isFavorite: Boolean = false,
        lastWatchedEpisode: Int = 0,
        lastPlaybackPositionMs: Long = 0L,
        lastDurationMs: Long = 1_440_000L,
        updatedAt: Long = System.currentTimeMillis()
    ) = LibraryEntryEntity(
        animeId = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        episodesCount = episodesCount,
        score = score,
        genresCsv = genres.joinToString(","),
        format = format,
        year = year,
        inWatchlist = inWatchlist,
        isFavorite = isFavorite,
        lastWatchedEpisode = lastWatchedEpisode,
        lastPlaybackPositionMs = lastPlaybackPositionMs,
        lastDurationMs = lastDurationMs,
        updatedAt = updatedAt
    )

    private fun AnimeItem.toCachedEntity() = CachedAnimeEntity(
        animeId = id,
        title = title,
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        synopsis = synopsis,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        score = score,
        scoredBy = scoredBy,
        episodesCount = episodesCount,
        format = format,
        status = status,
        season = season,
        year = year,
        rating = rating,
        genresCsv = genres.joinToString(","),
        themesCsv = themes.joinToString(","),
        demographicsCsv = demographics.joinToString(","),
        studiosCsv = studios.joinToString(","),
        trailerYoutubeId = trailerYoutubeId
    )

    private fun CachedAnimeEntity.toDomainModel(): AnimeItem {
        val curated = CatalogSeedData.curatedAnimeCatalog.find { it.id == animeId }
        if (curated != null) return curated
        return AnimeItem(
            id = animeId,
            title = title,
            titleEnglish = titleEnglish,
            titleJapanese = titleJapanese,
            synopsis = synopsis,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            score = score,
            scoredBy = scoredBy,
            episodesCount = episodesCount,
            format = format,
            status = status,
            season = season,
            year = year,
            rating = rating,
            genres = genresCsv.split(",").filter { it.isNotBlank() },
            themes = themesCsv.split(",").filter { it.isNotBlank() },
            demographics = demographicsCsv.split(",").filter { it.isNotBlank() },
            studios = studiosCsv.split(",").filter { it.isNotBlank() },
            trailerYoutubeId = trailerYoutubeId,
            episodes = CatalogSeedData.buildEpisodesForAnime(
                animeTitle = title,
                totalCount = episodesCount,
                thumbnail = posterUrl,
                freeEpisodeCount = 2,
                streamUrl = CatalogSeedData.OPEN_STREAM_SPRITE_FRIGHT,
                youtubeId = trailerYoutubeId
            )
        )
    }

    companion object {
        fun createDefaultApiService(): JikanApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val okHttp = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .client(okHttp)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(JikanApiService::class.java)
        }
    }
}
