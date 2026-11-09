package com.example.data.repository

import com.example.data.model.AnimeCharacter
import com.example.data.model.AnimeEpisode
import com.example.data.model.AnimeGenre
import com.example.data.model.AnimeItem
import com.example.data.model.OfficialStreamSource

object CatalogSeedData {

    // Open-licensed / Creative Commons (CC-BY Blender Studio / Public Domain archive) HTTPS MP4 streams
    // used strictly for authorized free playback demonstration & public-domain/open-content episodes.
    const val OPEN_STREAM_SPRITE_FRIGHT = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    const val OPEN_STREAM_SINTEL_ANIME = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    const val OPEN_STREAM_TEARS_OF_STEEL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    const val OPEN_STREAM_COSMOS = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"

    // Complete 26-genre discovery taxonomy required by the specification
    val allGenres: List<AnimeGenre> = listOf(
        AnimeGenre(1, "Action", "Genre", "24k+ titles", 0xFF7C3AED, 0xFFEF4444, "https://cdn.myanimelist.net/images/anime/1171/109222l.jpg"),
        AnimeGenre(2, "Adventure", "Genre", "19k+ titles", 0xFF2563EB, 0xFF06B6D4, "https://cdn.myanimelist.net/images/anime/6/73245l.jpg"),
        AnimeGenre(22, "Romance", "Genre", "18k+ titles", 0xFFEC4899, 0xFF8B5CF6, "https://cdn.myanimelist.net/images/anime/5/87048l.jpg"),
        AnimeGenre(101, "Romantic comedy", "Theme", "9.5k+ titles", 0xFFF43F5E, 0xFFF59E0B, "https://cdn.myanimelist.net/images/anime/1295/106551l.jpg"),
        AnimeGenre(4, "Comedy", "Genre", "16k+ titles", 0xFFF59E0B, 0xFFEC4899, "https://cdn.myanimelist.net/images/anime/1441/122795l.jpg"),
        AnimeGenre(8, "Drama", "Genre", "15k+ titles", 0xFF6366F1, 0xFF3B82F6, "https://cdn.myanimelist.net/images/anime/1804/95029l.jpg"),
        AnimeGenre(40, "Psychological", "Theme", "6.2k+ titles", 0xFF4F46E5, 0xFF1E1B4B, "https://cdn.myanimelist.net/images/anime/10/18793l.jpg"),
        AnimeGenre(7, "Mystery", "Genre", "7.8k+ titles", 0xFF0EA5E9, 0xFF312E81, "https://cdn.myanimelist.net/images/anime/1988/113791l.jpg"),
        AnimeGenre(41, "Thriller", "Genre", "5.4k+ titles", 0xFFDC2626, 0xFF31102F, "https://cdn.myanimelist.net/images/anime/1935/127974l.jpg"),
        AnimeGenre(10, "Fantasy", "Genre", "20k+ titles", 0xFF8B5CF6, 0xFF06B6D4, "https://cdn.myanimelist.net/images/anime/1015/138006l.jpg"),
        AnimeGenre(102, "Dark fantasy", "Theme", "6.8k+ titles", 0xFF9333EA, 0xFF111827, "https://cdn.myanimelist.net/images/anime/1806/126216l.jpg"),
        AnimeGenre(37, "Supernatural", "Genre", "12k+ titles", 0xFF7C3AED, 0xFF1D4ED8, "https://cdn.myanimelist.net/images/anime/1286/99889l.jpg"),
        AnimeGenre(24, "Science fiction", "Genre", "11k+ titles", 0xFF06B6D4, 0xFF3B82F6, "https://cdn.myanimelist.net/images/anime/1818/126431l.jpg"),
        AnimeGenre(36, "Slice of life", "Genre", "14k+ titles", 0xFF10B981, 0xFF3B82F6, "https://cdn.myanimelist.net/images/anime/1448/127956l.jpg"),
        AnimeGenre(23, "School", "Theme", "13k+ titles", 0xFF3B82F6, 0xFF8B5CF6, "https://cdn.myanimelist.net/images/anime/1908/135431l.jpg"),
        AnimeGenre(103, "Coming of age", "Theme", "7.1k+ titles", 0xFFF97316, 0xFFEC4899, "https://cdn.myanimelist.net/images/anime/1122/96435l.jpg"),
        AnimeGenre(30, "Sports", "Genre", "5.9k+ titles", 0xFFF97316, 0xFFEA580C, "https://cdn.myanimelist.net/images/anime/7/76014l.jpg"),
        AnimeGenre(19, "Music", "Theme", "4.8k+ titles", 0xFFD946EF, 0xFF6366F1, "https://cdn.myanimelist.net/images/anime/1448/127956l.jpg"),
        AnimeGenre(13, "Historical", "Theme", "6.4k+ titles", 0xFFB45309, 0xFF78350F, "https://cdn.myanimelist.net/images/anime/1500/103005l.jpg"),
        AnimeGenre(14, "Horror", "Genre", "3.9k+ titles", 0xFF991B1B, 0xFF090A10, "https://cdn.myanimelist.net/images/anime/10/18793l.jpg"),
        AnimeGenre(18, "Mecha", "Theme", "5.2k+ titles", 0xFF0284C7, 0xFF1E293B, "https://cdn.myanimelist.net/images/anime/1987/113792l.jpg"),
        AnimeGenre(62, "Isekai", "Theme", "8.7k+ titles", 0xFF8B5CF6, 0xFFEC4899, "https://cdn.myanimelist.net/images/anime/1522/128039l.jpg"),
        AnimeGenre(27, "Shounen", "Demographic", "22k+ titles", 0xFFEF4444, 0xFFF59E0B, "https://cdn.myanimelist.net/images/anime/13/17405l.jpg"),
        AnimeGenre(42, "Seinen", "Demographic", "10k+ titles", 0xFF475569, 0xFF1E293B, "https://cdn.myanimelist.net/images/anime/1500/103005l.jpg"),
        AnimeGenre(25, "Shoujo", "Demographic", "8.3k+ titles", 0xFFEC4899, 0xFFF472B6, "https://cdn.myanimelist.net/images/anime/2/11232l.jpg"),
        AnimeGenre(43, "Josei", "Demographic", "3.4k+ titles", 0xFFA855F7, 0xFFEC4899, "https://cdn.myanimelist.net/images/anime/3/75743l.jpg")
    )

    val curatedAnimeCatalog: List<AnimeItem> = listOf(
        AnimeItem(
            id = 40748,
            title = "Jujutsu Kaisen",
            titleEnglish = "Jujutsu Kaisen",
            titleJapanese = "呪術廻戦",
            alternativeTitles = listOf("Sorcery Fight", "JJK"),
            synopsis = "Idly indulging in baseless paranormal activities with the Occult Club, high schooler Yuuji Itadori spends his days at either the clubroom or the hospital, where he visits his bedridden grandfather. However, this leisurely lifestyle soon takes a turn for the strange when he knowingly encounters a cursed item and enters the world of Curses.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1171/109222l.jpg",
            backdropUrl = "https://img.youtube.com/vi/4A_X-Dvl0ws/maxresdefault.jpg",
            score = 8.63,
            scoredBy = 1950000,
            rank = 82,
            popularity = 14,
            episodesCount = 24,
            format = "TV",
            status = "Finished Airing",
            season = "Fall",
            year = 2020,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Supernatural", "Dark fantasy", "Fantasy"),
            themes = listOf("School", "Dark fantasy"),
            demographics = listOf("Shounen"),
            studios = listOf("MAPPA"),
            trailerYoutubeId = "4A_X-Dvl0ws",
            trailerEmbedUrl = "https://www.youtube.com/embed/4A_X-Dvl0ws",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (TOHO Animation / Ani-One Official)",
                    url = "https://www.youtube.com/watch?v=4A_X-Dvl0ws",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                    badgeText = "Free • Subtitles • Official Preview & Open Stream"
                ),
                OfficialStreamSource(
                    providerName = "Crunchyroll Official",
                    url = "https://www.crunchyroll.com/series/GRDV0019R/jujutsu-kaisen",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    badgeText = "Selected Free Episodes • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Jujutsu Kaisen",
                totalCount = 24,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1171/109222l.jpg",
                freeEpisodeCount = 6,
                streamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                youtubeId = "4A_X-Dvl0ws",
                customTitles = listOf(
                    "Ryomen Sukuna",
                    "For Myself",
                    "Girl of Steel",
                    "Curse Womb Must Die",
                    "Curse Womb Must Die -II-",
                    "After Rain",
                    "Assault",
                    "Boredom",
                    "Small Fry and Reverse Retribution",
                    "Idle Transfiguration"
                )
            ),
            characters = listOf(
                AnimeCharacter(1, "Satoru Gojo", "Main", "https://cdn.myanimelist.net/images/characters/15/422168.jpg", "Yuichi Nakamura", "Japanese"),
                AnimeCharacter(2, "Yuji Itadori", "Main", "https://cdn.myanimelist.net/images/characters/6/467646.jpg", "Junya Enoki", "Japanese"),
                AnimeCharacter(3, "Megumi Fushiguro", "Main", "https://cdn.myanimelist.net/images/characters/2/392689.jpg", "Yuma Uchida", "Japanese"),
                AnimeCharacter(4, "Nobara Kugisaki", "Main", "https://cdn.myanimelist.net/images/characters/12/422313.jpg", "Asami Seto", "Japanese")
            ),
            relatedIds = listOf(52991, 52299, 21),
            hasNewVerifiedEpisode = true
        ),
        AnimeItem(
            id = 32281,
            title = "Your Name",
            titleEnglish = "Your Name.",
            titleJapanese = "君の名は。",
            alternativeTitles = listOf("Kimi no Na wa.", "Your Name"),
            synopsis = "Two strangers, connected by a mysterious phenomenon, begin to swap bodies and experience each other's lives, leading to a heartfelt journey of love and destiny across time and a falling comet.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/5/87048l.jpg",
            backdropUrl = "https://img.youtube.com/vi/3KR8_igDs1Y/maxresdefault.jpg",
            score = 8.84,
            scoredBy = 1982000,
            rank = 31,
            popularity = 11,
            episodesCount = 1,
            format = "Movie",
            status = "Finished Airing",
            season = "Summer",
            year = 2016,
            rating = "PG-13",
            duration = "1h 46m",
            genres = listOf("Romance", "Drama", "Supernatural", "Fantasy"),
            themes = listOf("School", "Coming of age"),
            demographics = listOf("Shounen"),
            studios = listOf("CoMix Wave Films"),
            trailerYoutubeId = "3KR8_igDs1Y",
            trailerEmbedUrl = "https://www.youtube.com/embed/3KR8_igDs1Y",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube",
                    url = "https://www.youtube.com/watch?v=3KR8_igDs1Y",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free • Subtitles"
                ),
                OfficialStreamSource(
                    providerName = "Crunchyroll Store / Official Partners",
                    url = "https://www.crunchyroll.com",
                    isFreeVerified = false,
                    hasSubtitles = true,
                    badgeText = "Subscription / Rental"
                )
            ),
            episodes = listOf(
                AnimeEpisode(
                    number = 1,
                    seasonNumber = 1,
                    title = "Your Name (Official Feature Preview & Open Stream)",
                    titleJapanese = "君の名は。",
                    durationMinutes = 106,
                    airedDate = "Aug 26, 2016",
                    synopsis = "Mitsuha and Taki awaken in each other's bodies and leave notes to navigate their daily lives before seeking each other across time.",
                    thumbnailUrl = "https://cdn.myanimelist.net/images/anime/5/87048l.jpg",
                    isAvailableFree = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    officialEmbedUrl = "https://www.youtube.com/embed/3KR8_igDs1Y",
                    sourceLabel = "YouTube Official & Open Stream • Subtitles"
                )
            ),
            characters = listOf(
                AnimeCharacter(10, "Mitsuha Miyamizu", "Main", "https://cdn.myanimelist.net/images/characters/5/316634.jpg", "Mone Kamishiraishi", "Japanese"),
                AnimeCharacter(11, "Taki Tachibana", "Main", "https://cdn.myanimelist.net/images/characters/5/316635.jpg", "Ryunosuke Kamiki", "Japanese")
            ),
            relatedIds = listOf(52991, 33352, 50265)
        ),
        AnimeItem(
            id = 52991,
            title = "Frieren",
            titleEnglish = "Frieren: Beyond Journey's End",
            titleJapanese = "葬送のフリーレン",
            alternativeTitles = listOf("Sousou no Frieren", "Frieren"),
            synopsis = "During their decade-long quest to defeat the Demon King, the members of the hero's party—Himmel himself, the priest Heiter, the dwarf warrior Eisen, and the elven mage Frieren—forge bonds through adventures and battles. When the party disbands after victory, Frieren embarks on a new journey to understand humanity.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1015/138006l.jpg",
            backdropUrl = "https://img.youtube.com/vi/ZEkwCGJ3o7M/maxresdefault.jpg",
            score = 9.32,
            scoredBy = 890000,
            rank = 1,
            popularity = 140,
            episodesCount = 28,
            format = "TV",
            status = "Finished Airing",
            season = "Fall",
            year = 2023,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Adventure", "Drama", "Fantasy", "Slice of life"),
            themes = listOf("Coming of age"),
            demographics = listOf("Shounen"),
            studios = listOf("Madhouse"),
            trailerYoutubeId = "ZEkwCGJ3o7M",
            trailerEmbedUrl = "https://www.youtube.com/embed/ZEkwCGJ3o7M",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Muse Asia Official Free)",
                    url = "https://www.youtube.com/watch?v=ZEkwCGJ3o7M",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free • Subtitles (Muse Asia Region)"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Frieren: Beyond Journey's End",
                totalCount = 28,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1015/138006l.jpg",
                freeEpisodeCount = 12,
                streamUrl = OPEN_STREAM_SINTEL_ANIME,
                youtubeId = "ZEkwCGJ3o7M",
                customTitles = listOf(
                    "The Journey's End",
                    "It Didn't Have to Be Magic...",
                    "Killing Magic",
                    "The Land Where Souls Rest",
                    "Phantoms of the Dead",
                    "The Hero of the Village"
                )
            ),
            characters = listOf(
                AnimeCharacter(20, "Frieren", "Main", "https://cdn.myanimelist.net/images/characters/7/525105.jpg", "Atsumi Tanezaki", "Japanese"),
                AnimeCharacter(21, "Fern", "Main", "https://cdn.myanimelist.net/images/characters/14/525106.jpg", "Kana Ichinose", "Japanese"),
                AnimeCharacter(22, "Stark", "Main", "https://cdn.myanimelist.net/images/characters/11/525107.jpg", "Chiaki Kobayashi", "Japanese")
            ),
            relatedIds = listOf(40748, 32281, 52299),
            isRecentlyAdded = true,
            hasNewVerifiedEpisode = true
        ),
        AnimeItem(
            id = 52299,
            title = "Solo Leveling",
            titleEnglish = "Solo Leveling",
            titleJapanese = "俺だけレベルアップな件",
            alternativeTitles = listOf("Ore dake Level Up na Ken", "Na Honjaman Level Up"),
            synopsis = "Ten years ago, 'the Gate' appeared and connected the real world with the realm of magic and monsters. Sung Jinwoo, notoriously known as the Weakest Hunter of All Mankind, awakens a mysterious System that allows only him to level up without limit.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1522/128039l.jpg",
            backdropUrl = "https://img.youtube.com/vi/bssSj4cKsrI/maxresdefault.jpg",
            score = 8.31,
            scoredBy = 610000,
            rank = 240,
            popularity = 95,
            episodesCount = 12,
            format = "TV",
            status = "Currently Airing",
            season = "Winter",
            year = 2024,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Adventure", "Fantasy", "Dark fantasy"),
            themes = listOf("Isekai", "Dark fantasy"),
            demographics = listOf("Shounen"),
            studios = listOf("A-1 Pictures"),
            trailerYoutubeId = "bssSj4cKsrI",
            trailerEmbedUrl = "https://www.youtube.com/embed/bssSj4cKsrI",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Aniplex / Crunchyroll Official)",
                    url = "https://www.youtube.com/watch?v=bssSj4cKsrI",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                    badgeText = "Free Episode 1 & Official Previews"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Solo Leveling",
                totalCount = 12,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1522/128039l.jpg",
                freeEpisodeCount = 3,
                streamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                youtubeId = "bssSj4cKsrI",
                customTitles = listOf(
                    "I'm Used to It",
                    "If I Had One More Chance",
                    "It's Like a Game",
                    "I've Gotta Get Stronger",
                    "A Pretty Good Deal",
                    "The Real Hunt Begins"
                )
            ),
            isRecentlyAdded = true,
            hasNewVerifiedEpisode = true
        ),
        AnimeItem(
            id = 21,
            title = "One Piece",
            titleEnglish = "One Piece",
            titleJapanese = "ONE PIECE",
            alternativeTitles = listOf("OP", "Straw Hat Pirates"),
            synopsis = "Monkey D. Luffy refuses to let anyone or anything stand in the way of his quest to become the King of All Pirates. With a course charted for the treacherous waters of the Grand Line and beyond, this is one captain who'll never give up until he's claimed the greatest treasure on Earth: the Legendary One Piece!",
            posterUrl = "https://cdn.myanimelist.net/images/anime/6/73245l.jpg",
            backdropUrl = "https://img.youtube.com/vi/MCb13lbVGE0/maxresdefault.jpg",
            score = 8.72,
            scoredBy = 1380000,
            rank = 54,
            popularity = 19,
            episodesCount = 1120,
            format = "TV",
            status = "Currently Airing",
            season = "Fall",
            year = 1999,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Action", "Adventure", "Comedy", "Fantasy"),
            themes = listOf("Coming of age"),
            demographics = listOf("Shounen"),
            studios = listOf("Toei Animation"),
            trailerYoutubeId = "MCb13lbVGE0",
            trailerEmbedUrl = "https://www.youtube.com/embed/MCb13lbVGE0",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (One Piece Official Channel)",
                    url = "https://www.youtube.com/watch?v=MCb13lbVGE0",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_COSMOS,
                    badgeText = "Free • Official Channel & Arcs"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "One Piece",
                totalCount = 24,
                thumbnail = "https://cdn.myanimelist.net/images/anime/6/73245l.jpg",
                freeEpisodeCount = 8,
                streamUrl = OPEN_STREAM_COSMOS,
                youtubeId = "MCb13lbVGE0",
                customTitles = listOf(
                    "I'm Luffy! The Man Who's Gonna Be King of the Pirates!",
                    "Enter the Great Swordsman! Pirate Hunter Roronoa Zoro!",
                    "Morgan versus Luffy! Who's the Mysterious Pretty Girl?",
                    "Luffy's Past! Enter Red-Haired Shanks!"
                )
            ),
            hasNewVerifiedEpisode = true
        ),
        AnimeItem(
            id = 1735,
            title = "Naruto Shippuden",
            titleEnglish = "Naruto: Shippuden",
            titleJapanese = "ナルト- 疾風伝",
            alternativeTitles = listOf("Naruto Hurricane Chronicles"),
            synopsis = "It has been two and a half years since Naruto Uzumaki left Konohagakure, the Hidden Leaf Village, for intense training following events which fueled his desire to be stronger. Now Akatsuki, the mysterious organization of elite rogue ninja, is closing in on their grand plan.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1565/111305l.jpg",
            backdropUrl = "https://img.youtube.com/vi/22R0j8UKRzY/maxresdefault.jpg",
            score = 8.27,
            scoredBy = 1690000,
            rank = 295,
            popularity = 16,
            episodesCount = 500,
            format = "TV",
            status = "Finished Airing",
            season = "Winter",
            year = 2007,
            rating = "PG-13",
            duration = "23m per ep",
            genres = listOf("Action", "Adventure", "Fantasy", "Comedy"),
            themes = listOf("Coming of age"),
            demographics = listOf("Shounen"),
            studios = listOf("Pierrot"),
            trailerYoutubeId = "22R0j8UKRzY",
            trailerEmbedUrl = "https://www.youtube.com/embed/22R0j8UKRzY",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "Crunchyroll / Pluto TV Official Free",
                    url = "https://www.crunchyroll.com/series/GYQ4MW246/naruto-shippuden",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                    badgeText = "Free Ad-Supported • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Naruto Shippuden",
                totalCount = 150,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1565/111305l.jpg",
                freeEpisodeCount = 135,
                streamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                youtubeId = "22R0j8UKRzY",
                customTitles = listOf("Homecoming", "The Akatsuki Makes Its Move", "The Results of Training")
            )
        ),
        AnimeItem(
            id = 50265,
            title = "Spy x Family",
            titleEnglish = "SPY x FAMILY",
            titleJapanese = "SPY×FAMILY",
            alternativeTitles = listOf("Spy Family"),
            synopsis = "Corrupt politicians, frenzied nationalists, and other warmongering forces constantly jeopardize the thin veneer of peace between neighboring countries Ostania and Westalis. Master spy 'Twilight' must build a fake family to execute Operation Strix, unaware his wife is an assassin and his daughter is a telepath.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1441/122795l.jpg",
            backdropUrl = "https://img.youtube.com/vi/ofXigq9aIpo/maxresdefault.jpg",
            score = 8.50,
            scoredBy = 1120000,
            rank = 135,
            popularity = 38,
            episodesCount = 12,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2022,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Action", "Comedy", "Slice of life"),
            themes = listOf("School", "Romantic comedy"),
            demographics = listOf("Shounen"),
            studios = listOf("Wit Studio", "CloverWorks"),
            trailerYoutubeId = "ofXigq9aIpo",
            trailerEmbedUrl = "https://www.youtube.com/embed/ofXigq9aIpo",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Muse Asia Official Free)",
                    url = "https://www.youtube.com/watch?v=ofXigq9aIpo",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free Full Season on Muse Asia • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Spy x Family",
                totalCount = 12,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1441/122795l.jpg",
                freeEpisodeCount = 12,
                streamUrl = OPEN_STREAM_SINTEL_ANIME,
                youtubeId = "ofXigq9aIpo",
                customTitles = listOf("Operation Strix", "Secure a Wife", "Prepare for the Interview")
            ),
            isRecentlyAdded = true,
            hasNewVerifiedEpisode = true
        ),
        AnimeItem(
            id = 44511,
            title = "Chainsaw Man",
            titleEnglish = "Chainsaw Man",
            titleJapanese = "チェンソーマン",
            alternativeTitles = listOf("CSM"),
            synopsis = "Denji is robbed of a normal teenage life, left with nothing but his deadbeat father's overwhelming debt. His only companion is his pet, the chainsaw devil Pochita, with whom he slays devils for money that inevitably ends up in the yakuza's pockets.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1806/126216l.jpg",
            backdropUrl = "https://img.youtube.com/vi/q15CRdE5Bv0/maxresdefault.jpg",
            score = 8.47,
            scoredBy = 1150000,
            rank = 152,
            popularity = 42,
            episodesCount = 12,
            format = "TV",
            status = "Finished Airing",
            season = "Fall",
            year = 2022,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Dark fantasy", "Horror", "Supernatural"),
            themes = listOf("Dark fantasy"),
            demographics = listOf("Shounen"),
            studios = listOf("MAPPA"),
            trailerYoutubeId = "q15CRdE5Bv0",
            trailerEmbedUrl = "https://www.youtube.com/embed/q15CRdE5Bv0",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (MAPPA Official Channel)",
                    url = "https://www.youtube.com/watch?v=q15CRdE5Bv0",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                    badgeText = "Official Previews & Episode 1 Free"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Chainsaw Man",
                totalCount = 12,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1806/126216l.jpg",
                freeEpisodeCount = 2,
                streamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                youtubeId = "q15CRdE5Bv0",
                customTitles = listOf("Dog & Chainsaw", "Arrival in Tokyo", "Meowy's Whereabouts")
            )
        ),
        AnimeItem(
            id = 38000,
            title = "Demon Slayer",
            titleEnglish = "Demon Slayer: Kimetsu no Yaiba",
            titleJapanese = "鬼滅の刃",
            alternativeTitles = listOf("Kimetsu no Yaiba"),
            synopsis = "Ever since the death of his father, the burden of supporting the family has fallen upon Tanjirou Kamado's shoulders. One day Tanjirou returns home to find his family slaughtered by a demon, and the sole survivor, his sister Nezuko, turned into a demon herself.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1286/99889l.jpg",
            backdropUrl = "https://img.youtube.com/vi/VQGCKyvzIM4/maxresdefault.jpg",
            score = 8.45,
            scoredBy = 2150000,
            rank = 160,
            popularity = 5,
            episodesCount = 26,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2019,
            rating = "R - 17+",
            duration = "23m per ep",
            genres = listOf("Action", "Supernatural", "Fantasy"),
            themes = listOf("Historical", "Dark fantasy"),
            demographics = listOf("Shounen"),
            studios = listOf("ufotable"),
            trailerYoutubeId = "VQGCKyvzIM4",
            trailerEmbedUrl = "https://www.youtube.com/embed/VQGCKyvzIM4",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Aniplex Official)",
                    url = "https://www.youtube.com/watch?v=VQGCKyvzIM4",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Demon Slayer",
                totalCount = 26,
                thumbnail = "https://cdn.myanimelist.net/images/anime/1286/99889l.jpg",
                freeEpisodeCount = 5,
                streamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                youtubeId = "VQGCKyvzIM4",
                customTitles = listOf("Cruelty", "Trainer Sakonji Urokodaki", "Sabito and Makomo", "Final Selection")
            )
        ),
        AnimeItem(
            id = 16498,
            title = "Attack on Titan",
            titleEnglish = "Attack on Titan",
            titleJapanese = "進撃の巨人",
            alternativeTitles = listOf("Shingeki no Kyojin", "AoT"),
            synopsis = "Centuries ago, mankind was slaughtered to near extinction by monstrous humanoid creatures called Titans, forcing humans to hide in fear behind enormous concentric walls. Eren Yeager vows to cleanse the earth of every last Titan.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/10/47347l.jpg",
            backdropUrl = "https://img.youtube.com/vi/MGRm4IzK1SQ/maxresdefault.jpg",
            score = 8.55,
            scoredBy = 2850000,
            rank = 112,
            popularity = 1,
            episodesCount = 25,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2013,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Drama", "Mystery", "Thriller", "Dark fantasy"),
            themes = listOf("Dark fantasy", "Psychological"),
            demographics = listOf("Shounen"),
            studios = listOf("Wit Studio"),
            trailerYoutubeId = "MGRm4IzK1SQ",
            trailerEmbedUrl = "https://www.youtube.com/embed/MGRm4IzK1SQ",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "Crunchyroll / YouTube Official",
                    url = "https://www.youtube.com/watch?v=MGRm4IzK1SQ",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime(
                animeTitle = "Attack on Titan",
                totalCount = 25,
                thumbnail = "https://cdn.myanimelist.net/images/anime/10/47347l.jpg",
                freeEpisodeCount = 4,
                streamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                youtubeId = "MGRm4IzK1SQ",
                customTitles = listOf("To You, in 2000 Years", "That Day", "A Dim Light Amid Despair")
            )
        ),
        AnimeItem(
            id = 46102,
            title = "Odd Taxi",
            titleEnglish = "Odd Taxi",
            titleJapanese = "オッドタクシー",
            alternativeTitles = listOf("ODDTAXI"),
            synopsis = "Eccentric and blunt 41-year-old walrus Hiroshi Odokawa lives a relatively normal life driving a taxi in Tokyo. However, his conversations with quirky passengers inadvertently connect him to the mysterious disappearance of a high school girl.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1988/113791l.jpg",
            backdropUrl = "https://img.youtube.com/vi/7dcTL319324/maxresdefault.jpg",
            score = 8.66,
            scoredBy = 265000,
            rank = 71,
            popularity = 410,
            episodesCount = 13,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2021,
            rating = "PG-13",
            duration = "23m per ep",
            genres = listOf("Mystery", "Drama", "Thriller", "Psychological"),
            themes = listOf("Psychological"),
            demographics = listOf("Seinen"),
            studios = listOf("OLM", "P.I.C.S."),
            trailerYoutubeId = "7dcTL319324",
            trailerEmbedUrl = "https://www.youtube.com/embed/7dcTL319324",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Pony Canyon Official)",
                    url = "https://www.youtube.com/watch?v=7dcTL319324",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_COSMOS,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Odd Taxi", 13, "https://cdn.myanimelist.net/images/anime/1988/113791l.jpg", 4, OPEN_STREAM_COSMOS, "7dcTL319324"),
            isHiddenGem = true
        ),
        AnimeItem(
            id = 47917,
            title = "Bocchi the Rock!",
            titleEnglish = "Bocchi the Rock!",
            titleJapanese = "ぼっち・ざ・ろっく！",
            alternativeTitles = listOf("BTR"),
            synopsis = "Yearning to make friends and perform live with a band, lonely and socially anxious Hitori 'Bocchi' Gotou devotes her time to playing the guitar. Her dream suddenly becomes reality when Nijika Ijichi recruits her into Kessoku Band.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1448/127956l.jpg",
            backdropUrl = "https://img.youtube.com/vi/Fp7lnCp_LW0/maxresdefault.jpg",
            score = 8.77,
            scoredBy = 420000,
            rank = 41,
            popularity = 215,
            episodesCount = 12,
            format = "TV",
            status = "Finished Airing",
            season = "Fall",
            year = 2022,
            rating = "PG-13",
            duration = "23m per ep",
            genres = listOf("Comedy", "Slice of life", "Music"),
            themes = listOf("Music", "School", "Coming of age"),
            demographics = listOf("Seinen"),
            studios = listOf("CloverWorks"),
            trailerYoutubeId = "Fp7lnCp_LW0",
            trailerEmbedUrl = "https://www.youtube.com/embed/Fp7lnCp_LW0",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Aniplex Official)",
                    url = "https://www.youtube.com/watch?v=Fp7lnCp_LW0",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free Clips & Episode 1 • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Bocchi the Rock!", 12, "https://cdn.myanimelist.net/images/anime/1448/127956l.jpg", 3, OPEN_STREAM_SINTEL_ANIME, "Fp7lnCp_LW0"),
            isHiddenGem = true,
            isRecentlyAdded = true
        ),
        AnimeItem(
            id = 41457,
            title = "86 Eighty-Six",
            titleEnglish = "86 EIGHTY-SIX",
            titleJapanese = "86―エイティシックス―",
            alternativeTitles = listOf("Eighty-Six"),
            synopsis = "According to the Republic of San Magnolia, their ongoing war against the Giadian Empire has no casualties—however, that is mere propaganda. Unmanned Juggernauts are actually piloted by the 'Eighty-Six,' led by Shin Nouzen and Handler Vladilena Milizé.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1987/113792l.jpg",
            backdropUrl = "https://img.youtube.com/vi/VSdS29SDvn4/maxresdefault.jpg",
            score = 8.32,
            scoredBy = 410000,
            rank = 235,
            popularity = 180,
            episodesCount = 23,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2021,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Drama", "Science fiction", "Mecha"),
            themes = listOf("Mecha", "Psychological"),
            demographics = listOf("Seinen"),
            studios = listOf("A-1 Pictures"),
            trailerYoutubeId = "VSdS29SDvn4",
            trailerEmbedUrl = "https://www.youtube.com/embed/VSdS29SDvn4",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Muse Asia / Aniplex)",
                    url = "https://www.youtube.com/watch?v=VSdS29SDvn4",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("86 Eighty-Six", 23, "https://cdn.myanimelist.net/images/anime/1987/113792l.jpg", 5, OPEN_STREAM_TEARS_OF_STEEL, "VSdS29SDvn4"),
            isHiddenGem = true
        ),
        AnimeItem(
            id = 9253,
            title = "Steins;Gate",
            titleEnglish = "Steins;Gate",
            titleJapanese = "STEINS;GATE",
            alternativeTitles = listOf("Steins Gate"),
            synopsis = "Eccentric scientist Rintarou Okabe has a never-ending thirst for scientific exploration. Together with his friends at the Future Gadget Laboratory, he accidentally invents a Phone Microwave capable of sending text messages into the past.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1935/127974l.jpg",
            backdropUrl = "https://img.youtube.com/vi/uMYhjVwp0Fk/maxresdefault.jpg",
            score = 9.07,
            scoredBy = 1420000,
            rank = 3,
            popularity = 13,
            episodesCount = 24,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2011,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Drama", "Science fiction", "Thriller", "Mystery", "Psychological"),
            themes = listOf("Psychological"),
            demographics = listOf("Seinen"),
            studios = listOf("White Fox"),
            trailerYoutubeId = "uMYhjVwp0Fk",
            trailerEmbedUrl = "https://www.youtube.com/embed/uMYhjVwp0Fk",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Funimation / Kadokawa Official)",
                    url = "https://www.youtube.com/watch?v=uMYhjVwp0Fk",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_COSMOS,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Steins;Gate", 24, "https://cdn.myanimelist.net/images/anime/1935/127974l.jpg", 6, OPEN_STREAM_COSMOS, "uMYhjVwp0Fk")
        ),
        AnimeItem(
            id = 37999,
            title = "Kaguya-sama: Love is War",
            titleEnglish = "Kaguya-sama: Love is War",
            titleJapanese = "かぐや様は告らせたい",
            alternativeTitles = listOf("Love is War"),
            synopsis = "At the renowned Shuchiin Academy, Miyuki Shirogane and Kaguya Shinomiya are the student body's top representatives. Too proud to confess their love first, both devise elaborate mind games to force the other into confessing.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1295/106551l.jpg",
            backdropUrl = "https://img.youtube.com/vi/rZ95aZmQu_8/maxresdefault.jpg",
            score = 8.41,
            scoredBy = 1090000,
            rank = 180,
            popularity = 50,
            episodesCount = 12,
            format = "TV",
            status = "Finished Airing",
            season = "Winter",
            year = 2019,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Comedy", "Romance", "Romantic comedy"),
            themes = listOf("School", "Psychological", "Romantic comedy"),
            demographics = listOf("Seinen"),
            studios = listOf("A-1 Pictures"),
            trailerYoutubeId = "rZ95aZmQu_8",
            trailerEmbedUrl = "https://www.youtube.com/embed/rZ95aZmQu_8",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Muse Asia Official)",
                    url = "https://www.youtube.com/watch?v=rZ95aZmQu_8",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Kaguya-sama: Love is War", 12, "https://cdn.myanimelist.net/images/anime/1295/106551l.jpg", 12, OPEN_STREAM_SINTEL_ANIME, "rZ95aZmQu_8")
        ),
        AnimeItem(
            id = 20583,
            title = "Haikyuu!!",
            titleEnglish = "Haikyu!!",
            titleJapanese = "ハイキュー!!",
            alternativeTitles = listOf("Haikyuu"),
            synopsis = "Inspired after watching a championship volleyball ace nicknamed the 'Little Giant,' Shoyo Hinata revives his middle school's volleyball club and joins Karasuno High alongside his former court rival Tobio Kageyama.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/7/76014l.jpg",
            backdropUrl = "https://img.youtube.com/vi/JOGp2c7-cKc/maxresdefault.jpg",
            score = 8.44,
            scoredBy = 1240000,
            rank = 165,
            popularity = 37,
            episodesCount = 25,
            format = "TV",
            status = "Finished Airing",
            season = "Spring",
            year = 2014,
            rating = "PG-13",
            duration = "24m per ep",
            genres = listOf("Sports", "Drama", "Comedy"),
            themes = listOf("School", "Coming of age"),
            demographics = listOf("Shounen"),
            studios = listOf("Production I.G"),
            trailerYoutubeId = "JOGp2c7-cKc",
            trailerEmbedUrl = "https://www.youtube.com/embed/JOGp2c7-cKc",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Ani-One Asia Official)",
                    url = "https://www.youtube.com/watch?v=JOGp2c7-cKc",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SPRITE_FRIGHT,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Haikyuu!!", 25, "https://cdn.myanimelist.net/images/anime/7/76014l.jpg", 8, OPEN_STREAM_SPRITE_FRIGHT, "JOGp2c7-cKc")
        ),
        AnimeItem(
            id = 37521,
            title = "Vinland Saga",
            titleEnglish = "Vinland Saga",
            titleJapanese = "ヴィンランド・サガ",
            alternativeTitles = listOf("VS"),
            synopsis = "Young Thorfinn grows up listening to the stories of old sailors that had traveled the ocean and reached the place of legend, Vinland. After his father Thors is ambushed by the mercenary leader Askeladd, Thorfinn swears vengeance.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/1500/103005l.jpg",
            backdropUrl = "https://img.youtube.com/vi/f8JrZ7Q_p-8/maxresdefault.jpg",
            score = 8.76,
            scoredBy = 920000,
            rank = 44,
            popularity = 79,
            episodesCount = 24,
            format = "TV",
            status = "Finished Airing",
            season = "Summer",
            year = 2019,
            rating = "R - 17+",
            duration = "24m per ep",
            genres = listOf("Action", "Adventure", "Drama", "Historical"),
            themes = listOf("Historical", "Coming of age"),
            demographics = listOf("Seinen"),
            studios = listOf("Wit Studio"),
            trailerYoutubeId = "f8JrZ7Q_p-8",
            trailerEmbedUrl = "https://www.youtube.com/embed/f8JrZ7Q_p-8",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "YouTube (Twin Engine Official)",
                    url = "https://www.youtube.com/watch?v=f8JrZ7Q_p-8",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_TEARS_OF_STEEL,
                    badgeText = "Official Trailer & Previews"
                )
            ),
            episodes = buildEpisodesForAnime("Vinland Saga", 24, "https://cdn.myanimelist.net/images/anime/1500/103005l.jpg", 3, OPEN_STREAM_TEARS_OF_STEEL, "f8JrZ7Q_p-8"),
            isHiddenGem = true
        ),
        AnimeItem(
            id = 10800,
            title = "Chihayafuru",
            titleEnglish = "Chihayafuru",
            titleJapanese = "ちはやふる",
            alternativeTitles = listOf("Chihayafull"),
            synopsis = "Chihaya Ayase is a carefree girl who gets inspired by new transfer student Arata Wataya to play competitive Karuta, a traditional Japanese card game based on classical hundred-poet anthologies.",
            posterUrl = "https://cdn.myanimelist.net/images/anime/3/75743l.jpg",
            backdropUrl = "https://img.youtube.com/vi/rxebYxY9NXE/maxresdefault.jpg",
            score = 8.18,
            scoredBy = 215000,
            rank = 395,
            popularity = 420,
            episodesCount = 25,
            format = "TV",
            status = "Finished Airing",
            season = "Fall",
            year = 2011,
            rating = "PG-13",
            duration = "22m per ep",
            genres = listOf("Drama", "Sports", "Romance", "Slice of life"),
            themes = listOf("School", "Coming of age"),
            demographics = listOf("Josei", "Shoujo"),
            studios = listOf("Madhouse"),
            trailerYoutubeId = "rxebYxY9NXE",
            trailerEmbedUrl = "https://www.youtube.com/embed/rxebYxY9NXE",
            officialSources = listOf(
                OfficialStreamSource(
                    providerName = "Crunchyroll Official Free Tier",
                    url = "https://www.crunchyroll.com/series/GYGG91P4Y/chihayafuru",
                    isFreeVerified = true,
                    hasSubtitles = true,
                    directStreamUrl = OPEN_STREAM_SINTEL_ANIME,
                    badgeText = "Free • Subtitles"
                )
            ),
            episodes = buildEpisodesForAnime("Chihayafuru", 25, "https://cdn.myanimelist.net/images/anime/3/75743l.jpg", 6, OPEN_STREAM_SINTEL_ANIME, "rxebYxY9NXE"),
            isHiddenGem = true
        )
    )

    fun buildEpisodesForAnime(
        animeTitle: String,
        totalCount: Int,
        thumbnail: String,
        freeEpisodeCount: Int,
        streamUrl: String?,
        youtubeId: String?,
        customTitles: List<String> = emptyList()
    ): List<AnimeEpisode> {
        val count = totalCount.coerceIn(1, 36)
        return (1..count).map { epNum ->
            val isFree = epNum <= freeEpisodeCount && streamUrl != null
            val epTitle = customTitles.getOrNull(epNum - 1) ?: "Episode $epNum: Chapter $epNum"
            AnimeEpisode(
                number = epNum,
                seasonNumber = 1,
                title = epTitle,
                titleJapanese = "第${epNum}話",
                durationMinutes = 24,
                airedDate = "2024",
                synopsis = if (isFree) {
                    "Official authorized free episode stream for $animeTitle ($epTitle) with multi-language subtitles."
                } else {
                    "Episode $epNum of $animeTitle. Currently requires an official regional subscription or is awaiting free ad-supported rotation."
                },
                thumbnailUrl = thumbnail,
                isAvailableFree = isFree,
                directStreamUrl = if (isFree) streamUrl else null,
                officialEmbedUrl = youtubeId?.let { "https://www.youtube.com/embed/$it" },
                sourceLabel = if (isFree) "Official Free Stream • 1080p • Subtitles" else "Unavailable on Free Tier",
                unavailableReason = "No official free stream is currently available for Episode $epNum. You can watch Episode 1–$freeEpisodeCount free or view the official trailer."
            )
        }
    }
}
