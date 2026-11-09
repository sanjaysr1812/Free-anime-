package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("filter") filter: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): JikanAnimeListResponse

    @GET("seasons/now")
    suspend fun getSeasonNow(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): JikanAnimeListResponse

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: String? = null,
        @Query("genres_exclude") genresExclude: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = "desc",
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("min_score") minScore: Double? = null,
        @Query("max_score") maxScore: Double? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sfw") sfw: Boolean = true
    ): JikanAnimeListResponse

    @GET("anime/{id}/full")
    suspend fun getAnimeFullById(
        @Path("id") id: Int
    ): JikanSingleAnimeResponse

    @GET("anime/{id}/characters")
    suspend fun getAnimeCharacters(
        @Path("id") id: Int
    ): JikanCharactersResponse

    @GET("anime/{id}/episodes")
    suspend fun getAnimeEpisodes(
        @Path("id") id: Int,
        @Query("page") page: Int = 1
    ): JikanEpisodesResponse

    @GET("random/anime")
    suspend fun getRandomAnime(): JikanSingleAnimeResponse
}

@JsonClass(generateAdapter = true)
data class JikanAnimeListResponse(
    @Json(name = "data") val data: List<JikanAnimeDto> = emptyList(),
    @Json(name = "pagination") val pagination: JikanPaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class JikanSingleAnimeResponse(
    @Json(name = "data") val data: JikanAnimeDto? = null
)

@JsonClass(generateAdapter = true)
data class JikanPaginationDto(
    @Json(name = "last_visible_page") val lastVisiblePage: Int = 1,
    @Json(name = "has_next_page") val hasNextPage: Boolean = false,
    @Json(name = "current_page") val currentPage: Int = 1
)

@JsonClass(generateAdapter = true)
data class JikanAnimeDto(
    @Json(name = "mal_id") val malId: Int = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "title_english") val titleEnglish: String? = null,
    @Json(name = "title_japanese") val titleJapanese: String? = null,
    @Json(name = "title_synonyms") val titleSynonyms: List<String>? = null,
    @Json(name = "synopsis") val synopsis: String? = null,
    @Json(name = "images") val images: JikanImagesDto? = null,
    @Json(name = "trailer") val trailer: JikanTrailerDto? = null,
    @Json(name = "score") val score: Double? = null,
    @Json(name = "scored_by") val scoredBy: Int? = null,
    @Json(name = "rank") val rank: Int? = null,
    @Json(name = "popularity") val popularity: Int? = null,
    @Json(name = "episodes") val episodes: Int? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "season") val season: String? = null,
    @Json(name = "year") val year: Int? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "duration") val duration: String? = null,
    @Json(name = "genres") val genres: List<JikanNamedItemDto>? = null,
    @Json(name = "themes") val themes: List<JikanNamedItemDto>? = null,
    @Json(name = "demographics") val demographics: List<JikanNamedItemDto>? = null,
    @Json(name = "studios") val studios: List<JikanNamedItemDto>? = null,
    @Json(name = "streaming") val streaming: List<JikanExternalLinkDto>? = null
)

@JsonClass(generateAdapter = true)
data class JikanImagesDto(
    @Json(name = "jpg") val jpg: JikanImageSetDto? = null,
    @Json(name = "webp") val webp: JikanImageSetDto? = null
)

@JsonClass(generateAdapter = true)
data class JikanImageSetDto(
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "large_image_url") val largeImageUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class JikanTrailerDto(
    @Json(name = "youtube_id") val youtubeId: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "embed_url") val embedUrl: String? = null,
    @Json(name = "images") val images: JikanTrailerImagesDto? = null
)

@JsonClass(generateAdapter = true)
data class JikanTrailerImagesDto(
    @Json(name = "maximum_image_url") val maximumImageUrl: String? = null,
    @Json(name = "large_image_url") val largeImageUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class JikanNamedItemDto(
    @Json(name = "mal_id") val malId: Int = 0,
    @Json(name = "name") val name: String = ""
)

@JsonClass(generateAdapter = true)
data class JikanExternalLinkDto(
    @Json(name = "name") val name: String = "",
    @Json(name = "url") val url: String = ""
)

@JsonClass(generateAdapter = true)
data class JikanCharactersResponse(
    @Json(name = "data") val data: List<JikanCharacterEntryDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class JikanCharacterEntryDto(
    @Json(name = "character") val character: JikanCharacterInfoDto? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "voice_actors") val voiceActors: List<JikanVoiceActorDto>? = null
)

@JsonClass(generateAdapter = true)
data class JikanCharacterInfoDto(
    @Json(name = "mal_id") val malId: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "images") val images: JikanImagesDto? = null
)

@JsonClass(generateAdapter = true)
data class JikanVoiceActorDto(
    @Json(name = "person") val person: JikanPersonDto? = null,
    @Json(name = "language") val language: String? = null
)

@JsonClass(generateAdapter = true)
data class JikanPersonDto(
    @Json(name = "name") val name: String = ""
)

@JsonClass(generateAdapter = true)
data class JikanEpisodesResponse(
    @Json(name = "data") val data: List<JikanEpisodeDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class JikanEpisodeDto(
    @Json(name = "mal_id") val malId: Int = 1,
    @Json(name = "title") val title: String? = null,
    @Json(name = "title_japanese") val titleJapanese: String? = null,
    @Json(name = "aired") val aired: String? = null,
    @Json(name = "filler") val filler: Boolean? = false
)
