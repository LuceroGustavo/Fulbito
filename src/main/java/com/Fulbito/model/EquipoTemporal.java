package com.Fulbito.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "equipos_temporales")
public class EquipoTemporal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "cantidad_jugadores", nullable = false)
    private Integer cantidadJugadores;
    
    @ManyToMany
    @JoinTable(
        name = "equipo_temporal_a_jugadores",
        joinColumns = @JoinColumn(name = "equipo_temporal_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Jugador> equipoA;
    
    @ManyToMany
    @JoinTable(
        name = "equipo_temporal_b_jugadores",
        joinColumns = @JoinColumn(name = "equipo_temporal_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Jugador> equipoB;
    
    @Column(name = "promedio_equipo_a")
    private Double promedioEquipoA;
    
    @Column(name = "promedio_equipo_b")
    private Double promedioEquipoB;
    
    @Column(name = "diferencia_promedios")
    private Double diferenciaPromedios;
    
    @Column(name = "promedio_edad_equipo_a")
    private Double promedioEdadEquipoA;
    
    @Column(name = "promedio_edad_equipo_b")
    private Double promedioEdadEquipoB;
    
    @Column(name = "diferencia_edades")
    private Double diferenciaEdades;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    // Campos de configuración del partido
    @Column(name = "hora_partido")
    private String horaPartido;
    
    @Column(name = "lugar_partido")
    private String lugarPartido;
    
    @Column(name = "precio_partido")
    private Double precioPartido;
    
    @Column(name = "observaciones_partido", columnDefinition = "TEXT")
    private String observacionesPartido;
    
    // Constructor por defecto
    public EquipoTemporal() {
        this.fechaCreacion = LocalDateTime.now();
        this.horaPartido = "20:00"; // Por defecto, 8 PM
        this.lugarPartido = "Mega Fútbol"; // Por defecto
        this.precioPartido = 5600.0; // Por defecto
        this.observacionesPartido = ""; // Por defecto, vacío
    }
    
    // Constructor con parámetros
    public EquipoTemporal(Integer cantidadJugadores, List<Jugador> equipoA, 
                         List<Jugador> equipoB, String sessionId) {
        this();
        this.cantidadJugadores = cantidadJugadores;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.sessionId = sessionId;
        this.calcularPromedios();
    }
    
    // Método para calcular promedios
    public void calcularPromedios() {
        if (equipoA != null && !equipoA.isEmpty()) {
            this.promedioEquipoA = equipoA.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
            
            this.promedioEdadEquipoA = equipoA.stream()
                    .mapToDouble(Jugador::getEdad)
                    .average()
                    .orElse(0.0);
        }
        
        if (equipoB != null && !equipoB.isEmpty()) {
            this.promedioEquipoB = equipoB.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
            
            this.promedioEdadEquipoB = equipoB.stream()
                    .mapToDouble(Jugador::getEdad)
                    .average()
                    .orElse(0.0);
        }
        
        if (this.promedioEquipoA != null && this.promedioEquipoB != null) {
            this.diferenciaPromedios = Math.abs(this.promedioEquipoA - this.promedioEquipoB);
        }
        
        if (this.promedioEdadEquipoA != null && this.promedioEdadEquipoB != null) {
            this.diferenciaEdades = Math.abs(this.promedioEdadEquipoA - this.promedioEdadEquipoB);
        }
    }
    
    // Método para validar que los equipos tengan la misma cantidad
    @Transient
    public boolean equiposBalanceados() {
        if (equipoA == null || equipoB == null) {
            return false;
        }
        return equipoA.size() == equipoB.size();
    }
    
    // Método para obtener el total de jugadores
    @Transient
    public int getTotalJugadores() {
        int totalA = equipoA != null ? equipoA.size() : 0;
        int totalB = equipoB != null ? equipoB.size() : 0;
        return totalA + totalB;
    }
    
    // Método para obtener el balance de equipos
    @Transient
    public String getBalanceEquipos() {
        if (diferenciaPromedios == null) {
            return "No calculado";
        }
        
        if (diferenciaPromedios <= 0.5) {
            return "Excelente";
        } else if (diferenciaPromedios <= 1.0) {
            return "Bueno";
        } else {
            return "Regular";
        }
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Integer getCantidadJugadores() {
        return cantidadJugadores;
    }
    
    public void setCantidadJugadores(Integer cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
    }
    
    public List<Jugador> getEquipoA() {
        return equipoA;
    }
    
    public void setEquipoA(List<Jugador> equipoA) {
        this.equipoA = equipoA;
        this.calcularPromedios();
    }
    
    public List<Jugador> getEquipoB() {
        return equipoB;
    }
    
    public void setEquipoB(List<Jugador> equipoB) {
        this.equipoB = equipoB;
        this.calcularPromedios();
    }
    
    public Double getPromedioEquipoA() {
        return promedioEquipoA;
    }
    
    public void setPromedioEquipoA(Double promedioEquipoA) {
        this.promedioEquipoA = promedioEquipoA;
    }
    
    public Double getPromedioEquipoB() {
        return promedioEquipoB;
    }
    
    public void setPromedioEquipoB(Double promedioEquipoB) {
        this.promedioEquipoB = promedioEquipoB;
    }
    
    public Double getDiferenciaPromedios() {
        return diferenciaPromedios;
    }
    
    public void setDiferenciaPromedios(Double diferenciaPromedios) {
        this.diferenciaPromedios = diferenciaPromedios;
    }
    
    public Double getPromedioEdadEquipoA() {
        return promedioEdadEquipoA;
    }
    
    public void setPromedioEdadEquipoA(Double promedioEdadEquipoA) {
        this.promedioEdadEquipoA = promedioEdadEquipoA;
    }
    
    public Double getPromedioEdadEquipoB() {
        return promedioEdadEquipoB;
    }
    
    public void setPromedioEdadEquipoB(Double promedioEdadEquipoB) {
        this.promedioEdadEquipoB = promedioEdadEquipoB;
    }
    
    public Double getDiferenciaEdades() {
        return diferenciaEdades;
    }
    
    public void setDiferenciaEdades(Double diferenciaEdades) {
        this.diferenciaEdades = diferenciaEdades;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public String getHoraPartido() {
        return horaPartido;
    }
    
    public void setHoraPartido(String horaPartido) {
        this.horaPartido = horaPartido;
    }
    
    public String getLugarPartido() {
        return lugarPartido;
    }
    
    public void setLugarPartido(String lugarPartido) {
        this.lugarPartido = lugarPartido;
    }
    
    public Double getPrecioPartido() {
        return precioPartido;
    }
    
    public void setPrecioPartido(Double precioPartido) {
        this.precioPartido = precioPartido;
    }
    
    public String getObservacionesPartido() {
        return observacionesPartido;
    }
    
    public void setObservacionesPartido(String observacionesPartido) {
        this.observacionesPartido = observacionesPartido;
    }
    
    @Override
    public String toString() {
        return "EquipoTemporal{" +
                "id=" + id +
                ", fechaCreacion=" + fechaCreacion +
                ", cantidadJugadores=" + cantidadJugadores +
                ", equipoA.size=" + (equipoA != null ? equipoA.size() : 0) +
                ", equipoB.size=" + (equipoB != null ? equipoB.size() : 0) +
                ", promedioEquipoA=" + promedioEquipoA +
                ", promedioEquipoB=" + promedioEquipoB +
                ", diferenciaPromedios=" + diferenciaPromedios +
                ", promedioEdadEquipoA=" + promedioEdadEquipoA +
                ", promedioEdadEquipoB=" + promedioEdadEquipoB +
                ", diferenciaEdades=" + diferenciaEdades +
                ", horaPartido='" + horaPartido + '\'' +
                ", lugarPartido='" + lugarPartido + '\'' +
                ", precioPartido=" + precioPartido +
                ", observacionesPartido='" + observacionesPartido + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", activo=" + activo +
                '}';
    }
}
