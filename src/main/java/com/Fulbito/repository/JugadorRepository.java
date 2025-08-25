package com.Fulbito.repository;

import com.Fulbito.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    
    // Buscar jugadores por nivel de edad
    List<Jugador> findByNivelEdad(String nivelEdad);
    
    // Buscar arqueros
    List<Jugador> findByEsArqueroTrue();
    
    // Buscar jugadores que no son arqueros
    List<Jugador> findByEsArqueroFalse();
    
    // Buscar jugadores por rango de calificación
    @Query("SELECT j FROM Jugador j WHERE j.calificacionTotal BETWEEN :minCalificacion AND :maxCalificacion ORDER BY j.calificacionTotal DESC")
    List<Jugador> findByCalificacionTotalBetween(@Param("minCalificacion") Double minCalificacion, 
                                                 @Param("maxCalificacion") Double maxCalificacion);
    
    // Buscar jugadores ordenados por calificación total (descendente)
    List<Jugador> findAllByOrderByCalificacionTotalDesc();
    
    // Buscar jugadores por nombre (búsqueda parcial)
    List<Jugador> findByNombreContainingIgnoreCase(String nombre);
    
    // Contar arqueros
    long countByEsArqueroTrue();
    
    // Contar jugadores por nivel de edad
    long countByNivelEdad(String nivelEdad);
}
