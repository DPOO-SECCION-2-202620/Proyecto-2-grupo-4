package consola;

import java.time.LocalTime;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.conjunto.Equipo;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;
import modelo.ventas.Sugerencia;

/**
 * Aplicación de los EMPLEADOS: entrenadores (cmendez / ent123) y fisioterapeutas (prios / fis123).
 *
 * Permite inscribirse en torneos individuales (HU-TOR-EMP-01), inscribir el equipo en torneos
 * de conjunto (solo entrenadores), revisar el mínimo de jugadores habilitados (HU-ENT-02),
 * pedir cambios de turno y sugerir productos.
 */
public class ConsolaEntrenador extends ConsolaBase {

    public static void main(String[] args) {
        ConsolaEntrenador consola = new ConsolaEntrenador();
        consola.ejecutar();
    }

    @Override
    protected String getNombreAplicacion() {
        return "Club Deportivo Séneca - Entrenadores y personal";
    }

    @Override
    protected boolean aceptaUsuario(Usuario u) {
        return u instanceof Empleado;
    }

    @Override
    protected String getMensajeOtroRol() {
        return "Esta aplicación es para entrenadores y fisioterapeutas.";
    }

    /** El usuario que inició sesión, visto como empleado. */
    private Empleado empleado() {
        return (Empleado) usuario;
    }

    private boolean esEntrenador() {
        return usuario instanceof Entrenador;
    }

    /** Equipos que dirige el entrenador (los fisioterapeutas no dirigen equipos). */
    @Override
    protected List<Equipo> equiposQueGestiono() {
        if (!esEntrenador()) {
            return super.equiposQueGestiono();
        }
        return club.getEquiposDe((Entrenador) usuario);
    }

    // ---------------------------------------------------------------
    //  Menús
    // ---------------------------------------------------------------

    @Override
    protected Menu crearMenuPrincipal() {
        Menu menu = new Menu("Menú de " + usuario.getNombre(), "Guardar y salir");
        menu.agregarOpcion("MENU_TORNEOS", "Torneos internos");
        menu.agregarOpcion("MENU_TURNOS", "Mis turnos");
        menu.agregarOpcion("MENU_SUGERENCIAS", "Sugerencias de productos");
        if (esEntrenador()) {
            menu.agregarOpcion("VERIFICAR_MINIMO", "Revisar el mínimo de jugadores habilitados (HU-ENT-02)");
            menu.agregarOpcion("MIS_EQUIPOS", "Ver mis equipos");
        }
        return menu;
    }

    private Menu crearMenuTorneos() {
        Menu menu = new Menu("Torneos internos", "Volver");
        menu.agregarOpcion(TORNEO_CONSULTAR, "Consultar torneos y su estado");
        menu.agregarOpcion(TORNEO_HISTORIAL, "Ver historial de un torneo");
        menu.agregarOpcion(TORNEO_INSCRIBIRME, "Inscribirme en un torneo individual (HU-TOR-EMP-01)");
        menu.agregarOpcion(TORNEO_SALIRME, "Salirme de un torneo individual");
        menu.agregarOpcion(TORNEO_MIS_TORNEOS, "Ver mis torneos");
        menu.agregarOpcion("DECLARAR_NIVEL", "Declarar mi nivel en una disciplina");
        if (esEntrenador()) {
            menu.agregarOpcion(TORNEO_INSCRIBIR_EQUIPO, "Inscribir a mi equipo en un torneo de conjunto");
            menu.agregarOpcion(TORNEO_RETIRAR_EQUIPO, "Retirar a mi equipo de un torneo");
        }
        return menu;
    }

    private Menu crearMenuTurnos() {
        Menu menu = new Menu("Mis turnos", "Volver");
        menu.agregarOpcion("VER_TURNOS", "Ver mis turnos");
        menu.agregarOpcion("PEDIR_CAMBIO", "Pedir un cambio de turno");
        menu.agregarOpcion("VER_SOLICITUDES", "Ver mis solicitudes");
        return menu;
    }

    private Menu crearMenuSugerencias() {
        Menu menu = new Menu("Sugerencias de productos", "Volver");
        menu.agregarOpcion("SUGERIR", "Sugerir un artículo o un snack");
        menu.agregarOpcion("VER_SUGERENCIAS", "Ver el estado de mis sugerencias");
        return menu;
    }

    @Override
    protected void atenderOpcion(String codigo) throws ReglaNegocioException {
        switch (codigo) {
            case "MENU_TORNEOS": ejecutarMenu(crearMenuTorneos()); break;
            case "MENU_TURNOS": ejecutarMenu(crearMenuTurnos()); break;
            case "MENU_SUGERENCIAS": ejecutarMenu(crearMenuSugerencias()); break;

            case "DECLARAR_NIVEL": declararNivel(); break;

            case "VER_TURNOS": mostrarLista("Mis turnos", empleado().getTurnos()); break;
            case "PEDIR_CAMBIO": pedirCambioTurno(); break;
            case "VER_SOLICITUDES": mostrarLista("Mis solicitudes", club.getSolicitudesDe(empleado())); break;

            case "SUGERIR": sugerirProducto(); break;
            case "VER_SUGERENCIAS": mostrarLista("Mis sugerencias", club.consultarSugerenciasDe(empleado())); break;

            // Solo entrenadores
            case "VERIFICAR_MINIMO": verificarMinimo(); break;
            case "MIS_EQUIPOS": verMisEquipos(); break;

            default: System.out.println("  Opción no reconocida.");
        }
    }

    // ---------------------------------------------------------------
    //  Torneos
    // ---------------------------------------------------------------

    private void declararNivel() throws ReglaNegocioException {
        Disciplina disciplina = lector.elegirObligatorio("Disciplina", Disciplina.values());
        Nivel nivel = lector.elegirObligatorio("Nivel", Nivel.values());
        club.declararNivel(usuario, disciplina, nivel);
        System.out.println("  [HECHO] Sus niveles: " + empleado().getNivelesDeclarados());
    }

    // ---------------------------------------------------------------
    //  Turnos
    // ---------------------------------------------------------------

    private void pedirCambioTurno() {
        Turno actual = lector.elegir("Turno que quiere cambiar", empleado().getTurnos());
        if (actual == null) {
            return;
        }
        String dia = lector.elegir("Nuevo día", club.getDiasAtencion());
        if (dia == null) {
            return;
        }
        // El turno nuevo tiene que quedar dentro del horario de atención del club.
        LocalTime apertura = club.getHoraApertura();
        LocalTime cierre = club.getHoraCierre();
        LocalTime inicio = lector.leerHora("Hora de inicio", apertura, cierre.minusHours(1));
        LocalTime fin = lector.leerHora("Hora de fin", inicio.plusHours(1), cierre);
        Turno propuesto = new Turno(dia, inicio, fin);
        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(empleado(), actual, propuesto);
        System.out.println("  [HECHO] " + solicitud + ". El administrador la va a revisar.");
    }

    // ---------------------------------------------------------------
    //  Sugerencias
    // ---------------------------------------------------------------

    private void sugerirProducto() {
        boolean esSnack = lector.leerSiNo("¿Es un snack de la cafetería? (n = artículo de la tienda)");
        String nombre = lector.leerTexto("Nombre del producto");
        String detalle;
        String tipo;
        if (esSnack) {
            detalle = lector.leerTextoOpcional("Alérgenos separados por coma (escriba NINGUNO si no tiene)");
            tipo = Sugerencia.SNACK;
        } else {
            detalle = lector.leerTexto("Categoría del artículo");
            tipo = Sugerencia.ARTICULO;
        }
        double precio = lector.leerDecimal("Precio sugerido", 1, 10000000);
        Sugerencia sugerencia = club.sugerirProducto(empleado(), tipo, nombre, detalle, precio, hoy());
        System.out.println("  [HECHO] " + sugerencia);
    }

    // ---------------------------------------------------------------
    //  Equipos (solo entrenadores)
    // ---------------------------------------------------------------

    /** HU-ENT-02. Dice si el equipo tiene el mínimo de habilitados; si no, muestra quién falta y por qué. */
    private void verificarMinimo() throws ReglaNegocioException {
        List<Equipo> misEquipos = equiposQueGestiono();
        if (misEquipos.isEmpty()) {
            System.out.println("  No dirige ningún equipo.");
            return;
        }
        Equipo equipo = lector.elegir("Equipo", misEquipos);
        if (equipo == null) {
            return;
        }
        mostrarTexto(club.verificarMinimoHabilitados((Entrenador) usuario, equipo.getNombre()));
    }

    private void verMisEquipos() {
        List<Equipo> misEquipos = equiposQueGestiono();
        if (misEquipos.isEmpty()) {
            System.out.println("  No dirige ningún equipo.");
            return;
        }
        for (Equipo equipo : misEquipos) {
            List<Socio> habilitados = equipo.getJugadoresHabilitados();
            System.out.println();
            System.out.println("  " + equipo.getNombre() + " (" + equipo.getDeporte() + " " + equipo.getCategoria() + ") - "
                    + habilitados.size() + " habilitados de " + equipo.contarActivos() + " activos");
            String nombres = "";
            for (int i = 0; i < habilitados.size(); i++) {
                if (i > 0) {
                    nombres = nombres + ", ";
                }
                nombres = nombres + habilitados.get(i).getNombre();
            }
            System.out.println("    Jugadores habilitados: " + nombres);
        }
    }
}
