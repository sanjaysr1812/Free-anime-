import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { BottomNav } from './components/BottomNav';
import { HomeView } from './views/HomeView';
import { DiscoverView } from './views/DiscoverView';
import { SearchView } from './views/SearchView';
import { LibraryView } from './views/LibraryView';
import { VideoPlayerModal } from './components/VideoPlayerModal';
import { AnimeDetailModal } from './components/AnimeDetailModal';
import { storageService } from './services/storage';
import { CURATED_ANIME } from './data/animeCatalog';
import { ShieldCheck, Download, Sparkles, Smartphone, Check, WifiOff } from 'lucide-react';

export function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [selectedAnime, setSelectedAnime] = useState(null);
  const [playingAnime, setPlayingAnime] = useState(null);
  const [preselectedGenre, setPreselectedGenre] = useState(null);

  // Storage and Network states
  const [watchlist, setWatchlist] = useState(storageService.getWatchlist());
  const [favorites, setFavorites] = useState(storageService.getFavorites());
  const [recentlyWatched, setRecentlyWatched] = useState(storageService.getRecentlyWatched());
  const [pwaInstalled, setPwaInstalled] = useState(false);
  const [showPwaBanner, setShowPwaBanner] = useState(true);
  const [isOffline, setIsOffline] = useState(typeof navigator !== 'undefined' ? !navigator.onLine : false);

  useEffect(() => {
    const handleOnline = () => setIsOffline(false);
    const handleOffline = () => setIsOffline(true);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  const refreshStorage = () => {
    setWatchlist(storageService.getWatchlist());
    setFavorites(storageService.getFavorites());
    setRecentlyWatched(storageService.getRecentlyWatched());
  };

  const isInWatchlist = (id) => {
    return watchlist.some(it => (it.mal_id || it.id) === id);
  };

  const handleToggleWatchlist = (anime) => {
    storageService.toggleWatchlist(anime);
    refreshStorage();
  };

  const handlePlay = (anime) => {
    setPlayingAnime(anime);
  };

  const handleSelect = (anime) => {
    setSelectedAnime(anime);
  };

  const handleRandomPick = () => {
    const randomIndex = Math.floor(Math.random() * CURATED_ANIME.length);
    setSelectedAnime(CURATED_ANIME[randomIndex]);
  };

  const handleGenreQuickSelect = (genre) => {
    setPreselectedGenre(genre);
    setActiveTab('discover');
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#090A10',
      color: '#F8FAFC',
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* Top Fixed Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onRandomPick={handleRandomPick}
        onSearchClick={() => setActiveTab('search')}
      />

      {/* Offline Alert Banner */}
      {isOffline && (
        <div style={{
          backgroundColor: '#7F1D1D',
          borderBottom: '1px solid #DC2626',
          color: '#FEE2E2',
          padding: '0.6rem 1.25rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '0.5rem',
          fontSize: '0.82rem',
          zIndex: 40
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <WifiOff size={16} color="#FCA5A5" />
            <span><strong>Offline Mode:</strong> Internet disconnected. Shell and your personal Library are fully cached and accessible.</span>
          </div>
          <button
            onClick={() => setActiveTab('library')}
            style={{
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              border: 'none',
              borderRadius: '6px',
              padding: '3px 10px',
              color: '#FFFFFF',
              fontSize: '0.75rem',
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            Go to Library
          </button>
        </div>
      )}

      {/* PWA Mobile App Download / Add to Home screen Banner */}
      {showPwaBanner && (
        <div style={{
          backgroundColor: 'linear-gradient(90deg, #1E1035 0%, #151828 100%)',
          background: 'linear-gradient(90deg, rgba(168, 85, 247, 0.2) 0%, rgba(99, 102, 241, 0.15) 100%)',
          borderBottom: '1px solid rgba(168, 85, 247, 0.3)',
          padding: '0.6rem 1.25rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '0.6rem',
          fontSize: '0.82rem'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Smartphone size={16} color="#C084FC" />
            <span>
              <strong>Install AnimeFlix:</strong> Tap your mobile browser menu (<span style={{ color: '#E9D5FF' }}>⋮ or Share</span>) and tap <strong>"Add to Home Screen"</strong> to run as a native mobile app!
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <a
              href="/animeflix.apk"
              download="AnimeFlix.apk"
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.35rem',
                backgroundColor: '#8B5CF6',
                color: '#FFFFFF',
                padding: '4px 10px',
                borderRadius: '8px',
                fontSize: '0.75rem',
                fontWeight: 700,
                textDecoration: 'none',
                boxShadow: '0 2px 8px rgba(139, 92, 246, 0.4)'
              }}
            >
              <Download size={13} />
              Download APK
            </a>
            <span style={{
              background: 'rgba(16, 185, 129, 0.2)',
              color: '#6EE7B7',
              border: '1px solid rgba(16, 185, 129, 0.4)',
              padding: '2px 8px',
              borderRadius: '999px',
              fontSize: '0.72rem',
              fontWeight: 700
            }}>
              PWA Ready
            </span>
            <button
              onClick={() => setShowPwaBanner(false)}
              style={{
                background: 'none',
                border: 'none',
                color: '#94A3B8',
                cursor: 'pointer',
                fontSize: '0.8rem',
                padding: '2px 6px'
              }}
            >
              Dismiss
            </button>
          </div>
        </div>
      )}

      {/* Main Body View */}
      <main style={{ flex: 1 }}>
        {activeTab === 'home' && (
          <HomeView
            onPlay={handlePlay}
            onSelect={handleSelect}
            onToggleWatchlist={handleToggleWatchlist}
            isInWatchlist={isInWatchlist}
            watchlist={watchlist}
            recentlyWatched={recentlyWatched}
            onGenreSelect={handleGenreQuickSelect}
          />
        )}

        {activeTab === 'discover' && (
          <DiscoverView
            onPlay={handlePlay}
            onSelect={handleSelect}
            onToggleWatchlist={handleToggleWatchlist}
            isInWatchlist={isInWatchlist}
            preselectedGenre={preselectedGenre}
          />
        )}

        {activeTab === 'search' && (
          <SearchView
            onPlay={handlePlay}
            onSelect={handleSelect}
            onToggleWatchlist={handleToggleWatchlist}
            isInWatchlist={isInWatchlist}
          />
        )}

        {activeTab === 'library' && (
          <LibraryView
            onPlay={handlePlay}
            onSelect={handleSelect}
            onToggleWatchlist={handleToggleWatchlist}
            isInWatchlist={isInWatchlist}
            watchlist={watchlist}
            favorites={favorites}
            recentlyWatched={recentlyWatched}
            onLibraryUpdated={refreshStorage}
          />
        )}
      </main>

      {/* Video Player Modal */}
      {playingAnime && (
        <VideoPlayerModal
          anime={playingAnime}
          onClose={() => {
            setPlayingAnime(null);
            refreshStorage();
          }}
          onAnimeChange={(a) => setPlayingAnime(a)}
        />
      )}

      {/* Anime Detail Modal */}
      {selectedAnime && (
        <AnimeDetailModal
          anime={selectedAnime}
          onClose={() => setSelectedAnime(null)}
          onPlay={handlePlay}
          onSelectRelated={handleSelect}
          isInWatchlist={isInWatchlist}
          onToggleWatchlist={handleToggleWatchlist}
        />
      )}

      {/* Legal & DMCA Protection Footer */}
      <footer style={{
        padding: '2.5rem 1.5rem 6rem',
        borderTop: '1px solid rgba(255, 255, 255, 0.08)',
        backgroundColor: '#07080D',
        textAlign: 'center',
        marginTop: 'auto'
      }}>
        <div style={{ maxWidth: '680px', margin: '0 auto' }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.4rem',
            color: '#10B981',
            fontSize: '0.85rem',
            fontWeight: 700,
            marginBottom: '0.5rem'
          }}>
            <ShieldCheck size={18} />
            <span>100% Authorized & Legal Streaming Only</span>
          </div>

          <p style={{ fontSize: '0.78rem', color: '#64748B', lineHeight: 1.6, marginBottom: '1rem' }}>
            AnimeFlix does not host, upload, or scrape copyrighted video content. All streams are embedded directly from official, authorized public distributor channels (e.g. Muse Asia, Ani-One, Crunchyroll Trailers/Previews, and open Creative Commons video works) under DMCA guidelines.
          </p>

          <p style={{ fontSize: '0.75rem', color: '#475569' }}>
            © {new Date().getFullYear()} AnimeFlix. All rights reserved by respective studios & distributors.
          </p>
        </div>
      </footer>

      {/* Mobile Floating Bottom Navigation */}
      <BottomNav
        activeTab={activeTab}
        setActiveTab={setActiveTab}
      />
    </div>
  );
}
export default App;
