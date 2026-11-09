package modelo.competencias; // Paquete de competencias externas.

/**
 * Resultado de un juego entre dos equipos identificados por su nombre.
 * Sirve para dos cosas: (1) representar los partidos del club dentro de las tablas y
 * (2) registrar resultados entre otras instituciones (necesarios para tablas completas).
 */
public class Enfrentamiento { // Clase de datos simple.

    private String equipoA; // Nombre del primer equipo.
    private String equipoB; // Nombre del segundo equipo.
    private int golesA;     // Marcador del primer equipo (goles, puntos o sets).
    private int golesB;     // Marcador del segundo equipo.
    private String fase;    // Fase a la que pertenece ("" si es liga).
    private String grupo;   // Grupo al que pertenece ("" si no aplica).

    public Enfrentamiento(String equipoA, String equipoB, int golesA, int golesB, String fase, String grupo) { // Constructor.
        this.equipoA = equipoA; // Guarda equipo A.
        this.equipoB = equipoB; // Guarda equipo B.
        this.golesA = golesA;   // Guarda marcador A.
        this.golesB = golesB;   // Guarda marcador B.
        this.fase = fase == null ? "" : fase;    // Evita null.
        this.grupo = grupo == null ? "" : grupo; // Evita null.
    }

    /** Nombre del ganador, o null si fue empate. */
    public String getGanador() { // Sin parámetros.
        if (golesA > golesB) return equipoA; // Ganó A.
        if (golesB > golesA) return equipoB; // Ganó B.
        return null;                         // Empate.
    }

    /** Indica si el enfrentamiento es entre exactamente esos dos equipos (en cualquier orden). */
    public boolean esEntre(String x, String y) { // Recibe dos nombres.
        return (equipoA.equals(x) && equipoB.equals(y)) || (equipoA.equals(y) && equipoB.equals(x)); // Ambos órdenes.
    }

    public String getEquipoA() { return equipoA; } // Getter.
    public String getEquipoB() { return equipoB; } // Getter.
    public int getGolesA() { return golesA; }      // Getter.
    public int getGolesB() { return golesB; }      // Getter.
    public String getFase() { return fase; }       // Getter.
    public String getGrupo() { return grupo; }     // Getter.

    @Override // Representación en texto.
    public String toString() { return equipoA + " " + golesA + "-" + golesB + " " + equipoB; } // Ej: "Séneca 2-1 Andes".
}
