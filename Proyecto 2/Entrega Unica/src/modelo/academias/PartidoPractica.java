package modelo.academias; // Paquete de academias.

import java.time.LocalDate;               // Fecha.
import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.enums.Disciplina;           // Disciplina.
import modelo.usuarios.Socio;             // Jugador.

/** Partido de práctica entre 2 (sencillos) o 4 (dobles) jugadores de una academia. */
public class PartidoPractica { // Clase PartidoPractica.

    /** Factor K del ELO simplificado: cuánto puede cambiar el ranking en un partido. */
    public static final double K_ELO = 32.0; // Constante.

    private int id;                                 // Identificador.
    private Disciplina disciplina;                  // Disciplina.
    private LocalDate fecha;                        // Fecha.
    private List<InscripcionDisciplina> jugadores;  // 2 o 4 jugadores (asociación "jugadores" 2..4).
    private List<EstadisticaPractica> estadisticas; // Estadísticas del partido.
    private boolean jugado;                         // Si ya se registró el resultado.

    public PartidoPractica(int id, Disciplina disciplina, LocalDate fecha, List<InscripcionDisciplina> jugadores) { // Constructor.
        this.id = id;                                 // Guarda el id.
        this.disciplina = disciplina;                 // Guarda la disciplina.
        this.fecha = fecha;                           // Guarda la fecha.
        this.jugadores = new ArrayList<>(jugadores);  // Copia la lista.
        this.estadisticas = new ArrayList<>();        // Sin estadísticas.
        this.jugado = false;                          // No jugado.
    }

    /** Lado A: el primer jugador (sencillos) o los dos primeros (dobles). */
    public List<InscripcionDisciplina> getLadoA() { return jugadores.subList(0, jugadores.size() / 2); } // Primera mitad.

    /** Lado B: el segundo jugador (sencillos) o los dos últimos (dobles). */
    public List<InscripcionDisciplina> getLadoB() { return jugadores.subList(jugadores.size() / 2, jugadores.size()); } // Segunda mitad.

    /**
     * Registra el resultado con las estadísticas de TODOS los jugadores y actualiza el ELO.
     * ELO simplificado: esperado = 1 / (1 + 10^((Rrival - Rpropio)/400)); cambio = K * (resultado - esperado).
     */
    public void registrarResultado(List<EstadisticaPractica> stats) throws ReglaNegocioException {
        if (jugado) {
            throw new ReglaNegocioException("El partido " + id + " ya tiene resultado registrado.");
        }
        if (stats.size() != jugadores.size()) {
            throw new ReglaNegocioException("Se esperaban estadísticas de " + jugadores.size() + " jugadores y llegaron " + stats.size() + ".");
        }
        for (EstadisticaPractica e : stats) {
            if (!e.correspondeA(disciplina)) {
                throw new ReglaNegocioException("La estadística de " + e.getJugador().getNombre() + " no corresponde a " + disciplina + ".");
            }
            if (buscarInscripcion(e.getJugador()) == null) {
                throw new ReglaNegocioException(e.getJugador().getNombre() + " no juega este partido.");
            }
        }
        int setsA = setsDelLado(getLadoA(), stats);
        int setsB = setsDelLado(getLadoB(), stats);
        if (setsA == setsB) {
            throw new ReglaNegocioException("El partido debe tener un ganador: los sets no pueden quedar empatados.");
        }
        double eloA = promedioElo(getLadoA());
        double eloB = promedioElo(getLadoB());
        double esperadoA = 1.0 / (1.0 + Math.pow(10, (eloB - eloA) / 400.0));
        double resultadoA = (setsA > setsB) ? 1.0 : 0.0;
        double delta = K_ELO * (resultadoA - esperadoA);

        for (EstadisticaPractica e : stats) {
            InscripcionDisciplina ins = buscarInscripcion(e.getJugador());
            double antes = ins.getRankingElo();
            if (getLadoA().contains(ins)) {
                ins.actualizarElo(delta);
            } else {
                ins.actualizarElo(-delta);
            }
            e.setElo(antes, ins.getRankingElo());
        }
        estadisticas.addAll(stats);
        jugado = true;
    }

    /** Sets de un lado: el máximo entre sus jugadores (en dobles ambos deberían tener el mismo valor). */
    private int setsDelLado(List<InscripcionDisciplina> lado, List<EstadisticaPractica> stats) { // Recibe el lado y las estadísticas.
        int max = 0; // Máximo encontrado.
        for (EstadisticaPractica e : stats) {                         // Recorre las estadísticas.
            InscripcionDisciplina ins = buscarInscripcion(e.getJugador()); // Inscripción del jugador.
            if (lado.contains(ins)) max = Math.max(max, e.getSetsGanados()); // Si es de este lado, compara.
        }
        return max; // Devuelve.
    }

    /** Promedio del ranking de un lado. */
    private double promedioElo(List<InscripcionDisciplina> lado) { // Recibe el lado.
        double suma = 0; // Acumulador.
        for (InscripcionDisciplina i : lado) suma += i.getRankingElo(); // Suma rankings.
        return suma / lado.size(); // Divide por la cantidad.
    }

    /** Inscripción de un socio en este partido (null si no jugó). */
    public InscripcionDisciplina buscarInscripcion(Socio s) { // Recibe el socio.
        for (InscripcionDisciplina i : jugadores) if (i.getSocio() == s) return i; // Busca.
        return null; // No está.
    }

    /** Socios ganadores (lado con más sets); lista vacía si no se ha jugado. */
    public List<Socio> getGanadores() { // Sin parámetros.
        List<Socio> g = new ArrayList<>(); // Resultado.
        if (!jugado) return g;             // Sin resultado aún.
        boolean ganaA = setsDelLado(getLadoA(), estadisticas) > setsDelLado(getLadoB(), estadisticas); // ¿Ganó A?
        for (InscripcionDisciplina i : (ganaA ? getLadoA() : getLadoB())) g.add(i.getSocio()); // Agrega los del lado ganador.
        return g; // Devuelve.
    }

    /** Agrega datos cargados desde archivo. */
    public void cargarResultado(List<EstadisticaPractica> stats) { estadisticas.addAll(stats); jugado = true; } // Sin validar.

    public int getId() { return id; }                                     // Getter.
    public Disciplina getDisciplina() { return disciplina; }              // Getter.
    public LocalDate getFecha() { return fecha; }                         // Getter.
    public List<InscripcionDisciplina> getJugadores() { return jugadores; } // Getter.
    public List<EstadisticaPractica> getEstadisticas() { return estadisticas; } // Getter.
    public boolean isJugado() { return jugado; }                          // Getter.

    @Override // Texto.
    public String toString() { // Ej: "Práctica #1 TENIS 2026-10-05: Ana vs Luis (jugado)".
        StringBuilder sb = new StringBuilder("Práctica #" + id + " " + disciplina + " " + fecha + ": "); // StringBuilder arma textos eficientemente.
        for (int i = 0; i < jugadores.size(); i++) {                       // Recorre jugadores.
            if (i == jugadores.size() / 2) sb.append(" vs ");              // Separador entre lados.
            else if (i > 0) sb.append(" & ");                              // Separador dentro de un lado.
            sb.append(jugadores.get(i).getSocio().getNombre());            // Nombre.
        }
        return sb.append(jugado ? " (jugado)" : " (programado)").toString(); // Estado.
    }
}
