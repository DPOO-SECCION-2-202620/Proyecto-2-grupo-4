package modelo.conjunto; // Paquete de deportes de conjunto.

import modelo.enums.Deporte;  // Deporte.
import modelo.usuarios.Socio; // Jugador.

/** Estadística de baloncesto: puntos y rebotes. */
public class EstadisticaBaloncesto extends EstadisticaPartido { // Hereda de EstadisticaPartido.

    private int puntos;   // Puntos anotados.
    private int rebotes;  // Rebotes capturados.

    public EstadisticaBaloncesto(Socio jugador, int puntos, int rebotes) { // Constructor.
        super(jugador);         // Parte común.
        this.puntos = puntos;   // Guarda puntos.
        this.rebotes = rebotes; // Guarda rebotes.
    }

    @Override public Deporte getDeporte() { return Deporte.BALONCESTO; } // Es de baloncesto.
    @Override public int getValorPrincipal() { return puntos; }           // Para la tabla de anotadores.
    @Override public double calcularValoracion() { return puntos + rebotes * 1.2; } // Fórmula simple de valoración.
    @Override public String describir() { return puntos + " puntos, " + rebotes + " rebotes"; } // Texto.

    public int getPuntos() { return puntos; }   // Devuelve los puntos.
    public int getRebotes() { return rebotes; } // Devuelve los rebotes.
}
