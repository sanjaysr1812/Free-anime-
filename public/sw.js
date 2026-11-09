// AnimeFlix Service Worker
// Version: animeflix-v1.1
const CACHE_NAME = 'animeflix-core-v1';
const RUNTIME_CACHE = 'animeflix-runtime-v1';

// Critical core assets to pre-cache on install
const PRECACHE_URLS = [
  '/',
  '/index.html',
  '/manifest.webmanifest',
  '/icon.svg'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(PRECACHE_URLS);
    }).then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  const currentCaches = [CACHE_NAME, RUNTIME_CACHE];
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (!currentCaches.includes(cacheName)) {
            return caches.delete(cacheName);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  const request = event.request;

  // Only handle GET requests
  if (request.method !== 'GET') {
    return;
  }

  const url = new URL(request.url);

  // Bypass AI Studio internal frame scripts and dev endpoints
  if (
    url.pathname.includes('_aistudio') ||
    url.pathname.includes('__aistudio') ||
    url.protocol.startsWith('chrome-extension')
  ) {
    return;
  }

  // Strategy 1: HTML Navigation Requests -> Network First with Cached /index.html Fallback
  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request)
        .then((response) => {
          // If valid response, clone and update cache
          if (response && response.status === 200) {
            const copy = response.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
          }
          return response;
        })
        .catch(async () => {
          // Offline fallback
          const cached = await caches.match(request);
          if (cached) return cached;
          const fallback = await caches.match('/index.html');
          if (fallback) return fallback;
          return new Response(
            `<!DOCTYPE html><html><head><meta charset="UTF-8"><title>AnimeFlix - Offline</title><style>body{background:#090A10;color:#F8FAFC;font-family:sans-serif;display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center;padding:1rem;}h1{color:#A855F7;}p{color:#94A3B8;max-width:400px;}</style></head><body><h1>AnimeFlix Offline</h1><p>You are currently offline. Please reconnect to browse new anime or access your cached library.</p><button onclick="window.location.reload()" style="background:#A855F7;color:white;border:none;padding:10px 20px;border-radius:8px;cursor:pointer;margin-top:1rem;font-weight:bold;">Retry</button></body></html>`,
            { headers: { 'Content-Type': 'text/html' } }
          );
        })
    );
    return;
  }

  // Strategy 2: Same-origin static assets (.js, .css, .svg, .png, .ico, /assets/*)
  const isStaticAsset =
    url.origin === self.location.origin &&
    (url.pathname.startsWith('/assets/') ||
     /\.(js|css|svg|png|jpg|jpeg|webp|ico|json|webmanifest|woff|woff2|ttf)$/i.test(url.pathname));

  if (isStaticAsset) {
    event.respondWith(
      caches.match(request).then((cachedResponse) => {
        if (cachedResponse) {
          // Serve cached asset immediately, update cache in background
          fetch(request)
            .then((networkResponse) => {
              if (networkResponse && networkResponse.status === 200) {
                caches.open(CACHE_NAME).then((cache) => cache.put(request, networkResponse));
              }
            })
            .catch(() => {/* Ignore background fetch failures when offline */});
          return cachedResponse;
        }

        // Not in cache, fetch from network and cache it
        return fetch(request).then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            const copy = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
          }
          return networkResponse;
        });
      })
    );
    return;
  }

  // Strategy 3: Third-party images (MAL posters) and Google Fonts
  const isImageOrFont =
    url.hostname.includes('myanimelist.net') ||
    url.hostname.includes('googleapis.com') ||
    url.hostname.includes('gstatic.com') ||
    /\.(jpg|jpeg|png|webp|svg|woff2)$/i.test(url.pathname);

  if (isImageOrFont) {
    event.respondWith(
      caches.match(request).then((cachedResponse) => {
        if (cachedResponse) return cachedResponse;

        return fetch(request)
          .then((networkResponse) => {
            if (networkResponse && networkResponse.status === 200) {
              const copy = networkResponse.clone();
              caches.open(RUNTIME_CACHE).then((cache) => cache.put(request, copy));
            }
            return networkResponse;
          })
          .catch(() => {
            // Fail gracefully for external assets when offline
            return new Response('', { status: 408, statusText: 'Offline' });
          });
      })
    );
    return;
  }

  // Default: Network with runtime cache fallback
  event.respondWith(
    fetch(request).catch(() => caches.match(request))
  );
});

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});
