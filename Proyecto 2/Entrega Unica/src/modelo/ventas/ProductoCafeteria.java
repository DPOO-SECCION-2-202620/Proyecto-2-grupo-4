package modelo.ventas; // Paquete de ventas.

/** Producto de la cafetería (bebida o snack). */
public abstract class ProductoCafeteria { // Clase abstracta.

    private String nombre; // Nombre (se asume único).
    private double precio; // Precio antes de impuesto.

    public ProductoCafeteria(String nombre, double precio) { // Constructor.
        this.nombre = nombre; // Guarda el nombre.
        this.precio = precio; // Guarda el precio.
    }

    /** "BEBIDA" o "SNACK". */
    public abstract String getTipo(); // Abstracto.

    public String getNombre() { return nombre; } // Getter.
    public double getPrecio() { return precio; } // Getter.

    @Override public String toString() { return String.format("%s %s $%,.0f", getTipo(), nombre, precio); } // Texto.
}
