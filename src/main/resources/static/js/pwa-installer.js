// 🚀 PWA INSTALLER - FULBITO
// Versión: 1.0.0
// Funcionalidad: Instalación PWA, promoción de instalación y gestión de app

class PWAInstaller {
  constructor() {
    this.deferredPrompt = null;
    this.isInstalled = false;
    this.installButton = null;
    this.installBanner = null;
    
    this.init();
    this.setupEventListeners();
  }

  // 🚀 INICIALIZACIÓN
  init() {
    console.log('🚀 Inicializando PWA Installer...');
    
    // 🎯 Verificar si ya está instalado
    this.checkInstallationStatus();
    
    // 🎯 Crear elementos de instalación
    this.createInstallElements();
    
    // 🎯 Solicitar permisos de notificación
    this.requestNotificationPermission();
    
    console.log('✅ PWA Installer inicializado');
  }

  // 🎯 CONFIGURAR EVENT LISTENERS
  setupEventListeners() {
    // 📱 Evento de instalación PWA
    window.addEventListener('beforeinstallprompt', (event) => {
      console.log('📱 Prompt de instalación PWA disponible');
      
      // 🎯 Prevenir que se muestre automáticamente
      event.preventDefault();
      
      // 🎯 Guardar el evento para usarlo después
      this.deferredPrompt = event;
      
      // 🎯 Mostrar botón de instalación
      this.showInstallButton();
    });

    // ✅ Evento de app instalada
    window.addEventListener('appinstalled', (event) => {
      console.log('✅ Fulbito instalado como PWA');
      
      // 🎯 Limpiar prompt diferido
      this.deferredPrompt = null;
      
      // 🎯 Ocultar elementos de instalación
      this.hideInstallElements();
      
      // 🎯 Mostrar mensaje de éxito
      this.showInstallationSuccess();
      
      // 🎯 Actualizar estado
      this.isInstalled = true;
      
      // 🎯 Guardar en localStorage
      localStorage.setItem('fulbito_installed', 'true');
      
      // 🎯 Analytics (opcional)
      this.trackInstallation();
    });

    // 🔄 Evento de cambio de visibilidad
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden) {
        this.checkInstallationStatus();
      }
    });
  }

  // 🎯 VERIFICAR ESTADO DE INSTALACIÓN
  checkInstallationStatus() {
    // 🎯 Verificar si está instalado
    if (window.matchMedia('(display-mode: standalone)').matches ||
        window.navigator.standalone === true ||
        localStorage.getItem('fulbito_installed') === 'true') {
      this.isInstalled = true;
      this.hideInstallElements();
      console.log('✅ Fulbito ya está instalado como PWA');
    } else {
      this.isInstalled = false;
      console.log('📱 Fulbito no está instalado como PWA');
    }
  }

  // 🎯 CREAR ELEMENTOS DE INSTALACIÓN
  createInstallElements() {
    // 🎯 Crear botón de instalación
    this.createInstallButton();
    
    // 🎯 Crear banner de instalación
    this.createInstallBanner();
    
    // 🎯 Crear indicador de estado
    this.createStatusIndicator();
  }

  // 📱 CREAR BOTÓN DE INSTALACIÓN
  createInstallButton() {
    // 🎯 Buscar si ya existe
    this.installButton = document.getElementById('pwa-install-btn');
    
    if (!this.installButton) {
      // 🎯 Crear botón
      this.installButton = document.createElement('button');
      this.installButton.id = 'pwa-install-btn';
      this.installButton.className = 'pwa-install-btn';
      this.installButton.innerHTML = `
        <span class="btn-icon">📱</span>
        <span class="btn-text">Instalar App</span>
      `;
      
      // 🎯 Agregar estilos
      this.installButton.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 9999;
        background: linear-gradient(135deg, #4CAF50, #2E7D32);
        color: white;
        border: none;
        border-radius: 50px;
        padding: 15px 25px;
        font-size: 16px;
        font-weight: bold;
        box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
        cursor: pointer;
        transition: all 0.3s ease;
        display: none;
        align-items: center;
        gap: 10px;
      `;
      
      // 🎯 Agregar hover effects
      this.installButton.addEventListener('mouseenter', () => {
        this.installButton.style.transform = 'translateY(-2px)';
        this.installButton.style.boxShadow = '0 6px 20px rgba(76, 175, 80, 0.4)';
      });
      
      this.installButton.addEventListener('mouseleave', () => {
        this.installButton.style.transform = 'translateY(0)';
        this.installButton.style.boxShadow = '0 4px 15px rgba(76, 175, 80, 0.3)';
      });
      
      // 🎯 Agregar click handler
      this.installButton.addEventListener('click', () => {
        this.installPWA();
      });
      
      // 🎯 Agregar a la página
      document.body.appendChild(this.installButton);
      
      console.log('✅ Botón de instalación PWA creado');
    }
  }

  // 🎯 CREAR BANNER DE INSTALACIÓN
  createInstallBanner() {
    // 🎯 Buscar si ya existe
    this.installBanner = document.getElementById('pwa-install-banner');
    
    if (!this.installBanner) {
      // 🎯 Crear banner
      this.installBanner = document.createElement('div');
      this.installBanner.id = 'pwa-install-banner';
      this.installBanner.className = 'pwa-install-banner';
      this.installBanner.innerHTML = `
        <div class="banner-content">
          <div class="banner-icon">⚽</div>
          <div class="banner-text">
            <h3>¡Instala Fulbito!</h3>
            <p>Accede rápido desde tu pantalla de inicio</p>
          </div>
          <div class="banner-actions">
            <button class="banner-install-btn" onclick="pwaInstaller.installPWA()">
              Instalar
            </button>
            <button class="banner-dismiss-btn" onclick="pwaInstaller.dismissBanner()">
              Más tarde
            </button>
          </div>
        </div>
        <button class="banner-close-btn" onclick="pwaInstaller.dismissBanner()">×</button>
      `;
      
      // 🎯 Agregar estilos
      this.installBanner.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        background: linear-gradient(135deg, #4CAF50, #2E7D32);
        color: white;
        padding: 15px 20px;
        z-index: 10000;
        transform: translateY(-100%);
        transition: transform 0.3s ease;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      `;
      
      // 🎯 Agregar estilos para el contenido
      const bannerContent = this.installBanner.querySelector('.banner-content');
      bannerContent.style.cssText = `
        display: flex;
        align-items: center;
        gap: 15px;
        max-width: 1200px;
        margin: 0 auto;
      `;
      
      // 🎯 Agregar estilos para el icono
      const bannerIcon = this.installBanner.querySelector('.banner-icon');
      bannerIcon.style.cssText = `
        font-size: 32px;
        flex-shrink: 0;
      `;
      
      // 🎯 Agregar estilos para el texto
      const bannerText = this.installBanner.querySelector('.banner-text');
      bannerText.style.cssText = `
        flex: 1;
      `;
      
      bannerText.querySelector('h3').style.cssText = `
        margin: 0 0 5px 0;
        font-size: 18px;
        font-weight: bold;
      `;
      
      bannerText.querySelector('p').style.cssText = `
        margin: 0;
        font-size: 14px;
        opacity: 0.9;
      `;
      
      // 🎯 Agregar estilos para los botones
      const bannerActions = this.installBanner.querySelector('.banner-actions');
      bannerActions.style.cssText = `
        display: flex;
        gap: 10px;
        flex-shrink: 0;
      `;
      
      const installBtn = bannerActions.querySelector('.banner-install-btn');
      installBtn.style.cssText = `
        background: white;
        color: #4CAF50;
        border: none;
        padding: 8px 16px;
        border-radius: 20px;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.2s ease;
      `;
      
      const dismissBtn = bannerActions.querySelector('.banner-dismiss-btn');
      dismissBtn.style.cssText = `
        background: transparent;
        color: white;
        border: 1px solid white;
        padding: 8px 16px;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.2s ease;
      `;
      
      // 🎯 Agregar estilos para el botón de cerrar
      const closeBtn = this.installBanner.querySelector('.banner-close-btn');
      closeBtn.style.cssText = `
        position: absolute;
        top: 10px;
        right: 15px;
        background: none;
        border: none;
        color: white;
        font-size: 24px;
        cursor: pointer;
        padding: 0;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
      `;
      
      // 🎯 Agregar a la página
      document.body.appendChild(this.installBanner);
      
      console.log('✅ Banner de instalación PWA creado');
    }
  }

  // 📊 CREAR INDICADOR DE ESTADO
  createStatusIndicator() {
    // 🎯 Buscar si ya existe
    let statusIndicator = document.getElementById('pwa-status-indicator');
    
    if (!statusIndicator) {
      // 🎯 Crear indicador
      statusIndicator = document.createElement('div');
      statusIndicator.id = 'pwa-status-indicator';
      statusIndicator.className = 'pwa-status-indicator';
      statusIndicator.innerHTML = `
        <span class="status-icon">📱</span>
        <span class="status-text">PWA</span>
      `;
      
      // 🎯 Agregar estilos
      statusIndicator.style.cssText = `
        position: fixed;
        top: 20px;
        left: 20px;
        z-index: 9998;
        background: rgba(0,0,0,0.7);
        color: white;
        padding: 8px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: bold;
        display: flex;
        align-items: center;
        gap: 5px;
        opacity: 0.8;
        transition: opacity 0.3s ease;
      `;
      
      // 🎯 Agregar a la página
      document.body.appendChild(statusIndicator);
      
      console.log('✅ Indicador de estado PWA creado');
    }
  }

  // 📱 MOSTRAR BOTÓN DE INSTALACIÓN
  showInstallButton() {
    if (this.installButton && !this.isInstalled) {
      this.installButton.style.display = 'flex';
      
      // 🎯 Mostrar banner después de 3 segundos
      setTimeout(() => {
        this.showInstallBanner();
      }, 3000);
      
      console.log('📱 Botón de instalación PWA mostrado');
    }
  }

  // 🎯 OCULTAR BOTÓN DE INSTALACIÓN
  hideInstallButton() {
    if (this.installButton) {
      this.installButton.style.display = 'none';
      console.log('📱 Botón de instalación PWA oculto');
    }
  }

  // 🎯 MOSTRAR BANNER DE INSTALACIÓN
  showInstallBanner() {
    if (this.installBanner && !this.isInstalled) {
      this.installBanner.style.transform = 'translateY(0)';
      
      // 🎯 Auto-ocultar después de 10 segundos
      setTimeout(() => {
        this.hideInstallBanner();
      }, 10000);
      
      console.log('🎯 Banner de instalación PWA mostrado');
    }
  }

  // 🎯 OCULTAR BANNER DE INSTALACIÓN
  hideInstallBanner() {
    if (this.installBanner) {
      this.installBanner.style.transform = 'translateY(-100%)';
      console.log('🎯 Banner de instalación PWA oculto');
    }
  }

  // 🎯 OCULTAR ELEMENTOS DE INSTALACIÓN
  hideInstallElements() {
    this.hideInstallButton();
    this.hideInstallBanner();
  }

  // 🚀 INSTALAR PWA
  async installPWA() {
    try {
      if (!this.deferredPrompt) {
        console.log('⚠️ No hay prompt de instalación disponible');
        this.showInstallationInstructions();
        return;
      }
      
      console.log('🚀 Iniciando instalación PWA...');
      
      // 🎯 Mostrar prompt de instalación
      this.deferredPrompt.prompt();
      
      // 🎯 Esperar respuesta del usuario
      const { outcome } = await this.deferredPrompt.userChoice;
      
      if (outcome === 'accepted') {
        console.log('✅ Usuario aceptó la instalación');
        this.showInstallationProgress();
      } else {
        console.log('❌ Usuario rechazó la instalación');
        this.showInstallationRejected();
      }
      
      // 🎯 Limpiar prompt
      this.deferredPrompt = null;
      
    } catch (error) {
      console.error('❌ Error durante la instalación:', error);
      this.showInstallationError();
    }
  }

  // 📋 MOSTRAR INSTRUCCIONES DE INSTALACIÓN
  showInstallationInstructions() {
    const instructions = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        max-width: 400px;
        text-align: center;
      ">
        <h3>📱 Instalar Fulbito</h3>
        <p>Para instalar Fulbito como app:</p>
        <ol style="text-align: left; margin: 20px 0;">
          <li>Presiona el botón de menú (⋮)</li>
          <li>Selecciona "Instalar app" o "Add to Home Screen"</li>
          <li>Confirma la instalación</li>
        </ol>
        <button onclick="this.parentElement.remove()" style="
          background: #4CAF50;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
        ">Entendido</button>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', instructions);
  }

  // 🔄 MOSTRAR PROGRESO DE INSTALACIÓN
  showInstallationProgress() {
    const progress = `
      <div id="install-progress" style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        text-align: center;
        min-width: 300px;
      ">
        <div style="font-size: 48px; margin-bottom: 20px;">⚡</div>
        <h3>Instalando Fulbito...</h3>
        <p>Por favor, espera mientras se instala la app</p>
        <div style="
          width: 100%;
          height: 4px;
          background: #f0f0f0;
          border-radius: 2px;
          margin: 20px 0;
          overflow: hidden;
        ">
          <div style="
            width: 0%;
            height: 100%;
            background: #4CAF50;
            border-radius: 2px;
            animation: progress 2s ease-in-out infinite;
          "></div>
        </div>
        <style>
          @keyframes progress {
            0% { width: 0%; }
            50% { width: 70%; }
            100% { width: 100%; }
          }
        </style>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', progress);
    
    // 🎯 Auto-remover después de 3 segundos
    setTimeout(() => {
      const progressElement = document.getElementById('install-progress');
      if (progressElement) {
        progressElement.remove();
      }
    }, 3000);
  }

  // ❌ MOSTRAR INSTALACIÓN RECHAZADA
  showInstallationRejected() {
    const rejected = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        text-align: center;
        max-width: 400px;
      ">
        <div style="font-size: 48px; margin-bottom: 20px;">😔</div>
        <h3>Instalación Cancelada</h3>
        <p>No te preocupes, puedes instalar Fulbito más tarde</p>
        <button onclick="this.parentElement.remove()" style="
          background: #4CAF50;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
          margin-top: 20px;
        ">Entendido</button>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', rejected);
  }

  // ❌ MOSTRAR ERROR DE INSTALACIÓN
  showInstallationError() {
    const error = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        text-align: center;
        max-width: 400px;
      ">
        <div style="font-size: 48px; margin-bottom: 20px;">⚠️</div>
        <h3>Error de Instalación</h3>
        <p>Hubo un problema al instalar Fulbito</p>
        <p>Intenta nuevamente más tarde</p>
        <button onclick="this.parentElement.remove()" style="
          background: #4CAF50;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
          margin-top: 20px;
        ">Entendido</button>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', error);
  }

  // ✅ MOSTRAR ÉXITO DE INSTALACIÓN
  showInstallationSuccess() {
    const success = `
      <div style="
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        z-index: 10001;
        text-align: center;
        max-width: 400px;
      ">
        <div style="font-size: 48px; margin-bottom: 20px;">🎉</div>
        <h3>¡Fulbito Instalado!</h3>
        <p>Ya puedes acceder a Fulbito desde tu pantalla de inicio</p>
        <p>La app funcionará offline y se actualizará automáticamente</p>
        <button onclick="this.parentElement.remove()" style="
          background: #4CAF50;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
          margin-top: 20px;
        ">¡Perfecto!</button>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', success);
    
    // 🎯 Auto-remover después de 8 segundos
    setTimeout(() => {
      const successElement = document.querySelector('div:has(> div:contains("🎉"))');
      if (successElement) {
        successElement.remove();
      }
    }, 8000);
  }

  // 🎯 DESESTIMAR BANNER
  dismissBanner() {
    this.hideInstallBanner();
    
    // 🎯 No mostrar banner por 24 horas
    localStorage.setItem('fulbito_banner_dismissed', Date.now().toString());
    
    console.log('🎯 Banner de instalación desestimado');
  }

  // 🔔 SOLICITAR PERMISO DE NOTIFICACIONES
  async requestNotificationPermission() {
    try {
      if ('Notification' in window && Notification.permission === 'default') {
        console.log('🔔 Solicitando permiso de notificaciones...');
        
        // 🎯 Mostrar solicitud después de 5 segundos
        setTimeout(async () => {
          const permission = await Notification.requestPermission();
          
          if (permission === 'granted') {
            console.log('✅ Permiso de notificaciones concedido');
            this.showNotificationPermissionGranted();
          } else {
            console.log('❌ Permiso de notificaciones denegado');
          }
        }, 5000);
      }
    } catch (error) {
      console.error('❌ Error solicitando permiso de notificaciones:', error);
    }
  }

  // 🔔 MOSTRAR PERMISO DE NOTIFICACIONES CONCEDIDO
  showNotificationPermissionGranted() {
    const notification = `
      <div style="
        position: fixed;
        bottom: 20px;
        left: 20px;
        background: #4CAF50;
        color: white;
        padding: 15px 20px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
        z-index: 9999;
        max-width: 300px;
        animation: slideInLeft 0.3s ease-out;
      ">
        <div style="display: flex; align-items: center; gap: 10px;">
          <span style="font-size: 20px;">🔔</span>
          <div>
            <strong>Notificaciones Activadas</strong>
            <p style="margin: 5px 0 0 0; font-size: 12px;">Recibirás alertas importantes</p>
          </div>
        </div>
        <style>
          @keyframes slideInLeft {
            from { transform: translateX(-100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
          }
        </style>
      </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', notification);
    
    // 🎯 Auto-remover después de 5 segundos
    setTimeout(() => {
      const notificationElement = document.querySelector('div:has(> div:contains("🔔"))');
      if (notificationElement) {
        notificationElement.remove();
      }
    }, 5000);
  }

  // 📊 SEGUIMIENTO DE INSTALACIÓN
  trackInstallation() {
    try {
      // 🎯 Aquí iría el código de analytics
      console.log('📊 Instalación PWA registrada para analytics');
      
      // 🎯 Ejemplo con Google Analytics (si estuviera configurado)
      if (typeof gtag !== 'undefined') {
        gtag('event', 'pwa_installed', {
          'event_category': 'engagement',
          'event_label': 'Fulbito PWA'
        });
      }
    } catch (error) {
      console.error('❌ Error en tracking de instalación:', error);
    }
  }

  // 📱 VERIFICAR SI DEBE MOSTRAR BANNER
  shouldShowBanner() {
    // 🎯 No mostrar si ya está instalado
    if (this.isInstalled) return false;
    
    // 🎯 No mostrar si fue desestimado recientemente
    const dismissed = localStorage.getItem('fulbito_banner_dismissed');
    if (dismissed) {
      const dismissedTime = parseInt(dismissed);
      const now = Date.now();
      const hoursSinceDismissed = (now - dismissedTime) / (1000 * 60 * 60);
      
      if (hoursSinceDismissed < 24) return false;
    }
    
    return true;
  }

  // 📊 OBTENER ESTADÍSTICAS
  getStats() {
    return {
      isInstalled: this.isInstalled,
      hasDeferredPrompt: !!this.deferredPrompt,
      notificationPermission: Notification.permission,
      bannerDismissed: localStorage.getItem('fulbito_banner_dismissed'),
      installTime: localStorage.getItem('fulbito_installed')
    };
  }

  // 🧹 LIMPIAR DATOS
  clearData() {
    try {
      localStorage.removeItem('fulbito_installed');
      localStorage.removeItem('fulbito_banner_dismissed');
      console.log('✅ Datos de instalación PWA limpiados');
    } catch (error) {
      console.error('❌ Error limpiando datos PWA:', error);
    }
  }
}

// 🚀 INSTANCIAR PWA INSTALLER
let pwaInstaller;

// 🎯 Esperar a que el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
  pwaInstaller = new PWAInstaller();
  
  // 🎯 Exponer globalmente para debugging
  window.pwaInstaller = pwaInstaller;
  
  console.log('🚀 PWA Installer cargado en DOM');
});

// 🌐 Exportar para uso en otros módulos
if (typeof module !== 'undefined' && module.exports) {
  module.exports = PWAInstaller;
}
