import React, { useState, useEffect } from 'react';
import { Play, Plus, Check, Info, Star, ChevronLeft, ChevronRight, ShieldCheck } from 'lucide-react';

export function HeroBanner({ animeList, onPlay, onSelect, onToggleWatchlist, isInWatchlist }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  // Auto-advance hero slides every 7 seconds
  useEffect(() => {
    if (!animeList || animeList.length === 0) return;
    const timer = setInterval(() => {
      setCurrentIndex(prev => (prev + 1) % animeList.length);
    }, 7000);
    return () => clearInterval(timer);
  }, [animeList]);

  if (!animeList || animeList.length === 0) return null;
  const current = animeList[currentIndex];
  const inWatchlist = isInWatchlist(current.mal_id);

  const prevSlide = () => {
    setCurrentIndex(prev => (prev - 1 + animeList.length) % animeList.length);
  };

  const nextSlide = () => {
    setCurrentIndex(prev => (prev + 1) % animeList.length);
  };

  return (
    <div style={{
      position: 'relative',
      width: '100%',
      minHeight: '460px',
      maxHeight: '560px',
      overflow: 'hidden',
      display: 'flex',
      alignItems: 'flex-end',
      padding: '2rem 1.5rem 3.5rem',
      backgroundColor: '#090A10'
    }}>
      {/* Background Backdrop Image */}
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundImage: `url(${current.banner_url || current.image_url})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center 25%',
        filter: 'brightness(0.65)',
        transition: 'all 0.8s ease-in-out',
        transform: 'scale(1.02)'
      }} />

      {/* Cinematic Vignette Gradients */}
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        background: 'linear-gradient(to top, #090A10 5%, rgba(9, 10, 16, 0.75) 45%, rgba(9, 10, 16, 0.25) 80%, rgba(9, 10, 16, 0.7) 100%), linear-gradient(to right, #090A10 10%, rgba(9, 10, 16, 0.8) 40%, transparent 80%)'
      }} />

      {/* Slide Navigation Arrows */}
      <button 
        onClick={prevSlide}
        aria-label="Previous Featured Anime"
        style={{
          position: 'absolute',
          left: '1rem',
          top: '50%',
          transform: 'translateY(-50%)',
          width: '42px',
          height: '42px',
          borderRadius: '50%',
          background: 'rgba(0, 0, 0, 0.4)',
          backdropFilter: 'blur(8px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          color: '#FFFFFF',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          zIndex: 10
        }}
      >
        <ChevronLeft size={22} />
      </button>

      <button 
        onClick={nextSlide}
        aria-label="Next Featured Anime"
        style={{
          position: 'absolute',
          right: '1rem',
          top: '50%',
          transform: 'translateY(-50%)',
          width: '42px',
          height: '42px',
          borderRadius: '50%',
          background: 'rgba(0, 0, 0, 0.4)',
          backdropFilter: 'blur(8px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          color: '#FFFFFF',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          zIndex: 10
        }}
      >
        <ChevronRight size={22} />
      </button>

      {/* Hero Content */}
      <div style={{
        position: 'relative',
        zIndex: 10,
        maxWidth: '680px',
        animation: 'fadeIn 0.6s ease'
      }}>
        {/* Meta badges */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', flexWrap: 'wrap' }}>
          <span style={{
            background: 'rgba(168, 85, 247, 0.25)',
            border: '1px solid #A855F7',
            color: '#E9D5FF',
            padding: '0.2rem 0.6rem',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 800,
            textTransform: 'uppercase',
            letterSpacing: '0.5px'
          }}>
            FEATURED
          </span>

          <span style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem',
            background: 'rgba(234, 179, 8, 0.15)',
            border: '1px solid rgba(234, 179, 8, 0.4)',
            color: '#FDE047',
            padding: '0.2rem 0.55rem',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 700
          }}>
            <Star size={12} fill="#FDE047" color="#FDE047" />
            {current.score}
          </span>

          <span style={{
            background: 'rgba(255, 255, 255, 0.1)',
            color: '#CBD5E1',
            padding: '0.2rem 0.5rem',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 600
          }}>
            {current.year}
          </span>

          <span style={{
            background: 'rgba(16, 185, 129, 0.15)',
            border: '1px solid rgba(16, 185, 129, 0.3)',
            color: '#6EE7B7',
            padding: '0.2rem 0.5rem',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            gap: '0.25rem'
          }}>
            <ShieldCheck size={12} />
            Official Free Stream
          </span>
        </div>

        {/* Title */}
        <h1 style={{
          fontSize: 'clamp(1.8rem, 4vw, 2.8rem)',
          fontWeight: 900,
          color: '#FFFFFF',
          lineHeight: 1.15,
          marginBottom: '0.4rem',
          textShadow: '0 2px 10px rgba(0, 0, 0, 0.8)'
        }}>
          {current.title}
        </h1>

        {/* Japanese Title */}
        {current.title_japanese && (
          <p style={{
            fontSize: '0.9rem',
            color: '#C084FC',
            fontWeight: 600,
            marginBottom: '0.8rem',
            letterSpacing: '0.5px'
          }}>
            {current.title_japanese}
          </p>
        )}

        {/* Synopsis snippet */}
        <p style={{
          fontSize: '0.92rem',
          color: '#CBD5E1',
          lineHeight: 1.5,
          marginBottom: '1.25rem',
          display: '-webkit-box',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden',
          textShadow: '0 1px 4px rgba(0,0,0,0.7)'
        }}>
          {current.synopsis}
        </p>

        {/* Action Buttons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
          <button
            onClick={() => onPlay(current)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              background: 'linear-gradient(135deg, #9333EA 0%, #7C3AED 100%)',
              border: 'none',
              borderRadius: '10px',
              color: '#FFFFFF',
              padding: '0.75rem 1.4rem',
              fontWeight: 700,
              fontSize: '0.95rem',
              cursor: 'pointer',
              boxShadow: '0 4px 20px rgba(147, 51, 234, 0.45)',
              transition: 'all 0.2s ease'
            }}
          >
            <Play size={18} fill="#FFFFFF" color="#FFFFFF" />
            Watch Now
          </button>

          <button
            onClick={() => onToggleWatchlist(current)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              background: inWatchlist ? 'rgba(16, 185, 129, 0.2)' : 'rgba(255, 255, 255, 0.1)',
              border: `1px solid ${inWatchlist ? '#10B981' : 'rgba(255, 255, 255, 0.2)'}`,
              borderRadius: '10px',
              color: inWatchlist ? '#6EE7B7' : '#FFFFFF',
              padding: '0.75rem 1.2rem',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer',
              backdropFilter: 'blur(8px)',
              transition: 'all 0.2s ease'
            }}
          >
            {inWatchlist ? <Check size={18} /> : <Plus size={18} />}
            {inWatchlist ? 'In Watchlist' : 'Watchlist'}
          </button>

          <button
            onClick={() => onSelect(current)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(255, 255, 255, 0.12)',
              borderRadius: '10px',
              color: '#94A3B8',
              padding: '0.75rem 1rem',
              fontWeight: 500,
              fontSize: '0.9rem',
              cursor: 'pointer',
              backdropFilter: 'blur(8px)'
            }}
          >
            <Info size={18} />
            Details
          </button>
        </div>
      </div>

      {/* Pagination Dots */}
      <div style={{
        position: 'absolute',
        bottom: '1rem',
        right: '1.5rem',
        display: 'flex',
        gap: '0.4rem',
        zIndex: 10
      }}>
        {animeList.map((_, idx) => (
          <div
            key={idx}
            onClick={() => setCurrentIndex(idx)}
            style={{
              width: idx === currentIndex ? '24px' : '8px',
              height: '8px',
              borderRadius: '4px',
              background: idx === currentIndex ? '#A855F7' : 'rgba(255, 255, 255, 0.25)',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
          />
        ))}
      </div>
    </div>
  );
}
