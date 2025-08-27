package com.Fulbito.controller;

import java.time.LocalDate;
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
     * Redirección para compatibilidad con enlaces antiguos
     */
    @GetMapping("/historial")
    public String redirigirHistorial() {
        return "redirect:/partidos/historial";
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
            // Primero verificar si el jugador existe
            Jugador jugador = jugadorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
            
            // Verificar si hay equipos temporales activos que contengan este jugador
            int equiposTemporalesActivos = formacionEquiposService.contarEquiposTemporalesConJugador(id);
            
            // Verificar si hay partidos permanentes que contengan este jugador
            int partidosPermanentes = formacionEquiposService.contarPartidosConJugador(id);
            
            // IMPORTANTE: SIEMPRE limpiar referencias en tablas de relación, 
            // sin importar si hay equipos temporales activos o no
            formacionEquiposService.limpiarReferenciasEnTablasRelacion(id);
            
            // Si hay partidos permanentes, eliminarlos también
            if (partidosPermanentes > 0) {
                int partidosEliminados = formacionEquiposService.eliminarPartidosConJugador(id);
                System.out.println("🗑️ Se eliminaron " + partidosEliminados + " partidos del historial");
            }
            
            if (equiposTemporalesActivos > 0) {
                // Limpiar equipos temporales que contengan este jugador
                formacionEquiposService.limpiarEquiposTemporalesConJugador(id);
                
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Jugador " + jugador.getNombre() + " eliminado exitosamente. " +
                    "Se desactivaron " + equiposTemporalesActivos + " equipo(s) temporal(es) en proceso. " +
                    "Se eliminaron " + partidosPermanentes + " partido(s) del historial.");
                redirectAttributes.addFlashAttribute("tipo", "warning");
            } else if (partidosPermanentes > 0) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Jugador " + jugador.getNombre() + " eliminado exitosamente. " +
                    "Se eliminaron " + partidosPermanentes + " partido(s) del historial.");
                redirectAttributes.addFlashAttribute("tipo", "warning");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Jugador " + jugador.getNombre() + " eliminado exitosamente");
                redirectAttributes.addFlashAttribute("tipo", "success");
            }
            
            // Ahora eliminar el jugador
            jugadorRepository.deleteById(id);
            
        } catch (Exception e) {
            System.err.println("Error al eliminar jugador ID " + id + ": " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("=== DEBUG: Guardando equipos temporales ===");
            System.out.println("Request completo: " + request);
            
            String sessionId = (String) request.get("sessionId");
            String fechaPartidoStr = (String) request.get("fechaPartido");
            String horaPartido = (String) request.get("horaPartido");
            String lugarPartido = (String) request.get("lugarPartido");
            Object precioPartidoObj = request.get("precioPartido");
            String observacionesPartido = (String) request.get("observacionesPartido");
            
            // Si las observaciones están vacías o solo contienen un guión, usar valor por defecto
            if (observacionesPartido == null || observacionesPartido.trim().isEmpty() || observacionesPartido.trim().equals("-")) {
                observacionesPartido = "Sin observaciones especiales";
                System.out.println("⚠️ Observaciones vacías, usando valor por defecto: " + observacionesPartido);
            }
            
            System.out.println("Session ID: " + sessionId);
            System.out.println("Fecha Partido: " + fechaPartidoStr);
            System.out.println("Hora Partido: " + horaPartido);
            System.out.println("Lugar Partido: " + lugarPartido);
            System.out.println("Precio Partido (raw): " + precioPartidoObj + " (tipo: " + (precioPartidoObj != null ? precioPartidoObj.getClass().getSimpleName() : "null") + ")");
            System.out.println("Observaciones: " + observacionesPartido);
            
            // Validar que la fecha esté presente
            if (fechaPartidoStr == null || fechaPartidoStr.trim().isEmpty()) {
                throw new RuntimeException("La fecha del partido es obligatoria");
            }
            
            // Convertir string a LocalDate
            LocalDate fechaPartido = LocalDate.parse(fechaPartidoStr);
            
            // Validar que la fecha no sea del pasado (permitir hoy y futuras)
            LocalDate hoy = LocalDate.now();
            System.out.println("=== DEBUG FECHA ===");
            System.out.println("Fecha seleccionada: " + fechaPartido);
            System.out.println("Fecha actual: " + hoy);
            System.out.println("Es antes de hoy: " + fechaPartido.isBefore(hoy));
            
            if (fechaPartido.isBefore(hoy)) {
                throw new RuntimeException("No se puede seleccionar una fecha del pasado para el partido. Puedes crear partidos para hoy o fechas futuras.");
            }
            
            // Convertir precio a Double con mejor manejo de errores
            Double precioPartido = null;
            try {
                if (precioPartidoObj != null) {
                    if (precioPartidoObj instanceof String) {
                        String precioStr = ((String) precioPartidoObj).trim();
                        if (!precioStr.isEmpty()) {
                            precioPartido = Double.parseDouble(precioStr);
                        }
                    } else if (precioPartidoObj instanceof Number) {
                        precioPartido = ((Number) precioPartidoObj).doubleValue();
                    }
                }
                
                // Si no se pudo convertir, usar valor por defecto
                if (precioPartido == null) {
                    precioPartido = 5600.0; // Valor por defecto
                    System.out.println("⚠️ Precio no válido, usando valor por defecto: " + precioPartido);
                }
            } catch (NumberFormatException e) {
                precioPartido = 5600.0; // Valor por defecto en caso de error
                System.out.println("⚠️ Error al convertir precio, usando valor por defecto: " + precioPartido);
            }
            
            System.out.println("=== Llamando al servicio ===");
            System.out.println("Precio final: " + precioPartido);
            
            Partido partido = formacionEquiposService.guardarEquiposTemporales(
                sessionId, fechaPartido, horaPartido, lugarPartido, precioPartido, observacionesPartido);
            
            System.out.println("=== Partido guardado exitosamente ===");
            System.out.println("ID del partido: " + partido.getId());
            
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
    
    /**
     * Eliminar partido del historial
     */
    @PostMapping("/partidos/eliminar/{id}")
    @ResponseBody
    public Map<String, Object> eliminarPartido(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            formacionEquiposService.eliminarPartido(id);
            
            response.put("success", true);
            response.put("mensaje", "Partido eliminado exitosamente");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al eliminar partido: " + e.getMessage());
        }
        
        return response;
    }
}
