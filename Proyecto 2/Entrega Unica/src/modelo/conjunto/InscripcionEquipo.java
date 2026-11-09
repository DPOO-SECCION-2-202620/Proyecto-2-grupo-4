package modelo.conjunto; // Paquete de los deportes de conjunto.

import modelo.usuarios.Socio; // Jugador inscrito.

/**
 * Clase de asociación entre Equipo y Socio: guarda los datos que solo tienen sentido
 * cuando un jugador pertenece a un equipo (posición, número de camiseta, estado).
 */
public class InscripcionEquipo { // Clase de asociación "tiene" entre Equipo y Socio.

    private Equipo equipo;                     // Equipo al que pertenece la inscripción.
    private Socio jugador;                     // Jugador inscrito.
    private String posicion;                   // Posición (ej. "PORTERO", "BASE", "LIBERO").
    private int numeroCamiseta;                // Número de camiseta, único dentro del equipo.
    private boolean activo;                    // false cuando el jugador fue dado de baja (se conserva el historial).
    private boolean suspendidoProximoPartido;  // true si no puede jugar el siguiente partido interno de su equipo.

    public InscripcionEquipo(Equipo equipo, Socio jugador, String posicion, int numeroCamiseta) { // Constructor.
        this.equipo = equipo;                 // Guarda el equipo.
        this.jugador = jugador;               // Guarda el jugador.
        this.posicion = posicion;             // Guarda la posición.
        this.numeroCamiseta = numeroCamiseta; // Guarda el número.
        this.activo = true;                   // Toda inscripción nueva está activa.
        this.suspendidoProximoPartido = false; // Y sin suspensión.
    }

    /** Un jugador está habilitado si está activo y no está suspendido. */
    public boolean estaHabilitado() { // Sin parámetros: usa el estado del objeto.
        return activo && !suspendidoProximoPartido; // Las dos condiciones deben cumplirse.
    }

    // ---------- Getters y setters ----------
    public Equipo getEquipo() { return equipo; }                     // Devuelve el equipo.
    public Socio getJugador() { return jugador; }                    // Devuelve el jugador.
    public String getPosicion() { return posicion; }                 // Devuelve la posición.
    public void setPosicion(String p) { this.posicion = p; }         // Cambia la posición.
    public int getNumeroCamiseta() { return numeroCamiseta; }        // Devuelve el número.
    public boolean isActivo() { return activo; }                     // Indica si está activo.
    public void setActivo(boolean a) { this.activo = a; }            // Activa / da de baja.
    public boolean isSuspendidoProximoPartido() { return suspendidoProximoPartido; } // Indica si está suspendido.
    public void setSuspendidoProximoPartido(boolean s) { this.suspendidoProximoPartido = s; } // Fija la suspensión.

    @Override // Representación en texto.
    public String toString() { // Ej: "#10 Ana Pérez - DELANTERO [SUSPENDIDO]".
        return "#" + numeroCamiseta + " " + jugador.getNombre() + " - " + posicion // Número, nombre y posición.
                + (activo ? "" : " [BAJA]")                                       // Marca si fue dado de baja.
                + (suspendidoProximoPartido ? " [SUSPENDIDO]" : "");              // Marca si está suspendido.
    }
}
