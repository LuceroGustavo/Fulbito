// 🚀 OFFLINE MANAGER - FULBITO PWA
// Versión: 1.0.0
// Funcionalidad: Gestión de datos offline, cache local y sincronización

class OfflineManager {
  constructor() {
    this.dbName = 'FulbitoDB';
    this.dbVersion = 1;
    this.db = null;
    this.isOnline = navigator.onLine;
    this.syncQueue = [];
    
    this.init();
    this.setupEventListeners();
  }

  // 🚀 INICIALIZACIÓN
  async init() {
    try {
      console.log('🚀 Inicializando Offline Manager...');
      
      // 🎯 Inicializar IndexedDB
      await this.initDatabase();
      
      // 🎯 Inicializar LocalStorage
      this.initLocalStorage();
      
      // 🎯 Verificar estado de conexión
      this.checkConnectionStatus();
      
      console.log('✅ Offline Manager inicializado exitosamente');
    } catch (error) {
      console.error('❌ Error inicializando Offline Manager:', error);
    }
  }

  // 🎯 CONFIGURAR EVENT LISTENERS
  setupEventListeners() {
    // 🌐 Cambios de conexión
    window.addEventListener('online', () => this.handleOnline());
    window.addEventListener('offline', () => this.handleOffline());
    
    // 🔄 Visibilidad de página
    document.addEventListener('visibilitychange', () => this.handleVisibilityChange());
    
    // 📱 Antes de cerrar página
    window.addEventListener('beforeunload', () => this.handleBeforeUnload());
  }

  // 🗄️ INICIALIZAR INDEXEDDB
  async initDatabase() {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbName, this.dbVersion);
      
      request.onerror = () => {
        console.error('❌ Error abriendo IndexedDB:', request.error);
        reject(request.error);
      };
      
      request.onsuccess = () => {
        this.db = request.result;
        console.log('✅ IndexedDB abierto exitosamente');
        resolve();
      };
      
      request.onupgradeneeded = (event) => {
        const db = event.target.result;
        
        // 🎯 Store para jugadores
        if (!db.objectStoreNames.contains('jugadores')) {
          const jugadoresStore = db.createObjectStore('jugadores', { keyPath: 'id' });
          jugadoresStore.createIndex('nombre', 'nombre', { unique: false });
          jugadoresStore.createIndex('esArquero', 'esArquero', { unique: false });
          console.log('✅ Store jugadores creado');
        }
        
        // 🎯 Store para partidos
        if (!db.objectStoreNames.contains('partidos')) {
          const partidosStore = db.createObjectStore('partidos', { keyPath: 'id' });
          partidosStore.createIndex('fecha', 'fecha', { unique: false });
          partidosStore.createIndex('sessionId', 'sessionId', { unique: false });
          console.log('✅ Store partidos creado');
        }
        
        // 🎯 Store para equipos temporales
        if (!db.objectStoreNames.contains('equiposTemporales')) {
          const equiposStore = db.createObjectStore('equiposTemporales', { keyPath: 'sessionId' });
          console.log('✅ Store equipos temporales creado');
        }
        
        // 🎯 Store para cache de recursos
        if (!db.objectStoreNames.contains('cache')) {
          const cacheStore = db.createObjectStore('cache', { keyPath: 'url' });
          cacheStore.createIndex('timestamp', 'timestamp', { unique: false });
          console.log('✅ Store cache creado');
        }
      };
    });
  }

  // 💾 INICIALIZAR LOCALSTORAGE
  initLocalStorage() {
    try {
      // 🎯 Configuración de la app
      if (!localStorage.getItem('fulbito_config')) {
        localStorage.setItem('fulbito_config', JSON.stringify({
          theme: 'light',
          language: 'es',
          notifications: true,
          autoSync: true,
          lastSync: null
        }));
      }
      
      // 🎯 Datos de sesión
      if (!localStorage.getItem('fulbito_session')) {
        localStorage.setItem('fulbito_session', JSON.stringify({
          currentUser: null,
          lastActivity: new Date().toISOString(),
          sessionId: this.generateSessionId()
        }));
      }
      
      console.log('✅ LocalStorage inicializado');
    } catch (error) {
      console.error('❌ Error inicializando LocalStorage:', error);
    }
  }

  // 🎯 GENERAR ID DE SESIÓN
  generateSessionId() {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  }

  // 🌐 VERIFICAR ESTADO DE CONEXIÓN
  checkConnectionStatus() {
    this.isOnline = navigator.onLine;
    this.updateConnectionIndicator();
    
    if (this.isOnline) {
      this.processSyncQueue();
    }
  }

  // 📱 ACTUALIZAR INDICADOR DE CONEXIÓN
  updateConnectionIndicator() {
    const indicator = document.getElementById('connection-indicator');
    if (indicator) {
      indicator.className = this.isOnline ? 'online' : 'offline';
      indicator.textContent = this.isOnline ? '🌐 En línea' : '🔌 Sin conexión';
      indicator.title = this.isOnline ? 'Conectado a internet' : 'Modo offline';
    }
  }

  // 🌐 MANEJAR CONEXIÓN ONLINE
  async handleOnline() {
    console.log('🌐 Conexión restaurada');
    this.isOnline = true;
    this.updateConnectionIndicator();
    
    // 🎯 Procesar cola de sincronización
    await this.processSyncQueue();
    
    // 🎯 Sincronizar datos locales
    await this.syncLocalData();
    
    // 🎯 Mostrar notificación
    this.showNotification('Conexión restaurada', 'Sincronizando datos...', 'success');
  }

  // 🔌 MANEJAR CONEXIÓN OFFLINE
  handleOffline() {
    console.log('🔌 Conexión perdida');
    this.isOnline = false;
    this.updateConnectionIndicator();
    
    // 🎯 Mostrar notificación
    this.showNotification('Sin conexión', 'Funcionando en modo offline', 'warning');
  }

  // 👁️ MANEJAR CAMBIO DE VISIBILIDAD
  handleVisibilityChange() {
    if (!document.hidden && this.isOnline) {
      // 🎯 Página visible y online - sincronizar
      this.syncLocalData();
    }
  }

  // 🚪 MANEJAR ANTES DE CERRAR
  handleBeforeUnload() {
    // 🎯 Guardar estado actual
    this.saveCurrentState();
    
    // 🎯 Limpiar datos temporales
    this.cleanupTempData();
  }

  // 💾 GUARDAR ESTADO ACTUAL
  saveCurrentState() {
    try {
      const currentState = {
        timestamp: new Date().toISOString(),
        currentPage: window.location.pathname,
        formData: this.getFormData(),
        scrollPosition: window.scrollY
      };
      
      localStorage.setItem('fulbito_current_state', JSON.stringify(currentState));
    } catch (error) {
      console.error('❌ Error guardando estado:', error);
    }
  }

  // 📝 OBTENER DATOS DE FORMULARIOS
  getFormData() {
    const forms = document.querySelectorAll('form');
    const formData = {};
    
    forms.forEach((form, index) => {
      const formElements = form.elements;
      const formValues = {};
      
      for (let element of formElements) {
        if (element.name) {
          formValues[element.name] = element.value;
        }
      }
      
      formData[`form_${index}`] = formValues;
    });
    
    return formData;
  }

  // 🧹 LIMPIAR DATOS TEMPORALES
  cleanupTempData() {
    try {
      // 🎯 Limpiar cache temporal
      sessionStorage.clear();
      
      // 🎯 Limpiar variables globales
      if (window.fulbitoTempData) {
        delete window.fulbitoTempData;
      }
    } catch (error) {
      console.error('❌ Error limpiando datos temporales:', error);
    }
  }

  // 🎯 OPERACIONES CRUD PARA JUGADORES
  async saveJugador(jugador) {
    try {
      if (this.isOnline) {
        // 🌐 Online - guardar en servidor
        return await this.saveJugadorOnline(jugador);
      } else {
        // 🔌 Offline - guardar localmente
        return await this.saveJugadorOffline(jugador);
      }
    } catch (error) {
      console.error('❌ Error guardando jugador:', error);
      throw error;
    }
  }

  // 🌐 GUARDAR JUGADOR ONLINE
  async saveJugadorOnline(jugador) {
    try {
      const response = await fetch('/jugador/guardar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(jugador)
      });
      
      if (response.ok) {
        const savedJugador = await response.json();
        
        // 💾 Cache local
        await this.cacheJugador(savedJugador);
        
        return savedJugador;
      } else {
        throw new Error('Error en servidor');
      }
    } catch (error) {
      // 🎯 Si falla, agregar a cola de sincronización
      this.addToSyncQueue('jugador', 'save', jugador);
      throw error;
    }
  }

  // 🔌 GUARDAR JUGADOR OFFLINE
  async saveJugadorOffline(jugador) {
    try {
      // 🎯 Generar ID temporal
      if (!jugador.id) {
        jugador.id = 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        jugador.tempId = true;
      }
      
      // 💾 Guardar en IndexedDB
      await this.saveToIndexedDB('jugadores', jugador);
      
      // 🎯 Agregar a cola de sincronización
      this.addToSyncQueue('jugador', 'save', jugador);
      
      // 🎯 Mostrar notificación
      this.showNotification('Jugador guardado', 'Se sincronizará cuando haya conexión', 'info');
      
      return jugador;
    } catch (error) {
      console.error('❌ Error guardando jugador offline:', error);
      throw error;
    }
  }

  // 💾 CACHEAR JUGADOR
  async cacheJugador(jugador) {
    try {
      await this.saveToIndexedDB('jugadores', jugador);
      console.log('✅ Jugador cacheadado:', jugador.nombre);
    } catch (error) {
      console.error('❌ Error cacheando jugador:', error);
    }
  }

  // 🗄️ GUARDAR EN INDEXEDDB
  async saveToIndexedDB(storeName, data) {
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      const request = store.put(data);
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }

  // 📖 LEER DE INDEXEDDB
  async readFromIndexedDB(storeName, key) {
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readonly');
      const store = transaction.objectStore(storeName);
      const request = store.get(key);
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }

  // 🔍 BUSCAR EN INDEXEDDB
  async searchInIndexedDB(storeName, indexName, value) {
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readonly');
      const store = transaction.objectStore(storeName);
      const index = store.index(indexName);
      const request = index.getAll(value);
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }

  // 🗑️ ELIMINAR DE INDEXEDDB
  async deleteFromIndexedDB(storeName, key) {
    return new Promise((resolve, reject) => {
      const transaction = this.db.transaction([storeName], 'readwrite');
      const store = transaction.objectStore(storeName);
      const request = store.delete(key);
      
      request.onsuccess = () => resolve(request.result);
      request.onerror = () => reject(request.error);
    });
  }

  // 📋 AGREGAR A COLA DE SINCRONIZACIÓN
  addToSyncQueue(type, action, data) {
    const syncItem = {
      id: Date.now() + '_' + Math.random().toString(36).substr(2, 9),
      type: type,
      action: action,
      data: data,
      timestamp: new Date().toISOString(),
      retries: 0,
      maxRetries: 3
    };
    
    this.syncQueue.push(syncItem);
    
    // 💾 Guardar en localStorage
    localStorage.setItem('fulbito_sync_queue', JSON.stringify(this.syncQueue));
    
    console.log('📋 Agregado a cola de sincronización:', syncItem);
  }

  // 🔄 PROCESAR COLA DE SINCRONIZACIÓN
  async processSyncQueue() {
    if (this.syncQueue.length === 0) return;
    
    console.log('🔄 Procesando cola de sincronización...');
    
    const itemsToProcess = [...this.syncQueue];
    const successfulItems = [];
    const failedItems = [];
    
    for (const item of itemsToProcess) {
      try {
        await this.processSyncItem(item);
        successfulItems.push(item);
      } catch (error) {
        console.error('❌ Error procesando item:', item, error);
        
        if (item.retries < item.maxRetries) {
          item.retries++;
          failedItems.push(item);
        } else {
          console.error('❌ Item falló definitivamente:', item);
        }
      }
    }
    
    // 🎯 Actualizar cola
    this.syncQueue = failedItems;
    localStorage.setItem('fulbito_sync_queue', JSON.stringify(this.syncQueue));
    
    // 🎯 Mostrar resultados
    if (successfulItems.length > 0) {
      this.showNotification('Sincronización completada', `${successfulItems.length} elementos sincronizados`, 'success');
    }
    
    if (failedItems.length > 0) {
      this.showNotification('Sincronización parcial', `${failedItems.length} elementos fallaron`, 'warning');
    }
  }

  // 🎯 PROCESAR ITEM DE SINCRONIZACIÓN
  async processSyncItem(item) {
    console.log('🔄 Procesando item:', item);
    
    switch (item.type) {
      case 'jugador':
        await this.processJugadorSync(item);
        break;
      case 'partido':
        await this.processPartidoSync(item);
        break;
      case 'equipo':
        await this.processEquipoSync(item);
        break;
      default:
        console.warn('⚠️ Tipo de sincronización desconocido:', item.type);
    }
  }

  // ⚽ PROCESAR SINCRONIZACIÓN DE JUGADOR
  async processJugadorSync(item) {
    switch (item.action) {
      case 'save':
        await this.saveJugadorOnline(item.data);
        break;
      case 'update':
        await this.updateJugadorOnline(item.data);
        break;
      case 'delete':
        await this.deleteJugadorOnline(item.data.id);
        break;
      default:
        console.warn('⚠️ Acción de jugador desconocida:', item.action);
    }
  }

  // 🎮 PROCESAR SINCRONIZACIÓN DE PARTIDO
  async processPartidoSync(item) {
    // 🎯 Implementar lógica de sincronización de partidos
    console.log('🎮 Sincronizando partido:', item);
  }

  // 👥 PROCESAR SINCRONIZACIÓN DE EQUIPO
  async processEquipoSync(item) {
    // 🎯 Implementar lógica de sincronización de equipos
    console.log('👥 Sincronizando equipo:', item);
  }

  // 🔄 SINCRONIZAR DATOS LOCALES
  async syncLocalData() {
    try {
      console.log('🔄 Sincronizando datos locales...');
      
      // 🎯 Sincronizar jugadores
      await this.syncJugadores();
      
      // 🎯 Sincronizar partidos
      await this.syncPartidos();
      
      // 🎯 Sincronizar equipos temporales
      await this.syncEquiposTemporales();
      
      // 🎯 Actualizar timestamp de última sincronización
      const config = JSON.parse(localStorage.getItem('fulbito_config'));
      config.lastSync = new Date().toISOString();
      localStorage.setItem('fulbito_config', JSON.stringify(config));
      
      console.log('✅ Sincronización local completada');
    } catch (error) {
      console.error('❌ Error en sincronización local:', error);
    }
  }

  // ⚽ SINCRONIZAR JUGADORES
  async syncJugadores() {
    try {
      // 🎯 Obtener jugadores del servidor
      const response = await fetch('/jugador/lista');
      if (response.ok) {
        const jugadores = await response.json();
        
        // 💾 Actualizar cache local
        for (const jugador of jugadores) {
          await this.saveToIndexedDB('jugadores', jugador);
        }
        
        console.log('✅ Jugadores sincronizados:', jugadores.length);
      }
    } catch (error) {
      console.error('❌ Error sincronizando jugadores:', error);
    }
  }

  // 🎮 SINCRONIZAR PARTIDOS
  async syncPartidos() {
    try {
      // 🎯 Obtener partidos del servidor
      const response = await fetch('/partidos/historial');
      if (response.ok) {
        const partidos = await response.json();
        
        // 💾 Actualizar cache local
        for (const partido of partidos) {
          await this.saveToIndexedDB('partidos', partido);
        }
        
        console.log('✅ Partidos sincronizados:', partidos.length);
      }
    } catch (error) {
      console.error('❌ Error sincronizando partidos:', error);
    }
  }

  // 👥 SINCRONIZAR EQUIPOS TEMPORALES
  async syncEquiposTemporales() {
    try {
      // 🎯 Obtener equipos temporales del servidor
      const response = await fetch('/equipos/temporales');
      if (response.ok) {
        const equipos = await response.json();
        
        // 💾 Actualizar cache local
        for (const equipo of equipos) {
          await this.saveToIndexedDB('equiposTemporales', equipo);
        }
        
        console.log('✅ Equipos temporales sincronizados:', equipos.length);
      }
    } catch (error) {
      console.error('❌ Error sincronizando equipos temporales:', error);
    }
  }

  // 📱 MOSTRAR NOTIFICACIÓN
  showNotification(title, message, type = 'info') {
    try {
      // 🎯 Verificar si el navegador soporta notificaciones
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(title, {
          body: message,
          icon: '/icons/icon-192x192.png',
          badge: '/icons/icon-72x72.png',
          tag: 'fulbito-notification'
        });
      }
      
      // 🎯 Mostrar toast en la UI
      this.showToast(title, message, type);
    } catch (error) {
      console.error('❌ Error mostrando notificación:', error);
    }
  }

  // 🍞 MOSTRAR TOAST
  showToast(title, message, type) {
    // 🎯 Crear elemento toast
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <div class="toast-header">
        <strong>${title}</strong>
        <button type="button" class="toast-close" onclick="this.parentElement.parentElement.remove()">×</button>
      </div>
      <div class="toast-body">${message}</div>
    `;
    
    // 🎯 Agregar estilos
    toast.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      min-width: 300px;
      background: white;
      border: 1px solid #ddd;
      border-radius: 5px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
      animation: slideIn 0.3s ease-out;
    `;
    
    // 🎯 Agregar a la página
    document.body.appendChild(toast);
    
    // 🎯 Auto-remover después de 5 segundos
    setTimeout(() => {
      if (toast.parentElement) {
        toast.remove();
      }
    }, 5000);
  }

  // 📊 OBTENER ESTADÍSTICAS
  getStats() {
    try {
      return {
        isOnline: this.isOnline,
        syncQueueLength: this.syncQueue.length,
        lastSync: JSON.parse(localStorage.getItem('fulbito_config'))?.lastSync,
        dbSize: this.getDatabaseSize(),
        cacheSize: this.getCacheSize()
      };
    } catch (error) {
      console.error('❌ Error obteniendo estadísticas:', error);
      return {};
    }
  }

  // 📏 OBTENER TAMAÑO DE BASE DE DATOS
  getDatabaseSize() {
    // 🎯 Implementar cálculo de tamaño de IndexedDB
    return 'N/A';
  }

  // 📏 OBTENER TAMAÑO DE CACHE
  getCacheSize() {
    // 🎯 Implementar cálculo de tamaño de cache
    return 'N/A';
  }

  // 🧹 LIMPIAR CACHE
  async clearCache() {
    try {
      // 🎯 Limpiar IndexedDB
      const transaction = this.db.transaction(['cache'], 'readwrite');
      const store = transaction.objectStore('cache');
      await store.clear();
      
      // 🎯 Limpiar localStorage
      localStorage.removeItem('fulbito_sync_queue');
      localStorage.removeItem('fulbito_current_state');
      
      console.log('✅ Cache limpiado exitosamente');
    } catch (error) {
      console.error('❌ Error limpiando cache:', error);
    }
  }
}

// 🚀 INSTANCIAR OFFLINE MANAGER
let offlineManager;

// 🎯 Esperar a que el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
  offlineManager = new OfflineManager();
  
  // 🎯 Exponer globalmente para debugging
  window.offlineManager = offlineManager;
  
  console.log('🚀 Offline Manager cargado en DOM');
});

// 🌐 Exportar para uso en otros módulos
if (typeof module !== 'undefined' && module.exports) {
  module.exports = OfflineManager;
}
