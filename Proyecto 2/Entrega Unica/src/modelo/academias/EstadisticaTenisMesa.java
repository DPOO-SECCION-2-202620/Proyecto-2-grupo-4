package modelo.academias; // Paquete de academias.

import modelo.enums.Disciplina; // Disciplina.
import modelo.usuarios.Socio;   // Jugador.

/** Estadística de tenis de mesa. */
public class EstadisticaTenisMesa extends EstadisticaPractica { // Hereda.

    private int setsGanados;    // Sets ganados.
    private int puntosTotales;  // Puntos totales.

    public EstadisticaTenisMesa(Socio jugador, int setsGanados, int puntosTotales) { // Constructor.
        super(jugador);                   // Parte común.
        this.setsGanados = setsGanados;   // Guarda sets.
        this.puntosTotales = puntosTotales; // Guarda puntos.
    }

    @Override public int getSetsGanados() { return setsGanados; } // Sets.
    @Override public boolean correspondeA(Disciplina d) { return d == Disciplina.TENIS_MESA; } // Solo tenis de mesa.
    @Override public String describir() { return setsGanados + " sets, " + puntosTotales + " puntos"; } // Texto.

    public int getPuntosTotales() { return puntosTotales; } // Getter.
}
