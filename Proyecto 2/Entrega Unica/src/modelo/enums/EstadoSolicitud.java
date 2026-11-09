package modelo.enums; // Paquete de enumeraciones.

/** Estado de una solicitud de cambio de turno. */
public enum EstadoSolicitud { // Enumeración de estados de solicitud.
    PENDIENTE, // Aún no se ha revisado.
    APROBADA,  // El administrador la aprobó.
    RECHAZADA  // El administrador la rechazó (violaba la regla de cobertura).
}
