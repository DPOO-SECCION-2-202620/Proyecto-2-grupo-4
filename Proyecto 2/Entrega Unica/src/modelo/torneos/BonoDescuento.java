package modelo.torneos; // Paquete de torneos internos (Proyecto 2).

import java.time.LocalDate;
import excepciones.ReglaNegocioException;

/**
 * Bono de descuento que gana el ganador de un torneo recreativo (HU-TOR-ADM-02 CA-1). El valor lo define el
 * administrador al crear el torneo. Es de uso único y sin vencimiento.
 * Pertenece a un Usuario (lista de bonos) y se puede aplicar a UNA venta de tienda o cafetería.
 */
public class BonoDescuento {

    private String codigo;          // Código único del bono (ej. "BONO-COPATENIS-S002").
    private double porcentaje;      // 0.15 = 15 %.
    private String torneoOrigen;    // Nombre del torneo donde se ganó.
    private LocalDate fechaOtorgado; // Fecha en que se entregó.
    private boolean usado;          // true cuando ya se aplicó en una compra.

    public BonoDescuento(String codigo, double porcentaje, String torneoOrigen, LocalDate fechaOtorgado) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.torneoOrigen = torneoOrigen;
        this.fechaOtorgado = fechaOtorgado;
        this.usado = false; // Todo bono nace sin usar.
    }

    /** Genera un código legible y único por torneo y usuario. */
    public static String generarCodigo(String nombreTorneo, String idUsuario) {
        String limpio = nombreTorneo.toUpperCase().replaceAll("[^A-Z0-9]", ""); // Solo letras y números.
        return "BONO-" + limpio + "-" + idUsuario;
    }

    /** ¿Se puede usar todavía? */
    public boolean estaDisponible() {
        return !usado;
    }

    /**
     * Marca el bono como usado (HU-TOR-SOC-04 CA-3: un bono ya usado no se puede volver a aplicar).
     */
    public void usar() throws ReglaNegocioException {
    	if (this.usado) {
            throw new ReglaNegocioException("El bono " + this.codigo + " ya fue usado.");
         } else {
            this.usado = true;
         }
    }

    /**
     * Valor en pesos que descuenta el bono sobre una base.
     * Ejemplo: base 100.000 y porcentaje 0.15 -> 15.000.
     */
    public double calcularDescuento(double base) {
        return base < (double)0.0F ? (double)0.0F : base * this.porcentaje;
    }

    /** Solo para la persistencia: fija el estado leído del archivo. */
    public void setUsado(boolean usado) { this.usado = usado; }

    public String getCodigo() { return codigo; }
    public double getPorcentaje() { return porcentaje; }
    public String getTorneoOrigen() { return torneoOrigen; }
    public LocalDate getFechaOtorgado() { return fechaOtorgado; }
    public boolean isUsado() { return usado; }

    @Override
    public String toString() {
        return String.format("%s: %.0f %% de descuento (torneo %s, %s) [%s]", codigo, porcentaje * 100, torneoOrigen,
                fechaOtorgado, usado ? "USADO" : "DISPONIBLE"); // HU-TOR-SOC-04 CA-4: valor y estado.
    }
}
