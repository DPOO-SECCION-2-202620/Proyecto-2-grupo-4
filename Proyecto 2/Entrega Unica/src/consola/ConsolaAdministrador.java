package consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.conjunto.Equipo;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.EstadoTorneo;
import modelo.enums.Nivel;
import modelo.enums.TipoTorneo;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Torneo;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Usuario;
import modelo.ventas.Articulo;
import modelo.ventas.Sugerencia;

/**
 * Aplicación del ADMINISTRADOR (usuario de prueba: admin / admin123).
 *
 * Permite manejar los torneos (HU-TOR-ADM-01, ADM-02 y ADM-03), los turnos del personal (HU-ADM-02),
 * las sugerencias de productos, el inventario, los equipos, los reportes y ver los usuarios.
 * Las acciones que no se pueden deshacer (iniciar y finalizar un torneo) piden confirmación.
 */
public class ConsolaAdministrador extends ConsolaBase {

    public static void main(String[] args) {
        ConsolaAdministrador consola = new ConsolaAdministrador();
        consola.ejecutar();
    }

    @Override
    protected String getNombreAplicacion() {
        return "Club Deportivo Séneca - Administración";
    }

    @Override
    protected boolean aceptaUsuario(Usuario u) {
        return u instanceof Administrador;
    }

    @Override
    protected String getMensajeOtroRol() {
        return "Esta aplicación es solo para el administrador.";
    }

    /** El usuario que inició sesión, visto como administrador. */
    private Administrador admin() {
        return (Administrador) usuario;
    }

    // ---------------------------------------------------------------
    //  Menús
    // ---------------------------------------------------------------

    @Override
    protected Menu crearMenuPrincipal() {
        Menu menu = new Menu("Menú del administrador", "Guardar y salir");
        menu.agregarOpcion("MENU_TORNEOS", "Torneos internos");
        menu.agregarOpcion("MENU_TURNOS", "Turnos del personal");
        menu.agregarOpcion("MENU_SUGERENCIAS", "Sugerencias de productos");
        menu.agregarOpcion("MENU_INVENTARIO", "Inventario y tienda");
        menu.agregarOpcion("MENU_EQUIPOS", "Equipos");
        menu.agregarOpcion("MENU_REPORTES", "Reportes");
        menu.agregarOpcion("VER_USUARIOS", "Ver usuarios registrados");
        return menu;
    }

    private Menu crearMenuTorneos() {
        Menu menu = new Menu("Torneos internos", "Volver");
        menu.agregarOpcion("CREAR_TORNEO_INDIVIDUAL", "Crear torneo de disciplina individual (HU-TOR-ADM-01)");
        menu.agregarOpcion("CREAR_TORNEO_CONJUNTO", "Crear torneo de deporte de conjunto (HU-TOR-ADM-01)");
        menu.agregarOpcion(TORNEO_CONSULTAR, "Consultar torneos y su estado (HU-TOR-ADM-03)");
        menu.agregarOpcion(TORNEO_HISTORIAL, "Ver historial de un torneo (HU-TOR-ADM-03)");
        menu.agregarOpcion("INICIAR_TORNEO", "Iniciar torneo (cerrar inscripciones)");
        menu.agregarOpcion("REGISTRAR_ENCUENTRO", "Registrar un encuentro jugado");
        menu.agregarOpcion("FINALIZAR_TORNEO", "Finalizar torneo y entregar premios (HU-TOR-ADM-02)");
        menu.agregarOpcion("CAMBIAR_PORCENTAJE", "Cambiar el % de lo recaudado para el premio en efectivo");
        return menu;
    }

    private Menu crearMenuTurnos() {
        Menu menu = new Menu("Turnos del personal", "Volver");
        menu.agregarOpcion("VER_SOLICITUDES", "Ver solicitudes de cambio pendientes");
        menu.agregarOpcion("APROBAR_SOLICITUD", "Aprobar una solicitud (HU-ADM-02)");
        menu.agregarOpcion("RECHAZAR_SOLICITUD", "Rechazar una solicitud");
        menu.agregarOpcion("VER_COBERTURA", "Revisar la cobertura actual");
        return menu;
    }

    private Menu crearMenuSugerencias() {
        Menu menu = new Menu("Sugerencias de productos", "Volver");
        menu.agregarOpcion("VER_SUGERENCIAS", "Ver sugerencias pendientes");
        menu.agregarOpcion("APROBAR_SUGERENCIA", "Aprobar una sugerencia");
        menu.agregarOpcion("RECHAZAR_SUGERENCIA", "Rechazar una sugerencia");
        return menu;
    }

    private Menu crearMenuInventario() {
        Menu menu = new Menu("Inventario y tienda", "Volver");
        menu.agregarOpcion("VER_INVENTARIO", "Ver el inventario completo");
        menu.agregarOpcion("MOVER_ARTICULO", "Mover un artículo entre bodega y tienda");
        menu.agregarOpcion("REABASTECER", "Reabastecer un artículo");
        return menu;
    }

    private Menu crearMenuEquipos() {
        Menu menu = new Menu("Equipos", "Volver");
        menu.agregarOpcion("VER_EQUIPOS", "Ver los equipos y su entrenador");
        menu.agregarOpcion("CREAR_EQUIPO", "Crear un equipo");
        menu.agregarOpcion("ASIGNAR_ENTRENADOR", "Asignar entrenador a un equipo");
        return menu;
    }

    private Menu crearMenuReportes() {
        Menu menu = new Menu("Reportes", "Volver");
        menu.agregarOpcion("REPORTE_VENTAS", "Reporte de ventas");
        menu.agregarOpcion("REPORTE_DEPORTIVO", "Reporte deportivo");
        menu.agregarOpcion("VER_HISTORIAL_CLUB", "Historial de reservas, partidos y sesiones");
        return menu;
    }

    @Override
    protected void atenderOpcion(String codigo) throws ReglaNegocioException {
        switch (codigo) {
            // Menú principal
            case "MENU_TORNEOS": ejecutarMenu(crearMenuTorneos()); break;
            case "MENU_TURNOS": ejecutarMenu(crearMenuTurnos()); break;
            case "MENU_SUGERENCIAS": ejecutarMenu(crearMenuSugerencias()); break;
            case "MENU_INVENTARIO": ejecutarMenu(crearMenuInventario()); break;
            case "MENU_EQUIPOS": ejecutarMenu(crearMenuEquipos()); break;
            case "MENU_REPORTES": ejecutarMenu(crearMenuReportes()); break;
            case "VER_USUARIOS": mostrarLista("Usuarios", club.getUsuarios()); break;

            // Torneos
            case "CREAR_TORNEO_INDIVIDUAL": crearTorneo(true); break;
            case "CREAR_TORNEO_CONJUNTO": crearTorneo(false); break;
            case "INICIAR_TORNEO": iniciarTorneo(); break;
            case "REGISTRAR_ENCUENTRO": registrarEncuentro(); break;
            case "FINALIZAR_TORNEO": finalizarTorneo(); break;
            case "CAMBIAR_PORCENTAJE": cambiarPorcentajePremio(); break;

            // Turnos
            case "VER_SOLICITUDES": mostrarLista("Solicitudes pendientes", club.getSolicitudesPendientes()); break;
            case "APROBAR_SOLICITUD": aprobarSolicitud(); break;
            case "RECHAZAR_SOLICITUD": rechazarSolicitud(); break;
            case "VER_COBERTURA": System.out.println("  " + club.verificarCobertura()); break;

            // Sugerencias
            case "VER_SUGERENCIAS": mostrarLista("Sugerencias pendientes", club.getSugerenciasPendientes()); break;
            case "APROBAR_SUGERENCIA": revisarSugerencia(true); break;
            case "RECHAZAR_SUGERENCIA": revisarSugerencia(false); break;

            // Inventario
            case "VER_INVENTARIO": System.out.println(club.generarEstadoInventario()); break;
            case "MOVER_ARTICULO": moverArticulo(); break;
            case "REABASTECER": reabastecer(); break;

            // Equipos
            case "VER_EQUIPOS": verEquipos(); break;
            case "CREAR_EQUIPO": crearEquipo(); break;
            case "ASIGNAR_ENTRENADOR": asignarEntrenador(); break;

            // Reportes
            case "REPORTE_VENTAS": reporteVentas(); break;
            case "REPORTE_DEPORTIVO": reporteDeportivo(); break;
            case "VER_HISTORIAL_CLUB": System.out.println(club.generarHistorial()); break;

            default: System.out.println("  Opción no reconocida.");
        }
    }

    // ---------------------------------------------------------------
    //  Torneos
    // ---------------------------------------------------------------

    /**
     * HU-TOR-ADM-01. La consola solo pide los datos. Las reglas (nombre único, cupos, tarifa, fechas,
     * pádel en dobles...) las revisa el modelo al crear el torneo.
     */
    private void crearTorneo(boolean esIndividual) throws ReglaNegocioException {
        String nombre = lector.leerTexto("Nombre del torneo");
        TipoTorneo tipo = lector.elegirObligatorio("Tipo de torneo", TipoTorneo.values());
        int cupos = lector.leerEntero("Número de cupos", 2, 128);

        double tarifa = 0;
        double bono = 0;
        if (tipo == TipoTorneo.COMPETITIVO) {
            tarifa = lector.leerDecimal("Tarifa de inscripción", 1, 10000000);
        } else {
            int porcentaje = lector.leerEntero("Descuento del bono para el ganador (%)", 1, 100);
            bono = porcentaje / 100.0;
        }

        LocalDate hoy = hoy();
        LocalDate fechaInicio = lector.leerFecha("Fecha en que se juega", hoy.plusDays(1), null);
        LocalDate fechaLimite = lector.leerFecha("Fecha límite de los cupos preferenciales", hoy, fechaInicio.minusDays(1));

        Torneo torneo;
        if (esIndividual) {
            Disciplina disciplina = lector.elegirObligatorio("Disciplina", Disciplina.values());
            Nivel nivel = lector.elegirObligatorio("Nivel", Nivel.values());
            boolean dobles = lector.leerSiNo("¿Es de dobles?");
            torneo = club.crearTorneoIndividual(admin(), nombre, tipo, disciplina, nivel, dobles, cupos, tarifa, bono,
                    fechaInicio, fechaLimite, hoy);
        } else {
            Deporte deporte = lector.elegirObligatorio("Deporte", Deporte.values());
            Categoria categoria = lector.elegirObligatorio("Categoría", Categoria.values());
            torneo = club.crearTorneoConjunto(admin(), nombre, tipo, deporte, categoria, cupos, tarifa, bono,
                    fechaInicio, fechaLimite, hoy);
        }
        int preferenciales = torneo.getCuposPreferenciales();
        System.out.println("  [HECHO] Torneo creado: " + torneo);
        System.out.println("  Cupos preferenciales: " + preferenciales + " | regulares: " + (torneo.getCupos() - preferenciales));
    }

    private void iniciarTorneo() throws ReglaNegocioException {
        Torneo torneo = torneos.elegirTorneo(EstadoTorneo.POR_INICIAR);
        if (torneo == null) {
            return;
        }
        System.out.println("  " + torneo.getNombre() + " tiene " + torneo.getInscripciones().size() + " inscripción(es).");
        if (!lector.confirmar("iniciar " + torneo.getNombre() + " y cerrar sus inscripciones")) {
            return;
        }
        club.iniciarTorneo(admin(), torneo.getNombre(), hoy());
        System.out.println("  [HECHO] " + torneo.getNombre() + " está en curso. Ya no se reciben inscripciones.");
    }

    private void registrarEncuentro() throws ReglaNegocioException {
        Torneo torneo = torneos.elegirTorneo(EstadoTorneo.EN_CURSO);
        if (torneo == null) {
            return;
        }
        InscripcionTorneo ladoA = lector.elegir("Participante A", torneo.getInscripciones());
        if (ladoA == null) {
            return;
        }
        // El participante B se elige entre los demás: nadie juega contra sí mismo.
        List<InscripcionTorneo> rivales = new ArrayList<>(torneo.getInscripciones());
        rivales.remove(ladoA);
        InscripcionTorneo ladoB = lector.elegir("Participante B", rivales);
        if (ladoB == null) {
            return;
        }
        int marcadorA = lector.leerEntero("Marcador de " + ladoA.getNombre(), 0, 999);
        int marcadorB = lector.leerEntero("Marcador de " + ladoB.getNombre(), 0, 999);
        String fase = lector.leerTextoOpcional("Fase (por ejemplo Semifinal)");
        EncuentroTorneo encuentro = club.registrarEncuentroTorneo(admin(), torneo.getNombre(), ladoA.getId(), ladoB.getId(),
                marcadorA, marcadorB, fase);
        System.out.println("  [HECHO] " + encuentro);
    }

    /** HU-TOR-ADM-02. El administrador dice las posiciones en orden y el sistema entrega los premios. */
    private void finalizarTorneo() throws ReglaNegocioException {
        Torneo torneo = torneos.elegirTorneo(EstadoTorneo.EN_CURSO);
        if (torneo == null) {
            return;
        }
        List<InscripcionTorneo> sinPuesto = new ArrayList<>(torneo.getInscripciones());
        List<InscripcionTorneo> posiciones = new ArrayList<>();
        boolean seguir = true;
        while (seguir && !sinPuesto.isEmpty()) {
            int puesto = posiciones.size() + 1;
            String titulo;
            if (puesto == 1) {
                titulo = "Puesto 1 (campeón)";
            } else {
                titulo = "Puesto " + puesto + " (0 para no registrar más puestos)";
            }
            InscripcionTorneo elegido = lector.elegir(titulo, sinPuesto);
            if (elegido == null) {
                seguir = false;
            } else {
                posiciones.add(elegido);
                sinPuesto.remove(elegido);
            }
        }
        if (posiciones.isEmpty()) {
            System.out.println("  No se finalizó el torneo: hay que indicar por lo menos el campeón.");
            return;
        }

        System.out.println("  Posiciones que se van a registrar:");
        for (int i = 0; i < posiciones.size(); i++) {
            System.out.println("    " + (i + 1) + ". " + posiciones.get(i).getNombre());
        }
        if (!lector.confirmar("finalizar " + torneo.getNombre() + " con estas posiciones (no se puede deshacer)")) {
            return;
        }
        List<Integer> idsEnOrden = new ArrayList<>();
        for (InscripcionTorneo inscripcion : posiciones) {
            idsEnOrden.add(inscripcion.getId());
        }
        club.finalizarTorneo(admin(), torneo.getNombre(), idsEnOrden);
        System.out.println("  [HECHO] Torneo finalizado.");
        System.out.println(torneo.generarHistorial());
    }

    private void cambiarPorcentajePremio() {
        double actual = club.getPorcentajePremioCompetitivo() * 100;
        System.out.printf("  Ahora el ganador de un torneo competitivo recibe el %.0f %% de lo recaudado.%n", actual);
        int nuevo = lector.leerEntero("Nuevo porcentaje", 1, 100);
        club.setPorcentajePremioCompetitivo(nuevo / 100.0);
        System.out.println("  [HECHO] Porcentaje actualizado. Se guarda en config.txt al salir.");
    }

    // ---------------------------------------------------------------
    //  Turnos
    // ---------------------------------------------------------------

    private void aprobarSolicitud() throws ReglaNegocioException {
        SolicitudCambioTurno solicitud = lector.elegir("Solicitud", club.getSolicitudesPendientes());
        if (solicitud == null) {
            return;
        }
        boolean aprobada = club.aprobarCambioTurno(admin(), solicitud);
        if (aprobada) {
            System.out.println("  [HECHO] Cambio de turno registrado: " + solicitud);
        } else {
            System.out.println("  [RECHAZADO] Si se aprueba quedarían menos de 2 entrenadores o menos de 1 fisioterapeuta. "
                    + solicitud);
        }
    }

    private void rechazarSolicitud() throws ReglaNegocioException {
        SolicitudCambioTurno solicitud = lector.elegir("Solicitud", club.getSolicitudesPendientes());
        if (solicitud == null) {
            return;
        }
        club.rechazarCambioTurno(admin(), solicitud.getId());
        System.out.println("  [HECHO] " + solicitud);
    }

    // ---------------------------------------------------------------
    //  Sugerencias
    // ---------------------------------------------------------------

    private void revisarSugerencia(boolean aprobar) throws ReglaNegocioException {
        Sugerencia sugerencia = lector.elegir("Sugerencia", club.getSugerenciasPendientes());
        if (sugerencia == null) {
            return;
        }
        if (aprobar) {
            club.aprobarSugerencia(admin(), sugerencia.getId());
        } else {
            club.rechazarSugerencia(admin(), sugerencia.getId());
        }
        System.out.println("  [HECHO] " + sugerencia);
    }

    // ---------------------------------------------------------------
    //  Inventario
    // ---------------------------------------------------------------

    private void moverArticulo() throws ReglaNegocioException {
        Articulo articulo = lector.elegir("Artículo", club.getArticulos());
        if (articulo == null) {
            return;
        }
        boolean haciaTienda = lector.leerSiNo("¿Mover de BODEGA a TIENDA? (n = de TIENDA a BODEGA)");
        int cantidad = lector.leerEntero("Cantidad", 1, 100000);
        String origen = Articulo.TIENDA;
        String destino = Articulo.BODEGA;
        if (haciaTienda) {
            origen = Articulo.BODEGA;
            destino = Articulo.TIENDA;
        }
        club.moverArticulo(admin(), articulo.getNombre(), origen, destino, cantidad);
        System.out.println("  [HECHO] " + articulo);
    }

    private void reabastecer() throws ReglaNegocioException {
        Articulo articulo = lector.elegir("Artículo", club.getArticulos());
        if (articulo == null) {
            return;
        }
        String ubicacion = Articulo.BODEGA;
        if (lector.leerSiNo("¿Reabastecer la TIENDA? (n = BODEGA)")) {
            ubicacion = Articulo.TIENDA;
        }
        int cantidad = lector.leerEntero("Cantidad", 1, 100000);
        club.reabastecer(admin(), articulo.getNombre(), ubicacion, cantidad);
        System.out.println("  [HECHO] " + articulo);
    }

    // ---------------------------------------------------------------
    //  Equipos
    // ---------------------------------------------------------------

    private void verEquipos() {
        System.out.println();
        System.out.println("--- Equipos ---");
        for (Equipo equipo : club.getEquipos()) {
            String entrenador = "(sin asignar)";
            if (equipo.getEntrenador() != null) {
                entrenador = equipo.getEntrenador().getNombre();
            }
            System.out.println("  " + equipo.getNombre() + " | " + equipo.getDeporte() + " " + equipo.getCategoria()
                    + " | " + equipo.contarActivos() + "/" + equipo.getCupoMaximo() + " jugadores | entrenador: " + entrenador);
        }
    }

    private void crearEquipo() throws ReglaNegocioException {
        String nombre = lector.leerTexto("Nombre del equipo");
        Deporte deporte = lector.elegirObligatorio("Deporte", Deporte.values());
        Categoria categoria = lector.elegirObligatorio("Categoría", Categoria.values());
        int minimo = deporte.getMinimoJugadores();
        int cupo = lector.leerEntero("Cupo máximo de jugadores", minimo, Math.max(40, minimo));
        club.crearEquipo(admin(), nombre, deporte, categoria, cupo);
        System.out.println("  [HECHO] Equipo creado. Asígnele un entrenador para que pueda inscribirse en torneos.");
    }

    private void asignarEntrenador() throws ReglaNegocioException {
        Equipo equipo = lector.elegir("Equipo", club.getEquipos());
        if (equipo == null) {
            return;
        }
        Entrenador entrenador = lector.elegir("Entrenador", club.getEntrenadores());
        if (entrenador == null) {
            return;
        }
        club.asignarEntrenador(admin(), equipo.getNombre(), entrenador);
        System.out.println("  [HECHO] " + entrenador.getNombre() + " ahora dirige a " + equipo.getNombre() + ".");
    }

    // ---------------------------------------------------------------
    //  Reportes
    // ---------------------------------------------------------------

    private void reporteVentas() {
        List<String> rubros = new ArrayList<>();
        rubros.add("TIENDA");
        rubros.add("CAFETERIA");
        rubros.add("MENSUALIDAD");
        rubros.add("TODOS");
        String rubro = lector.elegir("Rubro", rubros);
        if (rubro == null) {
            return;
        }
        String periodo = elegirPeriodo();
        if (periodo == null) {
            return;
        }
        LocalDate fecha = lector.leerFecha("Fecha de referencia del periodo");
        System.out.println(club.generarReporteVentas(admin(), rubro, periodo, fecha));
    }

    private void reporteDeportivo() {
        List<String> tipos = new ArrayList<>();
        tipos.add("ASISTENCIA");
        tipos.add("RESULTADOS");
        tipos.add("RANKINGS");
        String tipo = lector.elegir("Tipo de reporte", tipos);
        if (tipo == null) {
            return;
        }
        String periodo = elegirPeriodo();
        if (periodo == null) {
            return;
        }
        LocalDate fecha = lector.leerFecha("Fecha de referencia del periodo");
        System.out.println(club.generarReporteDeportivo(admin(), tipo, periodo, fecha));
    }

    /** Pide el periodo de un reporte. Devuelve null si el usuario cancela. */
    private String elegirPeriodo() {
        List<String> periodos = new ArrayList<>();
        periodos.add("DIARIO");
        periodos.add("SEMANAL");
        periodos.add("MENSUAL");
        return lector.elegir("Periodo", periodos);
    }
}
