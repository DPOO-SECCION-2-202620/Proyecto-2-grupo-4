
package modelo.conjunto;

import modelo.enums.Deporte;
import modelo.usuarios.Socio;

public class EstadisticaFutbol extends EstadisticaPartido {

    private int goles;
    private int asistencias;
    private int amarillas;
    private boolean rojas;

    // Guarda las estadisticas del jugador
    public EstadisticaFutbol(Socio jugador, int goles, int asistencias,
            int amarillas, boolean roja) {
        super(jugador);
        this.goles = goles;
        this.asistencias = asistencias;
        this.amarillas = amarillas;
        this.rojas = roja;
    }

    // Revisa si el jugador queda suspendido
    @Override
    public boolean quedaSuspendido() {
        return amarillas >= 2 || rojas;
    }

    // Devuelve el deporte
    @Override
    public Deporte getDeporte() {
        return Deporte.FUTBOL;
    }

    // Devuelve los goles
    @Override
    public int getValorPrincipal() {
        return goles;
    }

    @Override
    public int getAmarillas() {
        return amarillas;
    }

    @Override
    public boolean tieneRoja() {
        return rojas;
    }

    // Calcula la valoracion del jugador
    @Override
    public double calcularValoracion() {
        return goles * 3 + asistencias * 2 - amarillas - (rojas ? 3 : 0);
    }

    // Muestra las estadisticas del jugador
    @Override
    public String describir() {
        return goles + " goles, " + asistencias + " asist., "
                + amarillas + " amarillas" + (rojas ? ", ROJA" : "");
    }

    // Getters
    public int getGoles() {
        return goles;
    }

    public int getAsistencias() {
        return asistencias;
    }
}

