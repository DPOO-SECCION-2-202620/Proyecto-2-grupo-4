package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Herramientas para leer y escribir los archivos de texto del club.
 *
 * Formato de todos los archivos:
 * - la primera línea es un comentario que empieza por '#' y explica los campos;
 * - después va un registro por línea, con los campos separados por ';';
 * - si un campo es una lista, sus elementos se separan con '|';
 * - un campo vacío se escribe como "-".
 */
public class ArchivoTexto {

    public static final String SEPARADOR = ";";
    public static final String SEPARADOR_LISTA = "|";
    public static final String VACIO = "-";

    /** Escribe un archivo: primero el comentario con el formato y luego una línea por registro. */
    public static void escribir(File archivo, String formato, List<String> lineas) throws IOException {
        BufferedWriter escritor = Files.newBufferedWriter(archivo.toPath(), StandardCharsets.UTF_8);
        try {
            escritor.write("# " + formato);
            escritor.newLine();
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        } finally {
            escritor.close();
        }
    }

    /**
     * Lee un archivo y devuelve sus registros, cada uno como un arreglo de campos.
     * Se saltan las líneas vacías y los comentarios. Si el archivo no existe, devuelve una lista vacía.
     */
    public static List<String[]> leer(File archivo) throws IOException {
        List<String[]> registros = new ArrayList<>();
        if (!archivo.exists()) {
            return registros;
        }
        BufferedReader lector = Files.newBufferedReader(archivo.toPath(), StandardCharsets.UTF_8);
        try {
            String linea = lector.readLine();
            while (linea != null) {
                boolean esComentario = linea.startsWith("#");
                boolean estaVacia = linea.trim().isEmpty();
                if (!esComentario && !estaVacia) {
                    // El -1 hace que se conserven los campos vacíos del final.
                    registros.add(linea.split(SEPARADOR, -1));
                }
                linea = lector.readLine();
            }
        } finally {
            lector.close();
        }
        return registros;
    }

    /** Limpia un texto para que no dañe el formato: cambia ';' por ',', '|' por '/' y los saltos de línea por espacios. */
    public static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEPARADOR, ",").replace(SEPARADOR_LISTA, "/").replace("\n", " ").replace("\r", " ");
    }

    /** Une los elementos de una lista con '|'. Si la lista está vacía devuelve "-". */
    public static String unirLista(List<String> lista) {
        if (lista.isEmpty()) {
            return VACIO;
        }
        String resultado = "";
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) {
                resultado = resultado + SEPARADOR_LISTA;
            }
            resultado = resultado + limpiar(lista.get(i));
        }
        return resultado;
    }

    /** Separa un campo de lista. Si el campo es "-" o está vacío devuelve una lista vacía. */
    public static List<String> separarLista(String campo) {
        List<String> lista = new ArrayList<>();
        if (campo == null || campo.trim().isEmpty() || campo.equals(VACIO)) {
            return lista;
        }
        // Se escribe "\\|" porque split recibe una expresión regular y '|' tiene un significado especial.
        String[] partes = campo.split("\\|");
        for (String parte : partes) {
            lista.add(parte);
        }
        return lista;
    }

    /** Al guardar: un texto vacío (o null) se escribe como "-". */
    public static String marcarVacio(String texto) {
        if (texto == null || texto.isEmpty()) {
            return VACIO;
        }
        return texto;
    }

    /** Al cargar: un "-" vuelve a ser un texto vacío. */
    public static String quitarMarcaVacio(String texto) {
        if (texto.equals(VACIO)) {
            return "";
        }
        return texto;
    }
}
