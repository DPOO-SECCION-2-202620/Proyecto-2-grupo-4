package modelo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.conjunto.Equipo;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/** Inscripción de un equipo completo en un torneo de deporte de conjunto. */
public class InscripcionTorneoEquipo extends InscripcionTorneo {

    private Equipo equipo; // Equipo inscrito.

    public InscripcionTorneoEquipo(int id, LocalDate fecha, double ranking, Equipo equipo) {
        super(id, fecha, ranking);
        this.equipo = equipo;
    }

    /** Los integrantes son los jugadores activos del equipo. */
    @Override
    public List<Usuario> getIntegrantes() {
        List<Usuario> lista = new ArrayList<>();
        for (Socio s : equipo.getJugadoresActivos()) lista.add(s);
        return lista;
    }

    @Override
    public String getNombre() { return equipo.getNombre(); }

    public Equipo getEquipo() { return equipo; }
}
