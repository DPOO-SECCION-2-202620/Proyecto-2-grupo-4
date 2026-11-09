package modelo.torneos;

/**
 * Encuentro jugado dentro de un torneo, registrado por el administrador para el historial
 * (HU-TOR-ADM-03 CA-2). Las posiciones finales se registran aparte al finalizar el torneo (HU-TOR-ADM-02).
 */
public class EncuentroTorneo {

    private int id;                    // Id dentro del torneo.
    private String fase;               // Texto libre: "Ronda 1", "Semifinal", "Final"... (puede ser vacío).
    private InscripcionTorneo ladoA;   // Primer participante.
    private InscripcionTorneo ladoB;   // Segundo participante.
    private int marcadorA;             // Marcador de A.
    private int marcadorB;             // Marcador de B.

    public EncuentroTorneo(int id, String fase, InscripcionTorneo ladoA, InscripcionTorneo ladoB, int marcadorA, int marcadorB) {
        this.id = id;
        this.fase = fase == null ? "" : fase;
        this.ladoA = ladoA;
        this.ladoB = ladoB;
        this.marcadorA = marcadorA;
        this.marcadorB = marcadorB;
    }

    /** Ganador (null si fue empate). */
    public InscripcionTorneo getGanador() {
        if (marcadorA == marcadorB) return null;
        return marcadorA > marcadorB ? ladoA : ladoB;
    }

    public int getId() { return id; }
    public String getFase() { return fase; }
    public InscripcionTorneo getLadoA() { return ladoA; }
    public InscripcionTorneo getLadoB() { return ladoB; }
    public int getMarcadorA() { return marcadorA; }
    public int getMarcadorB() { return marcadorB; }

    @Override
    public String toString() {
        return String.format("#%d %s%s %d - %d %s", id, fase.isEmpty() ? "" : fase + ": ", ladoA.getNombre(), marcadorA, marcadorB,
                ladoB.getNombre());
    }
}
