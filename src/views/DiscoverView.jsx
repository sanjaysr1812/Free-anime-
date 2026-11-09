import React, { useState, useMemo } from 'react';
import { GENRES_LIST, CURATED_ANIME } from '../data/animeCatalog';
import { AnimeCard } from '../components/AnimeCard';
import { Filter, SlidersHorizontal, Check, RefreshCw } from 'lucide-react';

export function DiscoverView({ onPlay, onSelect, onToggleWatchlist, isInWatchlist, preselectedGenre = null }) {
  const [selectedGenres, setSelectedGenres] = useState(preselectedGenre ? [preselectedGenre] : []);
  const [selectedFormat, setSelectedFormat] = useState('ALL');
  const [sortBy, setSortBy] = useState('score'); // 'score', 'year', 'episodes'

  const toggleGenre = (genre) => {
    setSelectedGenres(prev => 
      prev.includes(genre)
        ? prev.filter(g => g !== genre)
        : [...prev, genre]
    );
  };

  const clearFilters = () => {
    setSelectedGenres([]);
    setSelectedFormat('ALL');
    setSortBy('score');
  };

  const filteredAnime = useMemo(() => {
    return CURATED_ANIME.filter(anime => {
      // Genre filter (must include all selected genres)
      if (selectedGenres.length > 0) {
        const hasAllGenres = selectedGenres.every(sg => 
          (anime.genres || []).some(g => g.toLowerCase() === sg.toLowerCase())
        );
        if (!hasAllGenres) return false;
      }

      // Format filter
      if (selectedFormat !== 'ALL' && anime.type !== selectedFormat) {
        return false;
      }

      return true;
    }).sort((a, b) => {
      if (sortBy === 'score') return (b.score || 0) - (a.score || 0);
      if (sortBy === 'year') return (b.year || 0) - (a.year || 0);
      if (sortBy === 'episodes') return (b.episodes || 0) - (a.episodes || 0);
      return 0;
    });
  }, [selectedGenres, selectedFormat, sortBy]);

  return (
    <div style={{ padding: '1.25rem 1.25rem 6rem', maxWidth: '1280px', margin: '0 auto' }}>
      {/* Title & Filter Header */}
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
        marginBottom: '1.5rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 style={{
              fontSize: '1.75rem',
              fontWeight: 900,
              color: '#FFFFFF',
              letterSpacing: '-0.5px',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem'
            }}>
              Discover & Genres
            </h1>
            <p style={{ fontSize: '0.88rem', color: '#94A3B8' }}>
              Explore across 26 official anime categories with multi-genre combinations
            </p>
          </div>

          {(selectedGenres.length > 0 || selectedFormat !== 'ALL' || sortBy !== 'score') && (
            <button
              onClick={clearFilters}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: 'rgba(239, 68, 68, 0.15)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                color: '#F87171',
                padding: '0.4rem 0.8rem',
                borderRadius: '8px',
                fontSize: '0.8rem',
                fontWeight: 600,
                cursor: 'pointer'
              }}
            >
              <RefreshCw size={13} />
              Reset Filters
            </button>
          )}
        </div>

        {/* 26 Genres Pill Selector */}
        <div style={{
          backgroundColor: '#0E1322',
          border: '1px solid rgba(255, 255, 255, 0.08)',
          borderRadius: '16px',
          padding: '1rem',
          marginTop: '0.75rem'
        }}>
          <div style={{
            fontSize: '0.8rem',
            fontWeight: 700,
            color: '#CBD5E1',
            textTransform: 'uppercase',
            letterSpacing: '0.5px',
            marginBottom: '0.75rem',
            display: 'flex',
            alignItems: 'center',
            gap: '0.4rem'
          }}>
            <Filter size={14} color="#A855F7" />
            <span>Select Multiple Genres ({selectedGenres.length} active)</span>
          </div>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
            {GENRES_LIST.map(genre => {
              const isSelected = selectedGenres.includes(genre);
              return (
                <button
                  key={genre}
                  onClick={() => toggleGenre(genre)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.3rem',
                    padding: '0.35rem 0.75rem',
                    borderRadius: '999px',
                    fontSize: '0.78rem',
                    fontWeight: isSelected ? 700 : 500,
                    backgroundColor: isSelected ? 'rgba(168, 85, 247, 0.25)' : 'rgba(255, 255, 255, 0.04)',
                    border: `1px solid ${isSelected ? '#A855F7' : 'rgba(255, 255, 255, 0.08)'}`,
                    color: isSelected ? '#E9D5FF' : '#94A3B8',
                    cursor: 'pointer',
                    transition: 'all 0.15s ease'
                  }}
                >
                  {isSelected && <Check size={12} />}
                  {genre}
                </button>
              );
            })}
          </div>
        </div>

        {/* Format and Sort Bar */}
        <div style={{
          display: 'flex',
          gap: '1rem',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginTop: '0.5rem'
        }}>
          {/* Format Tabs */}
          <div style={{ display: 'flex', gap: '0.4rem' }}>
            {['ALL', 'TV', 'ONA'].map(fmt => (
              <button
                key={fmt}
                onClick={() => setSelectedFormat(fmt)}
                style={{
                  padding: '0.4rem 0.8rem',
                  borderRadius: '8px',
                  border: 'none',
                  background: selectedFormat === fmt ? '#A855F7' : 'rgba(255, 255, 255, 0.06)',
                  color: '#FFFFFF',
                  fontSize: '0.78rem',
                  fontWeight: selectedFormat === fmt ? 700 : 500,
                  cursor: 'pointer'
                }}
              >
                {fmt === 'ALL' ? 'All Formats' : fmt}
              </button>
            ))}
          </div>

          {/* Sort Selector */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Sort By:</span>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              style={{
                backgroundColor: '#121626',
                color: '#FFFFFF',
                border: '1px solid rgba(255, 255, 255, 0.15)',
                padding: '0.4rem 0.8rem',
                borderRadius: '8px',
                fontSize: '0.8rem',
                cursor: 'pointer'
              }}
            >
              <option value="score">Highest Rating</option>
              <option value="year">Release Year</option>
              <option value="episodes">Episode Count</option>
            </select>
          </div>
        </div>
      </div>

      {/* Results Count Banner */}
      <div style={{ fontSize: '0.85rem', color: '#94A3B8', marginBottom: '1rem' }}>
        Found <strong style={{ color: '#F8FAFC' }}>{filteredAnime.length}</strong> matching titles
      </div>

      {/* Grid of Anime Cards */}
      {filteredAnime.length > 0 ? (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
          gap: '1rem',
          justifyItems: 'center'
        }}>
          {filteredAnime.map(anime => (
            <AnimeCard
              key={anime.mal_id}
              anime={anime}
              onSelect={onSelect}
              onPlay={onPlay}
              onToggleWatchlist={onToggleWatchlist}
              isInWatchlist={isInWatchlist}
            />
          ))}
        </div>
      ) : (
        <div style={{
          textAlign: 'center',
          padding: '4rem 1.5rem',
          backgroundColor: '#0E1322',
          borderRadius: '16px',
          border: '1px dashed rgba(255, 255, 255, 0.15)'
        }}>
          <Filter size={36} color="#A855F7" style={{ marginBottom: '0.75rem', opacity: 0.8 }} />
          <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF', marginBottom: '0.4rem' }}>No anime found for selected filters</h3>
          <p style={{ fontSize: '0.85rem', color: '#94A3B8', marginBottom: '1rem' }}>
            Try deselecting one or more genres to broaden your search.
          </p>
          <button
            onClick={clearFilters}
            style={{
              background: '#A855F7',
              color: '#FFFFFF',
              border: 'none',
              padding: '0.5rem 1.2rem',
              borderRadius: '8px',
              fontWeight: 700,
              fontSize: '0.85rem',
              cursor: 'pointer'
            }}
          >
            Clear All Filters
          </button>
        </div>
      )}
    </div>
  );
}
