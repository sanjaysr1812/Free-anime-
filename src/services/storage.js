// AnimeFlix Persistent Local Storage Service
// Handles Watchlist, FavoriteAnime, and RecentlyWatched with timestamps & progress tracking.

const WATCHLIST_KEY = 'animeflix_watchlist';
const FAVORITES_KEY = 'animeflix_favorites';
const RECENTLY_WATCHED_KEY = 'animeflix_recently_watched';

export const storageService = {
  // --- WATCHLIST ---
  getWatchlist() {
    try {
      const data = localStorage.getItem(WATCHLIST_KEY);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  isInWatchlist(animeId) {
    const list = this.getWatchlist();
    return list.some(item => (item.mal_id || item.id) === animeId);
  },

  toggleWatchlist(anime, status = 'watching') {
    const list = this.getWatchlist();
    const id = anime.mal_id || anime.id;
    const existsIndex = list.findIndex(item => (item.mal_id || item.id) === id);

    if (existsIndex >= 0) {
      list.splice(existsIndex, 1);
      localStorage.setItem(WATCHLIST_KEY, JSON.stringify(list));
      return false; // Removed
    } else {
      list.unshift({
        ...anime,
        savedStatus: status,
        savedAt: Date.now()
      });
      localStorage.setItem(WATCHLIST_KEY, JSON.stringify(list));
      return true; // Added
    }
  },

  updateWatchlistStatus(animeId, status) {
    const list = this.getWatchlist();
    const item = list.find(it => (it.mal_id || it.id) === animeId);
    if (item) {
      item.savedStatus = status;
      localStorage.setItem(WATCHLIST_KEY, JSON.stringify(list));
    }
    return list;
  },

  // --- FAVORITES ---
  getFavorites() {
    try {
      const data = localStorage.getItem(FAVORITES_KEY);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  isFavorite(animeId) {
    const list = this.getFavorites();
    return list.some(item => (item.mal_id || item.id) === animeId);
  },

  toggleFavorite(anime) {
    const list = this.getFavorites();
    const id = anime.mal_id || anime.id;
    const existsIndex = list.findIndex(item => (item.mal_id || item.id) === id);

    if (existsIndex >= 0) {
      list.splice(existsIndex, 1);
      localStorage.setItem(FAVORITES_KEY, JSON.stringify(list));
      return false;
    } else {
      list.unshift({
        ...anime,
        favoritedAt: Date.now()
      });
      localStorage.setItem(FAVORITES_KEY, JSON.stringify(list));
      return true;
    }
  },

  // --- RECENTLY WATCHED / CONTINUE WATCHING ---
  getRecentlyWatched() {
    try {
      const data = localStorage.getItem(RECENTLY_WATCHED_KEY);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  recordWatchProgress(anime, episodeNumber = 1, progress = 45) {
    const list = this.getRecentlyWatched();
    const id = anime.mal_id || anime.id;
    const filtered = list.filter(item => (item.mal_id || item.id) !== id);

    const entry = {
      ...anime,
      lastEpisode: episodeNumber,
      progressPercent: progress,
      lastWatchedTimestamp: Date.now()
    };

    filtered.unshift(entry);
    // Keep top 20 recent
    const trimmed = filtered.slice(0, 20);
    localStorage.setItem(RECENTLY_WATCHED_KEY, JSON.stringify(trimmed));
    return trimmed;
  },

  clearAllData() {
    localStorage.removeItem(WATCHLIST_KEY);
    localStorage.removeItem(FAVORITES_KEY);
    localStorage.removeItem(RECENTLY_WATCHED_KEY);
  },

  exportBackup() {
    return JSON.stringify({
      watchlist: this.getWatchlist(),
      favorites: this.getFavorites(),
      recentlyWatched: this.getRecentlyWatched(),
      exportedAt: new Date().toISOString()
    }, null, 2);
  }
};
