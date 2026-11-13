# AnimeFlix — Legal Anime Discovery & Streaming Platform

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Metadata-Jikan%20API-2E51A2" alt="Jikan API" />
</p>


AnimeFlix is a mobile-first, premium-quality anime discovery and authorized free-streaming application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, **Room SQLite**, **DataStore Preferences**, **Coil**, and the **Jikan v4 REST API** (MyAnimeList open metadata).

## Key Features
1. **Premium Midnight-Glass UI/UX**: Deep navy/obsidian dark interface with violet/purple accents, custom bundled Google Fonts (`Outfit` and `Plus Jakarta Sans`), smooth crossfade transitions, shimmer placeholders, and adaptive layouts (floating glass bottom navigation on phones, side `NavigationRail` on tablets/foldables).
2. **Rich Home Screen (12 Sections)**:
   - Cinematic Featured Anime Hero Carousel
   - Continue Watching (with local playback progress bar)
   - Trending Now
   - Popular This Season
   - New & Verified Free Episodes
   - Random Anime Discovery & Hidden Gems Spotlight
   - Hidden Gems & Underrated Anime
   - Recommended For You (dynamically tailored to your Watchlist genres)
   - My Watchlist
   - Recently Added Titles
   - Most Popular of All Time
   - Recently Watched
3. **26-Genre Discovery & Multi-Genre Combiner**:
   - Combine multiple genres (`+ Include`) and exclude unwanted genres (`✕ Exclude`) across all 26 genres, themes, and demographics: *Action, Adventure, Romance, Romantic comedy, Comedy, Drama, Psychological, Mystery, Thriller, Fantasy, Dark fantasy, Supernatural, Science fiction, Slice of life, School, Coming of age, Sports, Music, Historical, Horror, Mecha, Isekai, Shounen, Seinen, Shoujo, Josei*.
   - Sort by Popularity, Highest Rating, Release Year, or Title, and filter by Year, Season, Status, Format, Verified Free Streams, and Hidden Gems.
4. **Instant Debounced Search**:
   - 320ms debounced search across English titles, Japanese titles, synonyms, synopses, and genre tags, with instant autocomplete suggestions and persistent Recent Searches.
5. **Legal & Authorized Streaming Only**:
   - Links exclusively to official authorized free distributors (YouTube Official Channels such as Muse Asia, Ani-One Asia, TOHO Animation, Crunchyroll Official Free Tier) and open-licensed Creative Commons / Public Domain video streams.
   - Displays a clear **"No official free stream is currently available"** notice when an episode does not have a verified free stream—never using pirated links or fake playback buttons.
6. **Personal Library (Zero Registration Required)**:
   - Persistent local storage via **Room SQLite** & **DataStore Preferences** for Watchlist, Favorites, Continue Watching, Watched History, Custom Lists, and Appearance/Accessibility settings (`Glass Default`, `Deep OLED Black`, `Midnight Nebula`, and `Reduced Motion`).

## Running, Testing & Installing
- **Build & Run (Android APK)**:
  ```bash
  gradle assembleDebug
  ```
- **Run Local JVM & Robolectric Tests**:
  ```bash
  gradle :app:testDebugUnitTest
  ```
- **API & Rate Limits**:
  - Uses the free **Jikan v4 API** (`https://api.jikan.moe/v4/`) which requires no API key. Jikan enforces a rate limit of ~3 requests/second and 60 requests/minute. AnimeFlix automatically caches metadata in **Room SQLite** (`cached_anime`) so the app continues working smoothly offline or when rate-limited.
- **Installing on Android Home Screen**:
  - As a native Android APK built in AI Studio, you can export and install the signed APK directly onto any Android 7.0+ device.
  - When accessing a web/PWA deployment on Chrome for Android: tap the browser's **⋮ (More)** menu and select **Add to Home screen** / **Install app** to launch in standalone display mode.


## Contributing
Contributions are welcome! Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

### Good first issue
- [Add unit tests for combined genre filters](https://github.com/sanjaysr1812/Free-anime-/issues/1)
