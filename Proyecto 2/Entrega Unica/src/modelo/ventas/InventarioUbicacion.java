package modelo.ventas; // Paquete de tienda, cafetería y ventas.

/** Cantidad de un artículo guardada en una ubicación del inventario (ej. "TIENDA", "BODEGA"). */
public class InventarioUbicacion { // Clase InventarioUbicacion.

    private String ubicacion; // Nombre de la ubicación.
    private int cantidad;     // Unidades en esa ubicación.

    public InventarioUbicacion(String ubicacion, int cantidad) { // Constructor.
        this.ubicacion = ubicacion; // Guarda la ubicación.
        this.cantidad = cantidad;   // Guarda la cantidad.
    }

    public void agregar(int n) { cantidad += n; } // Suma unidades.
    public void retirar(int n) { cantidad -= n; } // Resta unidades (quien llama valida que alcance).

    public String getUbicacion() { return ubicacion; } // Getter.
    public int getCantidad() { return cantidad; }      // Getter.

    @Override public String toString() { return ubicacion + ": " + cantidad; } // Ej: "TIENDA: 12".
}
