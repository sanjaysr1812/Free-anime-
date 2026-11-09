import React, { useState, useEffect, useRef } from 'react';
import { Search as SearchIcon, X, Loader2, Sparkles } from 'lucide-react';
import { AnimeCard } from '../components/AnimeCard';
import { jikanApi } from '../services/jikanApi';
import { CURATED_ANIME } from '../data/animeCatalog';

const QUICK_TAGS = [
  "Demon Slayer", "Jujutsu Kaisen", "Frieren", "Attack on Titan",
  "Cyberpunk", "Steins;Gate", "Bocchi the Rock", "Chainsaw Man", "Action", "Romance", "Isekai"
];

export function SearchView({ onPlay, onSelect, onToggleWatchlist, isInWatchlist }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState(CURATED_ANIME);
  const [loading, setLoading] = useState(false);
  const inputRef = useRef(null);

  // Auto-focus search input
  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, []);

  // Debounced search effect
  useEffect(() => {
    if (!query.trim()) {
      setResults(CURATED_ANIME);
      setLoading(false);
      return;
    }

    setLoading(true);
    const handler = setTimeout(async () => {
      try {
        const data = await jikanApi.searchAnime(query.trim());
        setResults(data);
      } catch {
        setResults([]);
      } finally {
        setLoading(false);
      }
    }, 350);

    return () => clearTimeout(handler);
  }, [query]);

  return (
    <div style={{ padding: '1.25rem 1.25rem 6rem', maxWidth: '1280px', margin: '0 auto' }}>
      {/* Search Input Box */}
      <div style={{
        position: 'relative',
        width: '100%',
        maxWidth: '720px',
        margin: '0 auto 1.5rem'
      }}>
        <div style={{
          position: 'relative',
          display: 'flex',
          alignItems: 'center',
          backgroundColor: '#121626',
          borderRadius: '16px',
          border: '1.5px solid rgba(168, 85, 247, 0.4)',
          boxShadow: '0 8px 30px rgba(0, 0, 0, 0.4), 0 0 25px rgba(168, 85, 247, 0.15)',
          padding: '0.4rem 1rem'
        }}>
          {loading ? (
            <Loader2 size={20} color="#C084FC" style={{ animation: 'spin 1s linear infinite' }} />
          ) : (
            <SearchIcon size={20} color="#A855F7" />
          )}

          <input
            ref={inputRef}
            type="text"
            placeholder="Search anime by title, Japanese name, or genre..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            style={{
              flex: 1,
              background: 'transparent',
              border: 'none',
              outline: 'none',
              color: '#FFFFFF',
              fontSize: '1rem',
              padding: '0.6rem 0.8rem',
              fontWeight: 500
            }}
          />

          {query && (
            <button
              onClick={() => setQuery('')}
              style={{
                background: 'rgba(255, 255, 255, 0.1)',
                border: 'none',
                borderRadius: '50%',
                width: '24px',
                height: '24px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#FFFFFF',
                cursor: 'pointer'
              }}
            >
              <X size={14} />
            </button>
          )}
        </div>

        {/* Quick Search Tag Pills */}
        <div style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: '0.4rem',
          marginTop: '0.75rem',
          justifyContent: 'center'
        }}>
          {QUICK_TAGS.map(tag => (
            <button
              key={tag}
              onClick={() => setQuery(tag)}
              style={{
                background: query.toLowerCase() === tag.toLowerCase() ? '#A855F7' : 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                color: query.toLowerCase() === tag.toLowerCase() ? '#FFFFFF' : '#94A3B8',
                padding: '0.25rem 0.65rem',
                borderRadius: '999px',
                fontSize: '0.75rem',
                fontWeight: 600,
                cursor: 'pointer'
              }}
            >
              {tag}
            </button>
          ))}
        </div>
      </div>

      <style>{`
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>

      {/* Results Header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        marginBottom: '1rem'
      }}>
        <h2 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#FFFFFF' }}>
          {query ? `Results for "${query}"` : 'Recommended Titles'}
        </h2>
        <span style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
          {results.length} titles found
        </span>
      </div>

      {/* Results Grid */}
      {results.length > 0 ? (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
          gap: '1rem',
          justifyItems: 'center'
        }}>
          {results.map(anime => (
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
          border: '1px dashed rgba(255, 255, 255, 0.15)',
          maxWidth: '500px',
          margin: '2rem auto'
        }}>
          <Sparkles size={36} color="#A855F7" style={{ marginBottom: '0.75rem' }} />
          <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF', marginBottom: '0.4rem' }}>No anime found for "{query}"</h3>
          <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
            Try checking for spelling mistakes or explore our Discover genres tab.
          </p>
        </div>
      )}
    </div>
  );
}
