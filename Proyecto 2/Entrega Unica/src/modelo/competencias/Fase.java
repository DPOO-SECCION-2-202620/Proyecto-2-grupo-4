
package modelo.competencias;

import java.util.ArrayList;
import java.util.List;
import modelo.enums.Deporte;
import modelo.enums.TipoFase;

public class Fase {

    private String nombre;
    private TipoFase tipo;
    private List<Grupo> grupos;
    private List<String> participantes;
    private int clasificadosPorGrupo;
    private boolean finalizada;

    public Fase(String nombre, TipoFase tipo, int clasificadosPorGrupo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.clasificadosPorGrupo = clasificadosPorGrupo;
        grupos = new ArrayList<>();
        participantes = new ArrayList<>();
        finalizada = false;
    }

    // Agrega un grupo y sus equipos
    public void agregarGrupo(Grupo g) {
        grupos.add(g);

        for (String equipo : g.getEquipos()) {
            if (!participantes.contains(equipo)) {
                participantes.add(equipo);
            }
        }
    }

    // Calcula los equipos que pasan a la siguiente fase
    public List<String> calcularClasificados(
            List<Enfrentamiento> enfrentamientosDeLaFase, Deporte deporte) {

        List<String> clasificados = new ArrayList<>();

        if (tipo == TipoFase.GRUPOS) {
            for (Grupo g : grupos) {
                List<PosicionTabla> tabla =
                        g.calcularTabla(enfrentamientosDeLaFase, deporte);

                for (int i = 0;
                        i < clasificadosPorGrupo && i < tabla.size(); i++) {
                    clasificados.add(tabla.get(i).getNombreEquipo());
                }
            }
        } else {
            for (Enfrentamiento e : enfrentamientosDeLaFase) {
                String ganador = e.getGanador();

                if (ganador != null && participantes.contains(ganador)
                        && !clasificados.contains(ganador)) {
                    clasificados.add(ganador);
                }
            }
        }

        return clasificados;
    }

    // Marca la fase como terminada
    public void avanzar() {
        finalizada = true;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoFase getTipo() {
        return tipo;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public List<String> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<String> p) {
        participantes = new ArrayList<>(p);
    }

    public int getClasificadosPorGrupo() {
        return clasificadosPorGrupo;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean f) {
        finalizada = f;
    }
}
