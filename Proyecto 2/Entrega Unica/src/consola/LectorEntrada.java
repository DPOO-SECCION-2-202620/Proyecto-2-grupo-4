package consola;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase lee todo lo que el usuario escribe en la consola y revisa que sea válido.
 *
 * Cada método vuelve a preguntar hasta que el usuario escriba un valor correcto. Así las
 * consolas nunca reciben datos con un formato equivocado o fuera de rango, y no tienen que
 * repetir las mismas validaciones en todas partes.
 *
 * Reglas para todos los textos:
 * - se quitan los espacios del principio y del final;
 * - no se aceptan los caracteres ';' ni '|' porque son los separadores de los archivos;
 * - tienen una longitud máxima.
 */
public class LectorEntrada {

    /** Cantidad máxima de caracteres de un texto. */
    public static final int LONGITUD_MAXIMA = 80;

    private Scanner teclado;

    /** Crea un lector que lee del teclado. */
    public LectorEntrada() {
        teclado = new Scanner(System.in, "UTF-8");
    }

    /** Crea un lector que lee de otra fuente (sirve para probar las consolas con entradas escritas antes). */
    public LectorEntrada(InputStream fuente) {
        teclado = new Scanner(fuente, "UTF-8");
    }

    // ---------------------------------------------------------------
    //  Lectura básica
    // ---------------------------------------------------------------

    /** Muestra la pregunta y devuelve la línea escrita, sin espacios a los lados. */
    private String leerLinea(String pregunta) {
        System.out.print(pregunta);
        if (!teclado.hasNextLine()) {
            throw new EntradaTerminadaException();
        }
        return teclado.nextLine().trim();
    }

    /** Revisa las reglas comunes de un texto. Devuelve el problema encontrado o null si está bien. */
    private String revisarTexto(String texto) {
        if (texto.contains(";") || texto.contains("|")) {
            return "No use los caracteres ';' ni '|'.";
        }
        if (texto.length() > LONGITUD_MAXIMA) {
            return "Escriba máximo " + LONGITUD_MAXIMA + " caracteres.";
        }
        return null;
    }

    // ---------------------------------------------------------------
    //  Textos
    // ---------------------------------------------------------------

    /** Lee un texto que no puede quedar vacío. */
    public String leerTexto(String pregunta) {
        while (true) {
            String texto = leerLinea(pregunta + ": ");
            String problema;
            if (texto.isEmpty()) {
                problema = "Este dato no puede quedar vacío.";
            } else {
                problema = revisarTexto(texto);
            }
            if (problema == null) {
                return texto;
            }
            System.out.println("  " + problema);
        }
    }

    /** Lee un texto que puede quedar vacío (devuelve "" si el usuario solo presiona Enter). */
    public String leerTextoOpcional(String pregunta) {
        while (true) {
            String texto = leerLinea(pregunta + " (Enter para dejarlo vacío): ");
            String problema = revisarTexto(texto);
            if (problema == null) {
                return texto;
            }
            System.out.println("  " + problema);
        }
    }

    /** Lee un nombre de usuario: obligatorio y sin espacios. */
    public String leerNombreUsuario(String pregunta) {
        while (true) {
            String texto = leerTexto(pregunta);
            if (!texto.contains(" ")) {
                return texto;
            }
            System.out.println("  No use espacios.");
        }
    }

    /** Lee una contraseña (obligatoria). */
    public String leerClave(String pregunta) {
        return leerTexto(pregunta);
    }

    // ---------------------------------------------------------------
    //  Números
    // ---------------------------------------------------------------

    /** Lee un número entero entre minimo y maximo (ambos incluidos). */
    public int leerEntero(String pregunta, int minimo, int maximo) {
        while (true) {
            String texto = leerLinea(pregunta + " [" + minimo + "-" + maximo + "]: ");
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
                System.out.println("  El número debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Escriba un número entero.");
            }
        }
    }

    /**
     * Lee un número con decimales entre minimo y maximo. Acepta coma o punto
     * (por ejemplo 12,5 o 12.5). Solo se aceptan dígitos, así que textos como
     * "NaN" o "Infinity" se rechazan.
     */
    public double leerDecimal(String pregunta, double minimo, double maximo) {
        while (true) {
            String texto = leerLinea(pregunta + ": ").replace(",", ".");
            if (!texto.matches("\\d+(\\.\\d+)?")) {
                System.out.println("  Escriba un número positivo, por ejemplo 40000 o 12.5");
                continue;
            }
            double numero = Double.parseDouble(texto);
            if (numero < minimo) {
                System.out.println("  El número debe ser mayor o igual a " + mostrarNumero(minimo) + ".");
            } else if (numero > maximo) {
                System.out.println("  El número debe ser menor o igual a " + mostrarNumero(maximo) + ".");
            } else {
                return numero;
            }
        }
    }

    /** Muestra un número sin el ".0" cuando no tiene decimales. */
    private String mostrarNumero(double numero) {
        if (numero == Math.floor(numero)) {
            return String.valueOf((long) numero);
        }
        return String.valueOf(numero);
    }

    // ---------------------------------------------------------------
    //  Fechas y horas
    // ---------------------------------------------------------------

    /** Lee una fecha con el formato aaaa-mm-dd. */
    public LocalDate leerFecha(String pregunta) {
        return leerFecha(pregunta, null, null);
    }

    /**
     * Lee una fecha con el formato aaaa-mm-dd que esté entre minima y maxima.
     * Cualquiera de los dos límites puede ser null (sin límite). Fechas que no
     * existen, como 2026-02-30, se rechazan.
     */
    public LocalDate leerFecha(String pregunta, LocalDate minima, LocalDate maxima) {
        while (true) {
            String texto = leerLinea(pregunta + " (aaaa-mm-dd): ");
            try {
                LocalDate fecha = LocalDate.parse(texto);
                if (minima != null && fecha.isBefore(minima)) {
                    System.out.println("  La fecha no puede ser anterior a " + minima + ".");
                } else if (maxima != null && fecha.isAfter(maxima)) {
                    System.out.println("  La fecha no puede ser posterior a " + maxima + ".");
                } else {
                    return fecha;
                }
            } catch (DateTimeParseException e) {
                System.out.println("  Fecha inválida. Ejemplo: 2026-11-21");
            }
        }
    }

    /** Lee una fecha y hora con el formato aaaa-mm-dd hh:mm. */
    public LocalDateTime leerFechaHora(String pregunta) {
        while (true) {
            String texto = leerLinea(pregunta + " (aaaa-mm-dd hh:mm): ");
            try {
                // LocalDateTime espera una 'T' entre la fecha y la hora.
                return LocalDateTime.parse(texto.replace(" ", "T"));
            } catch (DateTimeParseException e) {
                System.out.println("  Fecha y hora inválidas. Ejemplo: 2026-11-21 08:00");
            }
        }
    }

    /** Lee una hora en punto (por ejemplo 14 para las 2:00 p. m.) entre dos horas dadas. */
    public LocalTime leerHora(String pregunta, LocalTime minima, LocalTime maxima) {
        int hora = leerEntero(pregunta + " (hora en punto)", minima.getHour(), maxima.getHour());
        return LocalTime.of(hora, 0);
    }

    // ---------------------------------------------------------------
    //  Preguntas y listas de opciones
    // ---------------------------------------------------------------

    /** Hace una pregunta de sí o no. */
    public boolean leerSiNo(String pregunta) {
        while (true) {
            String texto = leerLinea(pregunta + " (s/n): ").toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            System.out.println("  Responda s o n.");
        }
    }

    /** Pide confirmación antes de hacer algo que no se puede deshacer. */
    public boolean confirmar(String accion) {
        return leerSiNo("  ¿Seguro que quiere " + accion + "?");
    }

    /**
     * Muestra una lista numerada y devuelve el elemento elegido.
     * Devuelve null si la lista está vacía o si el usuario escribe 0 para cancelar.
     */
    public <T> T elegir(String titulo, List<T> opciones) {
        if (opciones == null || opciones.isEmpty()) {
            System.out.println("  (no hay opciones para elegir)");
            return null;
        }
        System.out.println(titulo + ":");
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        int numero = leerEntero("Opción (0 para cancelar)", 0, opciones.size());
        if (numero == 0) {
            return null;
        }
        return opciones.get(numero - 1);
    }

    /**
     * Muestra un arreglo numerado (por ejemplo los valores de un enum) y obliga
     * a elegir uno. No permite cancelar.
     */
    public <T> T elegirObligatorio(String titulo, T[] opciones) {
        System.out.println(titulo + ":");
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opciones[i]);
        }
        int numero = leerEntero("Opción", 1, opciones.length);
        return opciones[numero - 1];
    }
}
