-- =====================================================
-- SCRIPT ADAPTADO A TU BASE DE DATOS - FULBITO APP
-- =====================================================

-- Verificar jugadores existentes
SELECT COUNT(*) as total_jugadores FROM jugadores;

-- Ver estructura actual
SELECT 
    id,
    nombre,
    edad,
    nivel_edad,
    es_arquero,
    calificacion_total
FROM jugadores 
ORDER BY id;

-- =====================================================
-- AGREGAR JUGADORES ADICIONALES (ADAPTADOS A TU BASE)
-- =====================================================

-- Jugador 5: Delantero con buena calificación
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Cristiano Ronaldo', 28, 'INTERMEDIO', 8, 8, 8, 9, 7, 0, 8.0);

-- Jugador 6: Mediocampista técnico
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Kevin De Bruyne', 29, 'INTERMEDIO', 7, 9, 7, 8, 9, 0, 8.0);

-- Jugador 7: Defensa fuerte
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Virgil van Dijk', 31, 'INTERMEDIO', 7, 7, 9, 6, 8, 0, 7.4);

-- Jugador 8: Delantero veloz
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Kylian Mbappé', 24, 'INTERMEDIO', 9, 8, 7, 8, 7, 0, 7.8);

-- Jugador 9: Mediocampista creativo
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Luka Modric', 30, 'INTERMEDIO', 7, 8, 6, 7, 9, 0, 7.4);

-- Jugador 10: Defensa experimentado
INSERT INTO jugadores (nombre, edad, nivel_edad, velocidad, habilidad, fuerza_fisica, tiro, tactica, es_arquero, calificacion_total) 
VALUES ('Sergio Ramos', 29, 'INTERMEDIO', 7, 6, 9, 7, 8, 0, 7.4);

-- =====================================================
-- VERIFICAR INSERCIÓN
-- =====================================================

-- Mostrar todos los jugadores
SELECT 
    id,
    nombre,
    edad,
    CASE 
        WHEN es_arquero = 1 THEN '🥅 ARQUERO'
        ELSE '⚽ JUGADOR'
    END as tipo,
    nivel_edad,
    velocidad,
    habilidad,
    fuerza_fisica,
    tiro,
    tactica,
    calificacion_total
FROM jugadores 
ORDER BY id;

-- Estadísticas finales
SELECT 
    COUNT(*) as total_jugadores,
    COUNT(CASE WHEN es_arquero = 1 THEN 1 END) as total_arqueros,
    COUNT(CASE WHEN es_arquero = 0 THEN 1 END) as total_jugadores_campo,
    ROUND(AVG(calificacion_total), 2) as promedio_calificacion
FROM jugadores;

-- Distribución por nivel de edad
SELECT 
    nivel_edad,
    COUNT(*) as cantidad,
    ROUND(AVG(calificacion_total), 2) as promedio_calificacion
FROM jugadores 
GROUP BY nivel_edad 
ORDER BY promedio_calificacion DESC;
