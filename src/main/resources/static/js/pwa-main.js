// 🚀 PWA MAIN - FULBITO
// Versión: 1.0.0
// Funcionalidad: Integración principal de todos los componentes PWA

class PWAApp {
  constructor() {
    this.offlineManager = null;
    this.pwaInstaller = null;
    this.mobileFeatures = null;
    this.isInitialized = false;
    this.initPromise = null;
    
    this.init();
  }

  // 🚀 INICIALIZACIÓN PRINCIPAL
  async init() {
    try {
      console.log('🚀 Inicializando Fulbito PWA...');
      
      // 🎯 Esperar a que el DOM esté listo
      if (document.readyState === 'loading') {
        await new Promise(resolve => {
          document.addEventListener('DOMContentLoaded', resolve);
        });
      }
      
      // 🎯 Registrar service worker
      await this.registerServiceWorker();
      
      // 🎯 Inicializar componentes
      await this.initializeComponents();
      
      // 🎯 Configurar integración
      this.setupIntegration();
      
      // 🎯 Marcar como inicializado
      this.isInitialized = true;
      
      console.log('✅ Fulbito PWA inicializado exitosamente');
      
      // 🎯 Mostrar mensaje de bienvenida
      this.showWelcomeMessage();
      
    } catch (error) {
      console.error('❌ Error inicializando Fulbito PWA:', error);
      this.showErrorMessage('Error de inicialización', error.message);
    }
  }

  // 🔄 REGISTRAR SERVICE WORKER
  async registerServiceWorker() {
    try {
      if ('serviceWorker' in navigator) {
        console.log('🔄 Registrando Service Worker...');
        
        const registration = await navigator.serviceWorker.register('/sw.js', {
          scope: '/'
        });
        
        console.log('✅ Service Worker registrado:', registration);
        
        // 🎯 Escuchar actualizaciones
        registration.addEventListener('updatefound', () => {
          console.log('🔄 Nueva versión del Service Worker disponible');
          this.showUpdateNotification();
        });
        
        // 🎯 Escuchar estado del service worker
        navigator.serviceWorker.addEventListener('controllerchange', () => {
          console.log('🔄 Service Worker activado');
          this.showServiceWorkerActivated();
        });
        
        return registration;
      } else {
        console.warn('⚠️ Service Worker no soportado');
        return null;
      }
    } catch (error) {
      console.error('❌ Error registrando Service Worker:', error);
      throw error;
    }
  }

  // 🎯 INICIALIZAR COMPONENTES
  async initializeComponents() {
    try {
      console.log('🎯 Inicializando componentes PWA...');
      
      // 🎯 Esperar a que los componentes estén disponibles
      await this.waitForComponents();
      
      // 🎯 Inicializar Offline Manager
      if (window.offlineManager) {
        this.offlineManager = window.offlineManager;
        console.log('✅ Offline Manager inicializado');
      }
      
      // 🎯 Inicializar PWA Installer
      if (window.pwaInstaller) {
        this.pwaInstaller = window.pwaInstaller;
        console.log('✅ PWA Installer inicializado');
      }
      
      // 🎯 Inicializar Mobile Features
      if (window.mobileFeatures) {
        this.mobileFeatures = window.mobileFeatures;
        console.log('✅ Mobile Features inicializado');
      }
      
      console.log('✅ Todos los componentes inicializados');
      
    } catch (error) {
      console.error('❌ Error inicializando componentes:', error);
      throw error;
    }
  }

  // ⏳ ESPERAR COMPONENTES
  async waitForComponents() {
    const maxWaitTime = 10000; // 10 segundos
    const checkInterval = 100; // 100ms
    let elapsedTime = 0;
    
    while (elapsedTime < maxWaitTime) {
      if (window.offlineManager && window.pwaInstaller && window.mobileFeatures) {
        return true;
      }
      
      await new Promise(resolve => setTimeout(resolve, checkInterval));
      elapsedTime += checkInterval;
    }
    
    throw new Error('Timeout esperando componentes PWA');
  }

  // 🔗 CONFIGURAR INTEGRACIÓN
  setupIntegration() {
    try {
      console.log('🔗 Configurando integración entre componentes...');
      
      // 🎯 Integrar notificaciones
      this.setupNotificationIntegration();
      
      // 🎯 Integrar eventos de conexión
      this.setupConnectionIntegration();
      
      // 🎯 Integrar eventos de instalación
      this.setupInstallationIntegration();
      
      // 🎯 Integrar eventos móviles
      this.setupMobileIntegration();
      
      console.log('✅ Integración configurada');
      
    } catch (error) {
      console.error('❌ Error configurando integración:', error);
    }
  }

  // 🔔 CONFIGURAR INTEGRACIÓN DE NOTIFICACIONES
  setupNotificationIntegration() {
    // 🎯 Escuchar notificaciones del Offline Manager
    if (this.offlineManager) {
      // 🎯 Aquí se integrarían las notificaciones entre componentes
      console.log('🔔 Integración de notificaciones configurada');
    }
  }

  // 🌐 CONFIGURAR INTEGRACIÓN DE CONEXIÓN
  setupConnectionIntegration() {
    // 🎯 Escuchar cambios de conexión
    window.addEventListener('online', () => {
      console.log('🌐 Conexión restaurada - PWA App');
      this.handleConnectionRestored();
    });
    
    window.addEventListener('offline', () => {
      console.log('🔌 Conexión perdida - PWA App');
      this.handleConnectionLost();
    });
    
    console.log('🌐 Integración de conexión configurada');
  }

  // 📱 CONFIGURAR INTEGRACIÓN DE INSTALACIÓN
  setupInstallationIntegration() {
    // 🎯 Escuchar eventos de instalación
    window.addEventListener('appinstalled', () => {
      console.log('📱 App instalada - PWA App');
      this.handleAppInstalled();
    });
    
    console.log('📱 Integración de instalación configurada');
  }

  // 📱 CONFIGURAR INTEGRACIÓN MÓVIL
  setupMobileIntegration() {
    // 🎯 Integrar eventos móviles con otros componentes
    if (this.mobileFeatures) {
      // 🎯 Aquí se integrarían los eventos móviles
      console.log('📱 Integración móvil configurada');
    }
  }

  // 🌐 MANEJAR CONEXIÓN RESTAURADA
  handleConnectionRestored() {
    try {
      // 🎯 Sincronizar datos
      if (this.offlineManager) {
        this.offlineManager.syncLocalData();
      }
      
      // 🎯 Mostrar notificación
      this.showNotification('Conexión Restaurada', 'Sincronizando datos...', 'success');
      
      // 🎯 Actualizar UI
      this.updateConnectionStatus(true);
      
    } catch (error) {
      console.error('❌ Error manejando conexión restaurada:', error);
    }
  }

  // 🔌 MANEJAR CONEXIÓN PERDIDA
  handleConnectionLost() {
    try {
      // 🎯 Mostrar notificación
      this.showNotification('Sin Conexión', 'Funcionando en modo offline', 'warning');
      
      // 🎯 Actualizar UI
      this.updateConnectionStatus(false);
      
    } catch (error) {
      console.error('❌ Error manejando conexión perdida:', error);
    }
  }

  // 📱 MANEJAR APP INSTALADA
  handleAppInstalled() {
    try {
      // 🎯 Mostrar mensaje de éxito
      this.showNotification('¡App Instalada!', 'Fulbito ya está en tu pantalla de inicio', 'success');
      
      // 🎯 Actualizar UI
      this.updateInstallationStatus(true);
      
      // 🎯 Analytics
      this.trackInstallation();
      
    } catch (error) {
      console.error('❌ Error manejando app instalada:', error);
    }
  }

  // 🔄 MOSTRAR NOTIFICACIÓN DE ACTUALIZACIÓN
  showUpdateNotification() {
    const notification = `
      <div id="update-notification" style="
        position: fixed;
        top: 20px;
        right: 20px;
        background: #2196F3;
        color: white;
        padding: 15px 20px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
        z-index: 10000;
        max-width: 300px;
        animation: slideInRight 0.3s ease-out;
      ">
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="font-size: 20px;">🔄</span>
          <div>
            <strong>Nueva Versión</strong>
            <p style="margin: 5px 0 0 0; font-size: 12px;">Actualiza para obtener las últimas mejoras</p>
          </div>
        </div>
        <button onclick="this.parentElement.remove()" style="
          position: absolute;
          top: 5px;
          right: 10px;
          background: none;
          border: none;
          color: white;
          font-size: 18px;
          cursor: pointer;
        ">×</button>
        <style>
          @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
          }
        </style>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', notification);
    
    // 🎯 Auto-remover después de 8 segundos
    setTimeout(() => {
      const notificationElement = document.getElementById('update-notification');
      if (notificationElement) {
        notificationElement.remove();
      }
    }, 8000);
  }

  // 🚀 MOSTRAR SERVICE WORKER ACTIVADO
  showServiceWorkerActivated() {
    const notification = `
      <div id="sw-activated" style="
        position: fixed;
        bottom: 20px;
        left: 20px;
        background: #4CAF50;
        color: white;
        padding: 10px 15px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(76, 175, 80, 0.3);
        z-index: 9999;
        font-size: 12px;
        animation: slideInLeft 0.3s ease-out;
      ">
        <span style="margin-right: 8px;">✅</span>
        Service Worker activado
        <style>
          @keyframes slideInLeft {
            from { transform: translateX(-100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
          }
        </style>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', notification);
    
    // 🎯 Auto-remover después de 3 segundos
    setTimeout(() => {
      const notificationElement = document.getElementById('sw-activated');
      if (notificationElement) {
        notificationElement.remove();
      }
    }, 3000);
  }

  // 🎯 MOSTRAR MENSAJE DE BIENVENIDA
  showWelcomeMessage() {
    const welcome = `
      <div id="welcome-message" style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 15px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        text-align: center;
        max-width: 400px;
        animation: fadeIn 0.5s ease-out;
      ">
        <div style="font-size: 64px; margin-bottom: 20px;">⚽</div>
        <h2>¡Bienvenido a Fulbito PWA!</h2>
        <p>Tu app de gestión de equipos ahora funciona offline</p>
        <div style="margin: 20px 0;">
          <div style="display: flex; align-items: center; gap: 10px; margin: 10px 0; justify-content: center;">
            <span>📱</span>
            <span>Instalable como app</span>
          </div>
          <div style="display: flex; align-items: center; gap: 10px; margin: 10px 0; justify-content: center;">
            <span>🔌</span>
            <span>Funciona sin internet</span>
          </div>
          <div style="display: flex; align-items: center; gap: 10px; margin: 10px 0; justify-content: center;">
            <span>👆</span>
            <span>Touch gestures</span>
          </div>
        </div>
        <button onclick="this.parentElement.remove()" style="
          background: #4CAF50;
          color: white;
          border: none;
          padding: 12px 24px;
          border-radius: 25px;
          cursor: pointer;
          font-size: 16px;
          font-weight: bold;
        ">¡Perfecto!</button>
        <style>
          @keyframes fadeIn {
            from { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
            to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
          }
        </style>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', welcome);
    
    // 🎯 Auto-remover después de 10 segundos
    setTimeout(() => {
      const welcomeElement = document.getElementById('welcome-message');
      if (welcomeElement) {
        welcomeElement.remove();
      }
    }, 10000);
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

  // ❌ MOSTRAR MENSAJE DE ERROR
  showErrorMessage(title, message) {
    const error = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: #f44336;
        color: white;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(244, 67, 54, 0.3);
        z-index: 10001;
        text-align: center;
        max-width: 400px;
      ">
        <div style="font-size: 48px; margin-bottom: 15px;">⚠️</div>
        <h3>${title}</h3>
        <p>${message}</p>
        <button onclick="this.parentElement.remove()" style="
          background: white;
          color: #f44336;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
          margin-top: 15px;
        ">Entendido</button>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', error);
  }

  // 🌐 ACTUALIZAR ESTADO DE CONEXIÓN
  updateConnectionStatus(isOnline) {
    // 🎯 Actualizar indicadores en la UI
    const indicators = document.querySelectorAll('.connection-status, .online-indicator');
    
    indicators.forEach(indicator => {
      if (isOnline) {
        indicator.classList.add('online');
        indicator.classList.remove('offline');
        indicator.textContent = '🌐 En línea';
      } else {
        indicator.classList.add('offline');
        indicator.classList.remove('online');
        indicator.textContent = '🔌 Sin conexión';
      }
    });
  }

  // 📱 ACTUALIZAR ESTADO DE INSTALACIÓN
  updateInstallationStatus(isInstalled) {
    // 🎯 Actualizar indicadores en la UI
    const indicators = document.querySelectorAll('.installation-status, .pwa-indicator');
    
    indicators.forEach(indicator => {
      if (isInstalled) {
        indicator.classList.add('installed');
        indicator.classList.remove('not-installed');
        indicator.textContent = '✅ Instalado';
      } else {
        indicator.classList.add('not-installed');
        indicator.classList.remove('installed');
        indicator.textContent = '📱 No instalado';
      }
    });
  }

  // 📊 SEGUIMIENTO DE INSTALACIÓN
  trackInstallation() {
    try {
      console.log('📊 Instalación PWA registrada para analytics');
      
      // 🎯 Ejemplo con Google Analytics (si estuviera configurado)
      if (typeof gtag !== 'undefined') {
        gtag('event', 'pwa_installed', {
          'event_category': 'engagement',
          'event_label': 'Fulbito PWA'
        });
      }
      
      // 🎯 Guardar en localStorage
      localStorage.setItem('fulbito_installation_tracked', Date.now().toString());
      
    } catch (error) {
      console.error('❌ Error en tracking de instalación:', error);
    }
  }

  // 📊 OBTENER ESTADÍSTICAS COMPLETAS
  getStats() {
    try {
      const stats = {
        pwaApp: {
          isInitialized: this.isInitialized,
          components: {
            offlineManager: !!this.offlineManager,
            pwaInstaller: !!this.pwaInstaller,
            mobileFeatures: !!this.mobileFeatures
          }
        },
        offlineManager: this.offlineManager ? this.offlineManager.getStats() : null,
        pwaInstaller: this.pwaInstaller ? this.pwaInstaller.getStats() : null,
        mobileFeatures: this.mobileFeatures ? this.mobileFeatures.getStats() : null,
        serviceWorker: 'serviceWorker' in navigator,
        connection: navigator.onLine,
        timestamp: new Date().toISOString()
      };
      
      return stats;
      
    } catch (error) {
      console.error('❌ Error obteniendo estadísticas:', error);
      return { error: error.message };
    }
  }

  // 🧹 LIMPIAR RECURSOS
  cleanup() {
    try {
      // 🎯 Limpiar componentes
      if (this.offlineManager) {
        this.offlineManager.cleanup();
      }
      
      if (this.mobileFeatures) {
        this.mobileFeatures.cleanup();
      }
      
      // 🎯 Limpiar event listeners
      // 🎯 Limpiar timeouts
      
      console.log('✅ PWA App limpiado');
      
    } catch (error) {
      console.error('❌ Error limpiando PWA App:', error);
    }
  }

  // 🔄 REINICIALIZAR
  async reinitialize() {
    try {
      console.log('🔄 Reinicializando Fulbito PWA...');
      
      // 🎯 Limpiar recursos actuales
      this.cleanup();
      
      // 🎯 Reinicializar
      await this.init();
      
      console.log('✅ Fulbito PWA reinicializado');
      
    } catch (error) {
      console.error('❌ Error reinicializando:', error);
    }
  }
}

// 🚀 INSTANCIAR PWA APP
let pwaApp;

// 🎯 Esperar a que el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
  pwaApp = new PWAApp();
  
  // 🎯 Exponer globalmente para debugging
  window.pwaApp = pwaApp;
  
  console.log('🚀 PWA App cargado en DOM');
});

// 🌐 Exportar para uso en otros módulos
if (typeof module !== 'undefined' && module.exports) {
  module.exports = PWAApp;
}
