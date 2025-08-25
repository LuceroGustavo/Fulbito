# ⚽ Fulbito - Organizador de Partidos de Fútbol

**Fulbito** es una aplicación web desarrollada en Java con Spring Boot que permite organizar partidos de fútbol de manera inteligente, formando equipos balanceados basándose en las habilidades de cada jugador.

## 🎯 Características Principales

- **Sistema de Calificación Completo**: Cada jugador tiene calificaciones en 5 habilidades (velocidad, habilidad, fuerza física, tiro, táctica)
- **Formación Automática de Equipos**: Algoritmo inteligente que crea equipos lo más balanceados posible
- **Gestión de Arqueros**: Distribución automática de arqueros entre equipos
- **Historial de Partidos**: Registro completo de todos los partidos jugados
- **Interfaz Moderna y Responsiva**: Diseño atractivo con Bootstrap 5 y Font Awesome
- **Aleatoriedad en Equipos**: Cada partido tiene equipos diferentes para mayor variedad

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 21 (recomendado) o Java 17, Spring Boot 3.3.0, Spring Data JPA
- **Base de Datos**: MySQL 8.0
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool**: Maven
- **Servidor**: Tomcat (embebido en Spring Boot)

## 📋 Requisitos Previos

- Java 21 (recomendado) o Java 17 como mínimo
- MySQL 8.0 o superior
- Maven 3.6 o superior
- Navegador web moderno

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd fulbito
```

### 2. Configurar la Base de Datos

1. **Crear base de datos MySQL**:
```sql
CREATE DATABASE fulbito_db;
```

2. **Configurar credenciales** en `src/main/resources/application.properties`:
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### 3. Compilar y Ejecutar

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📱 Uso de la Aplicación

### 1. Agregar Jugadores

1. Ve a **"Jugadores"** → **"Nuevo Jugador"**
2. Completa la información del jugador:
   - Nombre completo
   - Edad (16-80 años)
   - Indica si es arquero
   - Califica cada habilidad del 1 al 10:
     - **Velocidad**: Capacidad de movimiento rápido
     - **Habilidad**: Técnica y control del balón
     - **Fuerza Física**: Resistencia y potencia
     - **Tiro**: Precisión en el disparo
     - **Táctica**: Comprensión del juego

### 2. Formar Equipos

1. Ve a **"Formar Equipos"**
2. Especifica cuántos jugadores participarán (mínimo 4)
3. Haz clic en **"¡Formar Equipos!"**
4. El sistema generará automáticamente equipos balanceados

### 3. Ver Resultados

- **Equipos Formados**: Muestra los equipos con estadísticas detalladas
- **Historial**: Revisa todos los partidos anteriores
- **Estadísticas**: Análisis del balance de equipos

## 🔧 Configuración Avanzada

### Personalizar el Algoritmo de Balance

El algoritmo de formación de equipos se encuentra en `FormacionEquiposService.java`:

- **Umbral de balance**: Ajusta la diferencia máxima aceptable entre equipos
- **Prioridad de arqueros**: Modifica la lógica de distribución de arqueros
- **Métricas de calificación**: Personaliza los pesos de cada habilidad

### Configuración de Base de Datos

```properties
# Configuración de conexión
spring.datasource.url=jdbc:mysql://localhost:3306/fulbito_db
spring.datasource.username=usuario
spring.datasource.password=password

# Configuración JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 📊 Estructura de la Base de Datos

### Tabla: `jugadores`
- `id`: Identificador único
- `nombre`: Nombre del jugador
- `edad`: Edad del jugador
- `nivel_edad`: Clasificación automática (Joven, Intermedio, Viejito)
- `velocidad`, `habilidad`, `fuerza_fisica`, `tiro`, `tactica`: Calificaciones (1-10)
- `es_arquero`: Indica si es arquero
- `calificacion_total`: Promedio automático de habilidades

### Tabla: `partidos`
- `id`: Identificador único del partido
- `fecha_creacion`: Fecha y hora de creación
- `cantidad_jugadores`: Número de jugadores participantes
- `promedio_equipo_a`, `promedio_equipo_b`: Promedios de calificación
- `diferencia_promedios`: Diferencia entre equipos

### Tablas de Relación
- `partido_equipo_a`: Jugadores del equipo A
- `partido_equipo_b`: Jugadores del equipo B

## 🎮 Algoritmo de Formación de Equipos

1. **Selección Aleatoria**: Se seleccionan jugadores aleatoriamente para evitar repetición
2. **Separación de Arqueros**: Los arqueros se distribuyen equitativamente
3. **Distribución por Calificación**: Los jugadores se distribuyen alternadamente por calificación
4. **Balanceo Automático**: Se intercambian jugadores si es necesario para mejorar el balance
5. **Validación Final**: Se verifica que la diferencia entre equipos sea mínima

## 🚨 Solución de Problemas

### Error de Conexión a Base de Datos
- Verifica que MySQL esté ejecutándose
- Confirma las credenciales en `application.properties`
- Asegúrate de que la base de datos `fulbito_db` exista

### Error de Compilación
- Verifica que tengas Java 17 instalado: `java -version`
- Limpia y recompila: `mvn clean compile`

### Problemas de Rendimiento
- Ajusta la configuración de JPA en `application.properties`
- Considera usar índices en la base de datos para consultas frecuentes

## 🔮 Funcionalidades Futuras

- [ ] **API REST**: Endpoints para aplicaciones móviles
- [ ] **Autenticación**: Sistema de usuarios y sesiones
- [ ] **Estadísticas Avanzadas**: Gráficos y análisis detallados
- [ ] **Notificaciones**: Recordatorios de partidos
- [ ] **Exportación**: Generar reportes en PDF/Excel
- [ ] **Modo Torneo**: Organización de campeonatos

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Soporte

Si tienes preguntas o problemas:

- Abre un issue en GitHub
- Contacta al equipo de desarrollo
- Consulta la documentación de Spring Boot

## 🙏 Agradecimientos

- **Spring Boot Team**: Por el excelente framework
- **Bootstrap Team**: Por el sistema de diseño responsivo
- **Font Awesome**: Por los iconos utilizados
- **Comunidad Java**: Por el soporte y recursos

---

**¡Disfruta organizando partidos de fútbol con Fulbito! ⚽🎯**
