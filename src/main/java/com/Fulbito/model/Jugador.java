package com.Fulbito.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "jugadores")
public class Jugador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 16, message = "La edad mínima es 16 años")
    @Max(value = 80, message = "La edad máxima es 80 años")
    @Column(name = "edad", nullable = false)
    private Integer edad;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_edad", nullable = false)
    private NivelEdad nivelEdad;
    
    @NotNull(message = "La velocidad es obligatoria")
    @Min(value = 1, message = "La velocidad debe ser entre 1 y 10")
    @Max(value = 10, message = "La velocidad debe ser entre 1 y 10")
    @Column(name = "velocidad", nullable = false)
    private Integer velocidad;
    
    @NotNull(message = "La habilidad es obligatoria")
    @Min(value = 1, message = "La habilidad debe ser entre 1 y 10")
    @Max(value = 10, message = "La habilidad debe ser entre 1 y 10")
    @Column(name = "habilidad", nullable = false)
    private Integer habilidad;
    
    @NotNull(message = "La fuerza física es obligatoria")
    @Min(value = 1, message = "La fuerza física debe ser entre 1 y 10")
    @Max(value = 10, message = "La fuerza física debe ser entre 1 y 10")
    @Column(name = "fuerza_fisica", nullable = false)
    private Integer fuerzaFisica;
    
    @NotNull(message = "El tiro es obligatorio")
    @Min(value = 1, message = "El tiro debe ser entre 1 y 10")
    @Max(value = 10, message = "El tiro debe ser entre 1 y 10")
    @Column(name = "tiro", nullable = false)
    private Integer tiro;
    
    @NotNull(message = "La táctica es obligatoria")
    @Min(value = 1, message = "La táctica debe ser entre 1 y 10")
    @Max(value = 10, message = "La táctica debe ser entre 1 y 10")
    @Column(name = "tactica", nullable = false)
    private Integer tactica;
    
    @Column(name = "es_arquero", nullable = false)
    private Boolean esArquero = false;
    
    @Column(name = "calificacion_total")
    private Double calificacionTotal;
    
    // Constructor por defecto
    public Jugador() {}
    
    // Constructor con parámetros
    public Jugador(String nombre, Integer edad, Integer velocidad, Integer habilidad, 
                   Integer fuerzaFisica, Integer tiro, Integer tactica, Boolean esArquero) {
        this.nombre = nombre;
        this.edad = edad;
        this.velocidad = velocidad;
        this.habilidad = habilidad;
        this.fuerzaFisica = fuerzaFisica;
        this.tiro = tiro;
        this.tactica = tactica;
        this.esArquero = esArquero;
        this.nivelEdad = calcularNivelEdad(edad);
        this.calificacionTotal = calcularCalificacionTotal();
    }
    
    // Método para calcular el nivel de edad
    private NivelEdad calcularNivelEdad(Integer edad) {
        if (edad < 25) {
            return NivelEdad.JOVEN;
        } else if (edad < 40) {
            return NivelEdad.INTERMEDIO;
        } else {
            return NivelEdad.VIEJITO;
        }
    }
    
    // Método para calcular la calificación total
    private Double calcularCalificacionTotal() {
        return (velocidad + habilidad + fuerzaFisica + tiro + tactica) / 5.0;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Integer getEdad() {
        return edad;
    }
    
    public void setEdad(Integer edad) {
        this.edad = edad;
        this.nivelEdad = calcularNivelEdad(edad);
    }
    
    public NivelEdad getNivelEdad() {
        return nivelEdad;
    }
    
    public void setNivelEdad(NivelEdad nivelEdad) {
        this.nivelEdad = nivelEdad;
    }
    
    public Integer getVelocidad() {
        return velocidad;
    }
    
    public void setVelocidad(Integer velocidad) {
        this.velocidad = velocidad;
    }
    
    public Integer getHabilidad() {
        return habilidad;
    }
    
    public void setHabilidad(Integer habilidad) {
        this.habilidad = habilidad;
    }
    
    public Integer getFuerzaFisica() {
        return fuerzaFisica;
    }
    
    public void setFuerzaFisica(Integer fuerzaFisica) {
        this.fuerzaFisica = fuerzaFisica;
    }
    
    public Integer getTiro() {
        return tiro;
    }
    
    public void setTiro(Integer tiro) {
        this.tiro = tiro;
    }
    
    public Integer getTactica() {
        return tactica;
    }
    
    public void setTactica(Integer tactica) {
        this.tactica = tactica;
    }
    
    public Boolean getEsArquero() {
        return esArquero;
    }
    
    public void setEsArquero(Boolean esArquero) {
        this.esArquero = esArquero;
    }
    
    public Double getCalificacionTotal() {
        if (calificacionTotal == null) {
            calificacionTotal = calcularCalificacionTotal();
        }
        return calificacionTotal;
    }
    
    public void setCalificacionTotal(Double calificacionTotal) {
        this.calificacionTotal = calificacionTotal;
    }
    
    /**
     * Método público para recalcular la calificación total
     * Útil cuando se han modificado las habilidades
     */
    public void recalcularCalificacionTotal() {
        this.calificacionTotal = calcularCalificacionTotal();
    }
    
    @Override
    public String toString() {
        return "Jugador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", nivelEdad=" + nivelEdad +
                ", calificacionTotal=" + calificacionTotal +
                ", esArquero=" + esArquero +
                '}';
    }
}
