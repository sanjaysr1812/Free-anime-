package com.example.data.model

data class AnimeItem(
    val id: Int,
    val title: String,
    val titleEnglish: String = "",
    val titleJapanese: String = "",
    val alternativeTitles: List<String> = emptyList(),
    val synopsis: String = "",
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val score: Double = 0.0,
    val scoredBy: Int = 0,
    val rank: Int = 0,
    val popularity: Int = 0,
    val episodesCount: Int = 12,
    val format: String = "TV", // TV, Movie, OVA, ONA, Special
    val status: String = "Finished Airing", // Currently Airing, Finished Airing, Upcoming
    val season: String = "Fall",
    val year: Int = 2024,
    val rating: String = "PG-13",
    val duration: String = "24m per ep",
    val genres: List<String> = emptyList(),
    val themes: List<String> = emptyList(),
    val demographics: List<String> = emptyList(),
    val studios: List<String> = emptyList(),
    val trailerYoutubeId: String? = null,
    val trailerEmbedUrl: String? = null,
    val officialSources: List<OfficialStreamSource> = emptyList(),
    val episodes: List<AnimeEpisode> = emptyList(),
    val characters: List<AnimeCharacter> = emptyList(),
    val relatedIds: List<Int> = emptyList(),
    val isHiddenGem: Boolean = false,
    val isRecentlyAdded: Boolean = false,
    val hasNewVerifiedEpisode: Boolean = false
) {
    val allTags: List<String>
        get() = (genres + themes + demographics).distinct()

    val hasVerifiedFreeStream: Boolean
        get() = officialSources.any { it.isFreeVerified } || episodes.any { it.isAvailableFree }
}

data class OfficialStreamSource(
    val providerName: String,
    val url: String,
    val isFreeVerified: Boolean,
    val hasSubtitles: Boolean = true,
    val subtitleLanguages: List<String> = listOf("English", "Japanese (CC)"),
    val directStreamUrl: String? = null,
    val badgeText: String = "Free • Subtitles",
    val availabilityNote: String = "Official authorized distributor"
)

data class AnimeEpisode(
    val number: Int,
    val seasonNumber: Int = 1,
    val title: String,
    val titleJapanese: String = "",
    val durationMinutes: Int = 24,
    val airedDate: String = "",
    val synopsis: String = "",
    val thumbnailUrl: String = "",
    val isAvailableFree: Boolean = false,
    val directStreamUrl: String? = null,
    val officialEmbedUrl: String? = null,
    val sourceLabel: String = "Official Free Stream",
    val unavailableReason: String = "No official free stream is currently available for this episode. Check official regional partners or watch the authorized preview."
)

data class AnimeCharacter(
    val id: Int,
    val name: String,
    val role: String,
    val imageUrl: String,
    val voiceActorName: String = "",
    val voiceActorLanguage: String = "Japanese"
)

data class AnimeGenre(
    val malId: Int,
    val name: String,
    val category: String, // Genre, Theme, Demographic
    val countLabel: String,
    val accentHex: Long,
    val secondaryHex: Long,
    val representativeImageUrl: String
)

enum class SortOption(val label: String, val jikanOrderBy: String) {
    POPULARITY("Most Popular", "members"),
    RATING("Highest Rated", "score"),
    RELEASE_DATE("Release Year (Newest)", "start_date"),
    TITLE("Alphabetical (A-Z)", "title")
}

data class FilterState(
    val query: String = "",
    val includedGenres: Set<String> = emptySet(),
    val excludedGenres: Set<String> = emptySet(),
    val sortBy: SortOption = SortOption.POPULARITY,
    val selectedYear: Int? = null,
    val selectedSeason: String? = null, // Winter, Spring, Summer, Fall
    val selectedStatus: String? = null, // Currently Airing, Finished Airing
    val selectedFormat: String? = null, // TV, Movie, OVA, ONA
    val onlyFreeVerified: Boolean = false,
    val onlyHiddenGems: Boolean = false
) {
    val activeFilterCount: Int
        get() = includedGenres.size +
            excludedGenres.size +
            (if (selectedYear != null) 1 else 0) +
            (if (selectedSeason != null) 1 else 0) +
            (if (selectedStatus != null) 1 else 0) +
            (if (selectedFormat != null) 1 else 0) +
            (if (onlyFreeVerified) 1 else 0) +
            (if (onlyHiddenGems) 1 else 0)
}
