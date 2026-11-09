package modelo.torneos;

import java.time.LocalDate;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.enums.TipoTorneo;
import modelo.usuarios.Usuario;

/** Torneo de una disciplina individual (tenis, pádel o tenis de mesa), en sencillos o dobles. */
public class TorneoIndividual extends Torneo {

    private Disciplina disciplina; // Disciplina del torneo.
    private Nivel nivel;           // Nivel requerido a los participantes.
    private boolean dobles;        // true = se inscriben parejas.

    public TorneoIndividual(String nombre, TipoTorneo tipo, Disciplina disciplina, Nivel nivel, boolean dobles, int cupos,
                            double tarifa, double porcentajeBono, LocalDate fechaInicio, LocalDate fechaCierrePreferencial) {
        super(nombre, tipo, cupos, tarifa, porcentajeBono, fechaInicio, fechaCierrePreferencial);
        this.disciplina = disciplina;
        this.nivel = nivel;
        this.dobles = dobles;
    }

    /** Jugadores por inscripción: 2 en dobles, 1 en sencillos. */
    public int getJugadoresPorInscripcion() {
        return dobles ? 2 : 1;
    }

    @Override
    protected void validarDatosEspecificos() throws ReglaNegocioException {
        if (this.disciplina == Disciplina.PADEL && !this.dobles) {
           throw new ReglaNegocioException("El pádel siempre se juega en dobles: el torneo debe ser de dobles.");
        }
     }

    /**
     * Inscribe a un jugador o a una pareja (HU-TOR-SOC-01 y HU-TOR-EMP-01). ClubDeportivo ya validó el nivel de cada
     * jugador y si un empleado está de turno; aquí se valida lo que depende solo del torneo.
     * @param jugadores 1 usuario (sencillos) o 2 usuarios (dobles).
     * @param ranking   ranking de la inscripción (en dobles, el promedio de la pareja; -1 si no tiene).
     * @param elegible  true si está entre los mejores del ranking (cupo preferencial).
     */
    public InscripcionTorneoIndividual inscribir(List<Usuario> jugadores, double ranking, boolean elegible, LocalDate hoy)
            throws ReglaNegocioException {
    	// Verificar la cantidad de jugadores.
        if (jugadores.size() != this.getJugadoresPorInscripcion()) {

            if (this.dobles) {
                throw new ReglaNegocioException(
                    "En dobles se inscribe la pareja completa (2 jugadores)."
                );
            } else {
                throw new ReglaNegocioException(
                    "En sencillos se inscribe 1 jugador."
                );
            }
        }

        // Verificar que los jugadores de dobles sean personas diferentes.
        if (this.dobles && jugadores.get(0) == jugadores.get(1)) {
            throw new ReglaNegocioException("La pareja debe ser otra persona.");
        }

        // Verificar que ningún jugador esté inscrito anteriormente.
        for (Usuario jugador : jugadores) {

            if (this.buscarInscripcion(jugador) != null) {
                throw new ReglaNegocioException(
                    jugador.getNombre() + " ya está inscrito en " + this.getNombre() + "."
                );
            }
        }

        // Crear y registrar la inscripción.
        InscripcionTorneoIndividual inscripcion =
            new InscripcionTorneoIndividual(
                this.nuevoIdInscripcion(),
                hoy,
                ranking,
                jugadores
            );

        this.registrarInscripcion(inscripcion, elegible, hoy);

        return inscripcion;
    }

    @Override
    public boolean esIndividual() { return true; }

    @Override
    public String getDescripcion() {
        return disciplina + " " + nivel + (dobles ? " dobles" : " sencillos");
    }

    public Disciplina getDisciplina() { return disciplina; }
    public Nivel getNivel() { return nivel; }
    public boolean isDobles() { return dobles; }
}
