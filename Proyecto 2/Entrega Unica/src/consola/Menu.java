package consola;

import java.util.ArrayList;
import java.util.List;

/**
 * Un menú de la consola: tiene un título, una lista de opciones y una opción 0 para salir o volver.
 *
 * Cada opción tiene un código (por ejemplo "CREAR_TORNEO") y un texto que ve el usuario.
 * La consola pregunta la opción con {@link #pedirOpcion(LectorEntrada)} y recibe el código elegido,
 * así puede atenderla con un switch sin depender del número que tenga en la pantalla.
 * Esto permite agregar opciones solo para algunos usuarios sin desordenar los números.
 */
public class Menu {

    /** Código que se devuelve cuando el usuario elige 0. */
    public static final String SALIR = "SALIR";

    private String titulo;
    private String textoSalir;
    private List<String> codigos;
    private List<String> textos;

    /**
     * @param titulo     título que se muestra arriba del menú.
     * @param textoSalir texto de la opción 0 (por ejemplo "Volver" o "Guardar y salir").
     */
    public Menu(String titulo, String textoSalir) {
        this.titulo = titulo;
        this.textoSalir = textoSalir;
        this.codigos = new ArrayList<>();
        this.textos = new ArrayList<>();
    }

    /** Agrega una opción al final del menú. */
    public void agregarOpcion(String codigo, String texto) {
        codigos.add(codigo);
        textos.add(texto);
    }

    /** Muestra el menú y devuelve el código de la opción elegida (o {@link #SALIR} si eligió 0). */
    public String pedirOpcion(LectorEntrada lector) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
        for (int i = 0; i < textos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + textos.get(i));
        }
        System.out.println("  0. " + textoSalir);
        int numero = lector.leerEntero("Opción", 0, textos.size());
        if (numero == 0) {
            return SALIR;
        }
        return codigos.get(numero - 1);
    }
}
