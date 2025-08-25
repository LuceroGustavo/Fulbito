@echo off
echo ========================================
echo    PRUEBA DE LA APLICACION FULBITO
echo ========================================
echo.

echo 1. Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java no esta instalado o no esta en el PATH
    pause
    exit /b 1
)

echo.
echo 2. Verificando Maven...
mvn -version
if %errorlevel% neq 0 (
    echo ERROR: Maven no esta instalado o no esta en el PATH
    pause
    exit /b 1
)

echo.
echo 3. Limpiando y compilando...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Error en la compilacion
    pause
    exit /b 1
)

echo.
echo 4. Iniciando la aplicacion...
echo La aplicacion estara disponible en: http://localhost:80
echo Presiona Ctrl+C para detener
echo.
mvn spring-boot:run

pause
