package modelo.ventas; // Paquete de ventas.

import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.

/** Artículo de la tienda deportiva, con inventario distribuido en varias ubicaciones. */
public class Articulo { // Clase Articulo.

    public static final String TIENDA = "TIENDA"; // Ubicación desde donde se vende.
    public static final String BODEGA = "BODEGA"; // Ubicación de almacenamiento.

    private String nombre;                       // Nombre del artículo (se asume único).
    private String categoria;                    // Categoría (raquetas, balones, uniformes...).
    private double precio;                       // Precio unitario antes de impuestos.
    private List<InventarioUbicacion> inventario; // Cantidades por ubicación.

    public Articulo(String nombre, String categoria, double precio) { // Constructor.
        this.nombre = nombre;         // Guarda el nombre.
        this.categoria = categoria;   // Guarda la categoría.
        this.precio = precio;         // Guarda el precio.
        this.inventario = new ArrayList<>(); // Sin inventario.
    }

    /** ¿Hay en la TIENDA al menos esa cantidad? (método del UML) */
    public boolean hayInventarioSuficiente(int cantidad) {
        return getCantidadEn(TIENDA) >= cantidad;
    }

    /** Unidades en una ubicación (0 si la ubicación no existe). */
    public int getCantidadEn(String ubicacion) { // Recibe la ubicación.
        InventarioUbicacion iu = buscarUbicacion(ubicacion); // Busca.
        return iu == null ? 0 : iu.getCantidad();            // 0 si no existe.
    }

    /** Unidades totales sumando todas las ubicaciones. */
    public int getCantidadTotal() { // Sin parámetros.
        int total = 0; // Acumulador.
        for (InventarioUbicacion iu : inventario) total += iu.getCantidad(); // Suma.
        return total; // Devuelve.
    }

    /** Agrega unidades a una ubicación (la crea si no existe). Sirve para reabastecer. */
    public void agregarInventario(String ubicacion, int cantidad) throws ReglaNegocioException { // Recibe ubicación y cantidad.
        if (cantidad <= 0) throw new ReglaNegocioException("La cantidad a agregar debe ser positiva."); // Validación.
        InventarioUbicacion iu = buscarUbicacion(ubicacion); // Busca la ubicación.
        if (iu == null) {                                    // Si no existe...
            iu = new InventarioUbicacion(ubicacion, 0);      // ...la crea vacía...
            inventario.add(iu);                              // ...y la agrega.
        }
        iu.agregar(cantidad); // Suma las unidades.
    }

    /** Retira unidades de una ubicación validando que alcancen. */
    public void retirarInventario(String ubicacion, int cantidad) throws ReglaNegocioException {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a retirar debe ser positiva.");
        }
        InventarioUbicacion iu = buscarUbicacion(ubicacion);
        if (iu == null || iu.getCantidad() < cantidad) {
            int disponibles = (iu == null) ? 0 : iu.getCantidad();
            throw new ReglaNegocioException("No hay suficiente " + nombre + " en " + ubicacion
                    + ": se pidieron " + cantidad + " y hay " + disponibles + ".");
        }
        iu.retirar(cantidad);
    }
    /** Busca una ubicación por nombre (null si no existe). */
    public InventarioUbicacion buscarUbicacion(String ubicacion) { // Recibe el nombre.
        for (InventarioUbicacion iu : inventario) if (iu.getUbicacion().equalsIgnoreCase(ubicacion)) return iu; // Busca ignorando mayúsculas.
        return null; // No existe.
    }

    public String getNombre() { return nombre; }                         // Getter.
    public String getCategoria() { return categoria; }                   // Getter.
    public double getPrecio() { return precio; }                         // Getter.
    public void setPrecio(double p) { this.precio = p; }                 // Setter.
    public List<InventarioUbicacion> getInventario() { return inventario; } // Getter.

    @Override // Texto.
    public String toString() { return String.format("%-22s %-12s $%,10.0f  %s", nombre, categoria, precio, inventario); } // Formateado.
}
