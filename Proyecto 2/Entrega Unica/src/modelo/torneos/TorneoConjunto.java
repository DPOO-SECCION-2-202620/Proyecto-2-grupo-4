package modelo.torneos;

import java.time.LocalDate;
import excepciones.ReglaNegocioException;
import modelo.conjunto.Equipo;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.TipoTorneo;

/** Torneo de un deporte de conjunto: se inscriben equipos completos del club. */
public class TorneoConjunto extends Torneo {

    private Deporte deporte;     // Deporte del torneo.
    private Categoria categoria; // Categoría de edad.

    public TorneoConjunto(String nombre, TipoTorneo tipo, Deporte deporte, Categoria categoria, int cupos, double tarifa,
                          double porcentajeBono, LocalDate fechaInicio, LocalDate fechaCierrePreferencial) {
        super(nombre, tipo, cupos, tarifa, porcentajeBono, fechaInicio, fechaCierrePreferencial);
        this.deporte = deporte;
        this.categoria = categoria;
    }

    @Override
    protected void validarDatosEspecificos() {
        // Los torneos de conjunto no tienen reglas extra de creación.
    }

    /** Inscripción de un equipo (null si no está inscrito). */
    public InscripcionTorneoEquipo buscarInscripcionDeEquipo(Equipo equipo) {
        for (InscripcionTorneo i : getInscripciones()) {
            if (i instanceof InscripcionTorneoEquipo && ((InscripcionTorneoEquipo) i).getEquipo() == equipo) {
                return (InscripcionTorneoEquipo) i;
            }
        }
        return null;
    }

    /**
     * Inscribe un equipo completo (HU-TOR-SOC-01 CA-2). ClubDeportivo ya validó que quien lo inscribe es del equipo.
     * @param ranking  puntos del equipo en partidos internos (ranking de equipos).
     * @param elegible true si está entre los mejores de la categoría (cupo preferencial).
     */
    public InscripcionTorneoEquipo inscribirEquipo(Equipo equipo, double ranking, boolean elegible, LocalDate hoy)
            throws ReglaNegocioException {
    	
    	// Verificar que el deporte del equipo coincida con el torneo.
        if (equipo.getDeporte() != this.deporte) {
            throw new ReglaNegocioException(
                "El deporte de " + equipo.getNombre() + " (" + equipo.getDeporte()
                + ") no coincide con el del torneo (" + this.deporte + ")."
            );
        }

        // Verificar que la categoría del equipo coincida con el torneo.
        if (equipo.getCategoria() != this.categoria) {
            throw new ReglaNegocioException(
                "La categoría de " + equipo.getNombre() + " (" + equipo.getCategoria()
                + ") no coincide con la del torneo (" + this.categoria + ")."
            );
        }

        // Verificar que el equipo no esté inscrito anteriormente.
        if (this.buscarInscripcionDeEquipo(equipo) != null) {
            throw new ReglaNegocioException(
                equipo.getNombre() + " ya está inscrito en " + this.getNombre() + "."
            );
        }

        // Crear y registrar la inscripción del equipo.
        InscripcionTorneoEquipo inscripcion = new InscripcionTorneoEquipo(
            this.nuevoIdInscripcion(),
            hoy,
            ranking,
            equipo
        );

        this.registrarInscripcion(inscripcion, elegible, hoy);

        return inscripcion;
        }

    @Override
    public boolean esIndividual() { return false; }

    @Override
    public String getDescripcion() { return deporte + " " + categoria; }

    public Deporte getDeporte() { return deporte; }
    public Categoria getCategoria() { return categoria; }
}
