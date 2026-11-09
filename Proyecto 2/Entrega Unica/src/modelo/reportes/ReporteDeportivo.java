package modelo.reportes; // Paquete de reportes.

import java.time.LocalDate;                        // Fecha.
import java.util.ArrayList;                        // Lista dinámica.
import java.util.LinkedHashMap;                    // Mapa ordenado.
import java.util.List;                             // Lista.
import java.util.Map;                              // Mapa.
import modelo.academias.EstadisticaPractica;       // Estadística de práctica (ELO).
import modelo.academias.InscripcionDisciplina;     // Inscripción a disciplina.
import modelo.academias.PartidoPractica;           // Partido de práctica.
import modelo.conjunto.Partido;                    // Partido de conjunto.
import modelo.entrenamiento.AsistenciaEvaluacion;  // Asistencia.
import modelo.entrenamiento.SesionEntrenamiento;   // Sesión.
import modelo.enums.EstadoAsistencia;              // Estado de asistencia.
import modelo.usuarios.Socio;                      // Jugador.

/** Reporte deportivo: ASISTENCIA a entrenamientos, RESULTADOS de partidos o evolución de RANKINGS. */
public class ReporteDeportivo extends Reporte { // Hereda de Reporte.

    private String tipo;                                 // "ASISTENCIA", "RESULTADOS" o "RANKINGS".
    private List<SesionEntrenamiento> sesiones;          // Datos de entrada.
    private List<Partido> partidos;                      // Datos de entrada.
    private List<PartidoPractica> partidosPractica;      // Datos de entrada.
    private List<InscripcionDisciplina> inscripciones;   // Datos de entrada.

    public ReporteDeportivo(String tipo, LocalDate inicio, LocalDate fin, List<SesionEntrenamiento> sesiones,
                            List<Partido> partidos, List<PartidoPractica> partidosPractica,
                            List<InscripcionDisciplina> inscripciones) { // Constructor.
        super(inicio, fin);                           // Parte común.
        this.tipo = tipo.toUpperCase();               // Normaliza.
        this.sesiones = sesiones;                     // Guarda.
        this.partidos = partidos;                     // Guarda.
        this.partidosPractica = partidosPractica;     // Guarda.
        this.inscripciones = inscripciones;           // Guarda.
    }

    /** Calcula las estadísticas según el tipo (método del UML) y las deja en el contenido. */
    public void calcularEstadisticas() { // Sin parámetros.
        switch (tipo) {                               // Según el tipo...
            case "ASISTENCIA": calcularAsistencia(); break; // Asistencia.
            case "RESULTADOS": calcularResultados(); break; // Resultados.
            default: calcularRankings();                     // Rankings.
        }
    }

    /** Por jugador: convocatorias, asistencias, % y nota promedio en el periodo. */
    private void calcularAsistencia() { // Sin parámetros.
        Map<Socio, int[]> datos = new LinkedHashMap<>(); // Por jugador: [convocatorias, asistencias, sumaNotas, numNotas].
        for (SesionEntrenamiento s : sesiones) {         // Recorre sesiones.
            if (!s.isFinalizada() || !enPeriodo(s.getFechaHora().toLocalDate())) continue; // Solo finalizadas y en el periodo.
            for (Socio j : s.getConvocados()) {          // Recorre convocados.
                int[] d = datos.computeIfAbsent(j, k -> new int[4]); // Obtiene (o crea) el arreglo del jugador.
                d[0]++;                                  // Una convocatoria más.
                AsistenciaEvaluacion a = s.buscarAsistencia(j); // Su asistencia.
                if (a != null && a.getEstado() != EstadoAsistencia.FALTO) d[1]++; // Asistió (a tiempo o tarde).
                if (a != null && a.tieneEvaluacion()) { d[2] += a.getNota(); d[3]++; } // Acumula notas.
            }
        }
        contenido.append("Jugador                      Conv  Asist   %     Nota prom.\n"); // Encabezado de la tabla.
        for (Map.Entry<Socio, int[]> e : datos.entrySet()) { // Una línea por jugador.
            int[] d = e.getValue(); // Datos del jugador.
            contenido.append(String.format("%-28s %4d %6d %5.0f%%  %s%n", e.getKey().getNombre(), d[0], d[1],
                    100.0 * d[1] / d[0], d[3] == 0 ? "-" : String.format("%.2f", (double) d[2] / d[3]))); // Línea formateada.
        }
    }

    /** Lista de partidos jugados en el periodo con su marcador. */
    private void calcularResultados() { // Sin parámetros.
        int n = 0; // Contador.
        for (Partido p : partidos) { // Recorre partidos de conjunto.
            if (p.isJugado() && enPeriodo(p.getFecha())) { contenido.append("  ").append(p).append("\n"); n++; } // Agrega.
        }
        for (PartidoPractica p : partidosPractica) { // Recorre partidos de práctica.
            if (p.isJugado() && enPeriodo(p.getFecha())) { // Solo jugados en el periodo.
                List<String> ganadores = new ArrayList<>(); // Nombres de ganadores.
                for (Socio s : p.getGanadores()) ganadores.add(s.getNombre()); // Llena.
                contenido.append("  ").append(p).append(" -> gana ").append(ganadores).append("\n"); n++; // Agrega.
            }
        }
        contenido.append("  Total de partidos jugados: ").append(n).append("\n"); // Total.
    }

    /** Evolución del ELO de cada inscripción en el periodo. */
    private void calcularRankings() { // Sin parámetros.
        for (InscripcionDisciplina ins : inscripciones) { // Recorre inscripciones.
            StringBuilder evol = new StringBuilder(String.format("%.0f", InscripcionDisciplina.ELO_INICIAL)); // Empieza en el ELO inicial.
            for (PartidoPractica p : partidosPractica) {  // Recorre partidos (están en orden de creación).
                if (!p.isJugado() || p.getDisciplina() != ins.getDisciplina() || !enPeriodo(p.getFecha())) continue; // Filtra.
                for (EstadisticaPractica e : p.getEstadisticas()) { // Busca la estadística del jugador.
                    if (e.getJugador() == ins.getSocio()) evol.append(String.format(" -> %.1f", e.getEloDespues())); // Agrega el nuevo valor.
                }
            }
            contenido.append(String.format("  %-24s %-10s %-12s %s%n", ins.getSocio().getNombre(), ins.getDisciplina(), ins.getNivel(), evol)); // Línea.
        }
    }

    @Override // Arma el reporte.
    public void generar() { // Sin parámetros.
        marcarGenerado();      // Fecha de generación.
        contenido.setLength(0); // Limpia.
        contenido.append("=== REPORTE DEPORTIVO (").append(tipo).append(") ").append(getPeriodoInicio()).append(" a ").append(getPeriodoFin()).append(" ===\n"); // Encabezado.
        calcularEstadisticas(); // Cuerpo.
    }

    public String getTipo() { return tipo; } // Getter.
}
