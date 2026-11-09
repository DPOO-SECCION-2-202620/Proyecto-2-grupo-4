package modelo.competencias; // Paquete de competencias.

import java.util.ArrayList;  // Lista dinámica.
import java.util.List;       // Lista.
import modelo.enums.Deporte; // Deporte.

/** Grupo de una fase de grupos: un conjunto de equipos que juegan entre sí. */
public class Grupo { // Clase Grupo.

    private String nombre;        // Nombre del grupo (ej. "A").
    private List<String> equipos; // Nombres de los equipos del grupo.

    public Grupo(String nombre, List<String> equipos) { // Constructor.
        this.nombre = nombre;                   // Guarda el nombre.
        this.equipos = new ArrayList<>(equipos); // Copia la lista.
    }

    /** Tabla del grupo, usando el enfrentamiento directo como desempate. */
    public List<PosicionTabla> calcularTabla(List<Enfrentamiento> enfrentamientosDeLaFase, Deporte deporte) {
        List<Enfrentamiento> delGrupo = new ArrayList<>(); // Solo los resultados de este grupo.
        for (Enfrentamiento e : enfrentamientosDeLaFase) { // Recorre los de la fase.
            if (e.getGrupo().equals(nombre)) delGrupo.add(e); // Filtra por nombre de grupo.
        }
        return PosicionTabla.construirTabla(equipos, delGrupo, deporte, true); // true = desempate por enfrentamiento directo.
    }

    public String getNombre() { return nombre; }         // Getter.
    public List<String> getEquipos() { return equipos; } // Getter.
}
