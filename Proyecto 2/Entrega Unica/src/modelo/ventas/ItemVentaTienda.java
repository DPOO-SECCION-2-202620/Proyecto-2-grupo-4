package modelo.ventas; // Paquete de ventas.

/** Línea de una venta de tienda: un artículo, una cantidad y el precio unitario al momento de la venta. */
public class ItemVentaTienda { // Clase ItemVentaTienda.

    private Articulo articulo;     // Artículo vendido.
    private int cantidad;          // Unidades.
    private double precioUnitario; // Precio congelado al momento de la venta.

    public ItemVentaTienda(Articulo articulo, int cantidad, double precioUnitario) { // Constructor.
        this.articulo = articulo;             // Guarda el artículo.
        this.cantidad = cantidad;             // Guarda la cantidad.
        this.precioUnitario = precioUnitario; // Guarda el precio.
    }

    public double calcularSubtotal() { return cantidad * precioUnitario; } // Cantidad x precio.

    public Articulo getArticulo() { return articulo; }         // Getter.
    public int getCantidad() { return cantidad; }              // Getter.
    public double getPrecioUnitario() { return precioUnitario; } // Getter.

    @Override public String toString() { return String.format("%d x %s @ $%,.0f = $%,.0f", cantidad, articulo.getNombre(), precioUnitario, calcularSubtotal()); }
}
