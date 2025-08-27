package com.Fulbito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Fulbito.model.EquipoTemporal;

@Repository
public interface EquipoTemporalRepository extends JpaRepository<EquipoTemporal, Long> {
    
    /**
     * Busca equipos temporales activos por session ID
     */
    List<EquipoTemporal> findBySessionIdAndActivoTrue(String sessionId);
    
    /**
     * Busca el equipo temporal más reciente por session ID
     */
    Optional<EquipoTemporal> findFirstBySessionIdAndActivoTrueOrderByFechaCreacionDesc(String sessionId);
    
    /**
     * Busca equipos temporales por cantidad de jugadores
     */
    List<EquipoTemporal> findByCantidadJugadoresAndActivoTrue(Integer cantidadJugadores);
    
    /**
     * Desactiva todos los equipos temporales de una sesión
     */
    @Modifying
    @Transactional
    @Query("UPDATE EquipoTemporal e SET e.activo = false WHERE e.sessionId = :sessionId")
    void desactivarPorSessionId(@Param("sessionId") String sessionId);
    
    /**
     * Cuenta equipos temporales activos por session ID
     */
    long countBySessionIdAndActivoTrue(String sessionId);
    
    /**
     * Busca equipos temporales con diferencia de promedios menor a un valor
     */
    @Query("SELECT e FROM EquipoTemporal e WHERE e.diferenciaPromedios < :diferenciaMaxima AND e.activo = true")
    List<EquipoTemporal> findByDiferenciaPromediosMenorQue(@Param("diferenciaMaxima") Double diferenciaMaxima);
    
    /**
     * Desactiva equipos temporales antiguos (más de 24 horas)
     */
    @Modifying
    @Transactional
    @Query("UPDATE EquipoTemporal e SET e.activo = false WHERE e.fechaCreacion < :fechaLimite AND e.activo = true")
    int desactivarAntiguos(@Param("fechaLimite") java.time.LocalDateTime fechaLimite);
    
    /**
     * Desactiva sesiones expiradas (más de 2 horas)
     */
    @Modifying
    @Transactional
    @Query("UPDATE EquipoTemporal e SET e.activo = false WHERE e.fechaCreacion < :fechaLimite AND e.activo = true")
    int desactivarSesionesExpiradas(@Param("fechaLimite") java.time.LocalDateTime fechaLimite);
}
