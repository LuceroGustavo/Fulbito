package com.Fulbito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "partidos")
public class Partido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
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
    }
    
    // Constructor con parámetros
    public Partido(Integer cantidadJugadores, List<Jugador> equipoA, List<Jugador> equipoB) {
        this();
        this.cantidadJugadores = cantidadJugadores;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        calcularPromedios();
    }
    
    // Método para calcular los promedios de los equipos
    private void calcularPromedios() {
        if (equipoA != null && !equipoA.isEmpty()) {
            this.promedioEquipoA = equipoA.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
        }
        
        if (equipoB != null && !equipoB.isEmpty()) {
            this.promedioEquipoB = equipoB.stream()
                    .mapToDouble(Jugador::getCalificacionTotal)
                    .average()
                    .orElse(0.0);
        }
        
        this.diferenciaPromedios = Math.abs(promedioEquipoA - promedioEquipoB);
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
                ", cantidadJugadores=" + cantidadJugadores +
                ", promedioEquipoA=" + promedioEquipoA +
                ", promedioEquipoB=" + promedioEquipoB +
                ", diferenciaPromedios=" + diferenciaPromedios +
                '}';
    }
}
