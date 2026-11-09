package modelo.ventas; // Paquete de ventas.

/** Línea de una venta de cafetería. */
public class ItemVentaCafeteria { // Clase ItemVentaCafeteria.

    private ProductoCafeteria producto; // Producto vendido.
    private int cantidad;               // Unidades.
    private double precioUnitario;      // Precio congelado al momento de la venta.

    public ItemVentaCafeteria(ProductoCafeteria producto, int cantidad, double precioUnitario) { // Constructor.
        this.producto = producto;             // Guarda el producto.
        this.cantidad = cantidad;             // Guarda la cantidad.
        this.precioUnitario = precioUnitario; // Guarda el precio.
    }

    public double calcularSubtotal() { return cantidad * precioUnitario; } // Cantidad x precio.

    public ProductoCafeteria getProducto() { return producto; } // Getter.
    public int getCantidad() { return cantidad; }               // Getter.
    public double getPrecioUnitario() { return precioUnitario; } // Getter.

    @Override public String toString() { return String.format("%d x %s @ $%,.0f = $%,.0f", cantidad, producto.getNombre(), precioUnitario, calcularSubtotal()); }
}
