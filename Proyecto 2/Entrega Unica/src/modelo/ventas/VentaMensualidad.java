package modelo.ventas; // Paquete de ventas.

import java.time.LocalDate;     // Fecha.
import modelo.usuarios.Socio;   // Socio que paga.

/** Pago de la mensualidad de un socio (rubro MENSUALIDAD, sin impuesto). */
public class VentaMensualidad extends Venta { // Hereda de Venta.

    private String periodo; // Periodo pagado, ej. "2026-10".
    private double valor;   // Valor de la mensualidad.

    public VentaMensualidad(int id, LocalDate fecha, Socio socio, String periodo, double valor) { // Constructor.
        super(id, fecha, socio); // Parte común.
        this.periodo = periodo;  // Guarda el periodo.
        this.valor = valor;      // Guarda el valor.
    }

    @Override protected double calcularSubtotal() { return valor; } // Subtotal = valor.
    @Override public double getTasaImpuesto() { return 0; }         // Supuesto: sin impuesto.
    @Override public String getRubro() { return "MENSUALIDAD"; }     // Rubro.
    @Override protected boolean admiteBono() { return false; }       // Proyecto 2: el bono es solo para tienda y cafetería.

    public String getPeriodo() { return periodo; } // Getter.
    public double getValor() { return valor; }     // Getter.
}
