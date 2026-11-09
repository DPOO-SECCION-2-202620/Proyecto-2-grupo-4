package excepciones; // Paquete donde viven las excepciones propias del sistema.

/**
 * Excepción que se lanza cuando una operación viola una regla del negocio del
 * Club Deportivo Séneca (por ejemplo: cupo lleno, jugador suspendido, reserva sin cupo).
 * Es "checked" (extiende Exception) para obligar a quien llama a manejarla.
 */
public class ReglaNegocioException extends Exception { // Hereda de Exception: el compilador obliga a capturarla o declararla.

    private static final long serialVersionUID = 1L; // Identificador de versión requerido por Exception (que es Serializable).

    public ReglaNegocioException(String mensaje) { // Constructor que recibe el mensaje que explica qué regla se violó.
        super(mensaje); // Le pasa el mensaje a la clase padre, que lo guarda y lo devuelve con getMessage().
    }
}
