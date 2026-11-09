package modelo.ventas; // Paquete de ventas.

import java.util.ArrayList; // Lista dinámica.
import java.util.List;      // Lista.

/** Snack de la cafetería con sus posibles alérgenos. */
public class Snack extends ProductoCafeteria { // Hereda de ProductoCafeteria.

    private List<String> alergenos; // Ej: ["MANI", "GLUTEN"].

    public Snack(String nombre, double precio, List<String> alergenos) { // Constructor.
        super(nombre, precio);                     // Parte común.
        this.alergenos = new ArrayList<>(alergenos); // Copia la lista.
    }

    public boolean tieneAlergenos() { return !alergenos.isEmpty(); } // ¿Tiene alguno?

    @Override public String getTipo() { return "SNACK"; }     // Tipo de producto.
    public List<String> getAlergenos() { return alergenos; }  // Getter.

    @Override public String toString() { return super.toString() + " alérgenos=" + alergenos; } // Texto.
}
