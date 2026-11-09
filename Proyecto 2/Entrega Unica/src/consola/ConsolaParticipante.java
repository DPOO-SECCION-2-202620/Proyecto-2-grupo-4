package consola;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import excepciones.ReglaNegocioException;
import modelo.academias.Instalacion;
import modelo.academias.Reserva;
import modelo.conjunto.Equipo;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.torneos.BonoDescuento;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;
import modelo.ventas.Articulo;
import modelo.ventas.ProductoCafeteria;
import modelo.ventas.Venta;
import modelo.ventas.VentaCafeteria;
import modelo.ventas.VentaTienda;

/**
 * Aplicación de los PARTICIPANTES: socios (s001 / 1234), estudiantes y externos (jospina / 1234, rvelez / 1234).
 *
 * Permite crear la cuenta de un externo o estudiante, manejar los torneos (HU-TOR-SOC-01, SOC-02,
 * SOC-03 y SOC-05), comprar usando los bonos (HU-TOR-SOC-04) y, solo para socios, reservar
 * instalaciones (HU-SOC-01) e inscribirse en equipos (HU-SOC-02).
 */
public class ConsolaParticipante extends ConsolaBase {

    public static void main(String[] args) {
        ConsolaParticipante consola = new ConsolaParticipante();
        consola.ejecutar();
    }

    @Override
    protected String getNombreAplicacion() {
        return "Club Deportivo Séneca - Socios y participantes";
    }

    @Override
    protected boolean aceptaUsuario(Usuario u) {
        return u instanceof Socio || u instanceof ParticipanteExterno;
    }

    @Override
    protected String getMensajeOtroRol() {
        return "Use la aplicación de su rol (administración o entrenadores y personal).";
    }

    /** Dice si el usuario que inició sesión es socio (los externos tienen menos opciones). */
    private boolean esSocio() {
        return usuario instanceof Socio;
    }

    /** Equipos del socio que puede inscribir o retirar de un torneo. */
    @Override
    protected List<Equipo> equiposQueGestiono() {
        if (!esSocio()) {
            return super.equiposQueGestiono();
        }
        return club.getEquiposDeSocio((Socio) usuario);
    }

    /** Antes de iniciar sesión, un externo o estudiante puede crear su cuenta. */
    @Override
    protected void antesDeIniciarSesion() {
        if (lector.leerSiNo("¿Es externo o estudiante y quiere crear una cuenta nueva?")) {
            try {
                registrarse();
            } catch (ReglaNegocioException e) {
                System.out.println("  [RECHAZADO] " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------
    //  Menús
    // ---------------------------------------------------------------

    @Override
    protected Menu crearMenuPrincipal() {
        Menu menu = new Menu("Menú de " + usuario.getNombre(), "Guardar y salir");
        menu.agregarOpcion("MENU_TORNEOS", "Torneos internos");
        menu.agregarOpcion("MENU_COMPRAS", "Tienda, cafetería y bonos");
        if (esSocio()) {
            menu.agregarOpcion("RESERVAR", "Reservar una instalación (HU-SOC-01)");
            menu.agregarOpcion("MIS_RESERVAS", "Ver mis reservas");
            menu.agregarOpcion("INSCRIBIRME_EQUIPO", "Inscribirme en un equipo de conjunto (HU-SOC-02)");
            menu.agregarOpcion("MIS_PUNTOS", "Ver mis puntos de fidelidad");
        } else {
            menu.agregarOpcion("MI_PERFIL", "Ver mi perfil");
            menu.agregarOpcion("DECLARAR_NIVEL", "Declarar mi nivel en una disciplina");
        }
        return menu;
    }

    private Menu crearMenuTorneos() {
        Menu menu = new Menu("Torneos internos", "Volver");
        menu.agregarOpcion(TORNEO_CONSULTAR, "Consultar torneos y su estado (HU-TOR-SOC-05)");
        menu.agregarOpcion(TORNEO_HISTORIAL, "Ver historial de un torneo (HU-TOR-SOC-05)");
        menu.agregarOpcion(TORNEO_INSCRIBIRME, "Inscribirme en un torneo individual (HU-TOR-SOC-01)");
        menu.agregarOpcion(TORNEO_SALIRME, "Salirme de un torneo individual (HU-TOR-SOC-02)");
        menu.agregarOpcion(TORNEO_MIS_TORNEOS, "Ver mis torneos");
        if (esSocio()) {
            menu.agregarOpcion(TORNEO_INSCRIBIR_EQUIPO, "Inscribir a mi equipo en un torneo de conjunto (HU-TOR-SOC-01)");
            menu.agregarOpcion(TORNEO_RETIRAR_EQUIPO, "Retirar a mi equipo de un torneo (HU-TOR-SOC-02)");
        }
        return menu;
    }

    private Menu crearMenuCompras() {
        Menu menu = new Menu("Tienda y cafetería", "Volver");
        menu.agregarOpcion("MIS_BONOS", "Ver mis bonos de torneos (HU-TOR-SOC-04)");
        menu.agregarOpcion("COMPRAR_TIENDA", "Comprar en la tienda");
        menu.agregarOpcion("COMPRAR_CAFETERIA", "Comprar en la cafetería");
        return menu;
    }

    @Override
    protected void atenderOpcion(String codigo) throws ReglaNegocioException {
        switch (codigo) {
            case "MENU_TORNEOS": ejecutarMenu(crearMenuTorneos()); break;
            case "MENU_COMPRAS": ejecutarMenu(crearMenuCompras()); break;

            // Solo socios
            case "RESERVAR": reservar(); break;
            case "MIS_RESERVAS": mostrarLista("Mis reservas", club.getReservasDe((Socio) usuario)); break;
            case "INSCRIBIRME_EQUIPO": inscribirmeEnEquipo(); break;
            case "MIS_PUNTOS": System.out.printf("  Tiene %,.0f puntos.%n", usuario.getPuntosFidelidad()); break;

            // Solo externos y estudiantes
            case "MI_PERFIL": System.out.println("  " + ((ParticipanteExterno) usuario).getPerfil()); break;
            case "DECLARAR_NIVEL": declararNivel(); break;

            // Compras
            case "MIS_BONOS": mostrarLista("Mis bonos", usuario.getBonos()); break;
            case "COMPRAR_TIENDA": comprarEnTienda(); break;
            case "COMPRAR_CAFETERIA": comprarEnCafeteria(); break;

            default: System.out.println("  Opción no reconocida.");
        }
    }

    // ---------------------------------------------------------------
    //  Cuenta de externos y estudiantes
    // ---------------------------------------------------------------

    private void registrarse() throws ReglaNegocioException {
        String nombre = lector.leerTexto("Nombre completo");
        String documento = lector.leerTexto("Documento de identidad");
        String nombreUsuario = lector.leerNombreUsuario("Usuario que quiere usar (sin espacios)");
        String clave = leerClaveDosVeces();
        boolean esEstudiante = lector.leerSiNo("¿Es estudiante de la universidad?");

        Map<Disciplina, Nivel> niveles = new EnumMap<>(Disciplina.class);
        while (lector.leerSiNo("¿Quiere declarar su nivel en una disciplina individual?")) {
            Disciplina disciplina = lector.elegirObligatorio("Disciplina", Disciplina.values());
            Nivel nivel = lector.elegirObligatorio("Nivel", Nivel.values());
            niveles.put(disciplina, nivel);
        }
        ParticipanteExterno nuevo = club.registrarExterno(nombre, documento, nombreUsuario, clave, esEstudiante, niveles);
        System.out.println("  [HECHO] Cuenta creada: " + nuevo.getPerfil() + ". Ya puede iniciar sesión.");
    }

    /** Pide la contraseña dos veces hasta que las dos sean iguales. */
    private String leerClaveDosVeces() {
        while (true) {
            String clave = lector.leerClave("Contraseña");
            String repetida = lector.leerClave("Repita la contraseña");
            if (clave.equals(repetida)) {
                return clave;
            }
            System.out.println("  Las contraseñas no coinciden. Intente otra vez.");
        }
    }

    private void declararNivel() throws ReglaNegocioException {
        Disciplina disciplina = lector.elegirObligatorio("Disciplina", Disciplina.values());
        Nivel nivel = lector.elegirObligatorio("Nivel", Nivel.values());
        club.declararNivel(usuario, disciplina, nivel);
        System.out.println("  [HECHO] " + ((ParticipanteExterno) usuario).getPerfil());
    }

    // ---------------------------------------------------------------
    //  Compras con bonos (HU-TOR-SOC-04)
    // ---------------------------------------------------------------

    private void comprarEnTienda() throws ReglaNegocioException {
        VentaTienda venta = club.iniciarVentaTienda(usuario, hoy());
        boolean seguir = true;
        while (seguir) {
            Articulo articulo = lector.elegir("Artículo", club.getArticulos());
            if (articulo == null) {
                seguir = false;
            } else {
                int cantidad = lector.leerEntero("Cantidad", 1, 1000);
                try {
                    venta.agregarItem(articulo, cantidad);
                } catch (ReglaNegocioException e) {
                    System.out.println("  [RECHAZADO] " + e.getMessage());
                }
                seguir = lector.leerSiNo("¿Agregar otro artículo?");
            }
        }
        if (venta.getItems().isEmpty()) {
            System.out.println("  Compra cancelada.");
            return;
        }
        if (lector.leerSiNo("¿Tiene un código de descuento de empleado?")) {
            String codigo = lector.leerTexto("Código");
            try {
                venta.aplicarDescuento(club.buscarCodigo(codigo));
            } catch (ReglaNegocioException e) {
                System.out.println("  [RECHAZADO] " + e.getMessage());
            }
        }
        terminarCompra(venta);
    }

    private void comprarEnCafeteria() throws ReglaNegocioException {
        VentaCafeteria venta = club.iniciarVentaCafeteria(usuario, hoy());
        boolean seguir = true;
        while (seguir) {
            ProductoCafeteria producto = lector.elegir("Producto", club.getProductosCafeteria());
            if (producto == null) {
                seguir = false;
            } else {
                int cantidad = lector.leerEntero("Cantidad", 1, 100);
                try {
                    venta.agregarItem(producto, cantidad);
                } catch (ReglaNegocioException e) {
                    System.out.println("  [RECHAZADO] " + e.getMessage());
                }
                seguir = lector.leerSiNo("¿Agregar otro producto?");
            }
        }
        if (venta.getItems().isEmpty()) {
            System.out.println("  Compra cancelada.");
            return;
        }
        System.out.print(venta.informarAlergenos());
        if (lector.leerSiNo("¿Quiere cambiar la propina sugerida (10 %)?")) {
            double propina = lector.leerDecimal("Propina", 0, 10000000);
            try {
                venta.ajustarPropina(propina);
            } catch (ReglaNegocioException e) {
                System.out.println("  [RECHAZADO] " + e.getMessage());
            }
        }
        terminarCompra(venta);
    }

    /** Pasos finales de cualquier compra: bono, puntos, resumen y confirmación. */
    private void terminarCompra(Venta venta) throws ReglaNegocioException {
        List<BonoDescuento> bonos = usuario.getBonosDisponibles();
        if (!bonos.isEmpty() && lector.leerSiNo("Tiene " + bonos.size() + " bono(s) de torneo. ¿Quiere usar uno?")) {
            BonoDescuento bono = lector.elegir("Bono", bonos);
            if (bono != null) {
                try {
                    club.aplicarBono(venta, bono.getCodigo());
                } catch (ReglaNegocioException e) {
                    System.out.println("  [RECHAZADO] " + e.getMessage());
                }
            }
        }
        double puntos = usuario.getPuntosFidelidad();
        if (puntos > 0 && lector.leerSiNo(String.format("Tiene %,.0f puntos. ¿Quiere usarlos?", puntos))) {
            try {
                venta.usarPuntosFidelidad();
            } catch (ReglaNegocioException e) {
                System.out.println("  [RECHAZADO] " + e.getMessage());
            }
        }
        venta.calcularTotal();
        System.out.println("  " + venta.generarDetalle());
        if (lector.leerSiNo("¿Confirmar la compra?")) {
            club.confirmarVenta(venta);
            System.out.println("  [HECHO] Compra registrada.");
        } else {
            System.out.println("  Compra cancelada.");
        }
    }

    // ---------------------------------------------------------------
    //  Solo socios
    // ---------------------------------------------------------------

    /** HU-SOC-01. El modelo revisa la disponibilidad, los cupos y que la fecha no haya pasado. */
    private void reservar() throws ReglaNegocioException {
        Instalacion instalacion = lector.elegir("Instalación", club.getInstalaciones());
        if (instalacion == null) {
            return;
        }
        LocalDateTime fechaHora = lector.leerFechaHora("Inicio del bloque de 60 minutos");
        String modalidad = Instalacion.SENCILLOS;
        if (lector.leerSiNo("¿Modalidad dobles? (n = sencillos)")) {
            modalidad = Instalacion.DOBLES;
        }
        int jugadores = lector.leerEntero("Número de jugadores", 1, 4);
        Reserva reserva = club.reservar(usuario, instalacion.getId(), fechaHora, jugadores, modalidad, hoy());
        System.out.println("  [HECHO] " + reserva);
    }

    /** HU-SOC-02. */
    private void inscribirmeEnEquipo() throws ReglaNegocioException {
        Equipo equipo = lector.elegir("Equipo", club.getEquipos());
        if (equipo == null) {
            return;
        }
        String posicion = lector.leerTexto("Posición");
        int camiseta = lector.leerEntero("Número de camiseta", 1, 99);
        club.inscribirJugadorEnEquipo((Socio) usuario, equipo.getNombre(), posicion, camiseta, hoy());
        int libres = equipo.getCupoMaximo() - equipo.contarActivos();
        System.out.println("  [HECHO] Quedó inscrito en " + equipo.getNombre() + " con la camiseta " + camiseta
                + " como " + posicion + ". Cupos libres: " + libres + ".");
    }
}
