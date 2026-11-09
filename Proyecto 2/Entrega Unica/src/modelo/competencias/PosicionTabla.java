
package modelo.competencias;

import java.util.ArrayList;
import java.util.List;
import modelo.enums.Deporte;

public class PosicionTabla {

    private String nombreEquipo;
    private int puntos;
    private int victorias;
    private int empates;
    private int derrotas;
    private int golesFavor;
    private int golesContra;

    public PosicionTabla(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    // Registra el resultado de un partido
    public void registrarResultado(int favor, int contra, Deporte deporte) {
        golesFavor += favor;
        golesContra += contra;

        if (favor > contra) {
            victorias++;
            puntos += deporte.puntosVictoria();
        } else if (favor == contra) {
            empates++;
            puntos += deporte.puntosEmpate();
        } else {
            derrotas++;
            puntos += deporte.puntosDerrota();
        }
    }

    // Construye y ordena la tabla de posiciones
    public static List<PosicionTabla> construirTabla(
            List<String> equipos, List<Enfrentamiento> enfrentamientos,
            Deporte deporte, boolean desempateDirecto) {

        List<PosicionTabla> tabla = new ArrayList<>();

        // Crea una fila para cada equipo
        for (String equipo : equipos) {
            tabla.add(new PosicionTabla(equipo));
        }

        // Registra los resultados de los partidos
        for (Enfrentamiento enf : enfrentamientos) {
            PosicionTabla a = buscar(tabla, enf.getEquipoA());
            PosicionTabla b = buscar(tabla, enf.getEquipoB());

            if (a != null && b != null) {
                a.registrarResultado(
                        enf.getGolesA(), enf.getGolesB(), deporte);

                b.registrarResultado(
                        enf.getGolesB(), enf.getGolesA(), deporte);
            }
        }

        // Ordena la tabla según los criterios establecidos
        tabla.sort((x, y) ->
                comparar(x, y, enfrentamientos, deporte, desempateDirecto));

        return tabla;
    }

    // Compara dos equipos para decidir cuál va primero
    private static int comparar(PosicionTabla x, PosicionTabla y,
            List<Enfrentamiento> enfs, Deporte deporte, boolean directo) {

        // Primero compara los puntos
        if (x.puntos != y.puntos) {
            return y.puntos - x.puntos;
        }

        // Si se usa el enfrentamiento directo, compara sus puntos
        if (directo) {
            int px = 0;
            int py = 0;

            for (Enfrentamiento e : enfs) {
                if (e.esEntre(x.nombreEquipo, y.nombreEquipo)) {
                    String ganador = e.getGanador();

                    if (ganador == null) {
                        px += deporte.puntosEmpate();
                        py += deporte.puntosEmpate();
                    } else if (ganador.equals(x.nombreEquipo)) {
                        px += deporte.puntosVictoria();
                    } else {
                        py += deporte.puntosVictoria();
                    }
                }
            }

            if (px != py) {
                return py - px;
            }
        }

        // Después compara la diferencia de goles
        if (x.getDiferencia() != y.getDiferencia()) {
            return y.getDiferencia() - x.getDiferencia();
        }

        // Si siguen empatados, compara los goles a favor
        if (x.golesFavor != y.golesFavor) {
            return y.golesFavor - x.golesFavor;
        }

        // Último criterio: orden alfabético
        return x.nombreEquipo.compareTo(y.nombreEquipo);
    }

    // Busca un equipo en la tabla
    private static PosicionTabla buscar(
            List<PosicionTabla> tabla, String nombre) {

        for (PosicionTabla p : tabla) {
            if (p.nombreEquipo.equals(nombre)) {
                return p;
            }
        }

        return null;
    }

    public int getDiferencia() {
        return golesFavor - golesContra;
    }

    public int getPartidosJugados() {
        return victorias + empates + derrotas;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getVictorias() {
        return victorias;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getGolesFavor() {
        return golesFavor;
    }

    public int getGolesContra() {
        return golesContra;
    }

    @Override
    public String toString() {
        return String.format(
                "%-28s PJ:%2d V:%2d E:%2d D:%2d GF:%3d GC:%3d DIF:%+4d PTS:%3d",
                nombreEquipo, getPartidosJugados(), victorias, empates,
                derrotas, golesFavor, golesContra, getDiferencia(), puntos);
    }
}
