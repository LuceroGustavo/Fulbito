package com.Fulbito.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Fulbito.model.Jugador;
import com.Fulbito.model.Partido;
import com.Fulbito.repository.JugadorRepository;
import com.Fulbito.repository.PartidoRepository;

@Service
public class FormacionEquiposService {
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    /**
     * Forma equipos balanceados basándose en las calificaciones de los jugadores
     * @param cantidadJugadores Cantidad total de jugadores para el partido
     * @return Partido con los equipos formados
     */
    public Partido formarEquipos(Integer cantidadJugadores) {
        // Validar que haya suficientes jugadores
        long totalJugadores = jugadorRepository.count();
        if (totalJugadores < cantidadJugadores) {
            throw new RuntimeException("No hay suficientes jugadores registrados. Se necesitan " + 
                                    cantidadJugadores + " pero solo hay " + totalJugadores);
        }
        
        // Obtener todos los jugadores ordenados por calificación
        List<Jugador> todosJugadores = jugadorRepository.findAllByOrderByCalificacionTotalDesc();
        
        // Intentar formar equipos sin repetir (máximo 10 intentos)
        Map<String, List<Jugador>> equipos = null;
        int intentos = 0;
        int maxIntentos = 10;
        
        while (equipos == null && intentos < maxIntentos) {
            intentos++;
            
            // Seleccionar jugadores aleatoriamente para este partido
            List<Jugador> jugadoresSeleccionados = seleccionarJugadoresAleatorios(todosJugadores, cantidadJugadores);
            
            // Formar equipos balanceados
            equipos = formarEquiposBalanceados(jugadoresSeleccionados);
            
            // Verificar si estos equipos ya se formaron antes
            if (equiposYaFormados(equipos.get("equipoA"), equipos.get("equipoB"))) {
                equipos = null; // Intentar de nuevo
            }
        }
        
        // Si después de 10 intentos no se pudo evitar la repetición, usar los últimos equipos formados
        if (equipos == null) {
            List<Jugador> jugadoresSeleccionados = seleccionarJugadoresAleatorios(todosJugadores, cantidadJugadores);
            equipos = formarEquiposBalanceados(jugadoresSeleccionados);
        }
        
        // Crear y guardar el partido
        Partido partido = new Partido(cantidadJugadores, equipos.get("equipoA"), equipos.get("equipoB"));
        return partidoRepository.save(partido);
    }
    
    /**
     * Selecciona jugadores aleatoriamente para el partido
     */
    private List<Jugador> seleccionarJugadoresAleatorios(List<Jugador> todosJugadores, Integer cantidadJugadores) {
        List<Jugador> jugadoresDisponibles = new ArrayList<>(todosJugadores);
        Collections.shuffle(jugadoresDisponibles);
        return jugadoresDisponibles.subList(0, cantidadJugadores);
    }
    
    /**
     * Forma equipos balanceados usando un algoritmo de distribución equilibrada
     */
    private Map<String, List<Jugador>> formarEquiposBalanceados(List<Jugador> jugadores) {
        // Separar arqueros del resto de jugadores
        List<Jugador> arqueros = jugadores.stream()
                .filter(Jugador::getEsArquero)
                .collect(Collectors.toList());
        
        List<Jugador> jugadoresCampo = jugadores.stream()
                .filter(j -> !j.getEsArquero())
                .collect(Collectors.toList());
        
        // Ordenar jugadores de campo por calificación (descendente)
        jugadoresCampo.sort((j1, j2) -> Double.compare(j2.getCalificacionTotal(), j1.getCalificacionTotal()));
        
        List<Jugador> equipoA = new ArrayList<>();
        List<Jugador> equipoB = new ArrayList<>();
        
        // Distribuir arqueros si los hay
        if (arqueros.size() >= 2) {
            equipoA.add(arqueros.get(0));
            equipoB.add(arqueros.get(1));
        } else if (arqueros.size() == 1) {
            equipoA.add(arqueros.get(0));
        }
        
        // Distribuir jugadores de campo de manera equilibrada
        for (int i = 0; i < jugadoresCampo.size(); i++) {
            if (i % 2 == 0) {
                equipoA.add(jugadoresCampo.get(i));
            } else {
                equipoB.add(jugadoresCampo.get(i));
            }
        }
        
        // Balancear equipos si es necesario
        balancearEquipos(equipoA, equipoB);
        
        Map<String, List<Jugador>> resultado = new HashMap<>();
        resultado.put("equipoA", equipoA);
        resultado.put("equipoB", equipoB);
        
        return resultado;
    }
    
    /**
     * Balancea los equipos intercambiando jugadores si es necesario
     */
    private void balancearEquipos(List<Jugador> equipoA, List<Jugador> equipoB) {
        double promedioA = calcularPromedioEquipo(equipoA);
        double promedioB = calcularPromedioEquipo(equipoB);
        
        // Si la diferencia es mayor a 0.5, intentar balancear
        if (Math.abs(promedioA - promedioB) > 0.5) {
            // Buscar el mejor intercambio posible
            for (int i = 0; i < equipoA.size(); i++) {
                for (int j = 0; j < equipoB.size(); j++) {
                    Jugador jugadorA = equipoA.get(i);
                    Jugador jugadorB = equipoB.get(j);
                    
                    // Calcular cómo quedarían los promedios después del intercambio
                    double nuevoPromedioA = (promedioA * equipoA.size() - jugadorA.getCalificacionTotal() + jugadorB.getCalificacionTotal()) / equipoA.size();
                    double nuevoPromedioB = (promedioB * equipoB.size() - jugadorB.getCalificacionTotal() + jugadorA.getCalificacionTotal()) / equipoB.size();
                    
                    // Si el intercambio mejora el balance, realizarlo
                    if (Math.abs(nuevoPromedioA - nuevoPromedioB) < Math.abs(promedioA - promedioB)) {
                        equipoA.set(i, jugadorB);
                        equipoB.set(j, jugadorA);
                        return; // Solo hacer un intercambio por iteración
                    }
                }
            }
        }
    }
    
    /**
     * Calcula el promedio de calificación de un equipo
     */
    private double calcularPromedioEquipo(List<Jugador> equipo) {
        if (equipo.isEmpty()) return 0.0;
        return equipo.stream()
                .mapToDouble(Jugador::getCalificacionTotal)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Obtiene el historial de partidos
     */
    public List<Partido> obtenerHistorialPartidos() {
        return partidoRepository.findAllByOrderByFechaCreacionDesc();
    }
    
    /**
     * Verifica si los equipos ya se formaron antes
     * @param equipoA Lista de jugadores del equipo A
     * @param equipoB Lista de jugadores del equipo B
     * @return true si los equipos ya se formaron antes, false en caso contrario
     */
    private boolean equiposYaFormados(List<Jugador> equipoA, List<Jugador> equipoB) {
        // Obtener el historial de partidos
        List<Partido> partidosAnteriores = partidoRepository.findAllByOrderByFechaCreacionDesc();
        
        for (Partido partido : partidosAnteriores) {
            // Verificar si los equipos son iguales (mismos jugadores)
            if (equiposSonIguales(equipoA, partido.getEquipoA()) && 
                equiposSonIguales(equipoB, partido.getEquipoB())) {
                return true;
            }
            
            // Verificar también la combinación inversa (A=B, B=A)
            if (equiposSonIguales(equipoA, partido.getEquipoB()) && 
                equiposSonIguales(equipoB, partido.getEquipoA())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si dos equipos tienen los mismos jugadores
     * @param equipo1 Primer equipo
     * @param equipo2 Segundo equipo
     * @return true si los equipos tienen los mismos jugadores, false en caso contrario
     */
    private boolean equiposSonIguales(List<Jugador> equipo1, List<Jugador> equipo2) {
        if (equipo1.size() != equipo2.size()) {
            return false;
        }
        
        // Crear sets de IDs de jugadores para comparación
        Set<Long> idsEquipo1 = equipo1.stream()
                .map(Jugador::getId)
                .collect(Collectors.toSet());
        
        Set<Long> idsEquipo2 = equipo2.stream()
                .map(Jugador::getId)
                .collect(Collectors.toSet());
        
        return idsEquipo1.equals(idsEquipo2);
    }
    
    /**
     * Obtiene estadísticas de los equipos formados
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        long totalJugadores = jugadorRepository.count();
        long totalArqueros = jugadorRepository.countByEsArqueroTrue();
        long totalPartidos = partidoRepository.count();
        
        estadisticas.put("totalJugadores", totalJugadores);
        estadisticas.put("totalArqueros", totalArqueros);
        estadisticas.put("totalPartidos", totalPartidos);
        
        if (totalPartidos > 0) {
            List<Partido> partidos = partidoRepository.findAll();
            double promedioDiferencia = partidos.stream()
                    .mapToDouble(Partido::getDiferenciaPromedios)
                    .average()
                    .orElse(0.0);
            
            estadisticas.put("promedioDiferenciaEquipos", promedioDiferencia);
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene estadísticas sobre la repetición de equipos
     * @return Map con estadísticas de repetición
     */
    public Map<String, Object> obtenerEstadisticasRepeticion() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        List<Partido> partidos = partidoRepository.findAllByOrderByFechaCreacionDesc();
        int totalPartidos = partidos.size();
        int partidosConRepeticion = 0;
        
        if (totalPartidos > 1) {
            for (int i = 0; i < partidos.size(); i++) {
                for (int j = i + 1; j < partidos.size(); j++) {
                    Partido partido1 = partidos.get(i);
                    Partido partido2 = partidos.get(j);
                    
                    // Verificar si hay repetición entre estos dos partidos
                    if (equiposSonIguales(partido1.getEquipoA(), partido2.getEquipoA()) && 
                        equiposSonIguales(partido1.getEquipoB(), partido2.getEquipoB())) {
                        partidosConRepeticion++;
                    } else if (equiposSonIguales(partido1.getEquipoA(), partido2.getEquipoB()) && 
                               equiposSonIguales(partido1.getEquipoB(), partido2.getEquipoA())) {
                        partidosConRepeticion++;
                    }
                }
            }
        }
        
        estadisticas.put("totalPartidos", totalPartidos);
        estadisticas.put("partidosConRepeticion", partidosConRepeticion);
        estadisticas.put("porcentajeRepeticion", totalPartidos > 0 ? 
                         (double) partidosConRepeticion / totalPartidos * 100 : 0.0);
        
        return estadisticas;
    }
}
