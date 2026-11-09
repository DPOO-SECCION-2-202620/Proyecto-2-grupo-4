package modelo.reportes; // Paquete de reportes.

import java.time.LocalDate;     // Fecha.
import java.util.ArrayList;     // Lista dinámica.
import java.util.LinkedHashMap; // Mapa ordenado.
import java.util.List;          // Lista.
import java.util.Map;           // Mapa.
import modelo.ventas.Venta;     // Venta.

/** Reporte de ventas por rubro (TIENDA, CAFETERIA, MENSUALIDAD o TODOS) en un periodo. */
public class ReporteVentas extends Reporte { // Hereda de Reporte.

    private String rubro;                     // Rubro pedido ("TODOS" para todos).
    private double totalPorRubro;             // Total del rubro pedido (o total general).
    private List<Venta> ventas;               // Ventas de entrada (todas las del club).
    private Map<String, Double> totales;      // Total por cada rubro.

    public ReporteVentas(LocalDate inicio, LocalDate fin, String rubro, List<Venta> ventas) { // Constructor.
        super(inicio, fin);                         // Parte común.
        this.rubro = rubro == null ? "TODOS" : rubro.toUpperCase(); // Normaliza.
        this.ventas = new ArrayList<>(ventas);      // Copia la lista.
        this.totales = new LinkedHashMap<>();       // Mapa vacío.
    }

    /** Calcula el total de cada rubro y devuelve el del rubro pedido (método del UML). */
    public double calcularTotalPorRubro() {
        totales.clear();
        totales.put("TIENDA", 0.0);
        totales.put("CAFETERIA", 0.0);
        totales.put("MENSUALIDAD", 0.0);

        for (Venta v : ventas) {
            if (v.isCerrada() && enPeriodo(v.getFecha())) {
                String r = v.getRubro();
                totales.put(r, totales.getOrDefault(r, 0.0) + v.getTotal());
            }
        }

        if (rubro.equals("TODOS")) {
            totalPorRubro = 0;
            for (double valor : totales.values()) {
                totalPorRubro += valor;
            }
        } else {
            totalPorRubro = totales.getOrDefault(rubro, 0.0);
        }
        return totalPorRubro;
    }

    @Override // Arma el texto del reporte.
    public void generar() { // Sin parámetros.
        calcularTotalPorRubro(); // Calcula los totales.
        marcarGenerado();        // Fecha de generación.
        contenido.setLength(0);  // Limpia el contenido anterior.
        contenido.append("=== REPORTE DE VENTAS (").append(rubro).append(") ").append(getPeriodoInicio()).append(" a ").append(getPeriodoFin()).append(" ===\n"); // Encabezado.
        for (Venta v : ventas) { // Detalle de cada venta incluida.
            if (v.isCerrada() && enPeriodo(v.getFecha()) && (rubro.equals("TODOS") || v.getRubro().equals(rubro))) { // Filtro.
                contenido.append("  ").append(v.generarDetalle()).append("\n"); // Línea de detalle.
            }
        }
        for (Map.Entry<String, Double> e : totales.entrySet()) { // Totales por rubro.
            contenido.append(String.format("  Total %-12s $%,14.0f%n", e.getKey(), e.getValue())); // Línea.
        }
        contenido.append(String.format("  TOTAL REPORTADO (%s): $%,.0f%n", rubro, totalPorRubro)); // Total final.
    }

    public String getRubro() { return rubro; }                  // Getter.
    public double getTotalPorRubro() { return totalPorRubro; }  // Getter.
    public Map<String, Double> getTotales() { return totales; } // Getter.
}
