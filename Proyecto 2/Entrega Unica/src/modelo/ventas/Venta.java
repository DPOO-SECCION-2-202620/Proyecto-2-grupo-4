package modelo.ventas; // Paquete de ventas.

import java.time.LocalDate;               // Fecha.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.usuarios.Usuario;           // Comprador.
import modelo.torneos.BonoDescuento;      // Proyecto 2: bono de torneo recreativo.

/**
 * Venta abstracta. Usa "Template Method": calcularTotal() y cerrar() definen los pasos comunes
 * (subtotal, descuento, puntos, impuesto, adicional, puntos de fidelidad) y cada subclase
 * define su subtotal, su tasa de impuesto y sus reglas propias.
 */
public abstract class Venta { // Clase abstracta.

    private int id;                          // Identificador.
    private LocalDate fecha;                 // Fecha de la venta (para reportes por periodo).
    private Usuario comprador;               // Quién compra.
    private double subtotal;                 // Suma de los ítems.
    private double descuento;                // Descuento por código.
    private double puntosRedimidos;          // Pesos pagados con puntos de fidelidad.
    private double impuesto;                 // IVA (19%), impuesto al consumo (8%) o 0.
    private double total;                    // Lo que paga el comprador.
    private double puntosFidelidadGenerados; // 2% del valor pagado (sin propina).
    private boolean cerrada;                 // true cuando la venta ya se confirmó.
    private BonoDescuento bonoAplicado;      // Proyecto 2: bono de torneo aplicado (0..1).

    public Venta(int id, LocalDate fecha, Usuario comprador) { // Constructor.
        this.id = id;               // Guarda el id.
        this.fecha = fecha;         // Guarda la fecha.
        this.comprador = comprador; // Guarda el comprador.
        this.cerrada = false;       // Abierta al crearse.
    }

    /** Suma de los ítems de la venta. */
    protected abstract double calcularSubtotal(); // Abstracto.

    /** Tasa de impuesto del rubro (0.19, 0.08 o 0). */
    public abstract double getTasaImpuesto(); // Abstracto.

    /** Rubro contable: "TIENDA", "CAFETERIA" o "MENSUALIDAD". */
    public abstract String getRubro(); // Abstracto.

    /** Descuento en pesos (por defecto 0; la tienda lo sobrescribe). */
    protected double calcularDescuento() { return 0; } // Gancho ("hook").

    /** Valor que se suma al final sin impuesto ni puntos (por defecto 0; la cafetería suma la propina). */
    protected double calcularAdicional() { return 0; } // Gancho.

    /** ¿Se pueden redimir puntos en esta venta? (la tienda no lo permite si hay código: no acumulable) */
    protected boolean permiteRedimirPuntos() { return true; } // Gancho.

    /** Proyecto 2: ¿esta venta admite un bono? (la mensualidad no; la tienda no si ya tiene código de empleado). */
    protected boolean admiteBono() { return true; } // Gancho.

    /**
     * Proyecto 2 (HU-TOR-SOC-04): aplica un bono de torneo recreativo a la venta.
     * No es acumulable con el código de empleado ni con la redención de puntos.
     */
    public void aplicarBono(BonoDescuento bono) throws ReglaNegocioException {
    	if (this.cerrada) {
            throw new ReglaNegocioException("La venta ya está cerrada.");
         } else if (bono != null && bono.estaDisponible()) {
            if (this.bonoAplicado != null) {
               throw new ReglaNegocioException("La venta ya tiene un bono aplicado.");
            } else if (!(this.puntosRedimidos > (double)0.0F) && this.admiteBono()) {
               this.bonoAplicado = bono;
            } else {
               throw new ReglaNegocioException("El bono no es acumulable con otros descuentos (código de empleado o puntos).");
            }
         } else {
            throw new ReglaNegocioException("El bono no existe o ya fue usado.");
         }
    }

    /** Proyecto 2 (HU-TOR-SOC-04 CA-1): pesos que descuenta el bono aplicado (0 si no hay). */
    protected double calcularDescuentoBono() {
        return this.bonoAplicado == null ? (double)0.0F : this.bonoAplicado.calcularDescuento(this.calcularSubtotal());

    }

    /** Validaciones propias antes de cerrar (por defecto ninguna). */
    protected void validarAntesDeCerrar() throws ReglaNegocioException { } // Gancho.

    /** Acciones propias al cerrar (descontar inventario, marcar bebida caliente...). */
    protected void alCerrar() throws ReglaNegocioException { } // Gancho.

    /** Calcula todos los valores de la venta y devuelve el total (método del UML). */
    public double calcularTotal() {
        subtotal = calcularSubtotal();
        descuento = Math.min(calcularDescuento() + calcularDescuentoBono(), subtotal); // Proyecto 2: incluye el bono.
        double base = Math.max(0, subtotal - descuento - puntosRedimidos);
        impuesto = base * getTasaImpuesto();
        total = base + impuesto + calcularAdicional();
        puntosFidelidadGenerados = (base + impuesto) * Usuario.PORCENTAJE_PUNTOS;
        return total;
    }

    /** Usa los puntos de fidelidad del comprador como descuento, hasta cubrir la base de la venta. */
    public void usarPuntosFidelidad() throws ReglaNegocioException { // Puede fallar.
    	if (this.cerrada) {
            throw new ReglaNegocioException("La venta ya está cerrada.");
         } else if (!this.permiteRedimirPuntos()) {
            throw new ReglaNegocioException("No se pueden redimir puntos: el descuento no es acumulable con otras promociones.");
         } else if (this.bonoAplicado != null) {
            throw new ReglaNegocioException("No se pueden redimir puntos: ya hay un bono aplicado (no acumulable).");
         } else {
            double maximo = this.calcularSubtotal() - Math.min(this.calcularDescuento(), this.calcularSubtotal());
            this.puntosRedimidos += this.comprador.redimirPuntos(maximo - this.puntosRedimidos);
         }
    
    }

    /** Confirma la venta: valida, calcula, ejecuta acciones propias y abona los puntos de fidelidad. */
    public void cerrar() throws ReglaNegocioException {
        if (cerrada) {
            throw new ReglaNegocioException("La venta " + id + " ya está cerrada.");
        }
        validarAntesDeCerrar();
        calcularTotal();
        alCerrar();
        if (bonoAplicado != null) bonoAplicado.usar(); // Proyecto 2: el bono queda usado (uso único).
        comprador.acumularPuntos(puntosFidelidadGenerados / Usuario.PORCENTAJE_PUNTOS);
        cerrada = true;
    }

    /** Fija los valores guardados en archivo (sin recalcular ni tocar inventario/puntos). */
    public void fijarValoresCargados(double subtotal, double descuento, double puntosRedimidos, double impuesto,
                                     double total, double puntos, boolean cerrada) { // Recibe los valores.
        this.subtotal = subtotal; this.descuento = descuento; this.puntosRedimidos = puntosRedimidos; // Asigna.
        this.impuesto = impuesto; this.total = total; this.puntosFidelidadGenerados = puntos; this.cerrada = cerrada; // Asigna.
    }

    /** Detalle contable con la discriminación de rubros. */
    public String generarDetalle() { // Sin parámetros.
        return String.format("Venta #%d %s [%s] %s | Subtotal $%,.0f  Desc. $%,.0f  Puntos $%,.0f  Impuesto(%.0f%%) $%,.0f  Adicional $%,.0f  TOTAL $%,.0f  (+%.0f pts)",
                id, fecha, getRubro(), comprador.getNombre(), subtotal, descuento, puntosRedimidos,
                getTasaImpuesto() * 100, impuesto, getAdicional(), total, puntosFidelidadGenerados); // Texto formateado.
    }

    /** Adicional (propina) calculado; 0 por defecto. */
    public double getAdicional() { return 0; } // La cafetería lo sobrescribe.

    public int getId() { return id; }                                   // Getter.
    public LocalDate getFecha() { return fecha; }                       // Getter.
    public Usuario getComprador() { return comprador; }                 // Getter.
    public double getSubtotal() { return subtotal; }                    // Getter.
    public double getDescuento() { return descuento; }                  // Getter.
    public double getPuntosRedimidos() { return puntosRedimidos; }      // Getter.
    public double getImpuesto() { return impuesto; }                    // Getter.
    public double getTotal() { return total; }                          // Getter.
    public double getPuntosFidelidadGenerados() { return puntosFidelidadGenerados; } // Getter.
    public boolean isCerrada() { return cerrada; }                      // Getter.
    public BonoDescuento getBonoAplicado() { return bonoAplicado; }     // Getter (Proyecto 2).
    public void setBonoAplicadoCargado(BonoDescuento b) { this.bonoAplicado = b; } // Persistencia (sin validar).

    @Override public String toString() { return generarDetalle(); } // El texto es el detalle.
}
