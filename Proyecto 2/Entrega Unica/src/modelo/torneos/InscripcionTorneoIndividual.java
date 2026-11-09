package modelo.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.usuarios.Usuario;

/** Inscripción de un jugador (sencillos) o de una pareja (dobles) en un torneo individual. */
public class InscripcionTorneoIndividual extends InscripcionTorneo {

    private List<Usuario> jugadores; // 1 o 2 usuarios (socios, externos o empleados).

    public InscripcionTorneoIndividual(int id, LocalDate fecha, double ranking, List<Usuario> jugadores) {
        super(id, fecha, ranking);
        this.jugadores = new ArrayList<>(jugadores);
    }

    @Override
    public List<Usuario> getIntegrantes() { return jugadores; }

    @Override
    public String getNombre() {
        List<String> nombres = new ArrayList<>();
        for (Usuario u : jugadores) nombres.add(u.getNombre());
        return String.join(" / ", nombres);
    }
}
