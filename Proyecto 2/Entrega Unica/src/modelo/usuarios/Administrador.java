package modelo.usuarios;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.academias.InscripcionDisciplina;
import modelo.academias.PartidoPractica;
import modelo.conjunto.Equipo;
import modelo.conjunto.Partido;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.EstadoSolicitud;
import modelo.reportes.ReporteDeportivo;
import modelo.reportes.ReporteVentas;
import modelo.ventas.Articulo;
import modelo.ventas.Venta;

public class Administrador extends Usuario {

    public static final int MIN_ENTRENADORES = 2;
    public static final int MIN_FISIOTERAPEUTAS = 1;
    public static final int PASO_MINUTOS = 15;

    // Crea un administrador
    public Administrador(String id, String nombre, String login, String password) {
        super(id, nombre, login, password);
    }

    // El administrador no puede reservar ni inscribirse a partidos
    @Override
    public boolean puedeUsarInstalacionesYPartidos() {
        return false;
    }

    // Crea un equipo
    public Equipo crearEquipo(String nombre, Deporte deporte, Categoria categoria, int cupoMaximo)
            throws ReglaNegocioException {

        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El equipo debe tener nombre.");
        }

        if (cupoMaximo < deporte.getMinimoJugadores()) {
            throw new ReglaNegocioException("El cupo máximo (" + cupoMaximo
                    + ") no alcanza el mínimo de " + deporte.getMinimoJugadores()
                    + " jugadores para jugar " + deporte + ".");
        }

        return new Equipo(nombre, deporte, categoria, cupoMaximo);
    }

    // Da de baja a un jugador
    public void darDeBajaJugador(Equipo equipo, Socio jugador) throws ReglaNegocioException {
        equipo.darDeBaja(jugador);
    }

    // Mueve articulos entre ubicaciones
    public void moverArticulo(Articulo articulo, String origen, String destino, int cantidad)
            throws ReglaNegocioException {

        if (origen.equalsIgnoreCase(destino)) {
            throw new ReglaNegocioException("El origen y el destino deben ser distintos.");
        }

        articulo.retirarInventario(origen, cantidad);
        articulo.agregarInventario(destino, cantidad);
    }

    // Agrega nuevas unidades al inventario
    public void reabastecer(Articulo articulo, String ubicacion, int cantidad)
            throws ReglaNegocioException {
        articulo.agregarInventario(ubicacion, cantidad);
    }

    // Revisa si se puede aprobar un cambio de turno
    public boolean aprobarCambioTurno(SolicitudCambioTurno solicitud, List<Empleado> empleados,
            List<String> diasAtencion, LocalTime apertura, LocalTime cierre)
            throws ReglaNegocioException {

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("La solicitud ya fue revisada.");
        }

        Empleado solicitante = solicitud.getEmpleado();
        Turno actual = buscarTurno(solicitante, solicitud.getTurnoActual());

        if (actual == null) {
            throw new ReglaNegocioException("El empleado no tiene ese turno.");
        }

        List<Turno> turnosSimulados = new ArrayList<>(solicitante.getTurnos());
        turnosSimulados.remove(actual);
        turnosSimulados.add(solicitud.getTurnoPropuesto());

        for (String dia : diasAtencion) {
            for (LocalTime t = apertura; t.isBefore(cierre);
                    t = t.plus(Duration.ofMinutes(PASO_MINUTOS))) {

                int entrenadores = 0;
                int fisios = 0;

                for (Empleado e : empleados) {
                    boolean disponible;

                    if (e == solicitante) {
                        disponible = cubre(turnosSimulados, dia, t);
                    } else {
                        disponible = e.estaDisponible(dia, t);
                    }

                    if (disponible && e instanceof Entrenador) {
                        entrenadores++;
                    }

                    if (disponible && e instanceof Fisioterapeuta) {
                        fisios++;
                    }
                }

                if (entrenadores < MIN_ENTRENADORES || fisios < MIN_FISIOTERAPEUTAS) {
                    solicitud.rechazar();
                    return false;
                }
            }
        }

        solicitante.getTurnos().remove(actual);
        solicitante.getTurnos().add(solicitud.getTurnoPropuesto());
        solicitud.aprobar();

        return true;
    }

    // Revisa si algun turno cubre el dia y la hora
    private static boolean cubre(List<Turno> turnos, String dia, LocalTime hora) {
        for (Turno t : turnos) {
            if (t.cubre(dia, hora)) {
                return true;
            }
        }
        return false;
    }

    // Busca un turno por su horario
    private static Turno buscarTurno(Empleado e, Turno buscado) {
        for (Turno t : e.getTurnos()) {
            if (t.mismoHorario(buscado)) {
                return t;
            }
        }
        return null;
    }

    // Genera un reporte de ventas
    public ReporteVentas generarReporteVentas(List<Venta> ventas, String rubro,
            LocalDate inicio, LocalDate fin) {

        ReporteVentas r = new ReporteVentas(inicio, fin, rubro, ventas);
        r.generar();
        return r;
    }

    // Genera un reporte deportivo
    public ReporteDeportivo generarReporteDeportivo(String tipo, LocalDate inicio, LocalDate fin,
            List<SesionEntrenamiento> sesiones, List<Partido> partidos,
            List<PartidoPractica> practicas, List<InscripcionDisciplina> inscripciones) {

        ReporteDeportivo r = new ReporteDeportivo(
                tipo, inicio, fin, sesiones, partidos, practicas, inscripciones);

        r.generar();
        return r;
    }

    // Devuelve el tipo de usuario
    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}
