import React, { useState, useEffect } from 'react';
import { X, Play, Pause, RotateCcw, Volume2, VolumeX, Maximize2, SkipForward, SkipBack, ShieldCheck, ExternalLink, List, Check } from 'lucide-react';
import { storageService } from '../services/storage';

export function VideoPlayerModal({ anime, onClose, onAnimeChange }) {
  const [currentEpisodeIndex, setCurrentEpisodeIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(true);
  const [isMuted, setIsMuted] = useState(false);
  const [speed, setSpeed] = useState(1);
  const [showEpisodeList, setShowEpisodeList] = useState(false);
  const [progress, setProgress] = useState(30);

  const episodes = anime.episodes_list || [
    { id: 1, title: 'Episode 1: The Beginning', duration: '24m', is_free: true, stream_url: anime.free_stream_embed }
  ];
  const currentEpisode = episodes[currentEpisodeIndex] || episodes[0];

  // Save to RecentlyWatched / Continue Watching
  useEffect(() => {
    storageService.recordWatchProgress(anime, currentEpisodeIndex + 1, progress);
  }, [anime, currentEpisodeIndex, progress]);

  const handleNextEp = () => {
    if (currentEpisodeIndex < episodes.length - 1) {
      setCurrentEpisodeIndex(prev => prev + 1);
    }
  };

  const handlePrevEp = () => {
    if (currentEpisodeIndex > 0) {
      setCurrentEpisodeIndex(prev => prev - 1);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      zIndex: 50,
      backgroundColor: 'rgba(5, 6, 10, 0.96)',
      backdropFilter: 'blur(24px)',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'space-between',
      overflowY: 'auto'
    }}>
      {/* Top Header */}
      <div style={{
        padding: '0.8rem 1.25rem',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
        background: 'rgba(9, 10, 16, 0.8)'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.35rem',
            background: 'rgba(16, 185, 129, 0.15)',
            border: '1px solid #10B981',
            color: '#6EE7B7',
            padding: '3px 8px',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 700
          }}>
            <ShieldCheck size={14} />
            <span>Official Legal Stream</span>
          </div>
          <div>
            <h3 style={{
              fontSize: '1rem',
              fontWeight: 800,
              color: '#FFFFFF',
              margin: 0
            }}>
              {anime.title}
            </h3>
            <span style={{ fontSize: '0.75rem', color: '#94A3B8' }}>
              {currentEpisode.title} • {anime.type || 'TV'}
            </span>
          </div>
        </div>

        <button
          onClick={onClose}
          aria-label="Close Player"
          style={{
            width: '36px',
            height: '36px',
            borderRadius: '50%',
            background: 'rgba(255, 255, 255, 0.1)',
            border: 'none',
            color: '#FFFFFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
        >
          <X size={20} />
        </button>
      </div>

      {/* Main Video Screen Container */}
      <div style={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '0.5rem 1rem',
        maxWidth: '1100px',
        margin: '0 auto',
        width: '100%'
      }}>
        <div style={{
          position: 'relative',
          width: '100%',
          paddingTop: '56.25%', // 16:9
          borderRadius: '16px',
          overflow: 'hidden',
          backgroundColor: '#000000',
          boxShadow: '0 20px 50px rgba(0, 0, 0, 0.8), 0 0 40px rgba(168, 85, 247, 0.15)',
          border: '1px solid rgba(255, 255, 255, 0.1)'
        }}>
          {/* Official Embedded Video Player */}
          {anime.free_stream_embed ? (
            <iframe
              src={`${anime.free_stream_embed}?autoplay=1&enablejsapi=1&origin=${window.location.origin}`}
              title={anime.title}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowFullScreen
              style={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                border: 'none'
              }}
            />
          ) : (
            <div style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              padding: '2rem',
              textAlign: 'center',
              background: '#0D111E'
            }}>
              <ShieldCheck size={48} color="#A855F7" style={{ marginBottom: '1rem' }} />
              <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#FFFFFF', marginBottom: '0.5rem' }}>
                No Unofficial or Pirated Streams
              </h3>
              <p style={{ fontSize: '0.9rem', color: '#94A3B8', maxWidth: '480px', marginBottom: '1.5rem' }}>
                AnimeFlix strictly complies with copyright law. No authorized free ad-supported stream is available for this title in your region. Watch it legally through official licensed partners below:
              </p>
              {anime.official_stream_url && (
                <a
                  href={anime.official_stream_url}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem',
                    background: '#A855F7',
                    color: '#FFFFFF',
                    padding: '0.65rem 1.4rem',
                    borderRadius: '8px',
                    fontWeight: 700,
                    textDecoration: 'none'
                  }}
                >
                  <ExternalLink size={16} />
                  Open on {anime.official_source_name || 'Official Partner'}
                </a>
              )}
            </div>
          )}
        </div>

        {/* Video Controls Bar */}
        <div style={{
          width: '100%',
          marginTop: '1rem',
          backgroundColor: '#121626',
          borderRadius: '12px',
          padding: '0.8rem 1.25rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '0.8rem',
          border: '1px solid rgba(255, 255, 255, 0.08)'
        }}>
          {/* Playback Controls */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <button
              onClick={handlePrevEp}
              disabled={currentEpisodeIndex === 0}
              title="Previous Episode"
              style={{
                background: 'rgba(255, 255, 255, 0.08)',
                border: 'none',
                color: currentEpisodeIndex === 0 ? '#475569' : '#FFFFFF',
                borderRadius: '8px',
                padding: '6px',
                cursor: currentEpisodeIndex === 0 ? 'not-allowed' : 'pointer'
              }}
            >
              <SkipBack size={18} />
            </button>

            <button
              onClick={handleNextEp}
              disabled={currentEpisodeIndex === episodes.length - 1}
              title="Next Episode"
              style={{
                background: 'rgba(255, 255, 255, 0.08)',
                border: 'none',
                color: currentEpisodeIndex === episodes.length - 1 ? '#475569' : '#FFFFFF',
                borderRadius: '8px',
                padding: '6px',
                cursor: currentEpisodeIndex === episodes.length - 1 ? 'not-allowed' : 'pointer'
              }}
            >
              <SkipForward size={18} />
            </button>

            <button
              onClick={() => setShowEpisodeList(!showEpisodeList)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.35rem',
                background: showEpisodeList ? 'rgba(168, 85, 247, 0.25)' : 'rgba(255, 255, 255, 0.08)',
                border: `1px solid ${showEpisodeList ? '#A855F7' : 'transparent'}`,
                color: showEpisodeList ? '#C084FC' : '#CBD5E1',
                padding: '0.35rem 0.75rem',
                borderRadius: '8px',
                fontSize: '0.8rem',
                fontWeight: 600,
                cursor: 'pointer'
              }}
            >
              <List size={16} />
              <span>Episodes ({episodes.length})</span>
            </button>
          </div>

          {/* Source Attribution & Legal Info */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            fontSize: '0.78rem',
            color: '#94A3B8'
          }}>
            <span>Distributor:</span>
            <span style={{ color: '#E2E8F0', fontWeight: 600 }}>{anime.official_source_name || 'Authorized Channel'}</span>
            {anime.official_stream_url && (
              <a
                href={anime.official_stream_url}
                target="_blank"
                rel="noreferrer"
                style={{
                  color: '#C084FC',
                  textDecoration: 'none',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '2px',
                  fontWeight: 600
                }}
              >
                Direct Link <ExternalLink size={12} />
              </a>
            )}
          </div>
        </div>

        {/* Episode Selector Drawer */}
        {showEpisodeList && (
          <div style={{
            width: '100%',
            marginTop: '0.75rem',
            backgroundColor: '#121626',
            borderRadius: '12px',
            padding: '1rem',
            border: '1px solid rgba(168, 85, 247, 0.3)',
            maxHeight: '220px',
            overflowY: 'auto'
          }}>
            <h4 style={{ fontSize: '0.85rem', color: '#94A3B8', marginBottom: '0.6rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Select Episode
            </h4>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '0.5rem' }}>
              {episodes.map((ep, idx) => {
                const isSelected = idx === currentEpisodeIndex;
                return (
                  <div
                    key={ep.id}
                    onClick={() => {
                      setCurrentEpisodeIndex(idx);
                      setShowEpisodeList(false);
                    }}
                    style={{
                      padding: '0.6rem 0.8rem',
                      borderRadius: '8px',
                      backgroundColor: isSelected ? 'rgba(168, 85, 247, 0.25)' : 'rgba(255, 255, 255, 0.04)',
                      border: `1px solid ${isSelected ? '#A855F7' : 'rgba(255, 255, 255, 0.08)'}`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      cursor: 'pointer'
                    }}
                  >
                    <div>
                      <div style={{ fontSize: '0.85rem', fontWeight: 700, color: isSelected ? '#FFFFFF' : '#CBD5E1' }}>
                        {ep.title}
                      </div>
                      <div style={{ fontSize: '0.7rem', color: '#94A3B8' }}>{ep.duration}</div>
                    </div>
                    {isSelected && <Check size={16} color="#A855F7" />}
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Footer Legal Protection notice */}
      <div style={{
        padding: '0.6rem 1.25rem',
        textAlign: 'center',
        fontSize: '0.72rem',
        color: '#64748B',
        borderTop: '1px solid rgba(255, 255, 255, 0.06)'
      }}>
        Protected under Digital Millennium Copyright Act (DMCA). Content delivered via authorized distributor embeds.
      </div>
    </div>
  );
}
