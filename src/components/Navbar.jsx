import React from 'react';
import { Film, Compass, Search, BookmarkCheck, Sparkles, Download, Play } from 'lucide-react';

export function Navbar({ activeTab, setActiveTab, onRandomPick, onSearchClick }) {
  return (
    <header style={{
      position: 'sticky',
      top: 0,
      zIndex: 40,
      backgroundColor: 'rgba(9, 10, 16, 0.85)',
      backdropFilter: 'blur(16px)',
      borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
      padding: '0.75rem 1.25rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: '1rem',
      transition: 'all 0.3s ease'
    }}>
      {/* Brand Logo */}
      <div 
        onClick={() => setActiveTab('home')}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.6rem',
          cursor: 'pointer',
          userSelect: 'none'
        }}
      >
        <div style={{
          width: '38px',
          height: '38px',
          borderRadius: '10px',
          background: 'linear-gradient(135deg, #A855F7 0%, #6366F1 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 0 20px rgba(168, 85, 247, 0.5)'
        }}>
          <Play size={18} fill="#FFFFFF" color="#FFFFFF" style={{ marginLeft: '2px' }} />
        </div>
        <div>
          <span style={{
            fontSize: '1.25rem',
            fontWeight: 900,
            letterSpacing: '1px',
            background: 'linear-gradient(to right, #FFFFFF 40%, #C084FC 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            display: 'block',
            lineHeight: 1.1
          }}>
            ANIME<span style={{ color: '#A855F7', WebkitTextFillColor: '#A855F7' }}>FLIX</span>
          </span>
          <span style={{
            fontSize: '0.62rem',
            fontWeight: 700,
            textTransform: 'uppercase',
            letterSpacing: '1.5px',
            color: '#A855F7',
            display: 'block'
          }}>
            PREMIUM STREAM
          </span>
        </div>
      </div>

      {/* Desktop Navigation Links */}
      <nav style={{
        display: 'none',
        gap: '0.5rem',
        alignItems: 'center'
      }} className="desktop-nav">
        {[
          { id: 'home', label: 'Home', icon: Film },
          { id: 'discover', label: 'Discover & Genres', icon: Compass },
          { id: 'search', label: 'Search', icon: Search },
          { id: 'library', label: 'My Library', icon: BookmarkCheck }
        ].map(item => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                padding: '0.5rem 0.9rem',
                borderRadius: '8px',
                border: 'none',
                background: isActive ? 'rgba(168, 85, 247, 0.15)' : 'transparent',
                color: isActive ? '#C084FC' : '#94A3B8',
                fontWeight: isActive ? 600 : 500,
                fontSize: '0.9rem',
                cursor: 'pointer',
                transition: 'all 0.2s ease'
              }}
            >
              <Icon size={16} />
              {item.label}
            </button>
          );
        })}
      </nav>

      {/* Right Action Buttons */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
        {/* Random / Surprise Me Button */}
        <button
          onClick={onRandomPick}
          title="Discover Random Anime"
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.4rem',
            padding: '0.5rem 0.85rem',
            background: 'linear-gradient(135deg, rgba(168, 85, 247, 0.2), rgba(99, 102, 241, 0.2))',
            border: '1px solid rgba(168, 85, 247, 0.4)',
            borderRadius: '999px',
            color: '#E9D5FF',
            fontSize: '0.8rem',
            fontWeight: 600,
            cursor: 'pointer',
            transition: 'all 0.2s ease'
          }}
        >
          <Sparkles size={14} color="#C084FC" />
          <span className="random-text">Surprise Me</span>
        </button>

        {/* Quick Search Button (Mobile & Desktop) */}
        <button
          onClick={onSearchClick}
          style={{
            width: '38px',
            height: '38px',
            borderRadius: '50%',
            background: 'rgba(255, 255, 255, 0.06)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#F8FAFC',
            cursor: 'pointer'
          }}
        >
          <Search size={17} />
        </button>
      </div>

      <style>{`
        @media (min-width: 768px) {
          .desktop-nav {
            display: flex !important;
          }
        }
        @media (max-width: 480px) {
          .random-text {
            display: none;
          }
        }
      `}</style>
    </header>
  );
}
