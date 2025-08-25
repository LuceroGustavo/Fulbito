package com.Fulbito.repository;

import com.Fulbito.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    
    // Buscar partidos por fecha
    List<Partido> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Buscar partidos por cantidad de jugadores
    List<Partido> findByCantidadJugadores(Integer cantidadJugadores);
    
    // Buscar partidos ordenados por fecha (más recientes primero)
    List<Partido> findAllByOrderByFechaCreacionDesc();
    
    // Buscar partidos con diferencia de promedios menor a un valor
    @Query("SELECT p FROM Partido p WHERE p.diferenciaPromedios < :diferenciaMaxima ORDER BY p.fechaCreacion DESC")
    List<Partido> findByDiferenciaPromediosLessThan(@Param("diferenciaMaxima") Double diferenciaMaxima);
    
    // Buscar partidos por rango de fechas y cantidad de jugadores
    @Query("SELECT p FROM Partido p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin AND p.cantidadJugadores = :cantidadJugadores ORDER BY p.fechaCreacion DESC")
    List<Partido> findByFechaCreacionBetweenAndCantidadJugadores(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                                 @Param("fechaFin") LocalDateTime fechaFin, 
                                                                 @Param("cantidadJugadores") Integer cantidadJugadores);
    
    // Contar partidos por cantidad de jugadores
    long countByCantidadJugadores(Integer cantidadJugadores);
    
    // Contar partidos en un rango de fechas
    long countByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
