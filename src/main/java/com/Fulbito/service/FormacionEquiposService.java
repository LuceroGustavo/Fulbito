package com.Fulbito.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Fulbito.model.EquipoTemporal;
import com.Fulbito.model.Jugador;
import com.Fulbito.model.Partido;
import com.Fulbito.repository.EquipoTemporalRepository;
import com.Fulbito.repository.JugadorRepository;
import com.Fulbito.repository.PartidoRepository;

@Service
public class FormacionEquiposService {
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private EquipoTemporalRepository equipoTemporalRepository;
    
    /**
     * Obtener estadísticas generales
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        long totalJugadores = jugadorRepository.count();
        long totalArqueros = jugadorRepository.countByEsArqueroTrue();
        
        estadisticas.put("totalJugadores", totalJugadores);
        estadisticas.put("totalPartidos", partidoRepository.count());
        estadisticas.put("totalArqueros", totalArqueros);
        estadisticas.put("totalJugadoresCampo", totalJugadores - totalArqueros);
        
        return estadisticas;
    }
    
    /**
     * Obtener historial de partidos
     */
    public List<Partido> obtenerHistorialPartidos() {
        return partidoRepository.findAllByOrderByFechaCreacionDesc();
    }
    
    /**
     * Formar equipos temporales (FASE 2 - Sistema Nuevo)
     */
    public EquipoTemporal formarEquiposTemporales(Integer cantidadJugadores, String sessionId) {
        try {
            System.out.println("=== DEBUG: Formando equipos temporales ===");
            System.out.println("Cantidad jugadores: " + cantidadJugadores);
            System.out.println("Session ID: " + sessionId);
            
        // Validar que haya suficientes jugadores
        long totalJugadores = jugadorRepository.count();
        if (totalJugadores < cantidadJugadores) {
            throw new RuntimeException("No hay suficientes jugadores registrados. Se necesitan " + 
                                    cantidadJugadores + " pero solo hay " + totalJugadores);
        }
            
            // Desactivar equipos temporales anteriores de esta sesión
            equipoTemporalRepository.desactivarPorSessionId(sessionId);
        
        // Obtener todos los jugadores ordenados por calificación
        List<Jugador> todosJugadores = jugadorRepository.findAllByOrderByCalificacionTotalDesc();
        
            // Seleccionar jugadores aleatoriamente
            List<Jugador> jugadoresSeleccionados = seleccionarJugadoresAleatorios(todosJugadores, cantidadJugadores);
            
            // Formar equipos balanceados
            Map<String, List<Jugador>> equipos = formarEquiposBalanceadosTemporales(jugadoresSeleccionados);
            
            // Crear equipo temporal
            EquipoTemporal equipoTemporal = new EquipoTemporal();
            equipoTemporal.setCantidadJugadores(cantidadJugadores);
            equipoTemporal.setEquipoA(equipos.get("equipoA"));
            equipoTemporal.setEquipoB(equipos.get("equipoB"));
            equipoTemporal.setSessionId(sessionId);
            equipoTemporal.setActivo(true);
            
            // Calcular promedios de calificación
            equipoTemporal.setPromedioEquipoA(calcularPromedioEquipo(equipos.get("equipoA")));
            equipoTemporal.setPromedioEquipoB(calcularPromedioEquipo(equipos.get("equipoB")));
            equipoTemporal.setDiferenciaPromedios(Math.abs(equipoTemporal.getPromedioEquipoA() - equipoTemporal.getPromedioEquipoB()));
            
            // Calcular promedios de edad
            equipoTemporal.setPromedioEdadEquipoA(calcularPromedioEdadEquipo(equipos.get("equipoA")));
            equipoTemporal.setPromedioEdadEquipoB(calcularPromedioEdadEquipo(equipos.get("equipoB")));
            equipoTemporal.setDiferenciaEdades(Math.abs(equipoTemporal.getPromedioEdadEquipoA() - equipoTemporal.getPromedioEdadEquipoB()));
            
            // Configurar campos del partido con valores por defecto
            equipoTemporal.setHoraPartido("20:00");
            equipoTemporal.setLugarPartido("Mega Fútbol");
            equipoTemporal.setPrecioPartido(5600.0);
            equipoTemporal.setObservacionesPartido("");
            
            // Guardar el equipo temporal
            return equipoTemporalRepository.save(equipoTemporal);
            
        } catch (Exception e) {
            System.err.println("=== ERROR en formarEquiposTemporales ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Formar equipos temporales con jugadores específicos
     */
    public EquipoTemporal formarEquiposTemporalesConJugadores(Integer cantidadJugadores, String sessionId, List<Long> jugadoresIds) {
        try {
            System.out.println("=== DEBUG: Formando equipos con jugadores específicos ===");
            System.out.println("Cantidad jugadores: " + cantidadJugadores);
            System.out.println("Session ID: " + sessionId);
            System.out.println("Jugadores IDs: " + jugadoresIds);
            
            // Validar cantidad
            if (jugadoresIds.size() != cantidadJugadores) {
                throw new RuntimeException("La cantidad de jugadores seleccionados (" + jugadoresIds.size() + 
                                        ") no coincide con la cantidad solicitada (" + cantidadJugadores + ")");
            }
            
            // Desactivar equipos temporales anteriores de esta sesión
            try {
                equipoTemporalRepository.desactivarPorSessionId(sessionId);
            } catch (Exception e) {
                System.err.println("Error al desactivar equipos anteriores: " + e.getMessage());
            }
            
            // Obtener jugadores seleccionados
            List<Jugador> jugadoresSeleccionados = jugadorRepository.findAllById(jugadoresIds);
            
            // Formar equipos balanceados
            Map<String, List<Jugador>> equipos = formarEquiposBalanceadosTemporales(jugadoresSeleccionados);
            
            // Crear equipo temporal
            EquipoTemporal equipoTemporal = new EquipoTemporal();
            equipoTemporal.setCantidadJugadores(cantidadJugadores);
            equipoTemporal.setEquipoA(equipos.get("equipoA"));
            equipoTemporal.setEquipoB(equipos.get("equipoB"));
            equipoTemporal.setSessionId(sessionId);
            equipoTemporal.setActivo(true);
            
            // Calcular promedios de calificación
            equipoTemporal.setPromedioEquipoA(calcularPromedioEquipo(equipos.get("equipoA")));
            equipoTemporal.setPromedioEquipoB(calcularPromedioEquipo(equipos.get("equipoB")));
            equipoTemporal.setDiferenciaPromedios(Math.abs(equipoTemporal.getPromedioEquipoA() - equipoTemporal.getPromedioEquipoB()));
            
            // Calcular promedios de edad
            equipoTemporal.setPromedioEdadEquipoA(calcularPromedioEdadEquipo(equipos.get("equipoA")));
            equipoTemporal.setPromedioEdadEquipoB(calcularPromedioEdadEquipo(equipos.get("equipoB")));
            equipoTemporal.setDiferenciaEdades(Math.abs(equipoTemporal.getPromedioEdadEquipoA() - equipoTemporal.getPromedioEdadEquipoB()));
            
            // Configurar campos del partido con valores por defecto
            equipoTemporal.setHoraPartido("20:00");
            equipoTemporal.setLugarPartido("Mega Fútbol");
            equipoTemporal.setPrecioPartido(5600.0);
            equipoTemporal.setObservacionesPartido("");
            
            // Guardar el equipo temporal
            return equipoTemporalRepository.save(equipoTemporal);
            
        } catch (Exception e) {
            System.err.println("=== ERROR en formarEquiposTemporalesConJugadores ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Seleccionar jugadores aleatoriamente
     */
    private List<Jugador> seleccionarJugadoresAleatorios(List<Jugador> todosJugadores, Integer cantidadJugadores) {
        List<Jugador> jugadoresDisponibles = new ArrayList<>(todosJugadores);
        Collections.shuffle(jugadoresDisponibles);
        return jugadoresDisponibles.subList(0, cantidadJugadores);
    }
    
    /**
     * Formar equipos balanceados para sistema temporal
     */
    private Map<String, List<Jugador>> formarEquiposBalanceadosTemporales(List<Jugador> jugadores) {
        // Separar arqueros del resto de jugadores
        List<Jugador> arqueros = jugadores.stream()
                .filter(Jugador::getEsArquero)
                .collect(Collectors.toList());
        
        List<Jugador> jugadoresCampo = jugadores.stream()
                .filter(j -> !j.getEsArquero())
                .collect(Collectors.toList());
        
        // Ordenar jugadores de campo por calificación (descendente)
        jugadoresCampo.sort((j1, j2) -> Double.compare(j2.getCalificacionTotal(), j1.getCalificacionTotal()));
        
        // Ordenar jugadores de campo por edad (descendente) para balance por edad
        jugadoresCampo.sort((j1, j2) -> Integer.compare(j2.getEdad(), j1.getEdad()));
        
        List<Jugador> equipoA = new ArrayList<>();
        List<Jugador> equipoB = new ArrayList<>();
        
        // Distribuir arqueros equitativamente
        if (arqueros.size() >= 2) {
            equipoA.add(arqueros.get(0));
            equipoB.add(arqueros.get(1));
        } else if (arqueros.size() == 1) {
            // Si solo hay un arquero, asignarlo al equipo con menos jugadores
            equipoA.add(arqueros.get(0));
        }
        
        // Calcular cuántos jugadores debe tener cada equipo
        int totalJugadores = jugadores.size();
        int jugadoresPorEquipo = totalJugadores / 2;
        
        // Distribuir jugadores de campo de manera equilibrada (ALTERNADO para balance por edad)
        for (int i = 0; i < jugadoresCampo.size(); i++) {
            if (i % 2 == 0) {
                // Jugadores pares van al equipo A (mayores primero)
                if (equipoA.size() < jugadoresPorEquipo) {
                equipoA.add(jugadoresCampo.get(i));
            } else {
                equipoB.add(jugadoresCampo.get(i));
                }
            } else {
                // Jugadores impares van al equipo B (jóvenes primero)
                if (equipoB.size() < jugadoresPorEquipo) {
                    equipoB.add(jugadoresCampo.get(i));
                } else {
                    equipoA.add(jugadoresCampo.get(i));
                }
            }
        }
        
        // Balancear equipos por calificación Y edad
        balancearEquiposTemporales(equipoA, equipoB);
        
        Map<String, List<Jugador>> resultado = new HashMap<>();
        resultado.put("equipoA", equipoA);
        resultado.put("equipoB", equipoB);
        
        return resultado;
    }
    
    /**
     * Balancear equipos temporales
     */
    private void balancearEquiposTemporales(List<Jugador> equipoA, List<Jugador> equipoB) {
        double promedioA = calcularPromedioEquipo(equipoA);
        double promedioB = calcularPromedioEquipo(equipoB);
        
        // Calcular promedios de edad
        double promedioEdadA = calcularPromedioEdadEquipo(equipoA);
        double promedioEdadB = calcularPromedioEdadEquipo(equipoB);
        
        // Si la diferencia de calificación es mayor a 0.5, intentar balancear
        if (Math.abs(promedioA - promedioB) > 0.5) {
            // Buscar el mejor intercambio posible considerando calificación Y edad
            for (int i = 0; i < equipoA.size(); i++) {
                for (int j = 0; j < equipoB.size(); j++) {
                    Jugador jugadorA = equipoA.get(i);
                    Jugador jugadorB = equipoB.get(j);
                    
                    // Calcular promedios después del intercambio
                    double nuevoPromedioA = (promedioA * equipoA.size() - jugadorA.getCalificacionTotal() + jugadorB.getCalificacionTotal()) / equipoA.size();
                    double nuevoPromedioB = (promedioB * equipoB.size() - jugadorB.getCalificacionTotal() + jugadorA.getCalificacionTotal()) / equipoB.size();
                    
                    double nuevaDiferencia = Math.abs(nuevoPromedioA - nuevoPromedioB);
                    double diferenciaActual = Math.abs(promedioA - promedioB);
                    
                    // Calcular promedios de edad después del intercambio
                    double nuevoPromedioEdadA = (promedioEdadA * equipoA.size() - jugadorA.getEdad() + jugadorB.getEdad()) / equipoA.size();
                    double nuevoPromedioEdadB = (promedioEdadB * equipoB.size() - jugadorB.getEdad() + jugadorA.getEdad()) / equipoB.size();
                    
                    double nuevaDiferenciaEdad = Math.abs(nuevoPromedioEdadA - nuevoPromedioEdadB);
                    double diferenciaEdadActual = Math.abs(promedioEdadA - promedioEdadB);
                    
                    // Si el intercambio mejora el balance de calificación Y edad, realizarlo
                    if (nuevaDiferencia < diferenciaActual && nuevaDiferenciaEdad <= diferenciaEdadActual) {
                        equipoA.set(i, jugadorB);
                        equipoB.set(j, jugadorA);
                        return; // Solo un intercambio por iteración
                    }
                }
            }
        }
        
        // Si la diferencia de edad es mayor a 2 años, intentar balancear por edad
        if (Math.abs(promedioEdadA - promedioEdadB) > 2.0) {
            // Buscar el mejor intercambio posible solo por edad
            for (int i = 0; i < equipoA.size(); i++) {
                for (int j = 0; j < equipoB.size(); j++) {
                    Jugador jugadorA = equipoA.get(i);
                    Jugador jugadorB = equipoB.get(j);
                    
                    // Calcular promedios de edad después del intercambio
                    double nuevoPromedioEdadA = (promedioEdadA * equipoA.size() - jugadorA.getEdad() + jugadorB.getEdad()) / equipoA.size();
                    double nuevoPromedioEdadB = (promedioEdadB * equipoB.size() - jugadorB.getEdad() + jugadorA.getEdad()) / equipoB.size();
                    
                    double nuevaDiferenciaEdad = Math.abs(nuevoPromedioEdadA - nuevoPromedioEdadB);
                    double diferenciaEdadActual = Math.abs(promedioEdadA - promedioEdadB);
                    
                    // Si el intercambio mejora el balance de edad sin empeorar mucho la calificación
                    if (nuevaDiferenciaEdad < diferenciaEdadActual) {
                        // Verificar que no empeore mucho la calificación
                        double nuevoPromedioA = (promedioA * equipoA.size() - jugadorA.getCalificacionTotal() + jugadorB.getCalificacionTotal()) / equipoA.size();
                        double nuevoPromedioB = (promedioB * equipoB.size() - jugadorB.getCalificacionTotal() + jugadorA.getCalificacionTotal()) / equipoB.size();
                        double nuevaDiferencia = Math.abs(nuevoPromedioA - nuevoPromedioB);
                        double diferenciaActual = Math.abs(promedioA - promedioB);
                        
                        // Solo si no empeora mucho la calificación (máximo 0.3 puntos)
                        if (nuevaDiferencia <= diferenciaActual + 0.3) {
                        equipoA.set(i, jugadorB);
                        equipoB.set(j, jugadorA);
                            return; // Solo un intercambio por iteración
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Calcular promedio de un equipo
     */
    private double calcularPromedioEquipo(List<Jugador> equipo) {
        if (equipo.isEmpty()) return 0.0;
        return equipo.stream()
                .mapToDouble(Jugador::getCalificacionTotal)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calcular promedio de edad de un equipo
     */
    private double calcularPromedioEdadEquipo(List<Jugador> equipo) {
        if (equipo.isEmpty()) return 0.0;
        return equipo.stream()
                .mapToDouble(Jugador::getEdad)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Obtener equipo temporal activo
     */
    public EquipoTemporal obtenerEquipoTemporal(String sessionId) {
        return equipoTemporalRepository.findFirstBySessionIdAndActivoTrueOrderByFechaCreacionDesc(sessionId)
            .orElse(null);
    }
    
    /**
     * Regenerar equipos temporales
     */
    public EquipoTemporal regenerarEquiposTemporales(String sessionId) {
        EquipoTemporal equipoActual = obtenerEquipoTemporal(sessionId);
        if (equipoActual == null) {
            throw new RuntimeException("No se encontró un equipo temporal activo para esta sesión");
        }
        
        // Formar nuevos equipos con la misma cantidad
        return formarEquiposTemporales(equipoActual.getCantidadJugadores(), sessionId);
    }
    
    /**
     * Intercambiar jugadores entre equipos
     */
    public EquipoTemporal intercambiarJugadores(String sessionId, Long jugadorIdA, Long jugadorIdB) {
        EquipoTemporal equipoTemporal = obtenerEquipoTemporal(sessionId);
        if (equipoTemporal == null) {
            throw new RuntimeException("No se encontró un equipo temporal activo para esta sesión");
        }
        
        // Buscar jugadores en ambos equipos
        Jugador jugadorA = null;
        Jugador jugadorB = null;
        String equipoOrigenA = null;
        String equipoOrigenB = null;
        
        // Buscar en equipo A
        for (Jugador jugador : equipoTemporal.getEquipoA()) {
            if (jugador.getId().equals(jugadorIdA)) {
                jugadorA = jugador;
                equipoOrigenA = "A";
            }
            if (jugador.getId().equals(jugadorIdB)) {
                jugadorB = jugador;
                equipoOrigenB = "A";
            }
        }
        
        // Buscar en equipo B
        for (Jugador jugador : equipoTemporal.getEquipoB()) {
            if (jugador.getId().equals(jugadorIdA)) {
                jugadorA = jugador;
                equipoOrigenA = "B";
            }
            if (jugador.getId().equals(jugadorIdB)) {
                jugadorB = jugador;
                equipoOrigenB = "B";
            }
        }
        
        // Validar que se encontraron ambos jugadores
        if (jugadorA == null || jugadorB == null) {
            throw new RuntimeException("No se encontraron ambos jugadores en los equipos");
        }
        
        // Validar que estén en equipos diferentes
        if (equipoOrigenA.equals(equipoOrigenB)) {
            throw new RuntimeException("Los jugadores deben estar en equipos diferentes para intercambiarse");
        }
        
        // Realizar el intercambio
        if (equipoOrigenA.equals("A")) {
            equipoTemporal.getEquipoA().remove(jugadorA);
            equipoTemporal.getEquipoA().add(jugadorB);
            equipoTemporal.getEquipoB().remove(jugadorB);
            equipoTemporal.getEquipoB().add(jugadorA);
        } else {
            equipoTemporal.getEquipoA().remove(jugadorB);
            equipoTemporal.getEquipoA().add(jugadorA);
            equipoTemporal.getEquipoB().remove(jugadorA);
            equipoTemporal.getEquipoB().add(jugadorB);
        }
        
        // Recalcular promedios
        equipoTemporal.setPromedioEquipoA(calcularPromedioEquipo(equipoTemporal.getEquipoA()));
        equipoTemporal.setPromedioEquipoB(calcularPromedioEquipo(equipoTemporal.getEquipoB()));
        equipoTemporal.setDiferenciaPromedios(Math.abs(equipoTemporal.getPromedioEquipoA() - equipoTemporal.getPromedioEquipoB()));
        
        // Guardar cambios
        return equipoTemporalRepository.save(equipoTemporal);
    }
    
    /**
     * Mover jugador de un equipo a otro (sin intercambio)
     */
    public EquipoTemporal moverJugador(String sessionId, Long jugadorId, String equipoOrigen, String equipoDestino) {
        EquipoTemporal equipoTemporal = obtenerEquipoTemporal(sessionId);
        if (equipoTemporal == null) {
            throw new RuntimeException("No se encontró un equipo temporal activo para esta sesión");
        }
        
        Jugador jugadorAMover = null;
        List<Jugador> equipoOrigenList = equipoOrigen.equals("A") ? equipoTemporal.getEquipoA() : equipoTemporal.getEquipoB();
        List<Jugador> equipoDestinoList = equipoDestino.equals("A") ? equipoTemporal.getEquipoA() : equipoTemporal.getEquipoB();
        
        // Buscar y remover jugador del equipo origen
        for (int i = 0; i < equipoOrigenList.size(); i++) {
            if (equipoOrigenList.get(i).getId().equals(jugadorId)) {
                jugadorAMover = equipoOrigenList.remove(i);
                break;
            }
        }
        
        if (jugadorAMover == null) {
            throw new RuntimeException("No se encontró el jugador en el equipo origen");
        }
        
        // Agregar jugador al equipo destino
        equipoDestinoList.add(jugadorAMover);
        
        // Recalcular promedios
        equipoTemporal.setPromedioEquipoA(calcularPromedioEquipo(equipoTemporal.getEquipoA()));
        equipoTemporal.setPromedioEquipoB(calcularPromedioEquipo(equipoTemporal.getEquipoB()));
        equipoTemporal.setDiferenciaPromedios(Math.abs(equipoTemporal.getPromedioEquipoA() - equipoTemporal.getPromedioEquipoB()));
        
        // Guardar cambios
        return equipoTemporalRepository.save(equipoTemporal);
    }
    
    /**
     * Validar equipos temporales
     */
    public boolean validarEquiposTemporales(String sessionId) {
        EquipoTemporal equipoTemporal = obtenerEquipoTemporal(sessionId);
        if (equipoTemporal == null) return false;
        
        // Verificar que ambos equipos tengan la misma cantidad
        int cantidadEquipoA = equipoTemporal.getEquipoA().size();
        int cantidadEquipoB = equipoTemporal.getEquipoB().size();
        
        if (cantidadEquipoA != cantidadEquipoB) {
            return false;
        }
        
        // Verificar que no se repitan equipos de partidos anteriores
        if (verificarRepeticionEquipos(equipoTemporal)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verificar si los equipos se repiten de partidos anteriores
     */
    private boolean verificarRepeticionEquipos(EquipoTemporal equipoTemporal) {
        // Obtener los últimos 3 partidos para verificar
        List<Partido> partidosRecientes = partidoRepository.findAllByOrderByFechaCreacionDesc();
        
        if (partidosRecientes.size() < 2) {
            return false; // No hay suficientes partidos para comparar
        }
        
        // Tomar solo los últimos 2 partidos para verificar
        List<Partido> partidosAVerificar = partidosRecientes.subList(0, Math.min(2, partidosRecientes.size()));
        
        for (Partido partidoAnterior : partidosAVerificar) {
            if (equiposSonIguales(equipoTemporal, partidoAnterior)) {
                return true; // Se encontró una repetición
            }
        }
        
        return false; // No hay repeticiones
    }
    
    /**
     * Comparar si dos conjuntos de equipos son iguales
     */
    private boolean equiposSonIguales(EquipoTemporal equipoTemporal, Partido partidoAnterior) {
        // Verificar si el equipo A se repite
        if (equiposSonIguales(equipoTemporal.getEquipoA(), partidoAnterior.getEquipoA()) &&
            equiposSonIguales(equipoTemporal.getEquipoB(), partidoAnterior.getEquipoB())) {
            return true;
        }
        
        // Verificar si el equipo A se repite pero invertido
        if (equiposSonIguales(equipoTemporal.getEquipoA(), partidoAnterior.getEquipoB()) &&
            equiposSonIguales(equipoTemporal.getEquipoB(), partidoAnterior.getEquipoA())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Comparar si dos listas de jugadores son iguales
     */
    private boolean equiposSonIguales(List<Jugador> equipo1, List<Jugador> equipo2) {
        if (equipo1 == null || equipo2 == null) return false;
        if (equipo1.size() != equipo2.size()) return false;
        
        // Crear sets de IDs para comparar
        Set<Long> idsEquipo1 = equipo1.stream().map(Jugador::getId).collect(Collectors.toSet());
        Set<Long> idsEquipo2 = equipo2.stream().map(Jugador::getId).collect(Collectors.toSet());
        
        return idsEquipo1.equals(idsEquipo2);
    }
    
    /**
     * Limpiar equipos temporales antiguos (más de 24 horas)
     */
    public void limpiarEquiposTemporalesAntiguos() {
        try {
            System.out.println("🧹 Iniciando limpieza de equipos temporales antiguos...");
            
            // Obtener equipos temporales inactivos de más de 24 horas
            LocalDateTime limiteTiempo = LocalDateTime.now().minusHours(24);
            
            // Desactivar equipos temporales antiguos
            int equiposDesactivados = equipoTemporalRepository.desactivarAntiguos(limiteTiempo);
            
            System.out.println("✅ Limpieza completada. Equipos desactivados: " + equiposDesactivados);
            
        } catch (Exception e) {
            System.err.println("❌ Error durante la limpieza de equipos temporales: " + e.getMessage());
        }
    }
    
    /**
     * Limpiar equipos temporales de sesiones expiradas
     */
    public void limpiarSesionesExpiradas() {
        try {
            System.out.println("🧹 Iniciando limpieza de sesiones expiradas...");
            
            // Obtener equipos temporales activos de más de 2 horas
            LocalDateTime limiteSesion = LocalDateTime.now().minusHours(2);
            
            // Desactivar sesiones expiradas
            int sesionesDesactivadas = equipoTemporalRepository.desactivarSesionesExpiradas(limiteSesion);
            
            System.out.println("✅ Limpieza de sesiones completada. Sesiones desactivadas: " + sesionesDesactivadas);
            
        } catch (Exception e) {
            System.err.println("❌ Error durante la limpieza de sesiones: " + e.getMessage());
        }
    }
    
    /**
     * Guardar equipos temporales como partido permanente
     */
    public Partido guardarEquiposTemporales(String sessionId, LocalDate fechaPartido, String horaPartido, 
                                           String lugarPartido, Object precioPartidoObj, String observacionesPartido) {
        EquipoTemporal equipoTemporal = obtenerEquipoTemporal(sessionId);
        if (equipoTemporal == null) {
            throw new RuntimeException("No se encontró un equipo temporal activo para esta sesión");
        }
        
        // Validar que los equipos estén balanceados
        if (!validarEquiposTemporales(sessionId)) {
            throw new RuntimeException("Los equipos no están balanceados. Ambos deben tener la misma cantidad de jugadores.");
        }
        
        // Validar fecha del partido
        if (fechaPartido == null) {
            throw new RuntimeException("La fecha del partido es obligatoria.");
        }
        
        if (fechaPartido.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede seleccionar una fecha del pasado para el partido.");
        }
        
        // Convertir precio a Double con mejor manejo de errores
        Double precioPartido = null;
        try {
            if (precioPartidoObj != null) {
                if (precioPartidoObj instanceof String) {
                    String precioStr = ((String) precioPartidoObj).trim();
                    if (!precioStr.isEmpty()) {
                        precioPartido = Double.parseDouble(precioStr);
                    }
                } else if (precioPartidoObj instanceof Number) {
                    precioPartido = ((Number) precioPartidoObj).doubleValue();
                }
            }
            
            // Si no se pudo convertir, usar valor por defecto
            if (precioPartido == null) {
                precioPartido = 5600.0; // Valor por defecto
            }
        } catch (NumberFormatException e) {
            precioPartido = 5600.0; // Valor por defecto en caso de error
        }
        
        System.out.println("=== DEBUG: Creando partido permanente ===");
        System.out.println("Cantidad jugadores: " + equipoTemporal.getCantidadJugadores());
        System.out.println("Equipo A size: " + (equipoTemporal.getEquipoA() != null ? equipoTemporal.getEquipoA().size() : "null"));
        System.out.println("Equipo B size: " + (equipoTemporal.getEquipoB() != null ? equipoTemporal.getEquipoB().size() : "null"));
        System.out.println("Fecha partido: " + fechaPartido);
        System.out.println("Hora partido: " + horaPartido);
        System.out.println("Lugar partido: " + lugarPartido);
        System.out.println("Precio partido: " + precioPartido);
        System.out.println("Observaciones: " + observacionesPartido);
        
        // Crear partido permanente con todos los campos
        Partido partido = new Partido();
        partido.setCantidadJugadores(equipoTemporal.getCantidadJugadores());
        
        // Crear nuevas listas de jugadores para evitar referencias compartidas
        List<Jugador> equipoANuevo = new ArrayList<>();
        List<Jugador> equipoBNuevo = new ArrayList<>();
        
        // Copiar jugadores del equipo A
        if (equipoTemporal.getEquipoA() != null) {
            for (Jugador jugador : equipoTemporal.getEquipoA()) {
                Jugador nuevoJugador = new Jugador();
                nuevoJugador.setId(jugador.getId());
                nuevoJugador.setNombre(jugador.getNombre());
                nuevoJugador.setEdad(jugador.getEdad());
                nuevoJugador.setNivelEdad(jugador.getNivelEdad());
                nuevoJugador.setVelocidad(jugador.getVelocidad());
                nuevoJugador.setHabilidad(jugador.getHabilidad());
                nuevoJugador.setFuerzaFisica(jugador.getFuerzaFisica());
                nuevoJugador.setTiro(jugador.getTiro());
                nuevoJugador.setTactica(jugador.getTactica());
                nuevoJugador.setEsArquero(jugador.getEsArquero());
                equipoANuevo.add(nuevoJugador);
            }
        }
        
        // Copiar jugadores del equipo B
        if (equipoTemporal.getEquipoB() != null) {
            for (Jugador jugador : equipoTemporal.getEquipoB()) {
                Jugador nuevoJugador = new Jugador();
                nuevoJugador.setId(jugador.getId());
                nuevoJugador.setNombre(jugador.getNombre());
                nuevoJugador.setEdad(jugador.getEdad());
                nuevoJugador.setNivelEdad(jugador.getNivelEdad());
                nuevoJugador.setVelocidad(jugador.getVelocidad());
                nuevoJugador.setHabilidad(jugador.getHabilidad());
                nuevoJugador.setFuerzaFisica(jugador.getFuerzaFisica());
                nuevoJugador.setTiro(jugador.getTiro());
                nuevoJugador.setTactica(jugador.getTactica());
                nuevoJugador.setEsArquero(jugador.getEsArquero());
                equipoBNuevo.add(nuevoJugador);
            }
        }
        
        partido.setEquipoA(equipoANuevo);
        partido.setEquipoB(equipoBNuevo);
        partido.setFechaPartido(fechaPartido);
        partido.setHoraPartido(horaPartido != null ? horaPartido : "20:00");
        partido.setLugarPartido(lugarPartido != null ? lugarPartido : "Mega Fútbol");
        partido.setPrecioPartido(precioPartido);
        partido.setObservacionesPartido(observacionesPartido != null ? observacionesPartido : "");
        
        System.out.println("=== DEBUG: Guardando partido en base de datos ===");
        
        // Guardar partido
        Partido partidoGuardado = partidoRepository.save(partido);
        System.out.println("✅ Partido guardado con ID: " + partidoGuardado.getId());
        
        // Desactivar equipo temporal
        equipoTemporal.setActivo(false);
        equipoTemporalRepository.save(equipoTemporal);
        System.out.println("✅ Equipo temporal desactivado");
        
        // Limpiar equipos temporales antiguos después de guardar
        limpiarEquiposTemporalesAntiguos();
        
        System.out.println("=== DEBUG: Proceso completado exitosamente ===");
        return partidoGuardado;
    }
    
    /**
     * Eliminar partido del historial
     */
    public void eliminarPartido(Long partidoId) {
        Partido partido = partidoRepository.findById(partidoId)
            .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        
        // Eliminar físicamente el partido
        partidoRepository.delete(partido);
    }
}
