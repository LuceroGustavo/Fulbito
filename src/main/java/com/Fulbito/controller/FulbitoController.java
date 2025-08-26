package com.Fulbito.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Fulbito.model.EquipoTemporal;
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
        Jugador nuevoJugador = new Jugador();
        // Establecer valores por defecto para las habilidades
        nuevoJugador.setVelocidad(5);
        nuevoJugador.setHabilidad(5);
        nuevoJugador.setFuerzaFisica(5);
        nuevoJugador.setTiro(5);
        nuevoJugador.setTactica(5);
        nuevoJugador.setCalificacionTotal(5.0);
        model.addAttribute("jugador", nuevoJugador);
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
        List<Jugador> jugadores = jugadorRepository.findAllByOrderByCalificacionTotalDesc();
        
        model.addAttribute("totalJugadores", totalJugadores);
        model.addAttribute("jugadores", jugadores);
        return "equipos/formar";
    }
    
    /**
     * Mostrar historial de partidos (ruta corregida)
     */
    @GetMapping("/partidos/historial")
    public String historialPartidos(Model model) {
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
    
    /**
     * API para obtener todos los jugadores (JSON)
     */
    @GetMapping("/jugadores/api/todos")
    @ResponseBody
    public List<Jugador> obtenerTodosLosJugadores() {
        return jugadorRepository.findAllByOrderByCalificacionTotalDesc();
    }
    
    /**
     * Formar equipos temporales (nueva funcionalidad FASE 2)
     */
    @GetMapping("/equipos/formar-temporales")
    public String formarEquiposTemporales(@RequestParam Integer cantidadJugadores, 
                                        @RequestParam String sessionId,
                                        @RequestParam(required = false) String jugadores,
                                        Model model, 
                                        RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== DEBUG: Formando equipos temporales ===");
            System.out.println("Cantidad jugadores: " + cantidadJugadores);
            System.out.println("Session ID: " + sessionId);
            System.out.println("Jugadores: " + jugadores);
            
            EquipoTemporal equipoTemporal;
            
            if (jugadores != null && !jugadores.trim().isEmpty()) {
                // Formar equipos con jugadores específicos seleccionados
                List<Long> jugadoresIds = Arrays.stream(jugadores.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
                
                System.out.println("Jugadores IDs: " + jugadoresIds);
                equipoTemporal = formacionEquiposService.formarEquiposTemporalesConJugadores(cantidadJugadores, sessionId, jugadoresIds);
                System.out.println("Equipo temporal creado: " + equipoTemporal.getId());
            } else {
                // Formar equipos con selección aleatoria (comportamiento original)
                System.out.println("Formando equipos aleatorios");
                equipoTemporal = formacionEquiposService.formarEquiposTemporales(cantidadJugadores, sessionId);
            }
            
            model.addAttribute("equipoTemporal", equipoTemporal);
            model.addAttribute("sessionId", sessionId);
            
            System.out.println("Retornando dashboard-formacion");
            return "equipos/dashboard-formacion";
            
        } catch (Exception e) {
            System.err.println("=== ERROR en formarEquiposTemporales ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            redirectAttributes.addFlashAttribute("mensaje", "Error al formar equipos: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/equipos/formar";
        }
    }
    
    /**
     * Intercambiar jugadores entre equipos
     */
    @PostMapping("/equipos/intercambiar")
    @ResponseBody
    public Map<String, Object> intercambiarJugadores(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = (String) request.get("sessionId");
            Long jugadorIdA = Long.parseLong(request.get("jugadorIdA").toString());
            Long jugadorIdB = Long.parseLong(request.get("jugadorIdB").toString());
            
            EquipoTemporal equipoTemporal = formacionEquiposService.intercambiarJugadores(sessionId, jugadorIdA, jugadorIdB);
            
            response.put("success", true);
            response.put("mensaje", "Jugadores intercambiados exitosamente");
            response.put("equipoTemporal", equipoTemporal);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al intercambiar jugadores: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Mover jugador de un equipo a otro (sin intercambio)
     */
    @PostMapping("/equipos/mover-jugador")
    @ResponseBody
    public Map<String, Object> moverJugador(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = (String) request.get("sessionId");
            Long jugadorId = Long.parseLong(request.get("jugadorId").toString());
            String equipoOrigen = (String) request.get("equipoOrigen");
            String equipoDestino = (String) request.get("equipoDestino");
            
            EquipoTemporal equipoTemporal = formacionEquiposService.moverJugador(sessionId, jugadorId, equipoOrigen, equipoDestino);
            
            response.put("success", true);
            response.put("mensaje", "Jugador movido exitosamente");
            response.put("equipoTemporal", equipoTemporal);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al mover jugador: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Regenerar equipos temporales
     */
    @PostMapping("/equipos/regenerar")
    @ResponseBody
    public Map<String, Object> regenerarEquipos(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = (String) request.get("sessionId");
            EquipoTemporal equipoTemporal = formacionEquiposService.regenerarEquiposTemporales(sessionId);
            
            response.put("success", true);
            response.put("mensaje", "Equipos regenerados exitosamente");
            response.put("equipoTemporal", equipoTemporal);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al regenerar equipos: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Guardar equipos temporales como partido permanente
     */
    @PostMapping("/equipos/guardar-temporales")
    @ResponseBody
    public Map<String, Object> guardarEquiposTemporales(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sessionId = (String) request.get("sessionId");
            Partido partido = formacionEquiposService.guardarEquiposTemporales(sessionId);
            
            response.put("success", true);
            response.put("mensaje", "Equipos guardados exitosamente");
            response.put("partido", partido);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al guardar equipos: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Vista de captura para WhatsApp
     */
    @GetMapping("/equipos/captura-whatsapp")
    public String capturaWhatsApp(@RequestParam String sessionId, Model model) {
        try {
            EquipoTemporal equipoTemporal = formacionEquiposService.obtenerEquipoTemporal(sessionId);
            
            if (equipoTemporal == null) {
                throw new RuntimeException("No se encontraron equipos temporales para esta sesión");
            }
            
            model.addAttribute("equipoTemporal", equipoTemporal);
            model.addAttribute("sessionId", sessionId);
            return "equipos/captura-whatsapp";
            
        } catch (Exception e) {
            // Redirigir a la página de formación si hay error
            return "redirect:/equipos/formar";
        }
    }
}
