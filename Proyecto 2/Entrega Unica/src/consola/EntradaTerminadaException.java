package consola;

/**
 * Se lanza cuando ya no hay más texto para leer en la consola (por ejemplo, si el usuario
 * presiona Ctrl+D o Ctrl+Z). No es un error del negocio: la consola la usa para cerrar el
 * programa de forma ordenada y guardar los cambios.
 */
public class EntradaTerminadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntradaTerminadaException() {
        super("Se terminó la entrada de datos.");
    }
}
