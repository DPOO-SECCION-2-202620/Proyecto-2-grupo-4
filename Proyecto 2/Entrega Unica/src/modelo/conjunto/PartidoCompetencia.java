package modelo.conjunto; // Paquete de deportes de conjunto.

import java.time.LocalDate;                          // Fecha.
import modelo.competencias.Competencia;              // Competencia a la que pertenece.
import modelo.competencias.Enfrentamiento;           // Resultado genérico para tablas.
import modelo.competencias.InscripcionCompetencia;   // Inscripción (equipo + nómina) en la competencia.
import modelo.usuarios.Socio;                        // Jugador.

/** Partido oficial de una competencia externa: equipo del club contra otra institución. */
public class PartidoCompetencia extends Partido { // Hereda de Partido.

    private InscripcionCompetencia inscripcion; // Inscripción del equipo del club (de ahí salen equipo y nómina).
    private String institucionRival;            // Nombre de la institución rival.
    private String escenario;                   // Escenario donde se juega.
    private String fase;                        // Fase (solo competencias por fases; "" en liga).
    private String grupo;                       // Grupo (solo fase de grupos; "" si no aplica).

    public PartidoCompetencia(int id, LocalDate fecha, InscripcionCompetencia inscripcion, String institucionRival,
                              String escenario, String fase, String grupo) { // Constructor.
        super(id, fecha, inscripcion.getCompetencia().getDeporte()); // El deporte es el de la competencia.
        this.inscripcion = inscripcion;           // Guarda la inscripción.
        this.institucionRival = institucionRival; // Guarda el rival.
        this.escenario = escenario;               // Guarda el escenario.
        this.fase = fase == null ? "" : fase;     // Evita null: usa cadena vacía.
        this.grupo = grupo == null ? "" : grupo;  // Evita null.
    }

    /** Solo se juega si el equipo tiene el mínimo de habilitados DENTRO DE SU NÓMINA. */
    @Override // Implementa el abstracto.
    public boolean puedeJugarse() { return inscripcion.tieneMinimoHabilitados(); } // Delega en la inscripción.

    /** Solo pueden jugar los de la nómina que no estén suspendidos en la competencia. */
    @Override // Implementa el abstracto.
    protected boolean jugadorPuedeParticipar(Socio jugador) { return inscripcion.estaHabilitado(jugador); } // Delega.

    /** Al cerrar, se acumulan amarillas y se calculan suspensiones dentro de la competencia. */
    @Override // Implementa el abstracto.
    protected void alFinalizar() { inscripcion.actualizarSanciones(getEstadisticas()); } // Delega.

    /** Convierte el partido en un Enfrentamiento genérico para construir tablas de posiciones. */
    public Enfrentamiento comoEnfrentamiento() { // Sin parámetros.
        return new Enfrentamiento(getNombreA(), getNombreB(), getMarcadorA(), getMarcadorB(), fase, grupo); // Crea el objeto.
    }

    @Override public String getNombreA() { return inscripcion.getEquipo().getNombre(); } // El equipo del club.
    @Override public String getNombreB() { return institucionRival; }                    // El rival.
    @Override public String getTipo() { return "COMPETENCIA"; }                          // Tipo.

    public Competencia getCompetencia() { return inscripcion.getCompetencia(); } // Devuelve la competencia.
    public InscripcionCompetencia getInscripcion() { return inscripcion; }      // Devuelve la inscripción.
    public Equipo getEquipo() { return inscripcion.getEquipo(); }               // Devuelve el equipo del club.
    public String getInstitucionRival() { return institucionRival; }            // Devuelve el rival.
    public String getEscenario() { return escenario; }                          // Devuelve el escenario.
    public String getFase() { return fase; }                                    // Devuelve la fase.
    public String getGrupo() { return grupo; }                                  // Devuelve el grupo.
}
