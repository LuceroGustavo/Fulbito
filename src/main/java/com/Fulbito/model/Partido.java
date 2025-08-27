package com.Fulbito.model;

import java.time.LocalDate;
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

@Entity
@Table(name = "partidos")
public class Partido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_partido", nullable = false)
    private LocalDate fechaPartido;
    
    @Column(name = "hora_partido")
    private String horaPartido;
    
    @Column(name = "lugar_partido")
    private String lugarPartido;
    
    @Column(name = "precio_partido")
    private Double precioPartido;
    
    @Column(name = "observaciones_partido", columnDefinition = "TEXT")
    private String observacionesPartido;
    
    @Column(name = "cantidad_jugadores", nullable = false)
    private Integer cantidadJugadores;
    
    @ManyToMany
    @JoinTable(
        name = "partido_equipo_a",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Jugador> equipoA;
    
    @ManyToMany
    @JoinTable(
        name = "partido_equipo_b",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Jugador> equipoB;
    
    @Column(name = "promedio_equipo_a")
    private Double promedioEquipoA;
    
    @Column(name = "promedio_equipo_b")
    private Double promedioEquipoB;
    
    @Column(name = "diferencia_promedios")
    private Double diferenciaPromedios;
    
    // Constructor por defecto
    public Partido() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaPartido = LocalDate.now(); // Por defecto, fecha actual
        this.horaPartido = "20:00"; // Por defecto, 8 PM
        this.lugarPartido = "Mega Fútbol"; // Por defecto
        this.precioPartido = 5600.0; // Por defecto
        this.observacionesPartido = ""; // Por defecto, vacío
    }
    
    // Constructor con parámetros
    public Partido(Integer cantidadJugadores, List<Jugador> equipoA, List<Jugador> equipoB) {
        this();
        this.cantidadJugadores = cantidadJugadores;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        calcularPromedios();
    }
    
    // Constructor con fecha específica
    public Partido(Integer cantidadJugadores, List<Jugador> equipoA, List<Jugador> equipoB, LocalDate fechaPartido) {
        this();
        this.cantidadJugadores = cantidadJugadores;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.fechaPartido = fechaPartido;
        calcularPromedios();
    }
    
    // Constructor completo con todos los parámetros
    public Partido(Integer cantidadJugadores, List<Jugador> equipoA, List<Jugador> equipoB, 
                   LocalDate fechaPartido, String horaPartido, String lugarPartido, 
                   Double precioPartido, String observacionesPartido) {
        this();
        this.cantidadJugadores = cantidadJugadores;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.fechaPartido = fechaPartido;
        this.horaPartido = horaPartido != null ? horaPartido : "20:00";
        this.lugarPartido = lugarPartido != null ? lugarPartido : "Mega Fútbol";
        this.precioPartido = precioPartido != null ? precioPartido : 5600.0;
        this.observacionesPartido = observacionesPartido != null ? observacionesPartido : "";
        calcularPromedios();
    }
    
    // Método para calcular los promedios de los equipos
    private void calcularPromedios() {
        if (equipoA != null && !equipoA.isEmpty()) {
            this.promedioEquipoA = equipoA.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
        } else {
            this.promedioEquipoA = 0.0;
        }
        
        if (equipoB != null && !equipoB.isEmpty()) {
            this.promedioEquipoB = equipoB.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
        } else {
            this.promedioEquipoB = 0.0;
        }
        
        // Calcular diferencia solo si ambos promedios son válidos
        if (this.promedioEquipoA != null && this.promedioEquipoB != null) {
            this.diferenciaPromedios = Math.abs(this.promedioEquipoA - this.promedioEquipoB);
        } else {
            this.diferenciaPromedios = 0.0;
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
    
    public LocalDate getFechaPartido() {
        return fechaPartido;
    }
    
    public void setFechaPartido(LocalDate fechaPartido) {
        this.fechaPartido = fechaPartido;
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
        calcularPromedios();
    }
    
    public List<Jugador> getEquipoB() {
        return equipoB;
    }
    
    public void setEquipoB(List<Jugador> equipoB) {
        this.equipoB = equipoB;
        calcularPromedios();
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
    
    @Override
    public String toString() {
        return "Partido{" +
                "id=" + id +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaPartido=" + fechaPartido +
                ", horaPartido='" + horaPartido + '\'' +
                ", lugarPartido='" + lugarPartido + '\'' +
                ", precioPartido=" + precioPartido +
                ", observacionesPartido='" + observacionesPartido + '\'' +
                ", cantidadJugadores=" + cantidadJugadores +
                ", promedioEquipoA=" + promedioEquipoA +
                ", promedioEquipoB=" + promedioEquipoB +
                ", diferenciaPromedios=" + diferenciaPromedios +
                '}';
    }
}
