package com.Fulbito.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Fulbito.model.Jugador;
import com.Fulbito.model.NivelEdad;
import com.Fulbito.model.Partido;
import com.Fulbito.repository.JugadorRepository;
import com.Fulbito.service.FormacionEquiposService;

import jakarta.validation.Valid;

@Controller
public class FulbitoController {
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private FormacionEquiposService formacionEquiposService;
    
    // Página principal
    @GetMapping("/")
    public String index(Model model) {
        Map<String, Object> estadisticas = formacionEquiposService.obtenerEstadisticas();
        model.addAttribute("estadisticas", estadisticas);
        return "index";
    }
    
    // Mostrar formulario para agregar jugador
    @GetMapping("/jugador/nuevo")
    public String nuevoJugador(Model model) {
        model.addAttribute("jugador", new Jugador());
        return "jugador/form";
    }
    
    // Guardar nuevo jugador
    @PostMapping("/jugador/guardar")
    public String guardarJugador(@Valid @ModelAttribute Jugador jugador, 
                                BindingResult result, 
                                RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG: Guardando jugador ===");
        System.out.println("Nombre: " + jugador.getNombre());
        System.out.println("Edad: " + jugador.getEdad());
        System.out.println("Velocidad: " + jugador.getVelocidad());
        System.out.println("Habilidad: " + jugador.getHabilidad());
        System.out.println("Fuerza Física: " + jugador.getFuerzaFisica());
        System.out.println("Tiro: " + jugador.getTiro());
        System.out.println("Táctica: " + jugador.getTactica());
        System.out.println("Es Arquero: " + jugador.getEsArquero());
        System.out.println("Nivel Edad: " + jugador.getNivelEdad());
        System.out.println("Calificación Total: " + jugador.getCalificacionTotal());
        
        if (result.hasErrors()) {
            System.out.println("=== ERRORES DE VALIDACIÓN ===");
            result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "jugador/form";
        }
        
        try {
            // Asegurar que se calculen los campos automáticos
            if (jugador.getNivelEdad() == null) {
                if (jugador.getEdad() < 25) {
                    jugador.setNivelEdad(NivelEdad.JOVEN);
                } else if (jugador.getEdad() < 40) {
                    jugador.setNivelEdad(NivelEdad.INTERMEDIO);
                } else {
                    jugador.setNivelEdad(NivelEdad.VIEJITO);
                }
            }
            
            if (jugador.getCalificacionTotal() == null) {
                double calificacionTotal = (jugador.getVelocidad() + jugador.getHabilidad() + 
                                          jugador.getFuerzaFisica() + jugador.getTiro() + jugador.getTactica()) / 5.0;
                jugador.setCalificacionTotal(calificacionTotal);
            }
            
            Jugador jugadorGuardado = jugadorRepository.save(jugador);
            System.out.println("=== JUGADOR GUARDADO EXITOSAMENTE ===");
            System.out.println("ID: " + jugadorGuardado.getId());
            
            redirectAttributes.addFlashAttribute("mensaje", "Jugador agregado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            System.out.println("=== ERROR AL GUARDAR ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el jugador: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/jugadores";
    }
    
    // Listar todos los jugadores
    @GetMapping("/jugadores")
    public String listarJugadores(Model model) {
        List<Jugador> jugadores = jugadorRepository.findAllByOrderByCalificacionTotalDesc();
        model.addAttribute("jugadores", jugadores);
        return "jugador/lista";
    }
    
    // Mostrar formulario para formar equipos
    @GetMapping("/equipos/formar")
    public String formarEquipos(Model model) {
        long totalJugadores = jugadorRepository.count();
        model.addAttribute("totalJugadores", totalJugadores);
        return "equipos/formar";
    }
    
    // Formar equipos
    @PostMapping("/equipos/formar")
    public String crearEquipos(@RequestParam Integer cantidadJugadores, 
                              Model model, 
                              RedirectAttributes redirectAttributes) {
        try {
            Partido partido = formacionEquiposService.formarEquipos(cantidadJugadores);
            redirectAttributes.addFlashAttribute("mensaje", "Equipos formados exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/equipos/resultado/" + partido.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al formar equipos: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/equipos/formar";
        }
    }
    
    // Mostrar resultado de los equipos
    @GetMapping("/equipos/resultado/{id}")
    public String mostrarResultado(@PathVariable Long id, Model model) {
        Partido partido = formacionEquiposService.obtenerHistorialPartidos()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (partido == null) {
            return "redirect:/";
        }
        
        model.addAttribute("partido", partido);
        return "equipos/resultado";
    }
    
    // Mostrar historial de partidos
    @GetMapping("/historial")
    public String historial(Model model) {
        List<Partido> partidos = formacionEquiposService.obtenerHistorialPartidos();
        model.addAttribute("partidos", partidos);
        return "partidos/historial";
    }
    
    // Editar jugador
    @GetMapping("/jugador/editar/{id}")
    public String editarJugador(@PathVariable Long id, Model model) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        model.addAttribute("jugador", jugador);
        return "jugador/form";
    }
    
    // Eliminar jugador
    @PostMapping("/jugador/eliminar/{id}")
    public String eliminarJugador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jugadorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Jugador eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el jugador: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/jugadores";
    }
    
    // Buscar jugadores
    @GetMapping("/jugadores/buscar")
    public String buscarJugadores(@RequestParam(required = false) String nombre, Model model) {
        List<Jugador> jugadores;
        if (nombre != null && !nombre.trim().isEmpty()) {
            jugadores = jugadorRepository.findByNombreContainingIgnoreCase(nombre.trim());
        } else {
            jugadores = jugadorRepository.findAllByOrderByCalificacionTotalDesc();
        }
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("nombreBusqueda", nombre);
        return "jugador/lista";
    }
}
