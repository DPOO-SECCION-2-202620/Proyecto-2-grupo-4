package modelo.ventas; // Paquete de ventas.

import modelo.usuarios.Empleado; // Dueño del código.
import modelo.usuarios.Usuario;  // Comprador.

/** Código de descuento de un empleado: 15% para él mismo y 8% para otros socios con quienes lo comparta. */
public class CodigoDescuento { // Clase CodigoDescuento.

    private String codigo;              // Texto del código (ej. "EMP-E001").
    private Empleado dueno;             // Empleado dueño del código.
    private double porcentajeEmpleado;  // 0.15
    private double porcentajeSocio;     // 0.08

    public CodigoDescuento(String codigo, Empleado dueno) { // Constructor.
        this.codigo = codigo;           // Guarda el código.
        this.dueno = dueno;             // Guarda el dueño.
        this.porcentajeEmpleado = 0.15; // 15% para el empleado.
        this.porcentajeSocio = 0.08;    // 8% para otros.
    }

    /** Porcentaje que aplica según quién compra. */
    public double calcularPorcentaje(Usuario comprador) {
        if (comprador == dueno) {
            return porcentajeEmpleado;
        } else {
            return porcentajeSocio;
        }
    }

    public String getCodigo() { return codigo; }                     // Getter.
    public Empleado getDueno() { return dueno; }                     // Getter.
    public double getPorcentajeEmpleado() { return porcentajeEmpleado; } // Getter.
    public double getPorcentajeSocio() { return porcentajeSocio; }   // Getter.

    @Override public String toString() { return codigo + " (de " + dueno.getNombre() + ")"; } // Texto.
}
