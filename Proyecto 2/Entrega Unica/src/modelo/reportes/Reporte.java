package modelo.reportes; // Paquete de reportes del administrador.

import java.time.DayOfWeek;   // Días de la semana.
import java.time.LocalDate;   // Fecha.

/** Reporte abstracto sobre un periodo de tiempo. */
public abstract class Reporte { // Clase abstracta.

    private LocalDate periodoInicio;   // Primer día del periodo (incluido).
    private LocalDate periodoFin;      // Último día del periodo (incluido).
    private LocalDate fechaGeneracion; // Cuándo se generó.
    protected StringBuilder contenido; // Texto del reporte (protected: lo llenan las subclases).

    public Reporte(LocalDate periodoInicio, LocalDate periodoFin) { // Constructor.
        this.periodoInicio = periodoInicio; // Guarda el inicio.
        this.periodoFin = periodoFin;       // Guarda el fin.
        this.contenido = new StringBuilder(); // Contenido vacío.
    }

    /** Calcula los datos y arma el contenido del reporte. */
    public abstract void generar(); // Abstracto.

    /** Marca la fecha de generación (lo llaman las subclases al generar). */
    protected void marcarGenerado() { fechaGeneracion = LocalDate.now(); } // Fecha de hoy.

    /** ¿La fecha está dentro del periodo? */
    public boolean enPeriodo(LocalDate f) { return !f.isBefore(periodoInicio) && !f.isAfter(periodoFin); } // inicio <= f <= fin.

    /**
     * Calcula el periodo [inicio, fin] para un tipo de periodo y una fecha de referencia.
     * DIARIO: ese día. SEMANAL: de lunes a domingo de esa semana. MENSUAL: el mes completo.
     */
    public static LocalDate[] calcularPeriodo(String tipoPeriodo, LocalDate referencia) { // Método de clase.
        switch (tipoPeriodo.toUpperCase()) { // Según el tipo...
            case "DIARIO":                   // Diario.
                return new LocalDate[] { referencia, referencia }; // Mismo día.
            case "SEMANAL": {                // Semanal.
                LocalDate lunes = referencia.minusDays(referencia.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()); // Retrocede hasta el lunes.
                return new LocalDate[] { lunes, lunes.plusDays(6) }; // Lunes a domingo.
            }
            default:                         // Mensual (por defecto).
                LocalDate primero = referencia.withDayOfMonth(1); // Primer día del mes.
                return new LocalDate[] { primero, primero.plusMonths(1).minusDays(1) }; // Hasta el último día.
        }
    }

    public LocalDate getPeriodoInicio() { return periodoInicio; }     // Getter.
    public LocalDate getPeriodoFin() { return periodoFin; }           // Getter.
    public LocalDate getFechaGeneracion() { return fechaGeneracion; } // Getter.
    public String getContenido() { return contenido.toString(); }     // Texto del reporte.

    @Override public String toString() { return getContenido(); } // Imprimir = contenido.
}
