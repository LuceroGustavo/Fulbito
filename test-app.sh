#!/bin/bash

echo "========================================"
echo "   PRUEBA DE LA APLICACION FULBITO"
echo "========================================"
echo

echo "1. Verificando Java..."
java -version
if [ $? -ne 0 ]; then
    echo "ERROR: Java no esta instalado o no esta en el PATH"
    exit 1
fi

echo
echo "2. Verificando Maven..."
mvn -version
if [ $? -ne 0 ]; then
    echo "ERROR: Maven no esta instalado o no esta en el PATH"
    exit 1
fi

echo
echo "3. Limpiando y compilando..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "ERROR: Error en la compilacion"
    exit 1
fi

echo
echo "4. Iniciando la aplicacion..."
echo "La aplicacion estara disponible en: http://localhost:80"
echo "Presiona Ctrl+C para detener"
echo
mvn spring-boot:run
