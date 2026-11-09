package modelo.academias; // Paquete de academias.

import modelo.enums.Disciplina; // Disciplina.
import modelo.usuarios.Socio;   // Jugador.

/** Estadística de tenis o pádel. */
public class EstadisticaTenisPadel extends EstadisticaPractica { // Hereda.

    private int setsGanados;        // Sets ganados.
    private int gamesGanados;       // Games ganados.
    private int aces;               // Aces.
    private int erroresNoForzados;  // Errores no forzados.

    public EstadisticaTenisPadel(Socio jugador, int setsGanados, int gamesGanados, int aces, int erroresNoForzados) {
        super(jugador);                         // Parte común.
        this.setsGanados = setsGanados;         // Guarda sets.
        this.gamesGanados = gamesGanados;       // Guarda games.
        this.aces = aces;                       // Guarda aces.
        this.erroresNoForzados = erroresNoForzados; // Guarda errores.
    }

    @Override public int getSetsGanados() { return setsGanados; } // Sets.
    @Override public boolean correspondeA(Disciplina d) { return d == Disciplina.TENIS || d == Disciplina.PADEL; } // Tenis o pádel.
    @Override public String describir() { return setsGanados + " sets, " + gamesGanados + " games, " + aces + " aces, " + erroresNoForzados + " ENF"; }

    public int getGamesGanados() { return gamesGanados; }           // Getter.
    public int getAces() { return aces; }                           // Getter.
    public int getErroresNoForzados() { return erroresNoForzados; } // Getter.
}
