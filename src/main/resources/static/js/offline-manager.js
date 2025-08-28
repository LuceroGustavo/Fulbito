// 🚀 OFFLINE MANAGER - FULBITO PWA
// Versión: 2.0.0 - MODO OFFLINE COMPLETO
// Funcionalidad: Gestión 100% offline, sin conexión a servidor
class OfflineManager {
    constructor() {
        this.db = null;
        this.isOnline = navigator.onLine;
        this.syncQueue = [];
        this.connectionStatus = 'offline';
        this.init();
    }

    async init() {
        await this.initDatabase();
        this.initLocalStorage();
        this.setupEventListeners();
        this.checkConnectionStatus();
        this.loadSampleData(); // 🚀 CARGAR DATOS DE EJEMPLO
        console.log('🚀 Offline Manager inicializado - MODO OFFLINE COMPLETO');
    }

    setupEventListeners() {
        window.addEventListener('online', () => this.handleOnline());
        window.addEventListener('offline', () => this.handleOffline());
        document.addEventListener('visibilitychange', () => this.handleVisibilityChange());
        window.addEventListener('beforeunload', () => this.saveAppState());
    }

    async initDatabase() {
        try {
            this.db = await this.openIndexedDB();
            console.log('🚀 IndexedDB inicializado para almacenamiento offline');
        } catch (error) {
            console.error('❌ Error inicializando IndexedDB:', error);
            this.fallbackToLocalStorage();
        }
    }

    openIndexedDB() {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open('FulbitoPWA', 2);
            
            request.onerror = () => reject(request.error);
            request.onsuccess = () => resolve(request.result);
            
            request.onupgradeneeded = (event) => {
                const db = event.target.result;
                
                // 🚀 STORE: JUGADORES
                if (!db.objectStoreNames.contains('jugadores')) {
                    const jugadoresStore = db.createObjectStore('jugadores', { keyPath: 'id', autoIncrement: true });
                    jugadoresStore.createIndex('nombre', 'nombre', { unique: false });
                    jugadoresStore.createIndex('esArquero', 'esArquero', { unique: false });
                    jugadoresStore.createIndex('calificacionTotal', 'calificacionTotal', { unique: false });
                }
                
                // 🚀 STORE: PARTIDOS
                if (!db.objectStoreNames.contains('partidos')) {
                    const partidosStore = db.createObjectStore('partidos', { keyPath: 'id', autoIncrement: true });
                    partidosStore.createIndex('fecha', 'fecha', { unique: false });
                    partidosStore.createIndex('estado', 'estado', { unique: false });
                }
                
                // 🚀 STORE: EQUIPOS TEMPORALES
                if (!db.objectStoreNames.contains('equiposTemporales')) {
                    const equiposStore = db.createObjectStore('equiposTemporales', { keyPath: 'sessionId' });
                    equiposStore.createIndex('fechaCreacion', 'fechaCreacion', { unique: false });
                }
                
                // 🚀 STORE: CACHE
                if (!db.objectStoreNames.contains('cache')) {
                    const cacheStore = db.createObjectStore('cache', { keyPath: 'key' });
                    cacheStore.createIndex('tipo', 'tipo', { unique: false });
                    cacheStore.createIndex('fecha', 'fecha', { unique: false });
                }
            };
        });
    }

    initLocalStorage() {
        // 🚀 CONFIGURACIÓN PWA
        if (!localStorage.getItem('fulbito_pwa_config')) {
            localStorage.setItem('fulbito_pwa_config', JSON.stringify({
                version: '2.0.0',
                modo: 'offline_completo',
                fechaInstalacion: new Date().toISOString(),
                tema: 'auto',
                notificaciones: true,
                sonidos: true,
                vibracion: true
            }));
        }

        // 🚀 ESTADO DE LA APP
        if (!localStorage.getItem('fulbito_app_state')) {
            localStorage.setItem('fulbito_app_state', JSON.stringify({
                ultimaSincronizacion: null,
                modoOffline: true,
                datosCargados: false,
                preferenciasUsuario: {}
            }));
        }
    }

    // 🚀 CARGAR DATOS DE EJEMPLO
    async loadSampleData() {
        try {
            const jugadoresExistentes = await this.getAllJugadores();
            
            if (jugadoresExistentes.length === 0) {
                console.log('🚀 Cargando datos de ejemplo...');
                await this.cargarJugadoresEjemplo();
                await this.cargarPartidosEjemplo();
                console.log('✅ Datos de ejemplo cargados exitosamente');
            } else {
                console.log('🚀 Datos ya existen en IndexedDB');
            }
        } catch (error) {
            console.error('❌ Error cargando datos de ejemplo:', error);
        }
    }

    // 🚀 JUGADORES DE EJEMPLO
    async cargarJugadoresEjemplo() {
        const jugadoresEjemplo = [
            {
                id: 1,
                nombre: 'Kevin',
                edad: 31,
                esArquero: false,
                velocidad: 8,
                habilidad: 7,
                fuerzaFisica: 6,
                tiro: 9,
                tactica: 7,
                calificacionTotal: 7.4,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 2,
                nombre: 'Urti',
                edad: 28,
                esArquero: false,
                velocidad: 9,
                habilidad: 8,
                fuerzaFisica: 7,
                tiro: 8,
                tactica: 8,
                calificacionTotal: 8.0,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 3,
                nombre: 'Cona',
                edad: 26,
                esArquero: false,
                velocidad: 8,
                habilidad: 7,
                fuerzaFisica: 6,
                tiro: 7,
                tactica: 7,
                calificacionTotal: 7.0,
                nivelEdad: 'Joven',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 4,
                nombre: 'Ale',
                edad: 35,
                esArquero: true,
                velocidad: 6,
                habilidad: 8,
                fuerzaFisica: 7,
                tiro: 5,
                tactica: 9,
                calificacionTotal: 7.0,
                nivelEdad: 'Experimentado',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 5,
                nombre: 'Nano',
                edad: 24,
                esArquero: false,
                velocidad: 9,
                habilidad: 6,
                fuerzaFisica: 8,
                tiro: 7,
                tactica: 6,
                calificacionTotal: 7.2,
                nivelEdad: 'Joven',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 6,
                nombre: 'Marianp',
                edad: 29,
                esArquero: false,
                velocidad: 7,
                habilidad: 8,
                fuerzaFisica: 6,
                tiro: 8,
                tactica: 7,
                calificacionTotal: 7.2,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 7,
                nombre: 'Kevin 2',
                edad: 27,
                esArquero: false,
                velocidad: 8,
                habilidad: 7,
                fuerzaFisica: 7,
                tiro: 7,
                tactica: 8,
                calificacionTotal: 7.4,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 8,
                nombre: 'Tecla',
                edad: 32,
                esArquero: false,
                velocidad: 6,
                habilidad: 9,
                fuerzaFisica: 5,
                tiro: 9,
                tactica: 8,
                calificacionTotal: 7.4,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 9,
                nombre: 'Hector',
                edad: 30,
                esArquero: false,
                velocidad: 7,
                habilidad: 6,
                fuerzaFisica: 8,
                tiro: 6,
                tactica: 7,
                calificacionTotal: 6.8,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 10,
                nombre: 'Greco',
                edad: 33,
                esArquero: true,
                velocidad: 5,
                habilidad: 7,
                fuerzaFisica: 6,
                tiro: 4,
                tactica: 8,
                calificacionTotal: 6.0,
                nivelEdad: 'Experimentado',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 11,
                nombre: 'Edu',
                edad: 25,
                esArquero: false,
                velocidad: 9,
                habilidad: 8,
                fuerzaFisica: 7,
                tiro: 8,
                tactica: 7,
                calificacionTotal: 7.8,
                nivelEdad: 'Joven',
                fechaCreacion: new Date().toISOString()
            },
            {
                id: 12,
                nombre: 'Pacha',
                edad: 31,
                esArquero: false,
                velocidad: 7,
                habilidad: 7,
                fuerzaFisica: 8,
                tiro: 6,
                tactica: 8,
                calificacionTotal: 7.2,
                nivelEdad: 'Intermedio',
                fechaCreacion: new Date().toISOString()
            }
        ];

        for (const jugador of jugadoresEjemplo) {
            await this.saveJugador(jugador);
        }
    }

    // 🚀 PARTIDOS DE EJEMPLO
    async cargarPartidosEjemplo() {
        const partidosEjemplo = [
            {
                id: 1,
                fecha: '2025-08-28',
                hora: '20:00',
                lugar: 'Mega Fútbol',
                precio: 5600,
                observaciones: 'Sin observaciones especiales',
                estado: 'completado',
                fechaCreacion: '2025-08-28 12:50',
                jugadores: 8,
                equipoA: ['Marianp', 'Tecla', 'Kevin', 'Cona'],
                equipoB: ['Nano', 'Ale', 'Hector', 'Greco'],
                calificacionEquipoA: 7.0,
                calificacionEquipoB: 6.8,
                diferencia: 0.2
            },
            {
                id: 2,
                fecha: '2025-08-29',
                hora: '21:00',
                lugar: 'El campito',
                precio: 5000,
                observaciones: 'No falten putos',
                estado: 'completado',
                fechaCreacion: '2025-08-27 15:48',
                jugadores: 10,
                equipoA: ['Ari', 'Marianp', 'Pacha', 'Kevin', 'Mauri'],
                equipoB: ['Nano', 'Ale', 'Hector', 'Greco', 'Edu'],
                calificacionEquipoA: 6.1,
                calificacionEquipoB: 6.6,
                diferencia: 0.48
            }
        ];

        for (const partido of partidosEjemplo) {
            await this.savePartido(partido);
        }
    }

    checkConnectionStatus() {
        this.connectionStatus = this.isOnline ? 'online' : 'offline';
        this.updateUI();
        console.log(`🚀 Estado de conexión: ${this.connectionStatus}`);
    }

    handleOnline() {
        this.isOnline = true;
        this.connectionStatus = 'online';
        this.updateUI();
        this.showNotification('Conexión Restaurada', 'La aplicación está funcionando online', 'success');
        console.log('🚀 Conexión online restaurada');
    }

    handleOffline() {
        this.isOnline = false;
        this.connectionStatus = 'offline';
        this.updateUI();
        this.showNotification('Modo Offline', 'La aplicación funciona sin conexión', 'info');
        console.log('🚀 Modo offline activado');
    }

    handleVisibilityChange() {
        if (!document.hidden) {
            this.checkConnectionStatus();
        }
    }

    updateUI() {
        const statusElement = document.getElementById('pwa-status');
        const statusText = document.getElementById('pwa-status-text');
        
        if (statusElement && statusText) {
            statusElement.className = `pwa-status ${this.connectionStatus}`;
            statusText.textContent = this.connectionStatus === 'online' ? 'Online' : 'Offline';
            statusElement.style.display = 'block';
        }
    }

    // 🚀 CRUD JUGADORES (100% OFFLINE)
    async saveJugador(jugador) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['jugadores'], 'readwrite');
                const store = transaction.objectStore('jugadores');
                
                if (jugador.id) {
                    await store.put(jugador);
                } else {
                    jugador.id = Date.now();
                    await store.add(jugador);
                }
                
                console.log('✅ Jugador guardado en IndexedDB:', jugador.nombre);
                return jugador;
            } else {
                // Fallback a LocalStorage
                const jugadores = JSON.parse(localStorage.getItem('fulbito_jugadores') || '[]');
                if (jugador.id) {
                    const index = jugadores.findIndex(j => j.id === jugador.id);
                    if (index !== -1) {
                        jugadores[index] = jugador;
                    }
                } else {
                    jugador.id = Date.now();
                    jugadores.push(jugador);
                }
                localStorage.setItem('fulbito_jugadores', JSON.stringify(jugadores));
                return jugador;
            }
        } catch (error) {
            console.error('❌ Error guardando jugador:', error);
            throw error;
        }
    }

    async getAllJugadores() {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['jugadores'], 'readonly');
                const store = transaction.objectStore('jugadores');
                const request = store.getAll();
                
                return new Promise((resolve, reject) => {
                    request.onsuccess = () => resolve(request.result || []);
                    request.onerror = () => reject(request.error);
                });
            } else {
                // Fallback a LocalStorage
                return JSON.parse(localStorage.getItem('fulbito_jugadores') || '[]');
            }
        } catch (error) {
            console.error('❌ Error obteniendo jugadores:', error);
            return [];
        }
    }

    async getJugadorById(id) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['jugadores'], 'readonly');
                const store = transaction.objectStore('jugadores');
                const request = store.get(id);
                
                return new Promise((resolve, reject) => {
                    request.onsuccess = () => resolve(request.result);
                    request.onerror = () => reject(request.error);
                });
            } else {
                // Fallback a LocalStorage
                const jugadores = JSON.parse(localStorage.getItem('fulbito_jugadores') || '[]');
                return jugadores.find(j => j.id === id);
            }
        } catch (error) {
            console.error('❌ Error obteniendo jugador:', error);
            return null;
        }
    }

    async deleteJugador(id) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['jugadores'], 'readwrite');
                const store = transaction.objectStore('jugadores');
                await store.delete(id);
                console.log('✅ Jugador eliminado de IndexedDB');
            } else {
                // Fallback a LocalStorage
                const jugadores = JSON.parse(localStorage.getItem('fulbito_jugadores') || '[]');
                const filtered = jugadores.filter(j => j.id !== id);
                localStorage.setItem('fulbito_jugadores', JSON.stringify(filtered));
            }
        } catch (error) {
            console.error('❌ Error eliminando jugador:', error);
            throw error;
        }
    }

    // 🚀 CRUD PARTIDOS (100% OFFLINE)
    async savePartido(partido) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['partidos'], 'readwrite');
                const store = transaction.objectStore('partidos');
                
                if (partido.id) {
                    await store.put(partido);
                } else {
                    partido.id = Date.now();
                    await store.add(partido);
                }
                
                console.log('✅ Partido guardado en IndexedDB');
                return partido;
            } else {
                // Fallback a LocalStorage
                const partidos = JSON.parse(localStorage.getItem('fulbito_partidos') || '[]');
                if (partido.id) {
                    const index = partidos.findIndex(p => p.id === partido.id);
                    if (index !== -1) {
                        partidos[index] = partido;
                    }
                } else {
                    partido.id = Date.now();
                    partidos.push(partido);
                }
                localStorage.setItem('fulbito_partidos', JSON.stringify(partidos));
                return partido;
            }
        } catch (error) {
            console.error('❌ Error guardando partido:', error);
            throw error;
        }
    }

    async getAllPartidos() {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['partidos'], 'readonly');
                const store = transaction.objectStore('partidos');
                const request = store.getAll();
                
                return new Promise((resolve, reject) => {
                    request.onsuccess = () => resolve(request.result || []);
                    request.onerror = () => reject(request.error);
                });
            } else {
                // Fallback a LocalStorage
                return JSON.parse(localStorage.getItem('fulbito_partidos') || '[]');
            }
        } catch (error) {
            console.error('❌ Error obteniendo partidos:', error);
            return [];
        }
    }

    // 🚀 CRUD EQUIPOS TEMPORALES (100% OFFLINE)
    async saveEquipoTemporal(equipo) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['equiposTemporales'], 'readwrite');
                const store = transaction.objectStore('equiposTemporales');
                await store.put(equipo);
                console.log('✅ Equipo temporal guardado en IndexedDB');
            } else {
                // Fallback a LocalStorage
                const equipos = JSON.parse(localStorage.getItem('fulbito_equipos_temporales') || '[]');
                const index = equipos.findIndex(e => e.sessionId === equipo.sessionId);
                if (index !== -1) {
                    equipos[index] = equipo;
                } else {
                    equipos.push(equipo);
                }
                localStorage.setItem('fulbito_equipos_temporales', JSON.stringify(equipos));
            }
        } catch (error) {
            console.error('❌ Error guardando equipo temporal:', error);
            throw error;
        }
    }

    async getEquipoTemporal(sessionId) {
        try {
            if (this.db) {
                const transaction = this.db.transaction(['equiposTemporales'], 'readonly');
                const store = transaction.objectStore('equiposTemporales');
                const request = store.get(sessionId);
                
                return new Promise((resolve, reject) => {
                    request.onsuccess = () => resolve(request.result);
                    request.onerror = () => reject(request.error);
                });
            } else {
                // Fallback a LocalStorage
                const equipos = JSON.parse(localStorage.getItem('fulbito_equipos_temporales') || '[]');
                return equipos.find(e => e.sessionId === sessionId);
            }
        } catch (error) {
            console.error('❌ Error obteniendo equipo temporal:', error);
            return null;
        }
    }

    // 🚀 UTILIDADES
    async getEstadisticas() {
        try {
            const jugadores = await this.getAllJugadores();
            const partidos = await this.getAllPartidos();
            
            const totalJugadores = jugadores.length;
            const totalArqueros = jugadores.filter(j => j.esArquero).length;
            const totalPartidos = partidos.length;
            
            return {
                totalJugadores,
                totalArqueros,
                totalPartidos
            };
        } catch (error) {
            console.error('❌ Error obteniendo estadísticas:', error);
            return { totalJugadores: 0, totalArqueros: 0, totalPartidos: 0 };
        }
    }

    fallbackToLocalStorage() {
        console.log('⚠️ Fallback a LocalStorage - IndexedDB no disponible');
        this.db = null;
    }

    saveAppState() {
        try {
            const appState = {
                ultimaSincronizacion: new Date().toISOString(),
                modoOffline: true,
                datosCargados: true,
                preferenciasUsuario: JSON.parse(localStorage.getItem('fulbito_preferencias') || '{}')
            };
            localStorage.setItem('fulbito_app_state', JSON.stringify(appState));
        } catch (error) {
            console.error('❌ Error guardando estado de la app:', error);
        }
    }

    showNotification(title, message, type = 'info') {
        // 🚀 NOTIFICACIÓN TOAST
        const toast = document.createElement('div');
        toast.className = `toast toast-${type} position-fixed top-0 end-0 m-3`;
        toast.style.zIndex = '9999';
        toast.innerHTML = `
            <div class="toast-header">
                <strong class="me-auto">${title}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">${message}</div>
        `;
        
        document.body.appendChild(toast);
        
        // Mostrar toast
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // Remover después de 5 segundos
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 5000);
    }

    // 🚀 MÉTODOS DE SINCRONIZACIÓN (NO USADOS EN MODO OFFLINE)
    addToSyncQueue(type, action, data) {
        // 🚀 NO SE USA EN MODO OFFLINE COMPLETO
        console.log('⚠️ Sincronización no disponible en modo offline completo');
    }

    async processSyncQueue() {
        // 🚀 NO SE USA EN MODO OFFLINE COMPLETO
        console.log('⚠️ Sincronización no disponible en modo offline completo');
    }

    async syncLocalData() {
        // 🚀 NO SE USA EN MODO OFFLINE COMPLETO
        console.log('⚠️ Sincronización no disponible en modo offline completo');
    }
}

// 🚀 INICIALIZACIÓN
let offlineManager;
document.addEventListener('DOMContentLoaded', () => {
    offlineManager = new OfflineManager();
});

// 🚀 EXPORTAR PARA USO GLOBAL
window.OfflineManager = OfflineManager;
window.offlineManager = offlineManager;
