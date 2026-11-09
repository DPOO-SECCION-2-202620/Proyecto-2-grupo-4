package modelo.ventas; // Paquete de ventas.

import modelo.enums.TipoBebida; // Tipo de bebida.

/** Bebida de la cafetería: fría o caliente. */
public class Bebida extends ProductoCafeteria { // Hereda de ProductoCafeteria.

    private TipoBebida tipo; // FRIA o CALIENTE.

    public Bebida(String nombre, double precio, TipoBebida tipo) { // Constructor.
        super(nombre, precio); // Parte común.
        this.tipo = tipo;      // Guarda el tipo.
    }

    public boolean esCaliente() { return tipo == TipoBebida.CALIENTE; } // Atajo para la regla de seguridad.

    @Override public String getTipo() { return "BEBIDA"; } // Tipo de producto.
    public TipoBebida getTipoBebida() { return tipo; }      // Getter.

    @Override public String toString() { return super.toString() + " (" + tipo + ")"; } // Agrega el tipo al texto del padre.
}
