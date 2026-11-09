package modelo.entrenamiento; // Paquete de entrenamiento.

import java.time.LocalDateTime;           // Fecha y hora.
import java.util.ArrayList;               // Lista dinámica.
import java.util.List;                    // Lista.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.academias.Instalacion;      // Instalación utilizada.
import modelo.enums.EstadoAsistencia;     // Estado de asistencia.
import modelo.usuarios.Entrenador;        // Entrenador responsable.
import modelo.usuarios.Socio;             // Jugadores convocados.

/** Sesión de entrenamiento planeada por un entrenador. */
public class SesionEntrenamiento { // Clase SesionEntrenamiento.

    private int id;                                // Identificador.
    private LocalDateTime fechaHora;               // Fecha y hora de inicio.
    private int duracion;                          // Duración en minutos.
    private Entrenador entrenador;                 // Entrenador responsable.
    private Instalacion instalacion;               // Instalación (null si es en la cancha del equipo de conjunto).
    private List<Socio> convocados;                // Jugadores convocados.
    private List<AsistenciaEvaluacion> asistencias; // Composición: asistencias registradas.
    private boolean finalizada;                    // Si ya terminó.

    public SesionEntrenamiento(int id, LocalDateTime fechaHora, int duracion, Entrenador entrenador,
                               Instalacion instalacion, List<Socio> convocados) { // Constructor.
        this.id = id;                               // Guarda el id.
        this.fechaHora = fechaHora;                 // Guarda la fecha/hora.
        this.duracion = duracion;                   // Guarda la duración.
        this.entrenador = entrenador;               // Guarda el entrenador.
        this.instalacion = instalacion;             // Guarda la instalación.
        this.convocados = new ArrayList<>(convocados); // Copia la lista de convocados.
        this.asistencias = new ArrayList<>();       // Sin asistencias.
        this.finalizada = false;                    // No ha terminado.
    }

    /** Marca la sesión como terminada; a partir de ahí se puede registrar la asistencia. */
    public void finalizar() throws ReglaNegocioException { // Puede fallar.
        if (finalizada) throw new ReglaNegocioException("La sesión " + id + " ya estaba finalizada."); // No dos veces.
        finalizada = true; // Cambia el estado.
    }

    /** Registra (o corrige) la asistencia y evaluación de un convocado. */
    public void registrarAsistencia(Socio jugador, EstadoAsistencia estado, int nota, String comentario) throws ReglaNegocioException {
        if (!finalizada) {
            throw new ReglaNegocioException("La sesión " + id + " aún no ha finalizado; no se puede registrar asistencia.");
        }
        if (!convocados.contains(jugador)) {
            throw new ReglaNegocioException(jugador.getNombre() + " no fue convocado a la sesión " + id + ".");
        }
        if (nota != 0 && (nota < 1 || nota > 5)) {
            throw new ReglaNegocioException("La nota debe estar entre 1 y 5 (0 = sin evaluación).");
        }
        if (estado == EstadoAsistencia.FALTO && nota != 0) {
            throw new ReglaNegocioException("Un jugador que faltó no puede tener evaluación.");
        }
        AsistenciaEvaluacion previa = buscarAsistencia(jugador);
        if (previa != null) {
            asistencias.remove(previa);
        }
        asistencias.add(new AsistenciaEvaluacion(jugador, estado, nota, comentario));
    }

    /** Agrega una asistencia cargada desde archivo. */
    public void agregarAsistenciaCargada(AsistenciaEvaluacion a) { asistencias.add(a); } // Sin validar.

    /** Busca la asistencia de un jugador (null si no hay). */
    public AsistenciaEvaluacion buscarAsistencia(Socio jugador) { // Recibe el jugador.
        for (AsistenciaEvaluacion a : asistencias) if (a.getJugador() == jugador) return a; // Busca.
        return null; // No hay.
    }

    /** Hora de fin de la sesión. */
    public LocalDateTime getFin() { return fechaHora.plusMinutes(duracion); } // Inicio + duración.

    /** ¿La sesión se cruza con un intervalo [inicio, inicio+minutos)? */
    public boolean seCruzaCon(LocalDateTime inicio, int minutos) { // Recibe el otro intervalo.
        return fechaHora.isBefore(inicio.plusMinutes(minutos)) && inicio.isBefore(getFin()); // Regla de cruce de intervalos.
    }

    /** Porcentaje de convocados que asistieron (a tiempo o tarde). */
    public double calcularPorcentajeAsistencia() { // Sin parámetros.
        if (convocados.isEmpty()) return 0; // Evita dividir por cero.
        int asistieron = 0; // Contador.
        for (AsistenciaEvaluacion a : asistencias) if (a.getEstado() != EstadoAsistencia.FALTO) asistieron++; // Cuenta.
        return 100.0 * asistieron / convocados.size(); // Porcentaje.
    }

    public int getId() { return id; }                                      // Getter.
    public LocalDateTime getFechaHora() { return fechaHora; }              // Getter.
    public int getDuracion() { return duracion; }                          // Getter.
    public Entrenador getEntrenador() { return entrenador; }               // Getter.
    public Instalacion getInstalacion() { return instalacion; }            // Getter.
    public List<Socio> getConvocados() { return convocados; }              // Getter.
    public List<AsistenciaEvaluacion> getAsistencias() { return asistencias; } // Getter.
    public boolean isFinalizada() { return finalizada; }                   // Getter.
    public void setFinalizada(boolean f) { this.finalizada = f; }          // Setter (persistencia).

    @Override // Texto.
    public String toString() { // Ej: "Sesión #1 2026-10-06T16:00 (90 min) - Carlos @ TEN-1 - 4 convocados [finalizada]".
        return "Sesión #" + id + " " + fechaHora + " (" + duracion + " min) - " + entrenador.getNombre()
                + " @ " + (instalacion == null ? "cancha del equipo" : instalacion.getId())
                + " - " + convocados.size() + " convocados" + (finalizada ? " [finalizada]" : "");
    }
}
