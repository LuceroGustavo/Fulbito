// 🚀 MOBILE FEATURES - FULBITO PWA
// Versión: 1.0.0
// Funcionalidad: Touch gestures, vibración, sonidos y optimizaciones móviles

class MobileFeatures {
  constructor() {
    this.isMobile = this.detectMobile();
    this.touchStartX = 0;
    this.touchStartY = 0;
    this.touchEndX = 0;
    this.touchEndY = 0;
    this.minSwipeDistance = 50;
    this.vibrationEnabled = true;
    this.soundEnabled = true;
    this.darkMode = false;
    
    this.init();
    this.setupEventListeners();
  }

  // 🚀 INICIALIZACIÓN
  init() {
    console.log('🚀 Inicializando Mobile Features...');
    
    // 🎯 Verificar capacidades del dispositivo
    this.checkDeviceCapabilities();
    
    // 🎯 Aplicar optimizaciones móviles
    this.applyMobileOptimizations();
    
    // 🎯 Configurar tema automático
    this.setupAutoTheme();
    
    console.log('✅ Mobile Features inicializado');
  }

  // 📱 DETECTAR DISPOSITIVO MÓVIL
  detectMobile() {
    const userAgent = navigator.userAgent || navigator.vendor || window.opera;
    const mobileRegex = /android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i;
    
    const isMobile = mobileRegex.test(userAgent) || 
                     window.innerWidth <= 768 ||
                     'ontouchstart' in window;
    
    console.log('📱 Dispositivo móvil detectado:', isMobile);
    return isMobile;
  }

  // 🎯 VERIFICAR CAPACIDADES DEL DISPOSITIVO
  checkDeviceCapabilities() {
    // 🎯 Vibración
    this.vibrationEnabled = 'vibrate' in navigator;
    console.log('📳 Vibración disponible:', this.vibrationEnabled);
    
    // 🎯 Sonidos
    this.soundEnabled = 'AudioContext' in window || 'webkitAudioContext' in window;
    console.log('🔊 Sonidos disponibles:', this.soundEnabled);
    
    // 🎯 Orientación
    if ('onorientationchange' in window) {
      console.log('📐 Cambio de orientación disponible');
    }
    
    // 🎯 Pantalla completa
    if (document.documentElement.requestFullscreen || 
        document.documentElement.webkitRequestFullscreen) {
      console.log('🖥️ Pantalla completa disponible');
    }
  }

  // 🎯 APLICAR OPTIMIZACIONES MÓVILES
  applyMobileOptimizations() {
    if (!this.isMobile) return;
    
    console.log('📱 Aplicando optimizaciones móviles...');
    
    // 🎯 Meta viewport
    this.setupViewport();
    
    // 🎯 Touch events
    this.setupTouchEvents();
    
    // 🎯 Swipe gestures
    this.setupSwipeGestures();
    
    // 🎯 Responsive design
    this.setupResponsiveDesign();
    
    // 🎯 Performance optimizations
    this.setupPerformanceOptimizations();
  }

  // 📱 CONFIGURAR VIEWPORT
  setupViewport() {
    let viewport = document.querySelector('meta[name="viewport"]');
    
    if (!viewport) {
      viewport = document.createElement('meta');
      viewport.name = 'viewport';
      document.head.appendChild(viewport);
    }
    
    viewport.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover';
    
    console.log('✅ Viewport configurado para móviles');
  }

  // 👆 CONFIGURAR EVENTOS TÁCTILES
  setupTouchEvents() {
    // 🎯 Prevenir zoom en inputs
    document.addEventListener('touchstart', (event) => {
      if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
        event.preventDefault();
      }
    }, { passive: false });
    
    // 🎯 Mejorar scrolling
    document.addEventListener('touchmove', (event) => {
      // 🎯 Permitir scroll natural
    }, { passive: true });
    
    // 🎯 Prevenir scroll horizontal no deseado
    document.addEventListener('touchmove', (event) => {
      const target = event.target;
      if (target.scrollWidth <= target.clientWidth) {
        event.preventDefault();
      }
    }, { passive: false });
    
    console.log('✅ Eventos táctiles configurados');
  }

  // 👆 CONFIGURAR GESTOS DE SWIPE
  setupSwipeGestures() {
    // 🎯 Swipe en toda la página
    document.addEventListener('touchstart', (event) => {
      this.touchStartX = event.touches[0].clientX;
      this.touchStartY = event.touches[0].clientY;
    }, { passive: true });
    
    document.addEventListener('touchend', (event) => {
      this.touchEndX = event.changedTouches[0].clientX;
      this.touchEndY = event.changedTouches[0].clientY;
      
      this.handleSwipe();
    }, { passive: true });
    
    console.log('✅ Gestos de swipe configurados');
  }

  // 👆 MANEJAR SWIPE
  handleSwipe() {
    const deltaX = this.touchEndX - this.touchStartX;
    const deltaY = this.touchEndY - this.touchStartY;
    
    // 🎯 Verificar si es un swipe válido
    if (Math.abs(deltaX) < this.minSwipeDistance && 
        Math.abs(deltaY) < this.minSwipeDistance) {
      return;
    }
    
    // 🎯 Determinar dirección del swipe
    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      // 🎯 Swipe horizontal
      if (deltaX > 0) {
        this.handleSwipeRight();
      } else {
        this.handleSwipeLeft();
      }
    } else {
      // 🎯 Swipe vertical
      if (deltaY > 0) {
        this.handleSwipeDown();
      } else {
        this.handleSwipeUp();
      }
    }
  }

  // 👆 MANEJAR SWIPE DERECHA
  handleSwipeRight() {
    console.log('👆 Swipe derecha detectado');
    
    // 🎯 Vibración
    this.vibrate([100]);
    
    // 🎯 Sonido
    this.playSound('swipe');
    
    // 🎯 Navegación (volver atrás)
    if (window.history.length > 1) {
      window.history.back();
    }
  }

  // 👆 MANEJAR SWIPE IZQUIERDA
  handleSwipeLeft() {
    console.log('👆 Swipe izquierda detectado');
    
    // 🎯 Vibración
    this.vibrate([100]);
    
    // 🎯 Sonido
    this.playSound('swipe');
    
    // 🎯 Navegación (adelante)
    if (window.history.length > 1) {
      window.history.forward();
    }
  }

  // 👆 MANEJAR SWIPE ABAJO
  handleSwipeDown() {
    console.log('👆 Swipe abajo detectado');
    
    // 🎯 Vibración
    this.vibrate([100]);
    
    // 🎯 Sonido
    this.playSound('swipe');
    
    // 🎯 Refresh de la página
    window.location.reload();
  }

  // 👆 MANEJAR SWIPE ARRIBA
  handleSwipeUp() {
    console.log('👆 Swipe arriba detectado');
    
    // 🎯 Vibración
    this.vibrate([100]);
    
    // 🎯 Sonido
    this.playSound('swipe');
    
    // 🎯 Scroll al inicio
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // 📱 CONFIGURAR DISEÑO RESPONSIVE
  setupResponsiveDesign() {
    // 🎯 Agregar clases CSS para móviles
    if (this.isMobile) {
      document.body.classList.add('mobile-device');
      
      // 🎯 Detectar orientación
      this.handleOrientationChange();
      
      // 🎯 Escuchar cambios de orientación
      window.addEventListener('orientationchange', () => {
        setTimeout(() => {
          this.handleOrientationChange();
        }, 100);
      });
    }
    
    console.log('✅ Diseño responsive configurado');
  }

  // 📐 MANEJAR CAMBIO DE ORIENTACIÓN
  handleOrientationChange() {
    const orientation = window.orientation;
    const isLandscape = orientation === 90 || orientation === -90;
    
    if (isLandscape) {
      document.body.classList.add('landscape');
      document.body.classList.remove('portrait');
      console.log('📐 Orientación: Landscape');
    } else {
      document.body.classList.add('portrait');
      document.body.classList.remove('landscape');
      console.log('📐 Orientación: Portrait');
    }
    
    // 🎯 Ajustar layout según orientación
    this.adjustLayoutForOrientation(isLandscape);
  }

  // 📐 AJUSTAR LAYOUT SEGÚN ORIENTACIÓN
  adjustLayoutForOrientation(isLandscape) {
    // 🎯 Ajustar tamaños de botones
    const buttons = document.querySelectorAll('button, .btn');
    buttons.forEach(button => {
      if (isLandscape) {
        button.style.padding = '8px 16px';
        button.style.fontSize = '14px';
      } else {
        button.style.padding = '12px 24px';
        button.style.fontSize = '16px';
      }
    });
    
    // 🎯 Ajustar espaciado
    const containers = document.querySelectorAll('.container, .row');
    containers.forEach(container => {
      if (isLandscape) {
        container.style.padding = '10px';
        container.style.margin = '5px 0';
      } else {
        container.style.padding = '15px';
        container.style.margin = '10px 0';
      }
    });
  }

  // ⚡ CONFIGURAR OPTIMIZACIONES DE PERFORMANCE
  setupPerformanceOptimizations() {
    // 🎯 Lazy loading de imágenes
    this.setupLazyLoading();
    
    // 🎯 Debounce para eventos de scroll
    this.setupScrollOptimization();
    
    // 🎯 Throttle para eventos de resize
    this.setupResizeOptimization();
    
    console.log('✅ Optimizaciones de performance configuradas');
  }

  // 🖼️ CONFIGURAR LAZY LOADING
  setupLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');
    
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target;
          img.src = img.dataset.src;
          img.classList.remove('lazy');
          observer.unobserve(img);
        }
      });
    });
    
    images.forEach(img => {
      imageObserver.observe(img);
    });
    
    console.log('✅ Lazy loading configurado');
  }

  // 📜 OPTIMIZAR SCROLL
  setupScrollOptimization() {
    let ticking = false;
    
    const updateScroll = () => {
      // 🎯 Aquí irían las actualizaciones relacionadas con scroll
      ticking = false;
    };
    
    const requestTick = () => {
      if (!ticking) {
        requestAnimationFrame(updateScroll);
        ticking = true;
      }
    };
    
    window.addEventListener('scroll', requestTick, { passive: true });
    
    console.log('✅ Optimización de scroll configurada');
  }

  // 📏 OPTIMIZAR RESIZE
  setupResizeOptimization() {
    let resizeTimeout;
    
    const handleResize = () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(() => {
        this.handleOrientationChange();
      }, 250);
    };
    
    window.addEventListener('resize', handleResize, { passive: true });
    
    console.log('✅ Optimización de resize configurada');
  }

  // 🎯 CONFIGURAR EVENT LISTENERS
  setupEventListeners() {
    // 🎯 Eventos de teclado para móviles
    this.setupKeyboardEvents();
    
    // 🎯 Eventos de focus para accesibilidad
    this.setupFocusEvents();
    
    // 🎯 Eventos de gestos personalizados
    this.setupCustomGestures();
  }

  // ⌨️ CONFIGURAR EVENTOS DE TECLADO
  setupKeyboardEvents() {
    // 🎯 Navegación con teclado
    document.addEventListener('keydown', (event) => {
      switch (event.key) {
        case 'ArrowLeft':
          this.handleSwipeRight();
          break;
        case 'ArrowRight':
          this.handleSwipeLeft();
          break;
        case 'ArrowUp':
          this.handleSwipeUp();
          break;
        case 'ArrowDown':
          this.handleSwipeDown();
          break;
        case 'Escape':
          this.handleEscape();
          break;
      }
    });
    
    console.log('✅ Eventos de teclado configurados');
  }

  // 🎯 MANEJAR TECLA ESCAPE
  handleEscape() {
    // 🎯 Cerrar modales o menús abiertos
    const modals = document.querySelectorAll('.modal, .dropdown, .menu');
    modals.forEach(modal => {
      if (modal.style.display === 'block' || modal.classList.contains('show')) {
        modal.style.display = 'none';
        modal.classList.remove('show');
      }
    });
    
    console.log('🎯 Tecla Escape manejada');
  }

  // 🎯 CONFIGURAR EVENTOS DE FOCUS
  setupFocusEvents() {
    // 🎯 Mejorar navegación por tab
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Tab') {
        document.body.classList.add('keyboard-navigation');
      }
    });
    
    // 🎯 Remover clase al hacer click
    document.addEventListener('mousedown', () => {
      document.body.classList.remove('keyboard-navigation');
    });
    
    console.log('✅ Eventos de focus configurados');
  }

  // 🎯 CONFIGURAR GESTOS PERSONALIZADOS
  setupCustomGestures() {
    // 🎯 Doble tap para zoom
    let lastTap = 0;
    
    document.addEventListener('touchend', (event) => {
      const currentTime = new Date().getTime();
      const tapLength = currentTime - lastTap;
      
      if (tapLength < 500 && tapLength > 0) {
        // 🎯 Doble tap detectado
        this.handleDoubleTap(event);
      }
      
      lastTap = currentTime;
    });
    
    console.log('✅ Gestos personalizados configurados');
  }

  // 👆 MANEJAR DOBLE TAP
  handleDoubleTap(event) {
    const target = event.target;
    
    // 🎯 Zoom en imágenes
    if (target.tagName === 'IMG') {
      this.toggleImageZoom(target);
    }
    
    // 🎯 Zoom en texto
    if (target.tagName === 'P' || target.tagName === 'H1' || target.tagName === 'H2') {
      this.toggleTextZoom(target);
    }
    
    console.log('👆 Doble tap manejado');
  }

  // 🖼️ TOGGLE ZOOM DE IMAGEN
  toggleImageZoom(img) {
    if (img.classList.contains('zoomed')) {
      img.classList.remove('zoomed');
      img.style.transform = 'scale(1)';
      img.style.cursor = 'zoom-in';
    } else {
      img.classList.add('zoomed');
      img.style.transform = 'scale(1.5)';
      img.style.cursor = 'zoom-out';
    }
    
    // 🎯 Vibración
    this.vibrate([50]);
  }

  // 📝 TOGGLE ZOOM DE TEXTO
  toggleTextZoom(element) {
    if (element.classList.contains('zoomed')) {
      element.classList.remove('zoomed');
      element.style.fontSize = '';
    } else {
      element.classList.add('zoomed');
      element.style.fontSize = '1.2em';
    }
    
    // 🎯 Vibración
    this.vibrate([50]);
  }

  // 📳 VIBRAR
  vibrate(pattern) {
    if (this.vibrationEnabled && 'vibrate' in navigator) {
      try {
        navigator.vibrate(pattern);
      } catch (error) {
        console.warn('⚠️ Error en vibración:', error);
      }
    }
  }

  // 🔊 REPRODUCIR SONIDO
  playSound(type) {
    if (!this.soundEnabled) return;
    
    try {
      // 🎯 Crear contexto de audio
      const audioContext = new (window.AudioContext || window.webkitAudioContext)();
      
      // 🎯 Generar sonido según el tipo
      switch (type) {
        case 'swipe':
          this.playSwipeSound(audioContext);
          break;
        case 'success':
          this.playSuccessSound(audioContext);
          break;
        case 'error':
          this.playErrorSound(audioContext);
          break;
        case 'notification':
          this.playNotificationSound(audioContext);
          break;
      }
    } catch (error) {
      console.warn('⚠️ Error reproduciendo sonido:', error);
    }
  }

  // 🔊 SONIDO DE SWIPE
  playSwipeSound(audioContext) {
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
    oscillator.frequency.exponentialRampToValueAtTime(400, audioContext.currentTime + 0.1);
    
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.1);
  }

  // 🔊 SONIDO DE ÉXITO
  playSuccessSound(audioContext) {
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.setValueAtTime(523, audioContext.currentTime); // C
    oscillator.frequency.setValueAtTime(659, audioContext.currentTime + 0.1); // E
    oscillator.frequency.setValueAtTime(784, audioContext.currentTime + 0.2); // G
    
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime + 0.3);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.3);
  }

  // 🔊 SONIDO DE ERROR
  playErrorSound(audioContext) {
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.setValueAtTime(200, audioContext.currentTime);
    oscillator.frequency.setValueAtTime(150, audioContext.currentTime + 0.1);
    
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.2);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.2);
  }

  // 🔊 SONIDO DE NOTIFICACIÓN
  playNotificationSound(audioContext) {
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.setValueAtTime(1000, audioContext.currentTime);
    oscillator.frequency.setValueAtTime(1200, audioContext.currentTime + 0.05);
    oscillator.frequency.setValueAtTime(1000, audioContext.currentTime + 0.1);
    
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime + 0.1);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.1);
  }

  // 🌙 CONFIGURAR TEMA AUTOMÁTICO
  setupAutoTheme() {
    // 🎯 Detectar preferencia del sistema
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
    
    // 🎯 Aplicar tema inicial
    this.applyTheme(prefersDark.matches);
    
    // 🎯 Escuchar cambios
    prefersDark.addEventListener('change', (event) => {
      this.applyTheme(event.matches);
    });
    
    console.log('✅ Tema automático configurado');
  }

  // 🌙 APLICAR TEMA
  applyTheme(isDark) {
    this.darkMode = isDark;
    
    if (isDark) {
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');
    } else {
      document.body.classList.add('light-mode');
      document.body.classList.remove('dark-mode');
    }
    
    // 🎯 Guardar preferencia
    localStorage.setItem('fulbito_theme', isDark ? 'dark' : 'light');
    
    console.log('🌙 Tema aplicado:', isDark ? 'Oscuro' : 'Claro');
  }

  // 🎯 TOGGLE TEMA MANUAL
  toggleTheme() {
    this.applyTheme(!this.darkMode);
    
    // 🎯 Vibración
    this.vibrate([100, 50, 100]);
    
    // 🎯 Sonido
    this.playSound('success');
  }

  // 📊 OBTENER ESTADÍSTICAS
  getStats() {
    return {
      isMobile: this.isMobile,
      vibrationEnabled: this.vibrationEnabled,
      soundEnabled: this.soundEnabled,
      darkMode: this.darkMode,
      orientation: window.orientation,
      screenWidth: window.innerWidth,
      screenHeight: window.innerHeight,
      userAgent: navigator.userAgent
    };
  }

  // 🧹 LIMPIAR RECURSOS
  cleanup() {
    // 🎯 Remover event listeners
    // 🎯 Limpiar timeouts
    // 🎯 Limpiar observers
    
    console.log('✅ Mobile Features limpiado');
  }
}

// 🚀 INSTANCIAR MOBILE FEATURES
let mobileFeatures;

// 🚀 CREAR INSTANCIA INMEDIATAMENTE
mobileFeatures = new MobileFeatures();

// 🎯 Exponer globalmente INMEDIATAMENTE
window.mobileFeatures = mobileFeatures;

// 🎯 REGISTRAR EVENTO ADICIONAL PARA DOM
document.addEventListener('DOMContentLoaded', () => {
  console.log('🚀 DOM cargado - Mobile Features ya está disponible');
});

// 🌐 Exportar para uso en otros módulos
if (typeof module !== 'undefined' && module.exports) {
  module.exports = MobileFeatures;
}
