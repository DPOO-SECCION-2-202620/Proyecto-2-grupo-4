package persistencia;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import logica.ClubDeportivo;
import modelo.entrenamiento.AsistenciaEvaluacion;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisMesa;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.InscripcionDisciplina;
import modelo.academias.Instalacion;
import modelo.academias.MesaDeTenisMesa;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.competencias.Competencia;
import modelo.competencias.CompetenciaFases;
import modelo.competencias.CompetenciaLiga;
import modelo.competencias.Enfrentamiento;
import modelo.competencias.Fase;
import modelo.competencias.Grupo;
import modelo.competencias.InscripcionCompetencia;
import modelo.conjunto.Equipo;
import modelo.conjunto.EstadisticaBaloncesto;
import modelo.conjunto.EstadisticaFutbol;
import modelo.conjunto.EstadisticaPartido;
import modelo.conjunto.EstadisticaVoleibol;
import modelo.conjunto.InscripcionEquipo;
import modelo.conjunto.Partido;
import modelo.conjunto.PartidoCompetencia;
import modelo.conjunto.PartidoInterno;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.EstadoAsistencia;
import modelo.enums.EstadoSolicitud;
import modelo.enums.EstadoSugerencia;
import modelo.enums.EstadoTorneo;
import modelo.enums.Nivel;
import modelo.enums.TipoBebida;
import modelo.enums.TipoCupo;
import modelo.enums.TipoFase;
import modelo.enums.TipoTorneo;
import modelo.torneos.BonoDescuento;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.InscripcionTorneoEquipo;
import modelo.torneos.InscripcionTorneoIndividual;
import modelo.torneos.Premio;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Administrador;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;
import modelo.ventas.Articulo;
import modelo.ventas.Bebida;
import modelo.ventas.CodigoDescuento;
import modelo.ventas.ItemVentaCafeteria;
import modelo.ventas.ItemVentaTienda;
import modelo.ventas.Snack;
import modelo.ventas.Sugerencia;
import modelo.ventas.Venta;
import modelo.ventas.VentaCafeteria;
import modelo.ventas.VentaMensualidad;
import modelo.ventas.VentaTienda;

/**
 * Reconstruye el club leyendo los archivos de la carpeta de datos.
 *
 * El orden de lectura importa: primero se carga lo que otros archivos necesitan. Por ejemplo,
 * los usuarios van antes que los equipos porque una inscripción a un equipo guarda el id del socio.
 * En cada registro, c[0], c[1], ... son los campos de la línea en el orden en que se escribieron.
 */
public class LectorClub {

    private File carpeta;
    private ClubDeportivo club;
    private String archivoActual; // Último archivo leído, para decir dónde está un dato malo.

    public LectorClub(File carpeta) {
        this.carpeta = carpeta;
        this.archivoActual = "-";
    }

    /** Nombre del último archivo que se leyó. */
    public String getArchivoActual() {
        return archivoActual;
    }

    /** Lee todos los archivos y devuelve el club armado. */
    public ClubDeportivo cargarTodo() throws Exception {
        club = new ClubDeportivo();
        cargarConfiguracion();
        cargarInstalaciones();
        cargarUsuarios();
        cargarNivelesDeclarados();
        cargarBonos();
        cargarTurnos();
        cargarCodigosDescuento();
        cargarEquipos();
        cargarInscripcionesEquipo();
        cargarCompetencias();
        cargarFasesYGrupos();
        cargarInscripcionesCompetencia();
        cargarResultadosExternos();
        cargarPartidos();
        cargarEstadisticasPartido();
        cargarReservas();
        cargarInscripcionesDisciplina();
        cargarPartidosPractica();
        cargarSesiones();
        cargarAsistencias();
        cargarSolicitudesTurno();
        cargarArticulosEInventario();
        cargarProductosCafeteria();
        cargarVentas();
        cargarSugerencias();
        cargarTorneos();
        return club;
    }

    /** Lee un archivo de la carpeta y recuerda su nombre. */
    private List<String[]> leer(String nombreArchivo) throws IOException {
        archivoActual = nombreArchivo;
        return ArchivoTexto.leer(new File(carpeta, nombreArchivo));
    }

    // Atajos para convertir los campos de texto.
    private static int entero(String campo) {
        return Integer.parseInt(campo);
    }

    private static double decimal(String campo) {
        return Double.parseDouble(campo);
    }

    private static boolean booleano(String campo) {
        return Boolean.parseBoolean(campo);
    }

    private static List<String> lista(String campo) {
        return ArchivoTexto.separarLista(campo);
    }

    private static String texto(String campo) {
        return ArchivoTexto.quitarMarcaVacio(campo);
    }

    // ---------------------------------------------------------------
    //  Configuración, instalaciones y usuarios
    // ---------------------------------------------------------------

    private void cargarConfiguracion() throws IOException {
        for (String[] c : leer("config.txt")) {
            String clave = c[0];
            if (clave.equals("DIAS")) {
                club.setDiasAtencion(lista(c[1]));
            } else if (clave.equals("HORARIO")) {
                club.setHoraApertura(LocalTime.parse(c[1]));
                club.setHoraCierre(LocalTime.parse(c[2]));
            } else if (clave.equals("MAX")) {
                club.setMaxInstalacionesSimultaneas(Disciplina.valueOf(c[1]), entero(c[2]));
            } else if (clave.equals("CONTADOR")) {
                club.getContadores().put(c[1], entero(c[2]));
            } else if (clave.equals("PREMIO")) {
                club.setPorcentajePremioCompetitivo(decimal(c[1]));
            }
            // Una clave que no se conoce se ignora.
        }
    }

    /** Las instalaciones van antes que los usuarios porque el socio guarda la instalación donde está. */
    private void cargarInstalaciones() throws IOException {
        for (String[] c : leer("instalaciones.txt")) {
            Instalacion instalacion;
            if (c[0].equals("CANCHA_TENIS")) {
                instalacion = new CanchaTenis(c[1]);
            } else if (c[0].equals("CANCHA_PADEL")) {
                instalacion = new CanchaPadel(c[1]);
            } else {
                instalacion = new MesaDeTenisMesa(c[1], booleano(c[2]));
            }
            club.getInstalaciones().add(instalacion);
        }
    }

    /**
     * Los usuarios se leen en dos pasadas: el perfil de jugador de un entrenador es un socio que
     * puede aparecer más abajo en el archivo, así que primero se crean todos y después se conectan.
     */
    private void cargarUsuarios() throws Exception {
        Map<Entrenador, String> perfilesPendientes = new LinkedHashMap<>();
        for (String[] c : leer("usuarios.txt")) {
            String tipo = c[0];
            Usuario usuario;
            if (tipo.equals("SOCIO")) {
                Socio socio = new Socio(c[1], c[2], c[3], c[4], LocalDate.parse(c[6]));
                if (!c[7].equals(ArchivoTexto.VACIO)) {
                    socio.setInstalacionActual(club.buscarInstalacionONull(c[7]));
                }
                socio.setTieneBebidaCaliente(booleano(c[8]));
                usuario = socio;
            } else if (tipo.equals("ENTRENADOR")) {
                Entrenador entrenador = new Entrenador(c[1], c[2], c[3], c[4]);
                if (c.length > 6 && !c[6].equals(ArchivoTexto.VACIO)) {
                    perfilesPendientes.put(entrenador, c[6]);
                }
                usuario = entrenador;
            } else if (tipo.equals("FISIOTERAPEUTA")) {
                usuario = new Fisioterapeuta(c[1], c[2], c[3], c[4]);
            } else if (tipo.equals("EXTERNO")) {
                usuario = new ParticipanteExterno(c[1], c[2], c[3], c[4], c[6], booleano(c[7]));
            } else {
                usuario = new Administrador(c[1], c[2], c[3], c[4]);
            }
            usuario.setPuntosFidelidad(decimal(c[5]));
            // Se agrega directo a la lista: los códigos de descuento se cargan de su propio archivo.
            club.getUsuarios().add(usuario);
        }
        // Segunda pasada: se conecta cada entrenador con su perfil de jugador.
        for (Map.Entry<Entrenador, String> pendiente : perfilesPendientes.entrySet()) {
            pendiente.getKey().setPerfilJugador(club.buscarSocio(pendiente.getValue()));
        }
    }

    private void cargarNivelesDeclarados() throws Exception {
        for (String[] c : leer("niveles_declarados.txt")) {
            Usuario usuario = club.buscarUsuario(c[0]);
            Disciplina disciplina = Disciplina.valueOf(c[1]);
            Nivel nivel = Nivel.valueOf(c[2]);
            if (usuario instanceof ParticipanteExterno) {
                ((ParticipanteExterno) usuario).declararNivel(disciplina, nivel);
            } else if (usuario instanceof Empleado) {
                ((Empleado) usuario).declararNivel(disciplina, nivel);
            }
        }
    }

    private void cargarBonos() throws Exception {
        for (String[] c : leer("bonos.txt")) {
            BonoDescuento bono = new BonoDescuento(c[0], decimal(c[2]), c[3], LocalDate.parse(c[4]));
            bono.setUsado(booleano(c[5]));
            club.buscarUsuario(c[1]).agregarBono(bono);
        }
    }

    private void cargarTurnos() throws Exception {
        for (String[] c : leer("turnos.txt")) {
            Empleado empleado = (Empleado) club.buscarUsuario(c[0]);
            empleado.agregarTurno(new Turno(c[1], LocalTime.parse(c[2]), LocalTime.parse(c[3])));
        }
    }

    private void cargarCodigosDescuento() throws Exception {
        for (String[] c : leer("codigos_descuento.txt")) {
            Empleado dueno = (Empleado) club.buscarUsuario(c[1]);
            club.getCodigosDescuento().add(new CodigoDescuento(c[0], dueno));
        }
    }

    // ---------------------------------------------------------------
    //  Equipos y competencias
    // ---------------------------------------------------------------

    private void cargarEquipos() throws Exception {
        for (String[] c : leer("equipos.txt")) {
            Equipo equipo = new Equipo(c[0], Deporte.valueOf(c[1]), Categoria.valueOf(c[2]), entero(c[3]));
            if (c.length > 4 && !c[4].equals(ArchivoTexto.VACIO)) {
                equipo.setEntrenador((Entrenador) club.buscarUsuario(c[4]));
            }
            club.getEquipos().add(equipo);
        }
    }

    private void cargarInscripcionesEquipo() throws Exception {
        for (String[] c : leer("inscripciones_equipo.txt")) {
            Equipo equipo = club.buscarEquipo(c[0]);
            InscripcionEquipo inscripcion = new InscripcionEquipo(equipo, club.buscarSocio(c[1]), c[2], entero(c[3]));
            inscripcion.setActivo(booleano(c[4]));
            inscripcion.setSuspendidoProximoPartido(booleano(c[5]));
            equipo.agregarInscripcionCargada(inscripcion);
        }
    }

    private void cargarCompetencias() throws IOException {
        for (String[] c : leer("competencias.txt")) {
            Deporte deporte = Deporte.valueOf(c[4]);
            Categoria categoria = Categoria.valueOf(c[5]);
            int nominaMinima = entero(c[6]);
            if (c[0].equals("LIGA")) {
                club.getCompetencias().add(new CompetenciaLiga(c[1], c[2], c[3], deporte, categoria, nominaMinima));
            } else {
                CompetenciaFases conFases = new CompetenciaFases(c[1], c[2], c[3], deporte, categoria, nominaMinima);
                conFases.setIndiceFaseActual(entero(c[7]));
                conFases.setCampeon(texto(c[8]));
                club.getCompetencias().add(conFases);
            }
        }
    }

    /**
     * Fases y grupos. Los participantes de cada fase se asignan al final, después de crear los grupos,
     * porque agregar un grupo cambia la lista de participantes de la fase.
     */
    private void cargarFasesYGrupos() throws Exception {
        List<Fase> fases = new ArrayList<>();
        List<String> participantesDeCadaFase = new ArrayList<>();
        for (String[] c : leer("fases.txt")) {
            Fase fase = new Fase(c[1], TipoFase.valueOf(c[2]), entero(c[3]));
            fase.setFinalizada(booleano(c[4]));
            ((CompetenciaFases) club.buscarCompetencia(c[0])).agregarFase(fase);
            fases.add(fase);
            participantesDeCadaFase.add(c[5]);
        }
        for (String[] c : leer("grupos.txt")) {
            buscarFase(c[0], c[1]).agregarGrupo(new Grupo(c[2], lista(c[3])));
        }
        for (int i = 0; i < fases.size(); i++) {
            fases.get(i).setParticipantes(lista(participantesDeCadaFase.get(i)));
        }
    }

    private Fase buscarFase(String nombreCompetencia, String nombreFase) throws Exception {
        CompetenciaFases competencia = (CompetenciaFases) club.buscarCompetencia(nombreCompetencia);
        for (Fase fase : competencia.getFases()) {
            if (fase.getNombre().equals(nombreFase)) {
                return fase;
            }
        }
        throw new IOException("No se encontró la fase " + nombreFase + " de " + nombreCompetencia);
    }

    private void cargarInscripcionesCompetencia() throws Exception {
        for (String[] c : leer("inscripciones_competencia.txt")) {
            Competencia competencia = club.buscarCompetencia(c[0]);
            List<Socio> nomina = new ArrayList<>();
            for (String id : lista(c[2])) {
                nomina.add(club.buscarSocio(id));
            }
            InscripcionCompetencia inscripcion = new InscripcionCompetencia(competencia, club.buscarEquipo(c[1]), nomina);
            for (String par : lista(c[3])) {
                String[] partes = par.split(":"); // id:cantidad de amarillas
                inscripcion.getAmarillasAcumuladas().put(club.buscarSocio(partes[0]), entero(partes[1]));
            }
            for (String id : lista(c[4])) {
                inscripcion.getSuspendidos().add(club.buscarSocio(id));
            }
            competencia.agregarInscripcionCargada(inscripcion);
        }
    }

    private void cargarResultadosExternos() throws Exception {
        for (String[] c : leer("resultados_externos.txt")) {
            Enfrentamiento resultado = new Enfrentamiento(c[1], c[2], entero(c[3]), entero(c[4]), texto(c[5]), texto(c[6]));
            club.buscarCompetencia(c[0]).registrarResultadoExterno(resultado);
        }
    }

    private void cargarPartidos() throws Exception {
        for (String[] c : leer("partidos.txt")) {
            Partido partido;
            if (c[0].equals("INTERNO")) {
                PartidoInterno interno = new PartidoInterno(entero(c[1]), LocalDate.parse(c[2]),
                        club.buscarEquipo(c[3]), club.buscarEquipo(c[4]));
                club.getPartidosInternos().add(interno);
                partido = interno;
            } else {
                Competencia competencia = club.buscarCompetencia(c[3]);
                InscripcionCompetencia inscripcion = competencia.buscarInscripcion(club.buscarEquipo(c[4]));
                PartidoCompetencia deCompetencia = new PartidoCompetencia(entero(c[1]), LocalDate.parse(c[2]), inscripcion,
                        c[5], c[6], texto(c[7]), texto(c[8]));
                competencia.agregarPartido(deCompetencia);
                partido = deCompetencia;
            }
            // El resultado está en los tres últimos campos.
            int n = c.length;
            partido.fijarResultadoCargado(entero(c[n - 3]), entero(c[n - 2]), booleano(c[n - 1]));
        }
    }

    private void cargarEstadisticasPartido() throws Exception {
        for (String[] c : leer("estadisticas_partido.txt")) {
            Partido partido = club.buscarPartido(entero(c[0]));
            Socio jugador = club.buscarSocio(c[2]);
            EstadisticaPartido estadistica;
            if (c[1].equals("FUTBOL")) {
                estadistica = new EstadisticaFutbol(jugador, entero(c[3]), entero(c[4]), entero(c[5]), booleano(c[6]));
            } else if (c[1].equals("BALONCESTO")) {
                estadistica = new EstadisticaBaloncesto(jugador, entero(c[3]), entero(c[4]));
            } else {
                estadistica = new EstadisticaVoleibol(jugador, entero(c[3]), entero(c[4]));
            }
            partido.agregarEstadisticaCargada(estadistica);
        }
    }

    // ---------------------------------------------------------------
    //  Academias y entrenamiento
    // ---------------------------------------------------------------

    private void cargarReservas() throws Exception {
        for (String[] c : leer("reservas.txt")) {
            Instalacion instalacion = club.buscarInstalacion(c[1]);
            Reserva reserva = new Reserva(entero(c[0]), instalacion, club.buscarSocio(c[2]), LocalDateTime.parse(c[3]),
                    entero(c[4]), c[5]);
            instalacion.agregarReservaCargada(reserva);
        }
    }

    private void cargarInscripcionesDisciplina() throws Exception {
        for (String[] c : leer("inscripciones_disciplina.txt")) {
            InscripcionDisciplina inscripcion = new InscripcionDisciplina(club.buscarSocio(c[0]), Disciplina.valueOf(c[1]),
                    Nivel.valueOf(c[2]));
            inscripcion.setRankingElo(decimal(c[3]));
            club.getInscripcionesDisciplina().add(inscripcion);
        }
    }

    /** Primero se leen las estadísticas y se agrupan por partido; después se crean los partidos con su resultado. */
    private void cargarPartidosPractica() throws Exception {
        Map<Integer, List<EstadisticaPractica>> estadisticasPorPartido = new LinkedHashMap<>();
        for (String[] c : leer("estadisticas_practica.txt")) {
            Socio jugador = club.buscarSocio(c[1]);
            EstadisticaPractica estadistica;
            if (c[4].equals("TENIS_PADEL")) {
                estadistica = new EstadisticaTenisPadel(jugador, entero(c[5]), entero(c[6]), entero(c[7]), entero(c[8]));
            } else {
                estadistica = new EstadisticaTenisMesa(jugador, entero(c[5]), entero(c[6]));
            }
            estadistica.setElo(decimal(c[2]), decimal(c[3]));

            int idPartido = entero(c[0]);
            if (!estadisticasPorPartido.containsKey(idPartido)) {
                estadisticasPorPartido.put(idPartido, new ArrayList<EstadisticaPractica>());
            }
            estadisticasPorPartido.get(idPartido).add(estadistica);
        }

        for (String[] c : leer("partidos_practica.txt")) {
            Disciplina disciplina = Disciplina.valueOf(c[1]);
            List<InscripcionDisciplina> jugadores = new ArrayList<>();
            for (String id : lista(c[3])) {
                jugadores.add(club.buscarInscripcionDisciplina(club.buscarSocio(id), disciplina));
            }
            PartidoPractica partido = new PartidoPractica(entero(c[0]), disciplina, LocalDate.parse(c[2]), jugadores);
            if (booleano(c[4])) {
                List<EstadisticaPractica> estadisticas = estadisticasPorPartido.get(partido.getId());
                if (estadisticas == null) {
                    estadisticas = new ArrayList<>();
                }
                partido.cargarResultado(estadisticas);
            }
            club.getPartidosPractica().add(partido);
        }
    }

    private void cargarSesiones() throws Exception {
        for (String[] c : leer("sesiones.txt")) {
            List<Socio> convocados = new ArrayList<>();
            for (String id : lista(c[5])) {
                convocados.add(club.buscarSocio(id));
            }
            Instalacion instalacion = null;
            if (!c[2].equals(ArchivoTexto.VACIO)) {
                instalacion = club.buscarInstalacion(c[2]);
            }
            Entrenador entrenador = (Entrenador) club.buscarUsuario(c[1]);
            SesionEntrenamiento sesion = new SesionEntrenamiento(entero(c[0]), LocalDateTime.parse(c[3]), entero(c[4]),
                    entrenador, instalacion, convocados);
            sesion.setFinalizada(booleano(c[6]));
            club.getSesiones().add(sesion);
        }
    }

    private void cargarAsistencias() throws Exception {
        for (String[] c : leer("asistencias.txt")) {
            AsistenciaEvaluacion asistencia = new AsistenciaEvaluacion(club.buscarSocio(c[1]), EstadoAsistencia.valueOf(c[2]),
                    entero(c[3]), texto(c[4]));
            club.buscarSesion(entero(c[0])).agregarAsistenciaCargada(asistencia);
        }
    }

    private void cargarSolicitudesTurno() throws Exception {
        for (String[] c : leer("solicitudes_turno.txt")) {
            Empleado empleado = (Empleado) club.buscarUsuario(c[1]);
            Turno actual = new Turno(c[2], LocalTime.parse(c[3]), LocalTime.parse(c[4]));
            Turno propuesto = new Turno(c[5], LocalTime.parse(c[6]), LocalTime.parse(c[7]));
            SolicitudCambioTurno solicitud = new SolicitudCambioTurno(entero(c[0]), empleado, actual, propuesto);
            solicitud.setEstado(EstadoSolicitud.valueOf(c[8]));
            club.getSolicitudesTurno().add(solicitud);
        }
    }

    // ---------------------------------------------------------------
    //  Tienda, cafetería y ventas
    // ---------------------------------------------------------------

    private void cargarArticulosEInventario() throws Exception {
        for (String[] c : leer("articulos.txt")) {
            club.getArticulos().add(new Articulo(c[0], c[1], decimal(c[2])));
        }
        for (String[] c : leer("inventario.txt")) {
            club.buscarArticulo(c[0]).agregarInventario(c[1], entero(c[2]));
        }
    }

    private void cargarProductosCafeteria() throws IOException {
        for (String[] c : leer("productos_cafeteria.txt")) {
            if (c[0].equals("BEBIDA")) {
                club.getProductosCafeteria().add(new Bebida(c[1], decimal(c[2]), TipoBebida.valueOf(c[3])));
            } else {
                club.getProductosCafeteria().add(new Snack(c[1], decimal(c[2]), lista(c[3])));
            }
        }
    }

    /** Primero se leen los ítems y se agrupan por venta; después se arma cada venta con sus ítems. */
    private void cargarVentas() throws Exception {
        Map<Integer, List<String[]>> itemsPorVenta = new LinkedHashMap<>();
        for (String[] c : leer("items_venta.txt")) {
            int idVenta = entero(c[0]);
            if (!itemsPorVenta.containsKey(idVenta)) {
                itemsPorVenta.put(idVenta, new ArrayList<String[]>());
            }
            itemsPorVenta.get(idVenta).add(c);
        }

        for (String[] c : leer("ventas.txt")) {
            int id = entero(c[0]);
            LocalDate fecha = LocalDate.parse(c[2]);
            Usuario comprador = club.buscarUsuario(c[3]);
            List<String[]> items = itemsPorVenta.get(id);
            if (items == null) {
                items = new ArrayList<>();
            }

            Venta venta;
            if (c[1].equals("TIENDA")) {
                VentaTienda tienda = new VentaTienda(id, fecha, comprador);
                if (!c[10].equals(ArchivoTexto.VACIO)) {
                    tienda.setDescuentoAplicado(club.buscarCodigo(c[10]));
                }
                for (String[] item : items) {
                    tienda.agregarItemCargado(new ItemVentaTienda(club.buscarArticulo(item[1]), entero(item[2]), decimal(item[3])));
                }
                venta = tienda;
            } else if (c[1].equals("CAFETERIA")) {
                VentaCafeteria cafeteria = new VentaCafeteria(id, fecha, comprador);
                cafeteria.fijarPropinaCargada(decimal(c[10]));
                for (String[] item : items) {
                    cafeteria.agregarItemCargado(new ItemVentaCafeteria(club.buscarProducto(item[1]), entero(item[2]), decimal(item[3])));
                }
                venta = cafeteria;
            } else {
                venta = new VentaMensualidad(id, fecha, (Socio) comprador, c[10], decimal(c[11]));
            }

            if (c.length > 12 && !c[12].equals(ArchivoTexto.VACIO)) {
                venta.setBonoAplicadoCargado(comprador.buscarBono(c[12]));
            }
            // Las ventas guardadas ya están cerradas: se cargan con sus valores tal cual.
            venta.fijarValoresCargados(decimal(c[4]), decimal(c[5]), decimal(c[6]), decimal(c[7]), decimal(c[8]), decimal(c[9]), true);
            club.getVentas().add(venta);
        }
    }

    private void cargarSugerencias() throws Exception {
        for (String[] c : leer("sugerencias.txt")) {
            Empleado autor = (Empleado) club.buscarUsuario(c[1]);
            Sugerencia sugerencia = new Sugerencia(entero(c[0]), autor, c[2], c[3], texto(c[4]), decimal(c[5]), LocalDate.parse(c[7]));
            sugerencia.setEstado(EstadoSugerencia.valueOf(c[6]));
            club.getSugerencias().add(sugerencia);
        }
    }

    // ---------------------------------------------------------------
    //  Torneos (Proyecto 2)
    // ---------------------------------------------------------------

    /**
     * Torneos, inscripciones, posiciones, encuentros y premios. Las posiciones se asignan después de
     * cargar las inscripciones porque guardan el id de la inscripción.
     */
    private void cargarTorneos() throws Exception {
        List<Torneo> torneos = new ArrayList<>();
        List<String> posicionesDeCadaTorneo = new ArrayList<>();
        for (String[] c : leer("torneos.txt")) {
            String nombre = c[1];
            TipoTorneo tipo = TipoTorneo.valueOf(c[2]);
            int cupos = entero(c[3]);
            double tarifa = decimal(c[4]);
            double bono = decimal(c[5]);
            LocalDate inicio = LocalDate.parse(c[6]);
            LocalDate limitePreferenciales = LocalDate.parse(c[7]);

            Torneo torneo;
            if (c[0].equals("INDIVIDUAL")) {
                torneo = new TorneoIndividual(nombre, tipo, Disciplina.valueOf(c[10]), Nivel.valueOf(c[11]), booleano(c[12]),
                        cupos, tarifa, bono, inicio, limitePreferenciales);
            } else {
                torneo = new TorneoConjunto(nombre, tipo, Deporte.valueOf(c[10]), Categoria.valueOf(c[11]),
                        cupos, tarifa, bono, inicio, limitePreferenciales);
            }
            torneo.setEstado(EstadoTorneo.valueOf(c[8]));
            torneos.add(torneo);
            posicionesDeCadaTorneo.add(c[9]);
            club.getTorneos().add(torneo);
        }

        for (String[] c : leer("inscripciones_torneo.txt")) {
            Torneo torneo = club.buscarTorneo(c[0]);
            int id = entero(c[1]);
            LocalDate fecha = LocalDate.parse(c[2]);
            double ranking = decimal(c[5]);
            InscripcionTorneo inscripcion;
            if (c[6].equals("EQUIPO")) {
                inscripcion = new InscripcionTorneoEquipo(id, fecha, ranking, club.buscarEquipo(c[7]));
            } else {
                List<Usuario> jugadores = new ArrayList<>();
                for (String idJugador : lista(c[7])) {
                    jugadores.add(club.buscarUsuario(idJugador));
                }
                inscripcion = new InscripcionTorneoIndividual(id, fecha, ranking, jugadores);
            }
            inscripcion.setTipoCupo(TipoCupo.valueOf(c[3]));
            inscripcion.setTarifaPagada(decimal(c[4]));
            torneo.agregarInscripcionCargada(inscripcion);
        }

        for (int i = 0; i < torneos.size(); i++) {
            Torneo torneo = torneos.get(i);
            for (String idInscripcion : lista(posicionesDeCadaTorneo.get(i))) {
                torneo.agregarPosicionCargada(torneo.buscarInscripcion(entero(idInscripcion)));
            }
        }

        for (String[] c : leer("encuentros_torneo.txt")) {
            Torneo torneo = club.buscarTorneo(c[0]);
            InscripcionTorneo ladoA = torneo.buscarInscripcion(entero(c[3]));
            InscripcionTorneo ladoB = torneo.buscarInscripcion(entero(c[4]));
            torneo.agregarEncuentroCargado(new EncuentroTorneo(entero(c[1]), texto(c[2]), ladoA, ladoB, entero(c[5]), entero(c[6])));
        }

        for (String[] c : leer("premios_torneo.txt")) {
            Torneo torneo = club.buscarTorneo(c[0]);
            InscripcionTorneo ganador = torneo.buscarInscripcion(entero(c[2]));
            Premio premio = new Premio(entero(c[1]), ganador, decimal(c[3]));
            // Cada código de bono se busca entre los integrantes de la inscripción ganadora.
            for (String codigo : lista(c[4])) {
                for (Usuario integrante : ganador.getIntegrantes()) {
                    BonoDescuento bono = integrante.buscarBono(codigo);
                    if (bono != null) {
                        premio.agregarBono(bono);
                    }
                }
            }
            torneo.agregarPremioCargado(premio);
        }
    }
}
