import React, { useState } from 'react';
import { X, Play, Plus, Check, Heart, Share2, Star, ShieldCheck, ExternalLink, Calendar, Film, Layers } from 'lucide-react';
import { storageService } from '../services/storage';

export function AnimeDetailModal({ anime, onClose, onPlay, onSelectRelated, isInWatchlist, onToggleWatchlist }) {
  const [activeTab, setActiveTab] = useState('overview');
  const [copied, setCopied] = useState(false);
  const [isFav, setIsFav] = useState(storageService.isFavorite(anime.mal_id));
  const inWatchlist = isInWatchlist(anime.mal_id);

  const toggleFavorite = () => {
    const newState = storageService.toggleFavorite(anime);
    setIsFav(newState);
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: `${anime.title} - AnimeFlix`,
        text: `Watch ${anime.title} on AnimeFlix!`,
        url: window.location.href,
      }).catch(() => {});
    } else {
      navigator.clipboard.writeText(window.location.href);
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      zIndex: 45,
      backgroundColor: 'rgba(5, 6, 10, 0.88)',
      backdropFilter: 'blur(20px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '1rem',
      overflowY: 'auto'
    }}>
      <div style={{
        position: 'relative',
        width: '100%',
        maxWidth: '850px',
        maxHeight: '90vh',
        backgroundColor: '#0E1322',
        borderRadius: '20px',
        overflow: 'hidden',
        border: '1px solid rgba(255, 255, 255, 0.12)',
        boxShadow: '0 25px 60px rgba(0, 0, 0, 0.8), 0 0 40px rgba(168, 85, 247, 0.15)',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {/* Close Button */}
        <button
          onClick={onClose}
          aria-label="Close"
          style={{
            position: 'absolute',
            top: '1rem',
            right: '1rem',
            zIndex: 10,
            width: '38px',
            height: '38px',
            borderRadius: '50%',
            background: 'rgba(0, 0, 0, 0.65)',
            backdropFilter: 'blur(8px)',
            border: '1px solid rgba(255, 255, 255, 0.15)',
            color: '#FFFFFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
        >
          <X size={20} />
        </button>

        {/* Modal Scrollable Body */}
        <div style={{ overflowY: 'auto', flex: 1 }} className="no-scrollbar">
          {/* Backdrop Header */}
          <div style={{
            position: 'relative',
            height: '280px',
            width: '100%',
            backgroundImage: `url(${anime.banner_url || anime.image_url})`,
            backgroundSize: 'cover',
            backgroundPosition: 'center 30%'
          }}>
            <div style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'linear-gradient(to top, #0E1322 0%, rgba(14, 19, 34, 0.6) 60%, rgba(14, 19, 34, 0.3) 100%)'
            }} />

            {/* Poster + Header Info Overlay */}
            <div style={{
              position: 'absolute',
              bottom: '1rem',
              left: '1.5rem',
              right: '1.5rem',
              display: 'flex',
              gap: '1.25rem',
              alignItems: 'flex-end'
            }}>
              <img
                src={anime.image_url}
                alt={anime.title}
                style={{
                  width: '110px',
                  height: '160px',
                  borderRadius: '12px',
                  objectFit: 'cover',
                  boxShadow: '0 10px 30px rgba(0, 0, 0, 0.7)',
                  border: '2px solid rgba(255, 255, 255, 0.15)',
                  flexShrink: 0
                }}
              />

              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', gap: '0.4rem', marginBottom: '0.4rem', flexWrap: 'wrap' }}>
                  <span style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '3px',
                    background: 'rgba(234, 179, 8, 0.2)',
                    border: '1px solid rgba(234, 179, 8, 0.4)',
                    color: '#FDE047',
                    padding: '2px 8px',
                    borderRadius: '6px',
                    fontSize: '0.75rem',
                    fontWeight: 700
                  }}>
                    <Star size={12} fill="#FDE047" color="#FDE047" />
                    {anime.score || 8.0}
                  </span>

                  <span style={{
                    background: 'rgba(168, 85, 247, 0.2)',
                    border: '1px solid rgba(168, 85, 247, 0.4)',
                    color: '#D8B4FE',
                    padding: '2px 8px',
                    borderRadius: '6px',
                    fontSize: '0.75rem',
                    fontWeight: 600
                  }}>
                    Rank #{anime.rank || 1}
                  </span>

                  <span style={{
                    background: 'rgba(255, 255, 255, 0.1)',
                    color: '#E2E8F0',
                    padding: '2px 8px',
                    borderRadius: '6px',
                    fontSize: '0.75rem'
                  }}>
                    {anime.year} • {anime.type || 'TV'}
                  </span>
                </div>

                <h2 style={{
                  fontSize: 'clamp(1.2rem, 3vw, 1.8rem)',
                  fontWeight: 900,
                  color: '#FFFFFF',
                  lineHeight: 1.2,
                  marginBottom: '0.2rem'
                }}>
                  {anime.title}
                </h2>

                {anime.title_japanese && (
                  <p style={{ fontSize: '0.82rem', color: '#A855F7', fontWeight: 600 }}>
                    {anime.title_japanese}
                  </p>
                )}
              </div>
            </div>
          </div>

          {/* Action Buttons Row */}
          <div style={{
            padding: '1rem 1.5rem',
            display: 'flex',
            gap: '0.75rem',
            flexWrap: 'wrap',
            alignItems: 'center',
            borderBottom: '1px solid rgba(255, 255, 255, 0.08)'
          }}>
            <button
              onClick={() => {
                onClose();
                onPlay(anime);
              }}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem',
                background: 'linear-gradient(135deg, #A855F7 0%, #6366F1 100%)',
                border: 'none',
                borderRadius: '10px',
                color: '#FFFFFF',
                padding: '0.7rem 1.4rem',
                fontWeight: 700,
                fontSize: '0.9rem',
                cursor: 'pointer',
                boxShadow: '0 4px 15px rgba(168, 85, 247, 0.4)'
              }}
            >
              <Play size={18} fill="#FFFFFF" color="#FFFFFF" />
              Watch Now
            </button>

            <button
              onClick={() => onToggleWatchlist(anime)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: inWatchlist ? 'rgba(16, 185, 129, 0.2)' : 'rgba(255, 255, 255, 0.08)',
                border: `1px solid ${inWatchlist ? '#10B981' : 'rgba(255, 255, 255, 0.15)'}`,
                borderRadius: '10px',
                color: inWatchlist ? '#6EE7B7' : '#FFFFFF',
                padding: '0.7rem 1.2rem',
                fontWeight: 600,
                fontSize: '0.88rem',
                cursor: 'pointer'
              }}
            >
              {inWatchlist ? <Check size={16} /> : <Plus size={16} />}
              {inWatchlist ? 'In Watchlist' : 'Add to Watchlist'}
            </button>

            <button
              onClick={toggleFavorite}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: isFav ? 'rgba(236, 72, 153, 0.2)' : 'rgba(255, 255, 255, 0.08)',
                border: `1px solid ${isFav ? '#EC4899' : 'rgba(255, 255, 255, 0.15)'}`,
                borderRadius: '10px',
                color: isFav ? '#F472B6' : '#FFFFFF',
                padding: '0.7rem 1rem',
                fontWeight: 600,
                fontSize: '0.88rem',
                cursor: 'pointer'
              }}
            >
              <Heart size={16} fill={isFav ? '#EC4899' : 'none'} color={isFav ? '#EC4899' : '#FFFFFF'} />
              Favorite
            </button>

            <button
              onClick={handleShare}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                background: 'rgba(255, 255, 255, 0.08)',
                border: '1px solid rgba(255, 255, 255, 0.15)',
                borderRadius: '10px',
                color: '#CBD5E1',
                padding: '0.7rem 1rem',
                fontWeight: 600,
                fontSize: '0.88rem',
                cursor: 'pointer'
              }}
            >
              <Share2 size={16} />
              {copied ? 'Link Copied!' : 'Share'}
            </button>
          </div>

          {/* Navigation Tabs */}
          <div style={{
            display: 'flex',
            gap: '1.5rem',
            padding: '0 1.5rem',
            borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
            marginTop: '0.5rem'
          }}>
            {[
              { id: 'overview', label: 'Overview' },
              { id: 'episodes', label: `Episodes (${anime.episodes_list?.length || anime.episodes || 12})` },
              { id: 'sources', label: 'Official Streaming Sources' }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                style={{
                  background: 'none',
                  border: 'none',
                  padding: '0.8rem 0',
                  color: activeTab === tab.id ? '#C084FC' : '#94A3B8',
                  fontWeight: activeTab === tab.id ? 700 : 500,
                  fontSize: '0.9rem',
                  cursor: 'pointer',
                  position: 'relative'
                }}
              >
                {tab.label}
                {activeTab === tab.id && (
                  <span style={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    height: '2px',
                    background: '#A855F7',
                    boxShadow: '0 0 8px #A855F7'
                  }} />
                )}
              </button>
            ))}
          </div>

          {/* Tab Content */}
          <div style={{ padding: '1.5rem' }}>
            {activeTab === 'overview' && (
              <div>
                {/* Genres Pills */}
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem', marginBottom: '1.25rem' }}>
                  {(anime.genres || []).map(g => (
                    <span
                      key={g}
                      style={{
                        background: 'rgba(168, 85, 247, 0.12)',
                        border: '1px solid rgba(168, 85, 247, 0.25)',
                        color: '#E9D5FF',
                        padding: '3px 10px',
                        borderRadius: '999px',
                        fontSize: '0.78rem',
                        fontWeight: 600
                      }}
                    >
                      {g}
                    </span>
                  ))}
                </div>

                {/* Synopsis */}
                <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '0.5rem' }}>
                  Synopsis
                </h4>
                <p style={{ fontSize: '0.92rem', color: '#CBD5E1', lineHeight: 1.6, marginBottom: '1.5rem' }}>
                  {anime.synopsis}
                </p>

                {/* Quick Info Grid */}
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
                  gap: '1rem',
                  backgroundColor: 'rgba(255, 255, 255, 0.03)',
                  padding: '1rem',
                  borderRadius: '12px',
                  border: '1px solid rgba(255, 255, 255, 0.06)'
                }}>
                  <div>
                    <span style={{ fontSize: '0.72rem', color: '#94A3B8', display: 'block' }}>Format</span>
                    <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>{anime.type || 'TV'}</span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.72rem', color: '#94A3B8', display: 'block' }}>Status</span>
                    <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>{anime.status || 'Finished'}</span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.72rem', color: '#94A3B8', display: 'block' }}>Episodes</span>
                    <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>{anime.episodes || 12}</span>
                  </div>
                  <div>
                    <span style={{ fontSize: '0.72rem', color: '#94A3B8', display: 'block' }}>Age Rating</span>
                    <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>{anime.rating || 'PG-13'}</span>
                  </div>
                </div>
              </div>
            )}

            {activeTab === 'episodes' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
                {(anime.episodes_list || []).map((ep, idx) => (
                  <div
                    key={ep.id}
                    onClick={() => {
                      onClose();
                      onPlay(anime);
                    }}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '0.75rem 1rem',
                      borderRadius: '10px',
                      backgroundColor: 'rgba(255, 255, 255, 0.04)',
                      border: '1px solid rgba(255, 255, 255, 0.08)',
                      cursor: 'pointer',
                      transition: 'all 0.2s ease'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <div style={{
                        width: '32px',
                        height: '32px',
                        borderRadius: '50%',
                        background: 'rgba(168, 85, 247, 0.2)',
                        color: '#C084FC',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '0.8rem',
                        fontWeight: 700
                      }}>
                        {idx + 1}
                      </div>
                      <div>
                        <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#FFFFFF' }}>{ep.title}</div>
                        <div style={{ fontSize: '0.75rem', color: '#94A3B8' }}>{ep.duration} • Official HD Stream</div>
                      </div>
                    </div>

                    <button
                      style={{
                        background: 'none',
                        border: 'none',
                        color: '#A855F7',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.2rem',
                        cursor: 'pointer',
                        fontWeight: 600,
                        fontSize: '0.8rem'
                      }}
                    >
                      <Play size={16} fill="#A855F7" />
                      Play
                    </button>
                  </div>
                ))}
              </div>
            )}

            {activeTab === 'sources' && (
              <div>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  background: 'rgba(16, 185, 129, 0.1)',
                  border: '1px solid rgba(16, 185, 129, 0.3)',
                  padding: '0.8rem 1rem',
                  borderRadius: '10px',
                  color: '#6EE7B7',
                  fontSize: '0.85rem',
                  marginBottom: '1rem'
                }}>
                  <ShieldCheck size={18} />
                  <span>AnimeFlix only connects to officially verified, authorized legal distributors. No pirated or unauthorized streams.</span>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.8rem 1rem',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    borderRadius: '10px',
                    border: '1px solid rgba(255, 255, 255, 0.08)'
                  }}>
                    <div>
                      <div style={{ fontWeight: 700, color: '#FFFFFF', fontSize: '0.9rem' }}>
                        {anime.official_source_name || 'Crunchyroll / Muse Asia'}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: '#94A3B8' }}>Official Free / Subscription Streaming License</div>
                    </div>
                    {anime.official_stream_url && (
                      <a
                        href={anime.official_stream_url}
                        target="_blank"
                        rel="noreferrer"
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '0.3rem',
                          background: '#A855F7',
                          color: '#FFFFFF',
                          padding: '0.4rem 0.8rem',
                          borderRadius: '6px',
                          textDecoration: 'none',
                          fontSize: '0.8rem',
                          fontWeight: 600
                        }}
                      >
                        Visit Source <ExternalLink size={12} />
                      </a>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
