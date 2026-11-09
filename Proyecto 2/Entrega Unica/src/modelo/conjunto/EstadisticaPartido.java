package modelo.conjunto; // Paquete de deportes de conjunto.

import modelo.enums.Deporte;  // Deporte al que corresponde la estadística.
import modelo.usuarios.Socio; // Jugador dueño de la estadística.

/**
 * Estadística individual de un jugador en un partido de conjunto.
 * Cada deporte tiene su propia subclase con sus métricas.
 */
public abstract class EstadisticaPartido { // Abstracta: solo existen las de fútbol, baloncesto y voleibol.

    private Socio jugador; // Jugador al que pertenece la estadística.

    public EstadisticaPartido(Socio jugador) { // Constructor.
        this.jugador = jugador; // Guarda el jugador.
    }

    /** Deporte al que corresponde esta estadística (lo define cada subclase). */
    public abstract Deporte getDeporte(); // Método abstracto.

    /** Métrica principal para tablas de goleadores/anotadores (goles, puntos o aces). */
    public abstract int getValorPrincipal(); // Método abstracto.

    /** Valoración numérica del desempeño, para la clasificación del "jugador más destacado". */
    public abstract double calcularValoracion(); // Método abstracto.

    /** Descripción corta en texto de la estadística. */
    public abstract String describir(); // Método abstracto.

    /** Por defecto, ninguna estadística suspende (solo fútbol sobrescribe esto). */
    public boolean quedaSuspendido() { return false; } // Comportamiento por defecto.

    /** Por defecto no hay tarjetas amarillas (solo fútbol). */
    public int getAmarillas() { return 0; } // Comportamiento por defecto.

    /** Por defecto no hay tarjeta roja (solo fútbol). */
    public boolean tieneRoja() { return false; } // Comportamiento por defecto.

    public Socio getJugador() { return jugador; } // Devuelve el jugador.

    @Override // Representación en texto.
    public String toString() { return jugador.getNombre() + ": " + describir(); } // Ej: "Ana: 2 goles, 1 asistencia".
}
