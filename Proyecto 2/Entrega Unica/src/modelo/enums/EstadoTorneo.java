package modelo.enums; // Paquete de enumeraciones.

/** Estado de un torneo interno. */
public enum EstadoTorneo {
    POR_INICIAR, // Inscripciones abiertas: se puede inscribir y salir.
    EN_CURSO,    // Inscripciones cerradas: se registran encuentros.
    FINALIZADO   // El administrador registró las posiciones: hay premios.
}
