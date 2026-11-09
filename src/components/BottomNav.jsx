import React from 'react';
import { Home, Compass, Search, Bookmark } from 'lucide-react';

export function BottomNav({ activeTab, setActiveTab }) {
  const tabs = [
    { id: 'home', label: 'Home', icon: Home },
    { id: 'discover', label: 'Discover', icon: Compass },
    { id: 'search', label: 'Search', icon: Search },
    { id: 'library', label: 'Library', icon: Bookmark },
  ];

  return (
    <nav style={{
      position: 'fixed',
      bottom: 0,
      left: 0,
      right: 0,
      zIndex: 40,
      backgroundColor: 'rgba(9, 10, 16, 0.95)',
      backdropFilter: 'blur(20px)',
      borderTop: '1px solid rgba(255, 255, 255, 0.08)',
      padding: '0.45rem 1rem env(safe-area-inset-bottom, 0.5rem)',
      display: 'flex',
      justifyContent: 'space-around',
      alignItems: 'center'
    }} className="mobile-bottom-nav">
      {tabs.map(item => {
        const Icon = item.icon;
        const isActive = activeTab === item.id;
        return (
          <button
            key={item.id}
            onClick={() => setActiveTab(item.id)}
            style={{
              background: 'none',
              border: 'none',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '0.2rem',
              color: isActive ? '#C084FC' : '#64748B',
              cursor: 'pointer',
              padding: '0.3rem 0.8rem',
              borderRadius: '12px',
              transition: 'all 0.2s ease',
              position: 'relative'
            }}
          >
            {isActive && (
              <span style={{
                position: 'absolute',
                top: '-6px',
                width: '18px',
                height: '3px',
                borderRadius: '3px',
                background: '#A855F7',
                boxShadow: '0 0 10px #A855F7'
              }} />
            )}
            <Icon size={20} strokeWidth={isActive ? 2.5 : 2} />
            <span style={{
              fontSize: '0.72rem',
              fontWeight: isActive ? 700 : 500,
              letterSpacing: '0.2px'
            }}>
              {item.label}
            </span>
          </button>
        );
      })}

      <style>{`
        @media (min-width: 768px) {
          .mobile-bottom-nav {
            display: none !important;
          }
        }
      `}</style>
    </nav>
  );
}
