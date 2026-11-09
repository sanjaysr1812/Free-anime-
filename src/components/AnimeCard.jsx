import React from 'react';
import { Play, Star, Plus, Check } from 'lucide-react';

export function AnimeCard({ 
  anime, 
  onSelect, 
  onPlay, 
  onToggleWatchlist, 
  isInWatchlist,
  progress = null
}) {
  const inWatchlist = isInWatchlist ? isInWatchlist(anime.mal_id) : false;

  return (
    <div 
      style={{
        position: 'relative',
        width: '160px',
        minWidth: '160px',
        flexShrink: 0,
        borderRadius: '12px',
        overflow: 'hidden',
        backgroundColor: '#121626',
        border: '1px solid rgba(255, 255, 255, 0.08)',
        cursor: 'pointer',
        transition: 'transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease',
        userSelect: 'none'
      }}
      className="anime-card"
      onClick={() => onSelect(anime)}
    >
      {/* Poster Image Container */}
      <div style={{
        position: 'relative',
        width: '100%',
        paddingTop: '145%', // 2:3 aspect ratio
        overflow: 'hidden',
        backgroundColor: '#1A2035'
      }}>
        <img
          src={anime.image_url}
          alt={anime.title}
          loading="lazy"
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            transition: 'transform 0.4s ease'
          }}
          className="poster-img"
        />

        {/* Gradient shadow overlay */}
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'linear-gradient(to top, rgba(9, 10, 16, 0.95) 0%, rgba(9, 10, 16, 0.2) 50%, transparent 100%)'
        }} />

        {/* Top Badges */}
        <div style={{
          position: 'absolute',
          top: '8px',
          left: '8px',
          right: '8px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          zIndex: 2
        }}>
          {/* Score Badge */}
          <span style={{
            display: 'flex',
            alignItems: 'center',
            gap: '3px',
            background: 'rgba(0, 0, 0, 0.65)',
            backdropFilter: 'blur(6px)',
            color: '#FDE047',
            padding: '2px 6px',
            borderRadius: '6px',
            fontSize: '0.72rem',
            fontWeight: 700,
            border: '1px solid rgba(253, 224, 71, 0.2)'
          }}>
            <Star size={11} fill="#FDE047" color="#FDE047" />
            {anime.score || 8.0}
          </span>

          {/* Quick Watchlist Button */}
          {onToggleWatchlist && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                onToggleWatchlist(anime);
              }}
              title={inWatchlist ? "Remove from watchlist" : "Add to watchlist"}
              style={{
                width: '26px',
                height: '26px',
                borderRadius: '50%',
                background: inWatchlist ? '#10B981' : 'rgba(0, 0, 0, 0.6)',
                backdropFilter: 'blur(6px)',
                border: 'none',
                color: '#FFFFFF',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
            >
              {inWatchlist ? <Check size={14} /> : <Plus size={14} />}
            </button>
          )}
        </div>

        {/* Hover / Direct Play Button */}
        <div 
          onClick={(e) => {
            e.stopPropagation();
            onPlay(anime);
          }}
          className="play-overlay-button"
          style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            width: '44px',
            height: '44px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #A855F7 0%, #6366F1 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 20px rgba(168, 85, 247, 0.7)',
            opacity: 0,
            transition: 'opacity 0.2s ease, transform 0.2s ease',
            zIndex: 3
          }}
        >
          <Play size={20} fill="#FFFFFF" color="#FFFFFF" style={{ marginLeft: '3px' }} />
        </div>

        {/* Progress bar if present */}
        {progress !== null && (
          <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '4px',
            backgroundColor: 'rgba(255, 255, 255, 0.2)',
            zIndex: 4
          }}>
            <div style={{
              height: '100%',
              width: `${Math.min(100, Math.max(0, progress))}%`,
              background: 'linear-gradient(90deg, #A855F7, #EC4899)'
            }} />
          </div>
        )}
      </div>

      {/* Info Section */}
      <div style={{ padding: '0.6rem 0.65rem 0.75rem' }}>
        <h4 style={{
          fontSize: '0.85rem',
          fontWeight: 700,
          color: '#F8FAFC',
          lineHeight: 1.25,
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          marginBottom: '0.3rem'
        }}>
          {anime.title}
        </h4>

        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          fontSize: '0.72rem',
          color: '#94A3B8'
        }}>
          <span>{anime.year || '2023'}</span>
          <span style={{
            background: 'rgba(255, 255, 255, 0.08)',
            padding: '1px 5px',
            borderRadius: '4px',
            fontSize: '0.65rem',
            color: '#CBD5E1',
            fontWeight: 600
          }}>
            {anime.episodes ? `${anime.episodes} eps` : 'TV'}
          </span>
        </div>
      </div>

      <style>{`
        .anime-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 12px 25px -4px rgba(168, 85, 247, 0.25);
          border-color: rgba(168, 85, 247, 0.5) !important;
        }
        .anime-card:hover .poster-img {
          transform: scale(1.06);
        }
        .anime-card:hover .play-overlay-button {
          opacity: 1 !important;
          transform: translate(-50%, -50%) scale(1.08) !important;
        }
      `}</style>
    </div>
  );
}
