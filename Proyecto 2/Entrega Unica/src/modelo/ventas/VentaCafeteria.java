package modelo.ventas; // Paquete de ventas.

import java.time.LocalDate;               // Fecha.
import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.usuarios.Socio;             // Socio (para la regla de bebidas calientes).
import modelo.usuarios.Usuario;           // Comprador.

/** Venta en la cafetería: impuesto al consumo 8%, propina sugerida 10% y reglas de seguridad y alérgenos. */
public class VentaCafeteria extends Venta { // Hereda de Venta.

    public static final double IMPUESTO_CONSUMO = 0.08; // Impuesto al consumo.
    public static final double PROPINA_SUGERIDA = 0.10; // Propina sugerida.

    private List<ItemVentaCafeteria> items; // Líneas de la venta.
    private double propina;                 // Propina final.
    private boolean propinaAjustada;        // true si el socio cambió la propina sugerida.
    private boolean alergenosInformados;    // true si ya se informaron los alérgenos de los snacks.

    public VentaCafeteria(int id, LocalDate fecha, Usuario comprador) { // Constructor.
        super(id, fecha, comprador);   // Parte común.
        this.items = new ArrayList<>(); // Sin ítems.
        this.propinaAjustada = false;  // Propina sugerida por defecto.
        this.alergenosInformados = false; // Aún no se han informado.
    }

    /** Agrega un producto. Regla: no se despacha bebida caliente a un socio dentro de pádel o mesa techada. */
    public void agregarItem(ProductoCafeteria producto, int cantidad) throws ReglaNegocioException {
        if (isCerrada()) {
            throw new ReglaNegocioException("La venta ya está cerrada.");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero.");
        }
        if (producto instanceof Bebida && ((Bebida) producto).esCaliente() && getComprador() instanceof Socio) {
            Socio socio = (Socio) getComprador();
            if (socio.getInstalacionActual() != null && !socio.getInstalacionActual().permiteBebidaCaliente()) {
                throw new ReglaNegocioException("No se puede despachar " + producto.getNombre() + " (bebida caliente) dentro de "
                        + socio.getInstalacionActual().getId() + ": está prohibido en esa instalación.");
            }
        }
        if (producto instanceof Snack && ((Snack) producto).tieneAlergenos()) {
            alergenosInformados = false;
        }
        items.add(new ItemVentaCafeteria(producto, cantidad, producto.getPrecio()));
    }

    /** Devuelve el aviso de alérgenos de los snacks del pedido y lo marca como informado. */
    public String informarAlergenos() { // Sin parámetros.
        StringBuilder sb = new StringBuilder(); // Texto del aviso.
        for (ItemVentaCafeteria i : items) {    // Recorre los ítems.
            if (i.getProducto() instanceof Snack && ((Snack) i.getProducto()).tieneAlergenos()) { // Snacks con alérgenos.
                sb.append("  ATENCIÓN: ").append(i.getProducto().getNombre()).append(" contiene ") // Arma la línea.
                  .append(((Snack) i.getProducto()).getAlergenos()).append("\n");
            }
        }
        alergenosInformados = true; // Marca como informado.
        return sb.length() == 0 ? "  (ningún producto con alérgenos)\n" : sb.toString(); // Devuelve el aviso.
    }

    /** Cambia la propina (método del UML). */
    public void ajustarPropina(double nuevaPropina) throws ReglaNegocioException { // Recibe el nuevo valor.
        if (nuevaPropina < 0) throw new ReglaNegocioException("La propina no puede ser negativa."); // Validación.
        propina = nuevaPropina; // Guarda la propina.
        propinaAjustada = true; // Ya no se usa la sugerida.
    }

    /** ¿El pedido contiene una bebida caliente? */
    public boolean contieneBebidaCaliente() { // Sin parámetros.
        for (ItemVentaCafeteria i : items) if (i.getProducto() instanceof Bebida && ((Bebida) i.getProducto()).esCaliente()) return true; // Busca.
        return false; // No hay.
    }

    /** ¿Hay snacks con alérgenos en el pedido? */
    private boolean hayAlergenos() { // Sin parámetros.
        for (ItemVentaCafeteria i : items) if (i.getProducto() instanceof Snack && ((Snack) i.getProducto()).tieneAlergenos()) return true; // Busca.
        return false; // No hay.
    }

    public void agregarItemCargado(ItemVentaCafeteria i) { items.add(i); } // Carga desde archivo.
    public void fijarPropinaCargada(double p) { propina = p; propinaAjustada = true; } // Carga desde archivo.

    @Override // Subtotal = suma de líneas.
    protected double calcularSubtotal() { double s = 0; for (ItemVentaCafeteria i : items) s += i.calcularSubtotal(); return s; } // Suma.

    @Override // Propina: la sugerida (10% del consumo) o la ajustada.
    protected double calcularAdicional() {
        if (!propinaAjustada) {
            propina = calcularSubtotal() * PROPINA_SUGERIDA;
        }
        return propina;
    }

    @Override public double getAdicional() { return propina; } // Propina para el detalle.

    @Override // Validaciones antes de cerrar.
    protected void validarAntesDeCerrar() throws ReglaNegocioException {
        if (items.isEmpty()) {
            throw new ReglaNegocioException("El pedido no tiene productos.");
        }
        if (hayAlergenos() && !alergenosInformados) {
            throw new ReglaNegocioException("Debe informar los alérgenos al socio antes de cerrar la venta.");
        }
    }

    @Override // Si compró bebida caliente, el socio queda "con bebida caliente".
    protected void alCerrar() { // Acción.
        if (contieneBebidaCaliente() && getComprador() instanceof Socio) ((Socio) getComprador()).setTieneBebidaCaliente(true); // Marca.
    }

    @Override public double getTasaImpuesto() { return IMPUESTO_CONSUMO; } // 8%.
    @Override public String getRubro() { return "CAFETERIA"; }             // Rubro.

    public List<ItemVentaCafeteria> getItems() { return items; } // Getter.
    public double getPropina() { return propina; }               // Getter.
}
