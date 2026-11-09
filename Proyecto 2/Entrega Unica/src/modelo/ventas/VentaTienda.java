package modelo.ventas; // Paquete de ventas.

import java.time.LocalDate;               // Fecha.
import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.usuarios.Usuario;           // Comprador.

/** Venta en la tienda deportiva: IVA 19% y posible código de descuento de empleado. */
public class VentaTienda extends Venta { // Hereda de Venta.

    public static final double IVA = 0.19; // Tasa del IVA.

    private List<ItemVentaTienda> items;      // Composición: líneas de la venta.
    private CodigoDescuento descuentoAplicado; // Código aplicado (0..1).

    public VentaTienda(int id, LocalDate fecha, Usuario comprador) { // Constructor.
        super(id, fecha, comprador);  // Parte común.
        this.items = new ArrayList<>(); // Sin ítems.
    }

    /** Agrega un artículo validando que haya inventario en la tienda (contando lo que ya está en el carrito). */
    public void agregarItem(Articulo articulo, int cantidad) throws ReglaNegocioException {
        if (isCerrada()) {
            throw new ReglaNegocioException("La venta ya está cerrada.");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero.");
        }
        int yaEnCarrito = cantidadEnCarrito(articulo);
        if (!articulo.hayInventarioSuficiente(yaEnCarrito + cantidad)) {
            throw new ReglaNegocioException("No hay suficiente " + articulo.getNombre() + " en la tienda: se piden "
                    + (yaEnCarrito + cantidad) + " y hay " + articulo.getCantidadEn(Articulo.TIENDA) + ".");
        }
        items.add(new ItemVentaTienda(articulo, cantidad, articulo.getPrecio()));
    }

    /** Unidades de un artículo que ya están en el carrito. */
    private int cantidadEnCarrito(Articulo a) { // Recibe el artículo.
        int n = 0; // Contador.
        for (ItemVentaTienda i : items) if (i.getArticulo() == a) n += i.getCantidad(); // Suma.
        return n; // Devuelve.
    }

    /** Aplica un código de descuento de empleado (método del UML). No acumulable con otras promociones. */
    public void aplicarDescuento(CodigoDescuento codigo) throws ReglaNegocioException {
    	if (this.descuentoAplicado != null) {
            throw new ReglaNegocioException("La venta ya tiene un código de descuento aplicado.");
         } else if (this.getPuntosRedimidos() > (double)0.0F) {
            throw new ReglaNegocioException("No se puede aplicar un código: el descuento no es acumulable con la redención de puntos.");
         } else if (this.getBonoAplicado() != null) {
            throw new ReglaNegocioException("No se puede aplicar un código: ya hay un bono de torneo aplicado (no acumulable).");
         } else {
            this.descuentoAplicado = codigo;
         }
    }

    /** Agrega una línea cargada desde archivo. */
    public void agregarItemCargado(ItemVentaTienda i) { items.add(i); } // Sin validar.
    /** Fija el código cargado desde archivo. */
    public void setDescuentoAplicado(CodigoDescuento c) { this.descuentoAplicado = c; } // Setter.

    @Override // Subtotal = suma de las líneas.
    protected double calcularSubtotal() { // Sin parámetros.
        double s = 0; // Acumulador.
        for (ItemVentaTienda i : items) s += i.calcularSubtotal(); // Suma cada línea.
        return s; // Devuelve.
    }

    @Override // Descuento por código: 15% si es el dueño, 8% si es otro socio.
    protected double calcularDescuento() {
        if (descuentoAplicado == null) {
            return 0;
        }
        return calcularSubtotal() * descuentoAplicado.calcularPorcentaje(getComprador());
    }

    @Override protected boolean permiteRedimirPuntos() { return descuentoAplicado == null; } // No acumulable.

    @Override // Proyecto 2 (HU-TOR-SOC-04 CA-2): el bono no se acumula con el código de empleado.
    protected boolean admiteBono() {
    	return this.descuentoAplicado == null;
    }

    @Override // La venta debe tener ítems y aún debe haber inventario.
    protected void validarAntesDeCerrar() throws ReglaNegocioException { // Validación.
        if (items.isEmpty()) throw new ReglaNegocioException("La venta no tiene artículos."); // Sin ítems.
        for (ItemVentaTienda i : items) { // Revalida inventario (pudo cambiar).
            if (!i.getArticulo().hayInventarioSuficiente(cantidadEnCarrito(i.getArticulo()))) { // ¿Alcanza?
                throw new ReglaNegocioException("Ya no hay inventario suficiente de " + i.getArticulo().getNombre() + "."); // No alcanza.
            }
        }
    }

    @Override // Al cerrar se descuenta el inventario de la tienda.
    protected void alCerrar() throws ReglaNegocioException { // Acción.
        for (ItemVentaTienda i : items) i.getArticulo().retirarInventario(Articulo.TIENDA, i.getCantidad()); // Retira de la tienda.
    }

    @Override public double getTasaImpuesto() { return IVA; } // 19%.
    @Override public String getRubro() { return "TIENDA"; }    // Rubro.

    public List<ItemVentaTienda> getItems() { return items; }           // Getter.
    public CodigoDescuento getDescuentoAplicado() { return descuentoAplicado; } // Getter.
}
