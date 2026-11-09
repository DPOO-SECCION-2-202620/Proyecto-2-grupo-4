package modelo.entrenamiento; // Paquete de entrenamiento y turnos.

import modelo.enums.EstadoSolicitud; // Estado de la solicitud.
import modelo.usuarios.Empleado;     // Empleado solicitante.

/** Solicitud de un empleado para cambiar uno de sus turnos por otro. */
public class SolicitudCambioTurno { // Clase SolicitudCambioTurno.

    private int id;                 // Identificador.
    private Empleado empleado;      // Quién solicita.
    private Turno turnoActual;      // Turno que quiere dejar.
    private Turno turnoPropuesto;   // Turno que propone.
    private EstadoSolicitud estado; // PENDIENTE, APROBADA o RECHAZADA.

    public SolicitudCambioTurno(int id, Empleado empleado, Turno turnoActual, Turno turnoPropuesto) { // Constructor.
        this.id = id;                         // Guarda el id.
        this.empleado = empleado;             // Guarda el empleado.
        this.turnoActual = turnoActual;       // Guarda el turno actual.
        this.turnoPropuesto = turnoPropuesto; // Guarda el propuesto.
        this.estado = EstadoSolicitud.PENDIENTE; // Toda solicitud nace pendiente.
    }

    public void aprobar() { estado = EstadoSolicitud.APROBADA; }   // Cambia a aprobada.
    public void rechazar() { estado = EstadoSolicitud.RECHAZADA; } // Cambia a rechazada.

    public int getId() { return id; }                         // Getter.
    public Empleado getEmpleado() { return empleado; }        // Getter.
    public Turno getTurnoActual() { return turnoActual; }     // Getter.
    public Turno getTurnoPropuesto() { return turnoPropuesto; } // Getter.
    public EstadoSolicitud getEstado() { return estado; }     // Getter.
    public void setEstado(EstadoSolicitud e) { this.estado = e; } // Setter (persistencia).

    @Override // Texto.
    public String toString() { return "Solicitud #" + id + " de " + empleado.getNombre() + ": " + turnoActual + " -> " + turnoPropuesto + " [" + estado + "]"; }
}
