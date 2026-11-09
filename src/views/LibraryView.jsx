import React, { useState } from 'react';
import { Bookmark, Heart, Clock, Download, Trash2, Film, CheckCircle2 } from 'lucide-react';
import { AnimeCard } from '../components/AnimeCard';
import { storageService } from '../services/storage';

export function LibraryView({ 
  onPlay, 
  onSelect, 
  onToggleWatchlist, 
  isInWatchlist,
  watchlist,
  favorites,
  recentlyWatched,
  onLibraryUpdated
}) {
  const [activeTab, setActiveTab] = useState('watchlist'); // 'watchlist', 'favorites', 'history'
  const [watchlistFilter, setWatchlistFilter] = useState('all');

  const filteredWatchlist = watchlist.filter(item => {
    if (watchlistFilter === 'all') return true;
    return (item.savedStatus || 'watching') === watchlistFilter;
  });

  const handleExport = () => {
    const data = storageService.exportBackup();
    const blob = new Blob([data], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `animeflix_library_backup_${new Date().toISOString().slice(0, 10)}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleClearAll = () => {
    if (window.confirm("Are you sure you want to clear your local library (watchlist, favorites, and history)?")) {
      storageService.clearAllData();
      if (onLibraryUpdated) onLibraryUpdated();
    }
  };

  return (
    <div style={{ padding: '1.25rem 1.25rem 6rem', maxWidth: '1280px', margin: '0 auto' }}>
      {/* Header & Backup Actions */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: '1rem',
        marginBottom: '1.5rem'
      }}>
        <div>
          <h1 style={{
            fontSize: '1.75rem',
            fontWeight: 900,
            color: '#FFFFFF',
            letterSpacing: '-0.5px'
          }}>
            My Personal Library
          </h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginTop: '0.25rem', flexWrap: 'wrap' }}>
            <p style={{ fontSize: '0.88rem', color: '#94A3B8' }}>
              Saved locally in your device storage. No login or backend server required.
            </p>
            <span style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.35rem',
              backgroundColor: 'rgba(16, 185, 129, 0.15)',
              border: '1px solid rgba(16, 185, 129, 0.35)',
              color: '#34D399',
              padding: '2px 8px',
              borderRadius: '999px',
              fontSize: '0.72rem',
              fontWeight: 700
            }}>
              <CheckCircle2 size={12} />
              Offline Cached
            </span>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={handleExport}
            title="Download JSON backup"
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              backgroundColor: 'rgba(255, 255, 255, 0.08)',
              border: '1px solid rgba(255, 255, 255, 0.15)',
              color: '#CBD5E1',
              padding: '0.45rem 0.8rem',
              borderRadius: '8px',
              fontSize: '0.8rem',
              fontWeight: 600,
              cursor: 'pointer'
            }}
          >
            <Download size={14} />
            Export Backup
          </button>

          <button
            onClick={handleClearAll}
            title="Clear all saved data"
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              backgroundColor: 'rgba(239, 68, 68, 0.1)',
              border: '1px solid rgba(239, 68, 68, 0.25)',
              color: '#F87171',
              padding: '0.45rem 0.75rem',
              borderRadius: '8px',
              fontSize: '0.8rem',
              fontWeight: 600,
              cursor: 'pointer'
            }}
          >
            <Trash2 size={14} />
          </button>
        </div>
      </div>

      {/* Main Tabs */}
      <div style={{
        display: 'flex',
        gap: '0.6rem',
        borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
        marginBottom: '1.25rem',
        paddingBottom: '0.5rem'
      }}>
        {[
          { id: 'watchlist', label: `Watchlist (${watchlist.length})`, icon: Bookmark },
          { id: 'favorites', label: `Favorites (${favorites.length})`, icon: Heart },
          { id: 'history', label: `History & Progress (${recentlyWatched.length})`, icon: Clock },
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: isActive ? 'rgba(168, 85, 247, 0.2)' : 'transparent',
                border: `1px solid ${isActive ? '#A855F7' : 'transparent'}`,
                color: isActive ? '#E9D5FF' : '#94A3B8',
                padding: '0.5rem 1rem',
                borderRadius: '10px',
                fontWeight: isActive ? 700 : 500,
                fontSize: '0.88rem',
                cursor: 'pointer'
              }}
            >
              <Icon size={16} />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* Watchlist Sub-status filters */}
      {activeTab === 'watchlist' && (
        <div style={{ display: 'flex', gap: '0.4rem', marginBottom: '1.25rem', flexWrap: 'wrap' }}>
          {[
            { id: 'all', label: 'All' },
            { id: 'watching', label: 'Currently Watching' },
            { id: 'planning', label: 'Plan to Watch' },
            { id: 'completed', label: 'Completed' },
          ].map(st => (
            <button
              key={st.id}
              onClick={() => setWatchlistFilter(st.id)}
              style={{
                padding: '0.35rem 0.75rem',
                borderRadius: '999px',
                fontSize: '0.78rem',
                fontWeight: 600,
                backgroundColor: watchlistFilter === st.id ? '#A855F7' : 'rgba(255, 255, 255, 0.05)',
                border: 'none',
                color: '#FFFFFF',
                cursor: 'pointer'
              }}
            >
              {st.label}
            </button>
          ))}
        </div>
      )}

      {/* Grid Display based on Active Tab */}
      {activeTab === 'watchlist' && (
        filteredWatchlist.length > 0 ? (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
            gap: '1rem',
            justifyItems: 'center'
          }}>
            {filteredWatchlist.map(anime => (
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
            maxWidth: '450px',
            margin: '2rem auto'
          }}>
            <Bookmark size={36} color="#A855F7" style={{ marginBottom: '0.75rem', opacity: 0.8 }} />
            <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF', marginBottom: '0.4rem' }}>Your Watchlist is empty</h3>
            <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
              Browse titles in Home or Discover and click the "+" icon to save them to your library.
            </p>
          </div>
        )
      )}

      {activeTab === 'favorites' && (
        favorites.length > 0 ? (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
            gap: '1rem',
            justifyItems: 'center'
          }}>
            {favorites.map(anime => (
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
            maxWidth: '450px',
            margin: '2rem auto'
          }}>
            <Heart size={36} color="#EC4899" style={{ marginBottom: '0.75rem', opacity: 0.8 }} />
            <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF', marginBottom: '0.4rem' }}>No favorite anime yet</h3>
            <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
              Click the heart icon on any anime's detail page to add it to your favorites.
            </p>
          </div>
        )
      )}

      {activeTab === 'history' && (
        recentlyWatched.length > 0 ? (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
            gap: '1rem',
            justifyItems: 'center'
          }}>
            {recentlyWatched.map(anime => (
              <AnimeCard
                key={anime.mal_id}
                anime={anime}
                onSelect={onSelect}
                onPlay={onPlay}
                onToggleWatchlist={onToggleWatchlist}
                isInWatchlist={isInWatchlist}
                progress={anime.progressPercent || 40}
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
            maxWidth: '450px',
            margin: '2rem auto'
          }}>
            <Clock size={36} color="#A855F7" style={{ marginBottom: '0.75rem', opacity: 0.8 }} />
            <h3 style={{ fontSize: '1.1rem', color: '#FFFFFF', marginBottom: '0.4rem' }}>No watch history</h3>
            <p style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
              Anime you watch will automatically show up here with saved progress so you can resume anytime.
            </p>
          </div>
        )
      )}
    </div>
  );
}
