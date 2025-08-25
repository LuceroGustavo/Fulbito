package com.Fulbito.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Fulbito.model.Jugador;
import com.Fulbito.model.NivelEdad;
import com.Fulbito.repository.JugadorRepository;
import com.Fulbito.service.FormacionEquiposService;

@Controller
public class TestController {
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private FormacionEquiposService formacionEquiposService;
    
    @GetMapping("/test")
    public String test() {
        return "test";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    
    // Método de prueba para crear un jugador
    @PostMapping("/test/crear-jugador")
    @ResponseBody
    public String crearJugadorPrueba(@RequestParam String nombre,
                                    @RequestParam Integer edad,
                                    @RequestParam Integer velocidad,
                                    @RequestParam Integer habilidad,
                                    @RequestParam Integer fuerzaFisica,
                                    @RequestParam Integer tiro,
                                    @RequestParam Integer tactica,
                                    @RequestParam(required = false) Boolean esArquero) {
        try {
            System.out.println("=== CREANDO JUGADOR DE PRUEBA ===");
            System.out.println("Nombre: " + nombre);
            System.out.println("Edad: " + edad);
            System.out.println("Velocidad: " + velocidad);
            System.out.println("Habilidad: " + habilidad);
            System.out.println("Fuerza Física: " + fuerzaFisica);
            System.out.println("Tiro: " + tiro);
            System.out.println("Táctica: " + tactica);
            System.out.println("Es Arquero: " + esArquero);
            
            Jugador jugador = new Jugador();
            jugador.setNombre(nombre);
            jugador.setEdad(edad);
            jugador.setVelocidad(velocidad);
            jugador.setHabilidad(habilidad);
            jugador.setFuerzaFisica(fuerzaFisica);
            jugador.setTiro(tiro);
            jugador.setTactica(tactica);
            jugador.setEsArquero(esArquero != null ? esArquero : false);
            
            // Calcular nivel de edad y calificación total
            if (jugador.getEdad() < 25) {
                jugador.setNivelEdad(NivelEdad.JOVEN);
            } else if (jugador.getEdad() < 40) {
                jugador.setNivelEdad(NivelEdad.INTERMEDIO);
            } else {
                jugador.setNivelEdad(NivelEdad.VIEJITO);
            }
            
            double calificacionTotal = (jugador.getVelocidad() + jugador.getHabilidad() + 
                                      jugador.getFuerzaFisica() + jugador.getTiro() + jugador.getTactica()) / 5.0;
            jugador.setCalificacionTotal(calificacionTotal);
            
            System.out.println("Nivel Edad: " + jugador.getNivelEdad());
            System.out.println("Calificación Total: " + jugador.getCalificacionTotal());
            
            Jugador jugadorGuardado = jugadorRepository.save(jugador);
            
            System.out.println("=== JUGADOR GUARDADO EXITOSAMENTE ===");
            System.out.println("ID: " + jugadorGuardado.getId());
            
            return "Jugador creado exitosamente con ID: " + jugadorGuardado.getId() + 
                   ", Nombre: " + jugadorGuardado.getNombre() + 
                   ", Calificación: " + jugadorGuardado.getCalificacionTotal();
                   
        } catch (Exception e) {
            System.out.println("=== ERROR AL CREAR JUGADOR ===");
            e.printStackTrace();
            return "Error al crear jugador: " + e.getMessage() + "\n" + e.getStackTrace()[0];
        }
    }
    
    // Método para listar jugadores en formato JSON
    @GetMapping("/test/jugadores")
    @ResponseBody
    public String listarJugadores() {
        try {
            long total = jugadorRepository.count();
            return "Total de jugadores en la base de datos: " + total;
        } catch (Exception e) {
            return "Error al contar jugadores: " + e.getMessage();
        }
    }
    
    // Método para mostrar el template de prueba de resultados
    @GetMapping("/test-resultado")
    public String mostrarTestResultado() {
        return "test-resultado";
    }
    
    // Método para mostrar estadísticas de repetición
    @GetMapping("/test/estadisticas-repeticion")
    @ResponseBody
    public String mostrarEstadisticasRepeticion() {
        try {
            Map<String, Object> stats = formacionEquiposService.obtenerEstadisticasRepeticion();
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== ESTADÍSTICAS DE REPETICIÓN DE EQUIPOS ===\n");
            resultado.append("Total de partidos: ").append(stats.get("totalPartidos")).append("\n");
            resultado.append("Partidos con repetición: ").append(stats.get("partidosConRepeticion")).append("\n");
            resultado.append("Porcentaje de repetición: ").append(String.format("%.2f", stats.get("porcentajeRepeticion"))).append("%\n");
            
            if ((Integer) stats.get("totalPartidos") > 0) {
                resultado.append("\nLa aplicación está configurada para evitar repeticiones.\n");
                resultado.append("Se realizan hasta 10 intentos para formar equipos únicos.\n");
            }
            
            return resultado.toString();
        } catch (Exception e) {
            return "Error al obtener estadísticas: " + e.getMessage();
        }
    }
}
