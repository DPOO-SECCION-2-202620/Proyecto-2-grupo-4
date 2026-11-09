package modelo.academias; // Paquete de academias.

import java.time.LocalDate;               // Fecha.
import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.enums.Disciplina;           // Disciplina.
import modelo.enums.Nivel;                // Nivel.
import modelo.usuarios.Socio;             // Socio.

/** Inscripción individual de un socio en una academia: nivel y ranking ELO en esa disciplina. */
public class InscripcionDisciplina { // Clase de asociación Socio-Disciplina.

    /** Ranking con el que empieza todo jugador. */
    public static final double ELO_INICIAL = 1000.0; // Constante.

    private Socio socio;           // Socio inscrito.
    private Disciplina disciplina; // Disciplina.
    private Nivel nivel;           // Nivel asignado.
    private double rankingElo;     // Puntaje de ranking.

    public InscripcionDisciplina(Socio socio, Disciplina disciplina, Nivel nivel) { // Constructor.
        this.socio = socio;           // Guarda el socio.
        this.disciplina = disciplina; // Guarda la disciplina.
        this.nivel = nivel;           // Guarda el nivel.
        this.rankingElo = ELO_INICIAL; // Ranking inicial.
    }

    /**
     * Programa un partido de práctica entre este jugador y otros (1 rival en sencillos o 3 en dobles).
     * Regla: todos los pares de jugadores deben tener niveles iguales o adyacentes,
     * salvo que uno de los jugadores sea también entrenador.
     * En dobles, los dos primeros jugadores de la lista forman la pareja A y los dos últimos la B.
     */
    public PartidoPractica programarPartidoPractica(int idPartido, List<InscripcionDisciplina> otrosJugadores,
                                                    LocalDate fecha, boolean incluyeEntrenador) throws ReglaNegocioException {
    	List<InscripcionDisciplina> todos = new ArrayList<>();
        todos.add(this);
        todos.addAll(otrosJugadores);

        if (todos.size() != 2 && todos.size() != 4) {
            throw new ReglaNegocioException("Un partido de práctica necesita 2 jugadores (sencillos) o 4 (dobles), pero se indicaron " + todos.size() + ".");
        }
        for (int i = 0; i < todos.size(); i++) {
            InscripcionDisciplina a = todos.get(i);
            if (a.getDisciplina() != disciplina) {
                throw new ReglaNegocioException(a.getSocio().getNombre() + " no está inscrito en " + disciplina + ".");
            }
            for (int j = i + 1; j < todos.size(); j++) {
                InscripcionDisciplina b = todos.get(j);
                if (a.getSocio() == b.getSocio()) {
                    throw new ReglaNegocioException(a.getSocio().getNombre() + " aparece repetido en el partido.");
                }
                if (!incluyeEntrenador && !a.getNivel().esCompatibleCon(b.getNivel())) {
                    throw new ReglaNegocioException("Niveles no compatibles: " + a.getSocio().getNombre() + " (" + a.getNivel() + ") y "
                            + b.getSocio().getNombre() + " (" + b.getNivel() + "). Solo se permiten niveles iguales o adyacentes.");
                }
            }
        }
        return new PartidoPractica(idPartido, disciplina, fecha, todos);
    }

    /** Suma (o resta, si es negativo) un cambio al ranking. */
    public void actualizarElo(double delta) { rankingElo += delta; } // Actualiza.

    public Socio getSocio() { return socio; }                    // Getter.
    public Disciplina getDisciplina() { return disciplina; }     // Getter.
    public Nivel getNivel() { return nivel; }                    // Getter.
    public void setNivel(Nivel n) { this.nivel = n; }            // Setter.
    public double getRankingElo() { return rankingElo; }         // Getter.
    public void setRankingElo(double r) { this.rankingElo = r; } // Setter (persistencia).

    @Override // Texto.
    public String toString() { return String.format("%s - %s %s (ELO %.1f)", socio.getNombre(), disciplina, nivel, rankingElo); }
}
