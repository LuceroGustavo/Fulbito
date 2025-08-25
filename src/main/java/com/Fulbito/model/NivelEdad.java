package com.Fulbito.model;

public enum NivelEdad {
    JOVEN("Joven", "16-24 años"),
    INTERMEDIO("Intermedio", "25-39 años"),
    VIEJITO("Viejito", "40+ años");
    
    private final String descripcion;
    private final String rangoEdad;
    
    NivelEdad(String descripcion, String rangoEdad) {
        this.descripcion = descripcion;
        this.rangoEdad = rangoEdad;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getRangoEdad() {
        return rangoEdad;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}
