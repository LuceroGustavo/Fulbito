# 🚀 Instalación y Configuración de Fulbito

## 📋 Requisitos Previos

1. **Java 21** instalado y configurado (recomendado) o **Java 17** como mínimo
2. **MySQL 8.0** ejecutándose
3. **Maven 3.6+** instalado

## 🔧 Pasos de Instalación

### 1. Verificar Java
```bash
java -version
# Debe mostrar Java 21 (recomendado) o Java 17 como mínimo
```

### 2. Verificar Maven
```bash
mvn -version
# Debe mostrar Maven 3.6 o superior
```

### 3. Configurar MySQL
```sql
-- Conectar a MySQL como root
mysql -u root -p

-- Crear base de datos (se creará automáticamente si no existe)
CREATE DATABASE IF NOT EXISTS fulbito;
```

### 4. Configurar Credenciales
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password_de_mysql
```

### 5. Compilar el Proyecto
```bash
# Limpiar y compilar
mvn clean compile

# Si hay errores, intentar:
mvn clean install -DskipTests
```

### 6. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

## 🌐 Acceso a la Aplicación

- **URL Principal**: http://localhost:80
- **Página de Prueba**: http://localhost:80/test
- **Health Check**: http://localhost:80/health

## 🚨 Solución de Errores Comunes

### Error: "Cannot find symbol"
- Verificar que Java 21 esté instalado (recomendado) o Java 17 como mínimo
- Limpiar y recompilar: `mvn clean compile`

### Error: "Connection refused" (MySQL)
- Verificar que MySQL esté ejecutándose
- Verificar credenciales en `application.properties`
- Verificar que el puerto 3306 esté disponible

### Error: "Port already in use"
- Cambiar puerto en `application.properties`: `server.port=8080`
- O detener el servicio que use el puerto 80

### Error: "Package does not exist"
- Verificar estructura de directorios
- Limpiar y recompilar: `mvn clean compile`

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── Fulbito/
│   │           ├── FulbitoApplication.java
│   │           ├── controller/
│   │           ├── service/
│   │           ├── repository/
│   │           └── model/
│   └── resources/
│       ├── application.properties
│       └── templates/
└── test/
```

## 🔍 Verificación de Funcionamiento

1. **Compilación exitosa**: `mvn clean compile`
2. **Aplicación inicia**: Sin errores en consola
3. **Página de prueba**: http://localhost:80/test
4. **Base de datos**: Tablas creadas automáticamente

## 📞 Soporte

Si persisten los errores:
1. Verificar logs de Spring Boot
2. Verificar logs de MySQL
3. Verificar versión de Java y Maven
4. Revisar configuración de firewall/antivirus
