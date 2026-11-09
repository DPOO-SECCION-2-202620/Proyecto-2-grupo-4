package consola;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import logica.ClubDeportivo;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.Instalacion;
import modelo.academias.MesaDeTenisMesa;
import modelo.academias.PartidoPractica;
import modelo.competencias.CompetenciaFases;
import modelo.competencias.CompetenciaLiga;
import modelo.competencias.Fase;
import modelo.competencias.Grupo;
import modelo.conjunto.EstadisticaBaloncesto;
import modelo.conjunto.EstadisticaFutbol;
import modelo.conjunto.PartidoCompetencia;
import modelo.conjunto.PartidoInterno;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.entrenamiento.Turno;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.EstadoAsistencia;
import modelo.enums.Nivel;
import modelo.enums.TipoBebida;
import modelo.enums.TipoFase;
import modelo.enums.TipoTorneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.ventas.Articulo;
import modelo.ventas.Bebida;
import modelo.ventas.Snack;
import modelo.ventas.Sugerencia;
import modelo.ventas.VentaCafeteria;
import modelo.ventas.VentaTienda;

/**
 * Crea un club con datos de prueba y lo guarda en la carpeta "datos".
 *
 * Los datos se crean con los mismos métodos que usa el sistema, así respetan las reglas del negocio.
 * Ejecute este programa si quiere volver a generar los archivos de prueba desde cero.
 */
public class GeneradorDatosPrueba {

    /** Fecha fija para que las edades y los resultados siempre den lo mismo. */
    public static final LocalDate FECHA_REF = LocalDate.of(2026, 10, 1);

    public static void main(String[] args) throws Exception {
        ClubDeportivo club = crearClub();
        String carpeta = new GestorDatos().getRuta();
        club.guardar(carpeta);
        System.out.println("Datos de prueba creados en la carpeta " + carpeta + ".");
        System.out.println("Usuarios: " + club.getUsuarios().size() + ", equipos: " + club.getEquipos().size()
                + ", competencias: " + club.getCompetencias().size() + ", ventas: " + club.getVentas().size());
    }

    /** Arma el club de prueba paso a paso. */
    public static ClubDeportivo crearClub() throws Exception {
        ClubDeportivo club = new ClubDeportivo();

        Administrador admin = new Administrador("A001", "Marta Rojas", "admin", "admin123");
        club.registrarUsuario(admin);
        crearSocios(club);

        Entrenador carlos = new Entrenador("E001", "Carlos Méndez", "cmendez", "ent123");
        carlos.setPerfilJugador(club.buscarSocio("S021"));
        Entrenador laura = new Entrenador("E002", "Laura Gómez", "lgomez", "ent123");
        Entrenador andres = new Entrenador("E003", "Andrés Pardo", "apardo", "ent123");
        Entrenador diana = new Entrenador("E004", "Diana Torres", "dtorres", "ent123");
        Entrenador felipe = new Entrenador("E005", "Felipe Castro", "fcastro", "ent123");
        Fisioterapeuta paula = new Fisioterapeuta("F001", "Paula Ríos", "prios", "fis123");
        Fisioterapeuta jorge = new Fisioterapeuta("F002", "Jorge Silva", "jsilva", "fis123");
        club.registrarUsuario(carlos);
        club.registrarUsuario(laura);
        club.registrarUsuario(andres);
        club.registrarUsuario(diana);
        club.registrarUsuario(felipe);
        club.registrarUsuario(paula);
        club.registrarUsuario(jorge);

        crearTurnos(club, carlos, laura, andres, diana, felipe, paula, jorge);
        crearEquipos(club, admin, carlos, laura, andres, diana, felipe);
        crearLigaDeFutbol(club, andres);
        crearCopaDeBaloncesto(club, diana);
        crearPartidosInternos(club, andres);
        crearInstalacionesYAcademias(club);
        crearSesiones(club, laura, andres, diana);
        crearTiendaYCafeteria(club, admin, laura);
        crearSugerenciasYSolicitudes(club, andres, felipe, paula);
        crearExternosYTorneos(club, felipe, paula);
        return club;
    }

    // ---------------------------------------------------------------
    //  Usuarios y turnos
    // ---------------------------------------------------------------

    /** Socios S001 a S018 (mayores), S019 y S020 (Sub-15) y S021 (perfil de jugador de Carlos). */
    private static void crearSocios(ClubDeportivo club) throws Exception {
        String[] nombres = { "Ana Pérez", "Luis Martínez", "Sofía Castro", "Mateo Gómez", "Valentina Ruiz", "Santiago López",
                "Camila Herrera", "Daniel Vargas", "Isabella Moreno", "Samuel Jiménez", "Mariana Torres", "Nicolás Ramírez",
                "Gabriela Díaz", "Tomás Álvarez", "Lucía Romero", "Martín Suárez", "Paula Navarro", "Diego Ortiz" };
        for (int i = 0; i < nombres.length; i++) {
            String id = String.format("S%03d", i + 1);                        // S001, S002, ...
            LocalDate nacimiento = LocalDate.of(2000 + (i % 6), 1 + (i % 12), 10); // Nacidos entre 2000 y 2005.
            club.registrarUsuario(new Socio(id, nombres[i], id.toLowerCase(), "1234", nacimiento));
        }
        club.registrarUsuario(new Socio("S019", "Emilio Rincón", "s019", "1234", LocalDate.of(2012, 3, 5)));
        club.registrarUsuario(new Socio("S020", "Sara Quintero", "s020", "1234", LocalDate.of(2012, 8, 20)));
        club.registrarUsuario(new Socio("S021", "Carlos Méndez", "carlos.jugador", "1234", LocalDate.of(1990, 5, 15)));
    }

    /** Siempre hay 2 entrenadores y 1 fisioterapeuta de 6:00 a 22:00. Felipe solo refuerza los lunes. */
    private static void crearTurnos(ClubDeportivo club, Entrenador carlos, Entrenador laura, Entrenador andres,
                                    Entrenador diana, Entrenador felipe, Fisioterapeuta paula, Fisioterapeuta jorge) {
        LocalTime seis = LocalTime.of(6, 0);
        LocalTime dos = LocalTime.of(14, 0);
        LocalTime diez = LocalTime.of(22, 0);
        for (String dia : club.getDiasAtencion()) {
            carlos.agregarTurno(new Turno(dia, seis, dos));
            laura.agregarTurno(new Turno(dia, seis, dos));
            andres.agregarTurno(new Turno(dia, dos, diez));
            diana.agregarTurno(new Turno(dia, dos, diez));
            paula.agregarTurno(new Turno(dia, seis, dos));
            jorge.agregarTurno(new Turno(dia, dos, diez));
        }
        felipe.agregarTurno(new Turno("LUNES", LocalTime.of(10, 0), LocalTime.of(18, 0)));
    }

    // ---------------------------------------------------------------
    //  Equipos, competencias y partidos
    // ---------------------------------------------------------------

    private static void crearEquipos(ClubDeportivo club, Administrador admin, Entrenador carlos, Entrenador laura,
                                     Entrenador andres, Entrenador diana, Entrenador felipe) throws Exception {
        club.crearEquipo(admin, "Séneca Fútbol A", Deporte.FUTBOL, Categoria.MAYORES, 15);
        club.crearEquipo(admin, "Séneca Fútbol B", Deporte.FUTBOL, Categoria.MAYORES, 15);
        club.crearEquipo(admin, "Séneca Baloncesto", Deporte.BALONCESTO, Categoria.MAYORES, 12);
        club.crearEquipo(admin, "Séneca Voleibol", Deporte.VOLEIBOL, Categoria.MAYORES, 12);
        club.crearEquipo(admin, "Séneca Fútbol Sub-15", Deporte.FUTBOL, Categoria.SUB15, 18);
        club.buscarEquipo("Séneca Fútbol A").setEntrenador(carlos);
        club.buscarEquipo("Séneca Fútbol B").setEntrenador(laura);
        club.buscarEquipo("Séneca Baloncesto").setEntrenador(andres);
        club.buscarEquipo("Séneca Voleibol").setEntrenador(diana);
        club.buscarEquipo("Séneca Fútbol Sub-15").setEntrenador(felipe);

        String[] posicionesFutbol = { "PORTERO", "DEFENSA", "DEFENSA", "MEDIOCAMPISTA", "MEDIOCAMPISTA", "DELANTERO", "DELANTERO", "DEFENSA" };
        for (int i = 1; i <= 8; i++) {
            club.inscribirJugadorEnEquipo(socio(club, i), "Séneca Fútbol A", posicionesFutbol[i - 1], i, FECHA_REF);
        }
        for (int i = 9; i <= 16; i++) {
            club.inscribirJugadorEnEquipo(socio(club, i), "Séneca Fútbol B", posicionesFutbol[i - 9], i - 8, FECHA_REF);
        }

        int[] jugadoresBaloncesto = jugadoresDeBaloncesto();
        String[] posicionesBaloncesto = { "BASE", "ESCOLTA", "ALERO", "ALA-PIVOT", "PIVOT", "ALERO" };
        for (int i = 0; i < jugadoresBaloncesto.length; i++) {
            club.inscribirJugadorEnEquipo(socio(club, jugadoresBaloncesto[i]), "Séneca Baloncesto", posicionesBaloncesto[i], 4 + i, FECHA_REF);
        }

        int[] jugadoresVoleibol = { 2, 4, 10, 12, 13, 17, 18 };
        String[] posicionesVoleibol = { "ARMADOR", "OPUESTO", "CENTRAL", "CENTRAL", "PUNTA", "PUNTA", "LIBERO" };
        for (int i = 0; i < jugadoresVoleibol.length; i++) {
            club.inscribirJugadorEnEquipo(socio(club, jugadoresVoleibol[i]), "Séneca Voleibol", posicionesVoleibol[i], 1 + i, FECHA_REF);
        }

        club.inscribirJugadorEnEquipo(socio(club, 19), "Séneca Fútbol Sub-15", "DELANTERO", 9, FECHA_REF);
        club.inscribirJugadorEnEquipo(socio(club, 20), "Séneca Fútbol Sub-15", "PORTERO", 1, FECHA_REF);
    }

    /** Números de los socios que juegan baloncesto. */
    private static int[] jugadoresDeBaloncesto() {
        int[] numeros = { 1, 3, 9, 11, 17, 18 };
        return numeros;
    }

    /** Liga "Torneo de los Cerros" (fútbol): dos partidos jugados, uno pendiente y resultados de otros equipos. */
    private static void crearLigaDeFutbol(ClubDeportivo club, Entrenador andres) throws Exception {
        String liga = "Torneo de los Cerros";
        club.registrarCompetencia(new CompetenciaLiga(liga, "Universidades de Bogotá", "2026", Deporte.FUTBOL, Categoria.MAYORES, 7));
        List<Socio> nomina = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            nomina.add(socio(club, i));
        }
        club.inscribirEquipoEnCompetencia(liga, "Séneca Fútbol A", nomina);

        PartidoCompetencia fecha1 = club.programarPartidoCompetencia(liga, "Séneca Fútbol A", "U. de los Andes",
                "Estadio El Campín - Cancha 2", LocalDate.of(2026, 9, 20), "", "");
        fecha1.registrarEstadisticas(new EstadisticaFutbol(socio(club, 6), 2, 0, 0, false));
        fecha1.registrarEstadisticas(new EstadisticaFutbol(socio(club, 7), 1, 1, 1, false));
        fecha1.registrarEstadisticas(new EstadisticaFutbol(socio(club, 4), 0, 2, 0, false));
        andres.registrarResultadoPartido(fecha1, 3, 1);

        PartidoCompetencia fecha2 = club.programarPartidoCompetencia(liga, "Séneca Fútbol A", "U. Javeriana",
                "Cancha Javeriana", LocalDate.of(2026, 9, 27), "", "");
        fecha2.registrarEstadisticas(new EstadisticaFutbol(socio(club, 6), 1, 0, 0, false));
        fecha2.registrarEstadisticas(new EstadisticaFutbol(socio(club, 7), 0, 0, 1, false)); // Segunda amarilla: suspendido.
        andres.registrarResultadoPartido(fecha2, 1, 1);

        club.programarPartidoCompetencia(liga, "Séneca Fútbol A", "U. del Rosario", "Cancha Rosario", LocalDate.of(2026, 10, 11), "", "");
        club.registrarResultadoExterno(liga, "U. de los Andes", "U. Javeriana", 2, 0, "", "");
        club.registrarResultadoExterno(liga, "U. del Rosario", "U. de los Andes", 1, 1, "", "");
        club.registrarResultadoExterno(liga, "U. Javeriana", "U. del Rosario", 0, 2, "", "");
    }

    /** "Copa ASCUN" (baloncesto) por fases: grupos, semifinal y final. */
    private static void crearCopaDeBaloncesto(ClubDeportivo club, Entrenador diana) throws Exception {
        String copa = "Copa ASCUN";
        CompetenciaFases ascun = new CompetenciaFases(copa, "ASCUN", "2026", Deporte.BALONCESTO, Categoria.MAYORES, 5);
        Fase grupos = new Fase("Grupos", TipoFase.GRUPOS, 2);
        grupos.agregarGrupo(new Grupo("A", Arrays.asList("Séneca Baloncesto", "U. Nacional", "U. Externado")));
        grupos.agregarGrupo(new Grupo("B", Arrays.asList("U. de los Andes", "U. Javeriana", "U. Sabana")));
        ascun.agregarFase(grupos);
        ascun.agregarFase(new Fase("Semifinal", TipoFase.ELIMINACION, 0));
        ascun.agregarFase(new Fase("Final", TipoFase.ELIMINACION, 0));
        club.registrarCompetencia(ascun);

        List<Socio> nomina = new ArrayList<>();
        for (int numero : jugadoresDeBaloncesto()) {
            nomina.add(socio(club, numero));
        }
        club.inscribirEquipoEnCompetencia(copa, "Séneca Baloncesto", nomina);

        PartidoCompetencia partido1 = club.programarPartidoCompetencia(copa, "Séneca Baloncesto", "U. Nacional", "Coliseo UN",
                LocalDate.of(2026, 9, 18), "Grupos", "A");
        partido1.registrarEstadisticas(new EstadisticaBaloncesto(socio(club, 17), 24, 10));
        partido1.registrarEstadisticas(new EstadisticaBaloncesto(socio(club, 1), 18, 3));
        diana.registrarResultadoPartido(partido1, 70, 65);

        PartidoCompetencia partido2 = club.programarPartidoCompetencia(copa, "Séneca Baloncesto", "U. Externado", "Coliseo Séneca",
                LocalDate.of(2026, 9, 25), "Grupos", "A");
        partido2.registrarEstadisticas(new EstadisticaBaloncesto(socio(club, 17), 30, 12));
        partido2.registrarEstadisticas(new EstadisticaBaloncesto(socio(club, 9), 15, 4));
        diana.registrarResultadoPartido(partido2, 75, 70);

        club.registrarResultadoExterno(copa, "U. Nacional", "U. Externado", 80, 75, "Grupos", "A");
        club.registrarResultadoExterno(copa, "U. de los Andes", "U. Javeriana", 70, 60, "Grupos", "B");
        club.registrarResultadoExterno(copa, "U. Sabana", "U. de los Andes", 70, 66, "Grupos", "B");
        club.registrarResultadoExterno(copa, "U. Sabana", "U. Javeriana", 75, 72, "Grupos", "B");
    }

    /** Un partido interno jugado (Fútbol A 2 - 1 Fútbol B) y otro pendiente. */
    private static void crearPartidosInternos(ClubDeportivo club, Entrenador andres) throws Exception {
        PartidoInterno jugado = club.programarPartidoInterno(LocalDate.of(2026, 10, 3), "Séneca Fútbol A", "Séneca Fútbol B");
        jugado.registrarEstadisticas(new EstadisticaFutbol(socio(club, 1), 2, 0, 0, false));
        jugado.registrarEstadisticas(new EstadisticaFutbol(socio(club, 5), 0, 1, 2, false)); // Dos amarillas: suspendido.
        jugado.registrarEstadisticas(new EstadisticaFutbol(socio(club, 9), 1, 0, 0, false));
        jugado.registrarEstadisticas(new EstadisticaFutbol(socio(club, 10), 0, 0, 0, true)); // Roja: suspendido.
        andres.registrarResultadoPartido(jugado, 2, 1);
        club.programarPartidoInterno(LocalDate.of(2026, 10, 17), "Séneca Fútbol B", "Séneca Fútbol A");
    }

    // ---------------------------------------------------------------
    //  Academias, instalaciones y sesiones
    // ---------------------------------------------------------------

    private static void crearInstalacionesYAcademias(ClubDeportivo club) throws Exception {
        club.agregarInstalacion(new CanchaTenis("TEN-1"));
        club.agregarInstalacion(new CanchaTenis("TEN-2"));
        club.agregarInstalacion(new CanchaTenis("TEN-3"));
        club.agregarInstalacion(new CanchaPadel("PAD-1"));
        club.agregarInstalacion(new CanchaPadel("PAD-2"));
        club.agregarInstalacion(new MesaDeTenisMesa("MESA-1", true));
        club.agregarInstalacion(new MesaDeTenisMesa("MESA-2", false));
        for (Disciplina disciplina : Disciplina.values()) {
            club.setMaxInstalacionesSimultaneas(disciplina, 2);
        }

        club.inscribirEnDisciplina(socio(club, 1), Disciplina.TENIS, Nivel.AVANZADO);
        club.inscribirEnDisciplina(socio(club, 2), Disciplina.TENIS, Nivel.INTERMEDIO);
        club.inscribirEnDisciplina(socio(club, 3), Disciplina.TENIS, Nivel.PRINCIPIANTE);
        club.inscribirEnDisciplina(socio(club, 4), Disciplina.TENIS, Nivel.INTERMEDIO);
        club.inscribirEnDisciplina(club.buscarSocio("S021"), Disciplina.TENIS, Nivel.AVANZADO); // Perfil de Carlos.
        for (int i = 5; i <= 8; i++) {
            club.inscribirEnDisciplina(socio(club, i), Disciplina.PADEL, Nivel.INTERMEDIO);
        }
        club.inscribirEnDisciplina(socio(club, 9), Disciplina.TENIS_MESA, Nivel.PRINCIPIANTE);
        club.inscribirEnDisciplina(socio(club, 10), Disciplina.TENIS_MESA, Nivel.INTERMEDIO);

        club.reservar(socio(club, 1), "TEN-1", LocalDateTime.of(2026, 10, 12, 8, 0), 2, Instalacion.SENCILLOS);
        club.reservar(socio(club, 5), "PAD-1", LocalDateTime.of(2026, 10, 12, 9, 0), 4, Instalacion.DOBLES);

        // Ana le gana a Luis 2 sets a 1.
        PartidoPractica tenis = club.programarPartidoPractica(socio(club, 1), Disciplina.TENIS,
                Arrays.asList(socio(club, 1), socio(club, 2)), LocalDate.of(2026, 10, 4));
        club.registrarResultadoPractica(tenis.getId(), Arrays.asList(
                new EstadisticaTenisPadel(socio(club, 1), 2, 13, 4, 10),
                new EstadisticaTenisPadel(socio(club, 2), 1, 10, 1, 18)));

        // Partido de pádel en dobles: gana la pareja de S007 y S008.
        PartidoPractica padel = club.programarPartidoPractica(socio(club, 5), Disciplina.PADEL,
                Arrays.asList(socio(club, 5), socio(club, 6), socio(club, 7), socio(club, 8)), LocalDate.of(2026, 10, 5));
        club.registrarResultadoPractica(padel.getId(), Arrays.asList(
                new EstadisticaTenisPadel(socio(club, 5), 0, 7, 0, 12),
                new EstadisticaTenisPadel(socio(club, 6), 0, 7, 1, 9),
                new EstadisticaTenisPadel(socio(club, 7), 2, 12, 2, 6),
                new EstadisticaTenisPadel(socio(club, 8), 2, 12, 1, 8)));
    }

    private static void crearSesiones(ClubDeportivo club, Entrenador laura, Entrenador andres, Entrenador diana) throws Exception {
        SesionEntrenamiento tenis = club.planearSesion(laura, LocalDateTime.of(2026, 10, 6, 7, 0), 90, "TEN-2",
                Arrays.asList(socio(club, 1), socio(club, 2), socio(club, 3), socio(club, 4)));
        tenis.finalizar();
        laura.registrarAsistenciaYEvaluacion(tenis, socio(club, 1), EstadoAsistencia.ASISTIO, 5, "Excelente saque");
        laura.registrarAsistenciaYEvaluacion(tenis, socio(club, 2), EstadoAsistencia.TARDE, 3, "Llegó 15 min tarde");
        laura.registrarAsistenciaYEvaluacion(tenis, socio(club, 3), EstadoAsistencia.ASISTIO, 4, "");
        laura.registrarAsistenciaYEvaluacion(tenis, socio(club, 4), EstadoAsistencia.FALTO, 0, "");

        // Sesión del Fútbol A: todos asistieron menos S008.
        SesionEntrenamiento futbol = club.planearSesionEquipo(andres, LocalDateTime.of(2026, 10, 7, 15, 0), 120, "Séneca Fútbol A");
        futbol.finalizar();
        for (Socio jugador : futbol.getConvocados()) {
            if (jugador.getId().equals("S008")) {
                andres.registrarAsistenciaYEvaluacion(futbol, jugador, EstadoAsistencia.FALTO, 0, "");
            } else {
                andres.registrarAsistenciaYEvaluacion(futbol, jugador, EstadoAsistencia.ASISTIO, 4, "");
            }
        }
        club.planearSesionEquipo(diana, LocalDateTime.of(2026, 10, 14, 16, 0), 90, "Séneca Baloncesto");
    }

    // ---------------------------------------------------------------
    //  Tienda, cafetería, sugerencias y solicitudes
    // ---------------------------------------------------------------

    private static void crearTiendaYCafeteria(ClubDeportivo club, Administrador admin, Entrenador laura) throws Exception {
        // Cada fila: nombre, categoría, precio, unidades en tienda, unidades en bodega.
        String[][] articulos = {
                { "Raqueta Wilson Pro", "Raquetas", "450000", "5", "10" },
                { "Balón Fútbol Golty", "Balones", "120000", "10", "20" },
                { "Malla Voleibol", "Mallas", "200000", "0", "3" },
                { "Uniforme Séneca", "Uniformes", "90000", "15", "10" },
                { "Tubo Pelotas Tenis", "Pelotas", "35000", "30", "40" } };
        for (String[] fila : articulos) {
            club.agregarArticulo(new Articulo(fila[0], fila[1], Double.parseDouble(fila[2])));
            if (!fila[3].equals("0")) {
                club.reabastecer(admin, fila[0], Articulo.TIENDA, Integer.parseInt(fila[3]));
            }
            club.reabastecer(admin, fila[0], Articulo.BODEGA, Integer.parseInt(fila[4]));
        }

        club.agregarProductoCafeteria(new Bebida("Café americano", 4000, TipoBebida.CALIENTE));
        club.agregarProductoCafeteria(new Bebida("Chocolate caliente", 5000, TipoBebida.CALIENTE));
        club.agregarProductoCafeteria(new Bebida("Jugo natural", 6000, TipoBebida.FRIA));
        club.agregarProductoCafeteria(new Bebida("Agua", 3000, TipoBebida.FRIA));
        club.agregarProductoCafeteria(new Snack("Barra de maní", 3500, Arrays.asList("MANI")));
        club.agregarProductoCafeteria(new Snack("Galletas de avena", 3000, Arrays.asList("GLUTEN", "LACTEOS")));
        club.agregarProductoCafeteria(new Snack("Fruta picada", 5000, new ArrayList<String>()));

        LocalDate dosDeOctubre = LocalDate.of(2026, 10, 2);
        VentaTienda compraAna = club.iniciarVentaTienda(socio(club, 1), dosDeOctubre);
        compraAna.agregarItem(club.buscarArticulo("Balón Fútbol Golty"), 1);
        compraAna.agregarItem(club.buscarArticulo("Tubo Pelotas Tenis"), 2);
        club.confirmarVenta(compraAna);

        VentaTienda compraLaura = club.iniciarVentaTienda(laura, dosDeOctubre); // Usa su código de empleada (15 %).
        compraLaura.agregarItem(club.buscarArticulo("Raqueta Wilson Pro"), 1);
        compraLaura.aplicarDescuento(club.buscarCodigoDeEmpleado(laura));
        club.confirmarVenta(compraLaura);

        VentaCafeteria compraSofia = club.iniciarVentaCafeteria(socio(club, 3), dosDeOctubre);
        compraSofia.agregarItem(club.buscarProducto("Jugo natural"), 1);
        compraSofia.agregarItem(club.buscarProducto("Galletas de avena"), 2);
        compraSofia.informarAlergenos();
        club.confirmarVenta(compraSofia);

        for (int i = 1; i <= 4; i++) {
            club.pagarMensualidad(socio(club, i), "2026-10", 150000, LocalDate.of(2026, 10, 1));
        }
    }

    private static void crearSugerenciasYSolicitudes(ClubDeportivo club, Entrenador andres, Entrenador felipe,
                                                     Fisioterapeuta paula) throws Exception {
        club.sugerirProducto(andres, Sugerencia.SNACK, "Brownie", "GLUTEN,LACTEOS", 4500, LocalDate.of(2026, 10, 5));
        club.sugerirProducto(paula, Sugerencia.ARTICULO, "Banda elástica", "Rehabilitación", 30000, LocalDate.of(2026, 10, 6));

        // Esta se puede aprobar: Felipe solo es refuerzo.
        club.solicitarCambioTurno(felipe, new Turno("LUNES", LocalTime.of(10, 0), LocalTime.of(18, 0)),
                new Turno("MARTES", LocalTime.of(10, 0), LocalTime.of(18, 0)));
        // Esta se rechaza: el lunes en la mañana quedaría sin fisioterapeuta.
        club.solicitarCambioTurno(paula, new Turno("LUNES", LocalTime.of(6, 0), LocalTime.of(14, 0)),
                new Turno("MARTES", LocalTime.of(14, 0), LocalTime.of(22, 0)));
    }

    // ---------------------------------------------------------------
    //  Proyecto 2: externos, niveles y torneos
    // ---------------------------------------------------------------

    private static void crearExternosYTorneos(ClubDeportivo club, Entrenador felipe, Fisioterapeuta paula) throws Exception {
        ParticipanteExterno julian = new ParticipanteExterno("X001", "Julián Ospina", "jospina", "1234", "1020304050", true);
        julian.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
        ParticipanteExterno renata = new ParticipanteExterno("X002", "Renata Vélez", "rvelez", "1234", "52123456", false);
        renata.declararNivel(Disciplina.PADEL, Nivel.INTERMEDIO);
        renata.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
        club.registrarUsuario(julian);
        club.registrarUsuario(renata);
        club.siguienteId("EXTERNO"); // Ya se usó X001.
        club.siguienteId("EXTERNO"); // Ya se usó X002.
        paula.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
        felipe.declararNivel(Disciplina.PADEL, Nivel.INTERMEDIO);

        // Torneos con inscripciones abiertas. Se agregan directo a la lista porque sus fechas son fijas;
        // cuando se crean desde la consola, ClubDeportivo.registrarTorneo revisa las reglas.
        club.getTorneos().add(new TorneoIndividual("Copa Tenis Intermedio", TipoTorneo.RECREATIVO, Disciplina.TENIS,
                Nivel.INTERMEDIO, false, 8, 0, 0.15, LocalDate.of(2026, 11, 21), LocalDate.of(2026, 11, 14)));
        club.getTorneos().add(new TorneoIndividual("Pádel Dobles Abierto", TipoTorneo.COMPETITIVO, Disciplina.PADEL,
                Nivel.INTERMEDIO, true, 6, 40000, 0, LocalDate.of(2026, 11, 22), LocalDate.of(2026, 11, 15)));
        club.getTorneos().add(new TorneoConjunto("Mini Fútbol Mayores", TipoTorneo.RECREATIVO, Deporte.FUTBOL,
                Categoria.MAYORES, 4, 0, 0.10, LocalDate.of(2026, 11, 28), LocalDate.of(2026, 11, 20)));
        club.getTorneos().add(new TorneoIndividual("Relámpago Tenis de Mesa", TipoTorneo.COMPETITIVO, Disciplina.TENIS_MESA,
                Nivel.PRINCIPIANTE, false, 4, 20000, 0, LocalDate.of(2026, 12, 5), LocalDate.of(2026, 11, 28)));
    }

    /** Devuelve el socio número n (por ejemplo, 7 es S007). */
    private static Socio socio(ClubDeportivo club, int n) throws Exception {
        return club.buscarSocio(String.format("S%03d", n));
    }
}
