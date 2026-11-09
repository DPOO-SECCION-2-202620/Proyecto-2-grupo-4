package modelo.enums; // Paquete de enumeraciones.

/**
 * Deportes de conjunto. Cada deporte conoce sus propias reglas básicas:
 * mínimo de jugadores habilitados para disputar un partido y si admite empates.
 */
public enum Deporte { // Enumeración de deportes de conjunto.

    FUTBOL(7, true),      // Fútbol: mínimo 7 jugadores habilitados; sí hay empates.
    BALONCESTO(5, false), // Baloncesto: mínimo 5; no hay empates.
    VOLEIBOL(6, false);   // Voleibol: mínimo 6; no hay empates (marcador en sets).

    private final int minimoJugadores; // Mínimo de jugadores habilitados para jugar.
    private final boolean permiteEmpate; // Si el deporte admite empates.

    Deporte(int minimoJugadores, boolean permiteEmpate) { // Constructor del enum.
        this.minimoJugadores = minimoJugadores; // Guarda el mínimo.
        this.permiteEmpate = permiteEmpate; // Guarda si permite empate.
    }

    public int getMinimoJugadores() { return minimoJugadores; } // Devuelve el mínimo de jugadores.
    public boolean permiteEmpate() { return permiteEmpate; } // Devuelve si se permite empate.

    /** Puntos que otorga una victoria en formato de liga (supuesto: 3 en todos los deportes). */
    public int puntosVictoria() { return 3; } // La victoria vale 3 puntos.

    /** Puntos que otorga un empate (solo aplica a fútbol). */
    public int puntosEmpate() { return permiteEmpate ? 1 : 0; } // Operador ternario: 1 si hay empates, 0 si no.

    /** Puntos de una derrota. */
    public int puntosDerrota() { return 0; } // La derrota no da puntos.
}
