package modelo.enums; // Paquete de enumeraciones.

/** Estado de una sugerencia de nuevo artículo o snack hecha por un empleado. */
public enum EstadoSugerencia { // Enumeración de estados de sugerencia.
    PENDIENTE, // Aún no se ha revisado.
    APROBADA,  // Aprobada: el producto se crea en el catálogo.
    RECHAZADA  // Rechazada.
}
