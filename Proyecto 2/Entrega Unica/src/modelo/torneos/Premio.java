package modelo.torneos;

import java.util.ArrayList;
import java.util.List;

/**
 * Premio entregado al finalizar un torneo (HU-TOR-ADM-02). En competitivos es dinero (monto) para el primer
 * participante de las posiciones que no sea empleado. En recreativos el ganador recibe bonos (uno por integrante).
 */
public class Premio {

    private int puesto;                     // Puesto del que recibe el premio (1 = campeón).
    private InscripcionTorneo inscripcion;  // Quién lo recibe.
    private double monto;                   // Dinero (0 en recreativos).
    private List<BonoDescuento> bonos;      // Bonos entregados (solo recreativos).

    public Premio(int puesto, InscripcionTorneo inscripcion, double monto) {
        this.puesto = puesto;
        this.inscripcion = inscripcion;
        this.monto = monto;
        this.bonos = new ArrayList<>();
    }

    public void agregarBono(BonoDescuento b) { bonos.add(b); }

    /** Texto para el historial del torneo. */
    public String getDescripcion() {
        StringBuilder sb = new StringBuilder();
        sb.append(puesto).append("º ").append(inscripcion.getNombre()).append(": ");
        if (!bonos.isEmpty()) {
            sb.append(bonos.size()).append(" bono(s) de ").append(String.format("%.0f %%", bonos.get(0).getPorcentaje() * 100));
        } else {
            sb.append(String.format("premio en efectivo de $%,.0f", monto));
        }
        return sb.toString();
    }

    public int getPuesto() { return puesto; }
    public InscripcionTorneo getInscripcion() { return inscripcion; }
    public double getMonto() { return monto; }
    public List<BonoDescuento> getBonos() { return bonos; }

    @Override
    public String toString() { return getDescripcion(); }
}
