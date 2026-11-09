package modelo.conjunto; // Paquete de deportes de conjunto.

import modelo.enums.Deporte;  // Deporte.
import modelo.usuarios.Socio; // Jugador.

/** Estadística de voleibol: aces y bloqueos. */
public class EstadisticaVoleibol extends EstadisticaPartido { // Hereda de EstadisticaPartido.

    private int aces;      // Saques directos.
    private int bloqueos;  // Bloqueos efectivos.

    public EstadisticaVoleibol(Socio jugador, int aces, int bloqueos) { // Constructor.
        super(jugador);           // Parte común.
        this.aces = aces;         // Guarda aces.
        this.bloqueos = bloqueos; // Guarda bloqueos.
    }

    @Override public Deporte getDeporte() { return Deporte.VOLEIBOL; } // Es de voleibol.
    @Override public int getValorPrincipal() { return aces; }           // Métrica principal: aces.
    @Override public double calcularValoracion() { return aces * 2 + bloqueos * 2; } // Fórmula simple.
    @Override public String describir() { return aces + " aces, " + bloqueos + " bloqueos"; } // Texto.

    public int getAces() { return aces; }         // Devuelve los aces.
    public int getBloqueos() { return bloqueos; } // Devuelve los bloqueos.
}
