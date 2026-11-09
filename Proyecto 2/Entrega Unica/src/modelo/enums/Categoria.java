
package modelo.enums;

// Categorias de edad
public enum Categoria {

    SUB13(0, 12),
    SUB15(13, 14),
    SUB17(15, 16),
    MAYORES(17, Integer.MAX_VALUE);

    private final int edadMinima;
    private final int edadMaxima;

    // Guarda las edades de cada categoria
    Categoria(int edadMinima, int edadMaxima) {
        this.edadMinima = edadMinima;
        this.edadMaxima = edadMaxima;
    }

    // Verifica si la edad esta en el rango
    public boolean admiteEdad(int edad) {
        return edad >= edadMinima && edad <= edadMaxima;
    }

    // Busca la categoria segun la edad
    public static Categoria paraEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }

        for (Categoria c : values()) {
            if (c.admiteEdad(edad)) {
                return c;
            }
        }

        return MAYORES;
    }

    // Devuelve la edad minima
    public int getEdadMinima() {
        return edadMinima;
    }

    // Devuelve la edad maxima
    public int getEdadMaxima() {
        return edadMaxima;
    }
}

