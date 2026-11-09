import React, { useState } from 'react';
import { HeroBanner } from '../components/HeroBanner';
import { AnimeCarousel } from '../components/AnimeCarousel';
import { CURATED_ANIME, CATEGORIES_NAV } from '../data/animeCatalog';

export function HomeView({ 
  onPlay, 
  onSelect, 
  onToggleWatchlist, 
  isInWatchlist,
  watchlist,
  recentlyWatched,
  onGenreSelect
}) {
  const [selectedGenre, setSelectedGenre] = useState(null);

  const featured = CURATED_ANIME.filter(a => a.featured);
  const trending = CURATED_ANIME.filter(a => a.trending);
  const popularSeason = CURATED_ANIME.filter(a => a.popular_season);
  const allTime = CURATED_ANIME.filter(a => a.all_time_popular || a.score >= 8.8);
  const hiddenGems = CURATED_ANIME.filter(a => a.hidden_gem || (a.score >= 8.5 && a.rank > 6));

  const genreSpotlightList = selectedGenre
    ? CURATED_ANIME.filter(a => {
        const target = selectedGenre.toLowerCase();
        return (a.genres || []).some(g => g.toLowerCase().includes(target));
      })
    : [];

  const handleChipClick = (cat) => {
    if (cat.id === 'all') {
      setSelectedGenre(null);
      return;
    }
    const cleanGenre = cat.label.replace(/[^a-zA-Z -]/g, '').trim();
    setSelectedGenre(prev => prev === cleanGenre ? null : cleanGenre);
  };

  return (
    <div style={{ paddingBottom: '5rem' }}>
      {/* Cinematic Hero Banner */}
      <HeroBanner
        animeList={featured.length > 0 ? featured : CURATED_ANIME.slice(0, 4)}
        onPlay={onPlay}
        onSelect={onSelect}
        onToggleWatchlist={onToggleWatchlist}
        isInWatchlist={isInWatchlist}
      />

      {/* Horizontal Genre Filter Chips Bar */}
      <div 
        className="no-scrollbar"
        style={{
          display: 'flex',
          gap: '0.6rem',
          padding: '1.25rem 1.25rem 0.5rem',
          overflowX: 'auto',
          alignItems: 'center'
        }}
      >
        {CATEGORIES_NAV.map(cat => {
          const cleanGenre = cat.id === 'all' ? null : cat.label.replace(/[^a-zA-Z -]/g, '').trim();
          const isSelected = (cat.id === 'all' && selectedGenre === null) || (selectedGenre === cleanGenre);
          return (
            <button
              key={cat.id}
              onClick={() => handleChipClick(cat)}
              style={{
                padding: '0.45rem 1rem',
                borderRadius: '999px',
                backgroundColor: isSelected ? '#8B5CF6' : 'rgba(255, 255, 255, 0.05)',
                border: isSelected ? '1px solid #A78BFA' : '1px solid rgba(255, 255, 255, 0.12)',
                color: isSelected ? '#FFFFFF' : '#CBD5E1',
                fontSize: '0.82rem',
                fontWeight: isSelected ? 700 : 500,
                cursor: 'pointer',
                whiteSpace: 'nowrap',
                transition: 'all 0.2s ease',
                boxShadow: isSelected ? '0 2px 10px rgba(139, 92, 246, 0.4)' : 'none'
              }}
              className="category-pill"
            >
              {isSelected && cat.id !== 'all' ? `✓ ${cat.label}` : cat.label}
            </button>
          );
        })}
      </div>

      <style>{`
        .category-pill:hover {
          background-color: rgba(168, 85, 247, 0.25) !important;
          border-color: #A855F7 !important;
          color: #E9D5FF !important;
        }
      `}</style>

      {/* Instant Filtered Genre Spotlight */}
      {selectedGenre && (
        <AnimeCarousel
          title={`${selectedGenre} Spotlight`}
          icon="✨"
          animeList={genreSpotlightList.length > 0 ? genreSpotlightList : CURATED_ANIME.slice(0, 4)}
          onSelect={onSelect}
          onPlay={onPlay}
          onToggleWatchlist={onToggleWatchlist}
          isInWatchlist={isInWatchlist}
        />
      )}

      {/* Continue Watching / Recently Watched Section */}
      {recentlyWatched && recentlyWatched.length > 0 && (
        <AnimeCarousel
          title="Continue Watching"
          icon="🕒"
          animeList={recentlyWatched}
          onSelect={onSelect}
          onPlay={onPlay}
          onToggleWatchlist={onToggleWatchlist}
          isInWatchlist={isInWatchlist}
          showProgress={true}
        />
      )}

      {/* Trending Anime Carousel */}
      <AnimeCarousel
        title="Trending Anime"
        icon="🔥"
        animeList={trending}
        onSelect={onSelect}
        onPlay={onPlay}
        onToggleWatchlist={onToggleWatchlist}
        isInWatchlist={isInWatchlist}
      />

      {/* Popular This Season */}
      <AnimeCarousel
        title="Popular This Season"
        icon="⚡"
        animeList={popularSeason.length > 0 ? popularSeason : CURATED_ANIME.slice(3, 8)}
        onSelect={onSelect}
        onPlay={onPlay}
        onToggleWatchlist={onToggleWatchlist}
        isInWatchlist={isInWatchlist}
      />

      {/* My Watchlist Carousel (if user has items) */}
      {watchlist && watchlist.length > 0 && (
        <AnimeCarousel
          title="My Watchlist"
          icon="🔖"
          animeList={watchlist}
          onSelect={onSelect}
          onPlay={onPlay}
          onToggleWatchlist={onToggleWatchlist}
          isInWatchlist={isInWatchlist}
        />
      )}

      {/* Top Rated of All Time */}
      <AnimeCarousel
        title="All-Time Masterpieces"
        icon="🏆"
        animeList={allTime}
        onSelect={onSelect}
        onPlay={onPlay}
        onToggleWatchlist={onToggleWatchlist}
        isInWatchlist={isInWatchlist}
      />

      {/* Hidden Gems and Underrated */}
      <AnimeCarousel
        title="Hidden Gems & Underrated"
        icon="💎"
        animeList={hiddenGems}
        onSelect={onSelect}
        onPlay={onPlay}
        onToggleWatchlist={onToggleWatchlist}
        isInWatchlist={isInWatchlist}
      />
    </div>
  );
}
