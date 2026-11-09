// Jikan API Service (Unofficial MyAnimeList API v4) with in-memory caching and fallback to curated data
import { CURATED_ANIME } from '../data/animeCatalog';

const BASE_URL = 'https://api.jikan.moe/v4';
const cache = new Map();

function mapJikanToAnime(item) {
  return {
    mal_id: item.mal_id,
    title: item.title,
    title_japanese: item.title_japanese || item.title,
    score: item.score || 8.0,
    rank: item.rank || 50,
    year: item.year || (item.aired?.from ? new Date(item.aired.from).getFullYear() : 2023),
    episodes: item.episodes || 12,
    status: item.status || 'Finished Airing',
    type: item.type || 'TV',
    rating: item.rating || 'PG-13',
    genres: (item.genres || []).map(g => g.name),
    image_url: item.images?.webp?.large_image_url || item.images?.jpg?.large_image_url || 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800',
    banner_url: item.images?.webp?.large_image_url || item.images?.jpg?.large_image_url || 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1600',
    synopsis: item.synopsis || 'No synopsis provided.',
    trailer_url: item.trailer?.embed_url || 'https://www.youtube.com/embed/VQGCKyvzIM4',
    stream_type: item.trailer?.embed_url ? 'trailer_official' : 'preview',
    official_source_name: 'Crunchyroll & Authorized Distributers',
    official_stream_url: item.url || 'https://myanimelist.net',
    free_legal_stream_available: Boolean(item.trailer?.embed_url),
    free_stream_embed: item.trailer?.embed_url || 'https://www.youtube.com/embed/VQGCKyvzIM4',
    episodes_list: Array.from({ length: Math.min(item.episodes || 4, 12) }, (_, i) => ({
      id: i + 1,
      title: `Episode ${i + 1}`,
      duration: '24m',
      stream_url: item.trailer?.embed_url || 'https://www.youtube.com/embed/VQGCKyvzIM4',
      is_free: i < 2
    }))
  };
}

export const jikanApi = {
  async searchAnime(query, limit = 15) {
    if (!query || query.trim().length === 0) {
      return CURATED_ANIME;
    }

    const cacheKey = `search_${query.toLowerCase()}_${limit}`;
    if (cache.has(cacheKey)) {
      return cache.get(cacheKey);
    }

    try {
      const response = await fetch(`${BASE_URL}/anime?q=${encodeURIComponent(query)}&limit=${limit}&sfw=true`);
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const data = await response.json();
      const results = (data.data || []).map(mapJikanToAnime);
      cache.set(cacheKey, results);
      return results;
    } catch (err) {
      console.warn('Jikan API search fallback to local catalogue:', err);
      // Fallback to local catalog filtering
      const filtered = CURATED_ANIME.filter(anime =>
        anime.title.toLowerCase().includes(query.toLowerCase()) ||
        (anime.synopsis && anime.synopsis.toLowerCase().includes(query.toLowerCase())) ||
        (anime.genres && anime.genres.some(g => g.toLowerCase().includes(query.toLowerCase())))
      );
      return filtered.length > 0 ? filtered : CURATED_ANIME.slice(0, 6);
    }
  },

  async getTopAnime(limit = 10) {
    const cacheKey = `top_${limit}`;
    if (cache.has(cacheKey)) return cache.get(cacheKey);

    try {
      const response = await fetch(`${BASE_URL}/top/anime?limit=${limit}&filter=bypopularity`);
      if (!response.ok) throw new Error('Network error');
      const data = await response.json();
      const results = (data.data || []).map(mapJikanToAnime);
      cache.set(cacheKey, results);
      return results;
    } catch {
      return CURATED_ANIME;
    }
  }
};
