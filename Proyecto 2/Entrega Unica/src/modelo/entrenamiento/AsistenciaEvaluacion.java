package modelo.entrenamiento; // Paquete de entrenamiento y turnos.

import modelo.enums.EstadoAsistencia; // Estado de asistencia.
import modelo.usuarios.Socio;         // Jugador.

/** Asistencia de un jugador convocado a una sesión, con evaluación opcional (nota 1-5 y comentario). */
public class AsistenciaEvaluacion { // Clase AsistenciaEvaluacion.

    private Socio jugador;           // Jugador evaluado.
    private EstadoAsistencia estado; // ASISTIO, FALTO o TARDE.
    private int nota;                // Nota de 1 a 5; 0 significa "sin evaluación".
    private String comentario;       // Comentario breve ("" si no hay).

    public AsistenciaEvaluacion(Socio jugador, EstadoAsistencia estado, int nota, String comentario) { // Constructor.
        this.jugador = jugador;   // Guarda el jugador.
        this.estado = estado;     // Guarda el estado.
        this.nota = nota;         // Guarda la nota.
        this.comentario = comentario == null ? "" : comentario; // Evita null.
    }

    /** Indica si se registró una evaluación de desempeño. */
    public boolean tieneEvaluacion() { return nota >= 1; } // Nota 0 = sin evaluación.

    public Socio getJugador() { return jugador; }           // Getter.
    public EstadoAsistencia getEstado() { return estado; }  // Getter.
    public int getNota() { return nota; }                   // Getter.
    public String getComentario() { return comentario; }    // Getter.

    @Override // Texto.
    public String toString() { // Ej: "Ana: ASISTIO, nota 4 (Buena actitud)".
        return jugador.getNombre() + ": " + estado + (tieneEvaluacion() ? ", nota " + nota + (comentario.isEmpty() ? "" : " (" + comentario + ")") : "");
    }
}
