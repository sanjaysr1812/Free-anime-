import React, { useRef } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { AnimeCard } from './AnimeCard';

export function AnimeCarousel({ 
  title, 
  icon, 
  animeList, 
  onSelect, 
  onPlay, 
  onToggleWatchlist, 
  isInWatchlist,
  onViewAll,
  showProgress = false
}) {
  const scrollRef = useRef(null);

  if (!animeList || animeList.length === 0) return null;

  const scroll = (direction) => {
    if (scrollRef.current) {
      const scrollAmount = direction === 'left' ? -340 : 340;
      scrollRef.current.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  };

  return (
    <div style={{ marginBottom: '2.25rem' }}>
      {/* Section Header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 1.25rem',
        marginBottom: '0.9rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          {icon && <span style={{ fontSize: '1.25rem' }}>{icon}</span>}
          <h2 style={{
            fontSize: '1.2rem',
            fontWeight: 800,
            color: '#FFFFFF',
            letterSpacing: '0.2px'
          }}>
            {title}
          </h2>
          <span style={{
            fontSize: '0.75rem',
            color: '#A855F7',
            background: 'rgba(168, 85, 247, 0.12)',
            padding: '2px 7px',
            borderRadius: '999px',
            fontWeight: 700
          }}>
            {animeList.length}
          </span>
        </div>

        {/* Scroll Controls (Desktop) & View All */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          {onViewAll && (
            <button
              onClick={onViewAll}
              style={{
                background: 'none',
                border: 'none',
                color: '#C084FC',
                fontSize: '0.8rem',
                fontWeight: 600,
                cursor: 'pointer',
                marginRight: '0.5rem'
              }}
            >
              See All &rarr;
            </button>
          )}

          <button
            onClick={() => scroll('left')}
            aria-label="Scroll left"
            style={{
              width: '30px',
              height: '30px',
              borderRadius: '50%',
              background: 'rgba(255, 255, 255, 0.08)',
              border: 'none',
              color: '#FFFFFF',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer'
            }}
            className="carousel-nav-btn"
          >
            <ChevronLeft size={18} />
          </button>

          <button
            onClick={() => scroll('right')}
            aria-label="Scroll right"
            style={{
              width: '30px',
              height: '30px',
              borderRadius: '50%',
              background: 'rgba(255, 255, 255, 0.08)',
              border: 'none',
              color: '#FFFFFF',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer'
            }}
            className="carousel-nav-btn"
          >
            <ChevronRight size={18} />
          </button>
        </div>
      </div>

      {/* Horizontally Scrollable Cards Container */}
      <div 
        ref={scrollRef}
        className="no-scrollbar"
        style={{
          display: 'flex',
          gap: '1rem',
          overflowX: 'auto',
          padding: '0 1.25rem 0.5rem',
          scrollSnapType: 'x mandatory',
          scrollBehavior: 'smooth'
        }}
      >
        {animeList.map(anime => (
          <div key={anime.mal_id || anime.id} style={{ scrollSnapAlign: 'start' }}>
            <AnimeCard
              anime={anime}
              onSelect={onSelect}
              onPlay={onPlay}
              onToggleWatchlist={onToggleWatchlist}
              isInWatchlist={isInWatchlist}
              progress={showProgress ? (anime.progressPercent || 40) : null}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
