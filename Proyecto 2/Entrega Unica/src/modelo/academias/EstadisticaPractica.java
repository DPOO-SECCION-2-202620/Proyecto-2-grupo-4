package modelo.academias; // Paquete de academias.

import modelo.enums.Disciplina; // Disciplina.
import modelo.usuarios.Socio;   // Jugador.

/** Estadística de un jugador en un partido de práctica de una disciplina individual. */
public abstract class EstadisticaPractica { // Clase abstracta.

    private Socio jugador;     // Jugador.
    private double eloAntes;   // Ranking antes del partido (se llena al registrar el resultado).
    private double eloDespues; // Ranking después del partido.

    public EstadisticaPractica(Socio jugador) { this.jugador = jugador; } // Constructor.

    /** Sets ganados (lo tienen todas las disciplinas). */
    public abstract int getSetsGanados(); // Abstracto.

    /** Indica si esta estadística sirve para la disciplina dada. */
    public abstract boolean correspondeA(Disciplina d); // Abstracto.

    /** Texto con las métricas. */
    public abstract String describir(); // Abstracto.

    public Socio getJugador() { return jugador; }               // Getter.
    public double getEloAntes() { return eloAntes; }            // Getter.
    public double getEloDespues() { return eloDespues; }        // Getter.
    public void setElo(double antes, double despues) {          // Guarda la evolución del ranking.
        this.eloAntes = antes;      // Antes.
        this.eloDespues = despues;  // Después.
    }

    @Override // Texto.
    public String toString() { return jugador.getNombre() + ": " + describir() + String.format(" | ELO %.1f -> %.1f", eloAntes, eloDespues); }
}
