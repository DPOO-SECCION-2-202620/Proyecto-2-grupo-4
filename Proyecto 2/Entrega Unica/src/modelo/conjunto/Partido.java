
package modelo.conjunto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.enums.Deporte;
import modelo.usuarios.Socio;

public abstract class Partido {

    private int id;
    private LocalDate fecha;
    private Deporte deporte;
    private int marcadorA;
    private int marcadorB;
    private boolean jugado;
    private List<EstadisticaPartido> estadisticas;

    // Crea un partido
    public Partido(int id, LocalDate fecha, Deporte deporte) {
        this.id = id;
        this.fecha = fecha;
        this.deporte = deporte;
        this.jugado = false;
        this.estadisticas = new ArrayList<>();
    }

    // Metodos que implementan las subclases
    public abstract boolean puedeJugarse();

    protected abstract boolean jugadorPuedeParticipar(Socio jugador);

    protected abstract void alFinalizar();

    public abstract String getNombreA();

    public abstract String getNombreB();

    public abstract String getTipo();

    // Registra las estadisticas de un jugador
    public void registrarEstadisticas(EstadisticaPartido e)
            throws ReglaNegocioException {

        if (jugado) {
            throw new ReglaNegocioException(
                    "El partido " + id + " ya fue cerrado; no se pueden agregar estadísticas.");
        }

        if (e.getDeporte() != deporte) {
            throw new ReglaNegocioException(
                    "La estadística es de " + e.getDeporte()
                    + " pero el partido es de " + deporte + ".");
        }

        if (!jugadorPuedeParticipar(e.getJugador())) {
            throw new ReglaNegocioException(
                    e.getJugador().getNombre()
                    + " no está habilitado para jugar este partido.");
        }

        if (buscarEstadistica(e.getJugador()) != null) {
            throw new ReglaNegocioException(
                    "Ya hay estadísticas de " + e.getJugador().getNombre()
                    + " en este partido.");
        }

        estadisticas.add(e);
    }

    // Agrega estadisticas cargadas desde un archivo
    public void agregarEstadisticaCargada(EstadisticaPartido e) {
        estadisticas.add(e);
    }

    // Registra el resultado final
    public void registrarResultado(int marcadorA, int marcadorB)
            throws ReglaNegocioException {

        if (jugado) {
            throw new ReglaNegocioException(
                    "El partido " + id + " ya tiene resultado.");
        }

        if (!puedeJugarse()) {
            throw new ReglaNegocioException(
                    "El partido " + id
                    + " no puede jugarse: algún equipo no tiene el mínimo de jugadores habilitados.");
        }

        if (marcadorA < 0 || marcadorB < 0) {
            throw new ReglaNegocioException(
                    "El marcador no puede ser negativo.");
        }

        if (marcadorA == marcadorB && !deporte.permiteEmpate()) {
            throw new ReglaNegocioException(
                    "En " + deporte + " no existen los empates.");
        }

        this.marcadorA = marcadorA;
        this.marcadorB = marcadorB;
        this.jugado = true;

        alFinalizar();
    }

    // Carga un resultado sin validarlo
    public void fijarResultadoCargado(int a, int b, boolean jugado) {
        this.marcadorA = a;
        this.marcadorB = b;
        this.jugado = jugado;
    }

    // Busca las estadisticas de un jugador
    public EstadisticaPartido buscarEstadistica(Socio jugador) {
        for (EstadisticaPartido e : estadisticas) {
            if (e.getJugador() == jugador) {
                return e;
            }
        }
        return null;
    }

    // Devuelve el marcador del partido
    public String getMarcador() {
        return jugado ? marcadorA + "-" + marcadorB : "sin jugar";
    }

    // Getters
    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public int getMarcadorA() {
        return marcadorA;
    }

    public int getMarcadorB() {
        return marcadorB;
    }

    public boolean isJugado() {
        return jugado;
    }

    public List<EstadisticaPartido> getEstadisticas() {
        return estadisticas;
    }

    // Muestra la informacion del partido
    @Override
    public String toString() {
        return "[" + getTipo() + " #" + id + " " + fecha + " " + deporte
                + "] " + getNombreA() + " " + getMarcador() + " " + getNombreB();
    }
}
