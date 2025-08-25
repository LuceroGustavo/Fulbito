# 🏆 FULBITO - Aplicación de Gestión de Equipos de Fútbol

## 📋 **Descripción General**

**Fulbito** es una aplicación web desarrollada para gestionar y organizar partidos de fútbol semanales entre amigos. La aplicación está diseñada para crear equipos balanceados automáticamente, evitando repeticiones y asegurando una experiencia justa para todos los participantes.

## 🎯 **Propósito Principal**

- **Gestión de jugadores** con perfiles detallados y calificaciones
- **Formación automática de equipos** balanceados basados en habilidades
- **Prevención de repetición** de equipos entre partidos
- **Historial de partidos** para seguimiento y análisis
- **Interfaz web responsive** para uso en dispositivos móviles y desktop

## 🛠️ **Stack Tecnológico**

### **Backend**
- **Java 21** - Lenguaje de programación principal
- **Spring Boot 3.3.0** - Framework de desarrollo
- **Spring Data JPA** - Persistencia de datos
- **Spring MVC** - Arquitectura web
- **Spring Validation** - Validación de datos
- **Maven** - Gestión de dependencias y build

### **Base de Datos**
- **MySQL 8.0+** - Sistema de gestión de base de datos
- **Hibernate** - ORM para mapeo objeto-relacional
- **JPA** - API de persistencia de Java

### **Frontend**
- **Thymeleaf** - Motor de plantillas del lado del servidor
- **Bootstrap 5** - Framework CSS para diseño responsive
- **Font Awesome** - Iconografía
- **JavaScript** - Interactividad del lado del cliente

### **Herramientas de Desarrollo**
- **Spring Boot DevTools** - Auto-restart y LiveReload
- **Maven Wrapper** - Gestión de dependencias
- **IDE Compatible** - IntelliJ IDEA, Eclipse, VS Code

## 🏗️ **Arquitectura de la Aplicación**

### **Estructura de Paquetes**
```
com.Fulbito/
├── model/          # Entidades JPA
├── repository/     # Interfaces de acceso a datos
├── service/        # Lógica de negocio
├── controller/     # Controladores web
└── config/         # Configuraciones
```

### **Capas de la Aplicación**
1. **Capa de Presentación** - Controllers y Templates Thymeleaf
2. **Capa de Servicios** - Lógica de negocio y formación de equipos
3. **Capa de Persistencia** - Repositories y entidades JPA
4. **Capa de Base de Datos** - MySQL con Hibernate

## 🎮 **Funcionalidades Principales**

### **Gestión de Jugadores**
- **Registro de jugadores** con información personal
- **Sistema de calificaciones** (1-10) en 5 categorías:
  - Velocidad
  - Habilidad técnica
  - Fuerza física
  - Tiro
  - Táctica
- **Cálculo automático** de calificación total
- **Categorización por edad**:
  - Joven (16-24 años)
  - Intermedio (25-39 años)
  - Viejito (40+ años)
- **Posición especial** de arquero
- **Edición y eliminación** de jugadores

### **Formación de Equipos**
- **Algoritmo inteligente** de balanceo automático
- **Selección aleatoria** de jugadores para cada partido
- **Distribución equilibrada** de arqueros
- **Balance de calificaciones** entre equipos
- **Prevención de repetición** de equipos anteriores
- **Configuración de cantidad** de jugadores por partido

### **Gestión de Partidos**
- **Historial completo** de todos los partidos
- **Estadísticas detalladas** de cada formación
- **Comparación de promedios** entre equipos
- **Indicador de balance** (Excelente/Bueno/Regular)
- **Fecha y hora** de cada partido

### **Sistema Anti-Repetición**
- **Verificación automática** contra historial
- **Hasta 10 intentos** para formar equipos únicos
- **Detección inteligente** de equipos iguales
- **Estadísticas de repetición** para monitoreo
- **Fallback inteligente** en casos extremos

## 🗄️ **Modelo de Datos**

### **Entidad Jugador**
```java
- id: Long (PK)
- nombre: String (2-50 caracteres)
- edad: Integer (16-80 años)
- nivelEdad: Enum (JOVEN, INTERMEDIO, VIEJITO)
- velocidad: Integer (1-10)
- habilidad: Integer (1-10)
- fuerzaFisica: Integer (1-10)
- tiro: Integer (1-10)
- tactica: Integer (1-10)
- esArquero: Boolean
- calificacionTotal: Double (calculado automáticamente)
```

### **Entidad Partido**
```java
- id: Long (PK)
- fechaCreacion: LocalDateTime
- cantidadJugadores: Integer
- equipoA: List<Jugador> (ManyToMany)
- equipoB: List<Jugador> (ManyToMany)
- promedioEquipoA: Double
- promedioEquipoB: Double
- diferenciaPromedios: Double
```

## 🔧 **Configuración de Base de Datos**

### **application.properties**
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/fulbito
spring.datasource.username=root
spring.datasource.password=root

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Servidor
server.port=80
```

## 🚀 **Instalación y Ejecución**

### **Requisitos Previos**
- Java 21 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

### **Pasos de Instalación**
1. **Clonar repositorio**
2. **Configurar base de datos** MySQL
3. **Ejecutar**: `mvn spring-boot:run`
4. **Acceder**: `http://localhost:80`

### **Comandos Maven**
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Empaquetar
mvn clean package

# Ejecutar tests
mvn test
```

## 📱 **Características de Usabilidad**

### **Interfaz Responsive**
- **Diseño mobile-first** para uso en dispositivos móviles
- **Navegación intuitiva** con iconos descriptivos
- **Formularios validados** con feedback visual
- **Tablas y tarjetas** organizadas para mejor legibilidad

### **Experiencia de Usuario**
- **Feedback inmediato** en todas las operaciones
- **Mensajes de confirmación** para acciones importantes
- **Validación en tiempo real** de formularios
- **Navegación fluida** entre secciones

## 🔒 **Seguridad y Validación**

### **Validación de Datos**
- **Anotaciones JPA** para restricciones de base de datos
- **Validación Spring** para reglas de negocio
- **Sanitización de entrada** para prevenir inyecciones
- **Manejo de errores** con mensajes descriptivos

### **Integridad de Datos**
- **Transacciones JPA** para operaciones críticas
- **Validación de relaciones** entre entidades
- **Cálculos automáticos** para evitar inconsistencias
- **Auditoría de cambios** con timestamps

## 📊 **Métricas y Monitoreo**

### **Logging**
- **Nivel DEBUG** para desarrollo
- **SQL queries** visibles para debugging
- **Trazas de excepción** detalladas
- **Métricas de rendimiento** de base de datos

### **Estadísticas**
- **Conteo de jugadores** por categoría
- **Promedio de balance** de equipos
- **Tasa de repetición** de formaciones
- **Historial de partidos** con análisis

## 🌟 **Características Destacadas**

1. **Algoritmo de Balanceo Inteligente** - Equipos equilibrados automáticamente
2. **Sistema Anti-Repetición** - Variedad garantizada en formaciones
3. **Interfaz Mobile-First** - Optimizada para uso en dispositivos móviles
4. **Validación Robusta** - Prevención de errores y datos inconsistentes
5. **Historial Completo** - Seguimiento de todas las formaciones
6. **DevTools Integrado** - Desarrollo ágil con auto-restart

## 🔮 **Casos de Uso Principales**

### **Para Organizadores**
- Gestión centralizada de jugadores
- Formación automática de equipos
- Seguimiento de historial de partidos
- Análisis de balance de equipos

### **Para Jugadores**
- Perfiles personalizados con calificaciones
- Participación en equipos balanceados
- Rotación de compañeros de equipo
- Experiencia de juego más justa

## 📝 **Notas de Desarrollo**

- **Código limpio** siguiendo principios SOLID
- **Documentación inline** en métodos críticos
- **Manejo de excepciones** robusto
- **Testing preparado** con Spring Boot Test
- **Configuración externalizada** para diferentes entornos

---

*Documentación generada para el proyecto Fulbito - Sistema de Gestión de Equipos de Fútbol*
*Versión: 1.0.0 | Fecha: Agosto 2025*
