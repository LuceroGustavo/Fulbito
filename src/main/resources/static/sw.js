// 🚀 SERVICE WORKER - FULBITO PWA
// Versión: 1.0.0
// Funcionalidad: Cache offline, sincronización y funcionalidad sin internet

const CACHE_NAME = 'fulbito-v1.0.0';
const STATIC_CACHE = 'fulbito-static-v1.0.0';
const DYNAMIC_CACHE = 'fulbito-dynamic-v1.0.0';

// 📁 Recursos estáticos para cache inmediato
const STATIC_RESOURCES = [
  '/',
  '/css/style.css',
  '/js/main.js',
  '/js/offline-manager.js',
  '/js/mobile-features.js',
  '/js/pwa-installer.js',
  '/manifest.json',
  '/icons/icon-192x192.png',
  '/icons/icon-512x512.png'
];

// 🎯 Recursos dinámicos para cache bajo demanda
const DYNAMIC_RESOURCES = [
  '/equipos/formar',
  '/jugador/lista',
  '/partidos/historial'
];

// 🚀 INSTALACIÓN DEL SERVICE WORKER
self.addEventListener('install', (event) => {
  console.log('🚀 Service Worker instalando...');
  
  event.waitUntil(
    caches.open(STATIC_CACHE)
      .then((cache) => {
        console.log('📦 Cacheando recursos estáticos...');
        return cache.addAll(STATIC_RESOURCES);
      })
      .then(() => {
        console.log('✅ Recursos estáticos cacheados exitosamente');
        return self.skipWaiting();
      })
      .catch((error) => {
        console.error('❌ Error cacheando recursos estáticos:', error);
      })
  );
});

// 🔄 ACTIVACIÓN DEL SERVICE WORKER
self.addEventListener('activate', (event) => {
  console.log('🔄 Service Worker activando...');
  
  event.waitUntil(
    caches.keys()
      .then((cacheNames) => {
        return Promise.all(
          cacheNames.map((cacheName) => {
            if (cacheName !== STATIC_CACHE && cacheName !== DYNAMIC_CACHE) {
              console.log('🗑️ Eliminando cache obsoleto:', cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
      .then(() => {
        console.log('✅ Service Worker activado y cache limpio');
        return self.clients.claim();
      })
  );
});

// 🌐 INTERCEPTAR PETICIONES HTTP
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);
  
  // 🎯 Estrategia de cache según el tipo de recurso
  if (request.method === 'GET') {
    event.respondWith(handleFetch(request, url));
  }
});

// 🎯 MANEJO INTELIGENTE DE PETICIONES
async function handleFetch(request, url) {
  try {
    // 🏠 Página principal - Cache First
    if (url.pathname === '/' || url.pathname === '/index') {
      return await cacheFirst(request, STATIC_CACHE);
    }
    
    // 📱 Recursos estáticos (CSS, JS, imágenes) - Cache First
    if (isStaticResource(url.pathname)) {
      return await cacheFirst(request, STATIC_CACHE);
    }
    
    // 🎮 Páginas de la app - Network First con fallback
    if (isAppPage(url.pathname)) {
      return await networkFirst(request, DYNAMIC_CACHE);
    }
    
    // 🔄 API calls - Network First
    if (url.pathname.startsWith('/api/')) {
      return await networkFirst(request, DYNAMIC_CACHE);
    }
    
    // 🌐 Otros recursos - Network First
    return await networkFirst(request, DYNAMIC_CACHE);
    
  } catch (error) {
    console.error('❌ Error en handleFetch:', error);
    
    // 🆘 Fallback offline
    if (isAppPage(url.pathname)) {
      return await getOfflinePage();
    }
    
    throw error;
  }
}

// 📦 ESTRATEGIA CACHE FIRST
async function cacheFirst(request, cacheName) {
  const cachedResponse = await caches.match(request);
  
  if (cachedResponse) {
    console.log('📦 Serviendo desde cache:', request.url);
    return cachedResponse;
  }
  
  try {
    const networkResponse = await fetch(request);
    
    if (networkResponse.ok) {
      const cache = await caches.open(cacheName);
      cache.put(request, networkResponse.clone());
      console.log('💾 Cacheando nuevo recurso:', request.url);
    }
    
    return networkResponse;
  } catch (error) {
    console.error('❌ Error en cacheFirst:', error);
    throw error;
  }
}

// 🌐 ESTRATEGIA NETWORK FIRST
async function networkFirst(request, cacheName) {
  try {
    const networkResponse = await fetch(request);
    
    if (networkResponse.ok) {
      const cache = await caches.open(cacheName);
      cache.put(request, networkResponse.clone());
      console.log('💾 Cacheando respuesta de red:', request.url);
    }
    
    return networkResponse;
  } catch (error) {
    console.log('🌐 Red no disponible, usando cache:', request.url);
    
    const cachedResponse = await caches.match(request);
    if (cachedResponse) {
      return cachedResponse;
    }
    
    throw error;
  }
}

// 🎯 DETECTAR RECURSOS ESTÁTICOS
function isStaticResource(pathname) {
  return pathname.match(/\.(css|js|png|jpg|jpeg|gif|svg|ico|woff|woff2|ttf|eot)$/);
}

// 🎮 DETECTAR PÁGINAS DE LA APP
function isAppPage(pathname) {
  return pathname.startsWith('/equipos/') || 
         pathname.startsWith('/jugador/') || 
         pathname.startsWith('/partidos/') ||
         pathname === '/';
}

// 🆘 PÁGINA OFFLINE
async function getOfflinePage() {
  const cache = await caches.open(STATIC_CACHE);
  const offlineResponse = await cache.match('/offline.html');
  
  if (offlineResponse) {
    return offlineResponse;
  }
  
  // 🎯 Página offline básica si no existe
  const offlineHTML = `
    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Fulbito - Sin Conexión</title>
      <style>
        body { 
          font-family: Arial, sans-serif; 
          text-align: center; 
          padding: 50px; 
          background: #f5f5f5; 
        }
        .offline-icon { font-size: 64px; margin: 20px; }
        .retry-btn { 
          background: #4CAF50; 
          color: white; 
          padding: 15px 30px; 
          border: none; 
          border-radius: 5px; 
          cursor: pointer; 
          margin: 20px; 
        }
      </style>
    </head>
    <body>
      <div class="offline-icon">📱</div>
      <h1>Sin Conexión a Internet</h1>
      <p>Fulbito está funcionando en modo offline.</p>
      <p>Algunas funcionalidades pueden estar limitadas.</p>
      <button class="retry-btn" onclick="window.location.reload()">Reintentar</button>
      <script>
        // 🔄 Reintentar conexión automáticamente
        setInterval(() => {
          if (navigator.onLine) {
            window.location.reload();
          }
        }, 10000);
      </script>
    </body>
    </html>
  `;
  
  const response = new Response(offlineHTML, {
    headers: { 'Content-Type': 'text/html' }
  });
  
  cache.put('/offline.html', response.clone());
  return response;
}

// 🔄 SINCRONIZACIÓN EN BACKGROUND
self.addEventListener('sync', (event) => {
  console.log('🔄 Sincronización en background:', event.tag);
  
  if (event.tag === 'background-sync') {
    event.waitUntil(doBackgroundSync());
  }
});

// 📊 SINCRONIZACIÓN DE DATOS
async function doBackgroundSync() {
  try {
    console.log('📊 Iniciando sincronización en background...');
    
    // 🎯 Aquí iría la lógica de sincronización
    // Por ahora solo log para debugging
    
    console.log('✅ Sincronización completada');
  } catch (error) {
    console.error('❌ Error en sincronización:', error);
  }
}

// 📱 MANEJO DE PUSH NOTIFICACIONES
self.addEventListener('push', (event) => {
  console.log('📱 Push notification recibida:', event);
  
  if (event.data) {
    const data = event.data.json();
    
    const options = {
      body: data.body || 'Nueva notificación de Fulbito',
      icon: '/icons/icon-192x192.png',
      badge: '/icons/icon-72x72.png',
      vibrate: [200, 100, 200],
      data: data
    };
    
    event.waitUntil(
      self.registration.showNotification(data.title || 'Fulbito', options)
    );
  }
});

// 👆 MANEJO DE CLICKS EN NOTIFICACIONES
self.addEventListener('notificationclick', (event) => {
  console.log('👆 Notificación clickeada:', event);
  
  event.notification.close();
  
  event.waitUntil(
    clients.openWindow('/')
  );
});

// 🎯 MANEJO DE INSTALACIÓN PWA
self.addEventListener('beforeinstallprompt', (event) => {
  console.log('📱 Prompt de instalación PWA disponible');
  
  // 🎯 Guardar el evento para usarlo después
  self.deferredPrompt = event;
});

// 🚀 MANEJO DE APLICACIÓN INSTALADA
self.addEventListener('appinstalled', (event) => {
  console.log('✅ Fulbito instalado como PWA');
  
  // 🎯 Limpiar prompt diferido
  self.deferredPrompt = null;
  
  // 📊 Analytics de instalación (opcional)
  // sendAnalytics('pwa_installed');
});

console.log('🚀 Service Worker de Fulbito cargado exitosamente');
