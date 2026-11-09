
package modelo.conjunto;

import java.time.LocalDate;
import excepciones.ReglaNegocioException;
import modelo.usuarios.Socio;

public class PartidoInterno extends Partido {

    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    // Crea un partido entre dos equipos
    public PartidoInterno(int id, LocalDate fecha, Equipo equipoLocal,
            Equipo equipoVisitante) throws ReglaNegocioException {

        super(id, fecha, equipoLocal.getDeporte());

        if (equipoLocal == equipoVisitante) {
            throw new ReglaNegocioException(
                    "Un equipo no puede jugar contra sí mismo.");
        }

        if (equipoLocal.getDeporte() != equipoVisitante.getDeporte()) {
            throw new ReglaNegocioException(
                    "Los equipos deben ser del mismo deporte.");
        }

        if (equipoLocal.getCategoria() != equipoVisitante.getCategoria()) {
            throw new ReglaNegocioException(
                    "Los equipos deben ser de la misma categoría.");
        }

        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }

    // Revisa si los dos equipos tienen suficientes jugadores
    @Override
    public boolean puedeJugarse() {
        return equipoLocal.tieneMinimoJugadoresHabilitados()
                && equipoVisitante.tieneMinimoJugadoresHabilitados();
    }

    // Revisa si el jugador puede participar
    @Override
    protected boolean jugadorPuedeParticipar(Socio jugador) {
        return equipoLocal.estaHabilitado(jugador)
                || equipoVisitante.estaHabilitado(jugador);
    }

    // Actualiza las suspensiones al terminar el partido
    @Override
    protected void alFinalizar() {
        equipoLocal.actualizarSuspensiones(getEstadisticas());
        equipoVisitante.actualizarSuspensiones(getEstadisticas());
    }

    // Revisa si el equipo juega este partido
    public boolean involucraA(Equipo e) {
        return equipoLocal == e || equipoVisitante == e;
    }

    @Override
    public String getNombreA() {
        return equipoLocal.getNombre();
    }

    @Override
    public String getNombreB() {
        return equipoVisitante.getNombre();
    }

    @Override
    public String getTipo() {
        return "INTERNO";
    }

    // Getters
    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }
}
