package consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import logica.ClubDeportivo;
import modelo.conjunto.Equipo;
import modelo.usuarios.Usuario;

/**
 * Clase padre de las tres consolas del club.
 *
 * El método {@link #ejecutar()} define los pasos que siguen todas las aplicaciones y es final,
 * para que ninguna consola se pueda saltar el inicio de sesión ni el guardado:
 * <ol>
 *   <li>cargar los datos de la carpeta "datos";</li>
 *   <li>pedir usuario y contraseña (máximo 3 intentos) y revisar que el tipo de usuario pueda usar
 *       esta aplicación;</li>
 *   <li>mostrar el menú del rol;</li>
 *   <li>guardar los cambios al terminar, pase lo que pase en el paso 3.</li>
 * </ol>
 * Cada consola hija solo dice qué usuarios acepta, qué opciones tiene y qué hace cada opción.
 * Las reglas del negocio están en la fachada {@link ClubDeportivo}: las consolas solo leen datos,
 * llaman a la fachada y muestran el resultado.
 */
public abstract class ConsolaBase {

    /** Intentos permitidos para iniciar sesión. */
    public static final int INTENTOS_PERMITIDOS = 3;

    // Códigos de las opciones de torneos que comparten varias consolas.
    protected static final String TORNEO_CONSULTAR = "TORNEO_CONSULTAR";
    protected static final String TORNEO_HISTORIAL = "TORNEO_HISTORIAL";
    protected static final String TORNEO_MIS_TORNEOS = "TORNEO_MIS_TORNEOS";
    protected static final String TORNEO_INSCRIBIRME = "TORNEO_INSCRIBIRME";
    protected static final String TORNEO_SALIRME = "TORNEO_SALIRME";
    protected static final String TORNEO_INSCRIBIR_EQUIPO = "TORNEO_INSCRIBIR_EQUIPO";
    protected static final String TORNEO_RETIRAR_EQUIPO = "TORNEO_RETIRAR_EQUIPO";

    protected LectorEntrada lector;
    protected ClubDeportivo club;
    protected Usuario usuario;          // Usuario que inició sesión.
    protected FlujosTorneo torneos;     // Pasos de torneos que comparten las consolas.
    private GestorDatos datos;

    public ConsolaBase() {
        lector = new LectorEntrada();
        datos = new GestorDatos();
    }

    // ---------------------------------------------------------------
    //  Lo que cada consola debe definir
    // ---------------------------------------------------------------

    /** Nombre que se muestra al abrir la aplicación. */
    protected abstract String getNombreAplicacion();

    /** Dice si este tipo de usuario puede usar esta consola. */
    protected abstract boolean aceptaUsuario(Usuario u);

    /** Mensaje para un usuario que abrió la consola equivocada. */
    protected abstract String getMensajeOtroRol();

    /** Crea el menú principal del usuario que inició sesión. */
    protected abstract Menu crearMenuPrincipal();

    /** Hace lo que corresponde a la opción elegida en cualquiera de los menús de la consola. */
    protected abstract void atenderOpcion(String codigo) throws ReglaNegocioException;

    /** Algo que se puede hacer antes de iniciar sesión (por defecto nada). */
    protected void antesDeIniciarSesion() {
    }

    /** Equipos que el usuario puede inscribir o retirar de un torneo (por defecto ninguno). */
    protected List<Equipo> equiposQueGestiono() {
        return new ArrayList<>();
    }

    // ---------------------------------------------------------------
    //  Pasos comunes
    // ---------------------------------------------------------------

    /** Corre la aplicación completa. */
    public final void ejecutar() {
        mostrarTitulo(getNombreAplicacion());
        try {
            club = datos.cargar();
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudieron cargar los datos: " + e.getMessage());
            System.out.println("La aplicación se cierra sin cambiar ningún archivo.");
            return;
        }
        torneos = new FlujosTorneo(club, lector);

        try {
            antesDeIniciarSesion();
            usuario = iniciarSesion();
            if (usuario != null) {
                System.out.println();
                System.out.println("Bienvenido(a), " + usuario.getNombre() + " (" + usuario.getTipo() + ").");
                ejecutarMenu(crearMenuPrincipal());
            }
        } catch (EntradaTerminadaException e) {
            System.out.println();
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            // Un error que no esperábamos no debe hacer perder lo que se hizo en la sesión.
            System.out.println();
            System.out.println("[ERROR inesperado] " + e.getMessage());
        } finally {
            if (datos.guardar()) {
                System.out.println("¡Hasta pronto!");
            }
        }
    }

    /**
     * Pide usuario y contraseña. Devuelve el usuario solo si los datos coinciden con un usuario
     * del club y si su tipo puede usar esta consola. Después de 3 intentos fallidos devuelve null.
     */
    private Usuario iniciarSesion() {
        for (int intento = 1; intento <= INTENTOS_PERMITIDOS; intento++) {
            System.out.println();
            System.out.println("--- Iniciar sesión (intento " + intento + " de " + INTENTOS_PERMITIDOS + ") ---");
            String nombreUsuario = lector.leerNombreUsuario("Usuario");
            String clave = lector.leerClave("Contraseña");
            try {
                Usuario encontrado = club.autenticar(nombreUsuario, clave);
                if (aceptaUsuario(encontrado)) {
                    return encontrado;
                }
                System.out.println("  Un usuario de tipo " + encontrado.getTipo() + " no puede usar esta aplicación. "
                        + getMensajeOtroRol());
            } catch (ReglaNegocioException e) {
                System.out.println("  " + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("Hizo demasiados intentos. La aplicación se va a cerrar.");
        return null;
    }

    /**
     * Muestra un menú hasta que el usuario elija 0. Aquí se manejan los errores de todas las opciones:
     * si el modelo rechaza algo, se muestra el motivo y se vuelve al menú sin cerrar el programa.
     */
    protected void ejecutarMenu(Menu menu) {
        String codigo = menu.pedirOpcion(lector);
        while (!codigo.equals(Menu.SALIR)) {
            try {
                boolean eraDeTorneos = atenderOpcionDeTorneos(codigo);
                if (!eraDeTorneos) {
                    atenderOpcion(codigo);
                }
            } catch (ReglaNegocioException e) {
                System.out.println("  [RECHAZADO] " + e.getMessage());
            } catch (EntradaTerminadaException e) {
                throw e; // No es un error de la opción: hay que cerrar la aplicación.
            } catch (RuntimeException e) {
                System.out.println("  [ERROR] " + e.getMessage());
            }
            codigo = menu.pedirOpcion(lector);
        }
    }

    /** Atiende las opciones de torneos que son iguales en varias consolas. Devuelve false si no era una de ellas. */
    private boolean atenderOpcionDeTorneos(String codigo) throws ReglaNegocioException {
        switch (codigo) {
            case TORNEO_CONSULTAR:
                torneos.consultarTorneos();
                return true;
            case TORNEO_HISTORIAL:
                torneos.verHistorial();
                return true;
            case TORNEO_MIS_TORNEOS:
                torneos.verMisTorneos(usuario);
                return true;
            case TORNEO_INSCRIBIRME:
                torneos.inscribirmeEnIndividual(usuario, hoy());
                return true;
            case TORNEO_SALIRME:
                torneos.salirmeDeIndividual(usuario, hoy());
                return true;
            case TORNEO_INSCRIBIR_EQUIPO:
                torneos.inscribirEquipo(usuario, equiposQueGestiono(), hoy());
                return true;
            case TORNEO_RETIRAR_EQUIPO:
                torneos.retirarEquipo(usuario, equiposQueGestiono(), hoy());
                return true;
            default:
                return false;
        }
    }

    // ---------------------------------------------------------------
    //  Ayudas para las consolas hijas
    // ---------------------------------------------------------------

    /**
     * Fecha de hoy. Se puede simular otra fecha con -Dclub.hoy=aaaa-mm-dd
     * (útil para probar, por ejemplo, el día en que se juega un torneo).
     */
    protected LocalDate hoy() {
        String fechaSimulada = System.getProperty("club.hoy");
        if (fechaSimulada != null) {
            try {
                return LocalDate.parse(fechaSimulada);
            } catch (RuntimeException e) {
                System.out.println("(La fecha de club.hoy está mal escrita, se usa la fecha real.)");
            }
        }
        return LocalDate.now();
    }

    /** Muestra un título enmarcado. */
    public static void mostrarTitulo(String titulo) {
        String linea = "";
        for (int i = 0; i < titulo.length() + 8; i++) {
            linea = linea + "=";
        }
        System.out.println();
        System.out.println(linea);
        System.out.println("    " + titulo);
        System.out.println(linea);
    }

    /** Muestra una lista con un encabezado, o "(vacío)" si no tiene elementos. */
    public static void mostrarLista(String encabezado, List<?> lista) {
        System.out.println();
        System.out.println("--- " + encabezado + " ---");
        if (lista.isEmpty()) {
            System.out.println("  (vacío)");
        }
        for (Object elemento : lista) {
            System.out.println("  " + elemento);
        }
    }

    /** Muestra un texto de varias líneas con una pequeña sangría. */
    public static void mostrarTexto(String texto) {
        System.out.println("  " + texto.replace("\n", "\n  "));
    }
}
