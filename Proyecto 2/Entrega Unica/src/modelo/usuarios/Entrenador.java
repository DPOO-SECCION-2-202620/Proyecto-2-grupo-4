package modelo.usuarios; // Paquete de usuarios.

import java.time.LocalDateTime;                    // Fecha y hora.
import java.util.List;                             // Lista.
import excepciones.ReglaNegocioException;          // Excepción de reglas del negocio.
import modelo.academias.Instalacion;               // Instalación donde se entrena.
import modelo.conjunto.Partido;                    // Partido cuyo resultado se registra.
import modelo.entrenamiento.SesionEntrenamiento;   // Sesión de entrenamiento.
import modelo.enums.EstadoAsistencia;              // Estado de asistencia.

/** Entrenador: planea sesiones, registra asistencia/evaluaciones y resultados de partidos. */
public class Entrenador extends Empleado { // Entrenador ES UN Empleado.

    /**
     * Perfil de socio del entrenador, si también juega en las academias (puede ser null).
     * Permite aplicar la excepción de niveles: si uno de los jugadores es entrenador,
     * el partido de práctica se puede programar aunque los niveles no sean adyacentes.
     */
    private Socio perfilJugador; // Referencia opcional al Socio que representa al entrenador como jugador.

    public Entrenador(String id, String nombre, String login, String password) { // Constructor.
        super(id, nombre, login, password); // Inicializa la parte de Empleado/Usuario.
    }

    /** Crea una sesión de entrenamiento con este entrenador como responsable. */
    public SesionEntrenamiento planearSesion(int idSesion, LocalDateTime fechaHora, int duracionMinutos,
                                             Instalacion instalacion, List<Socio> convocados)
            throws ReglaNegocioException { // Puede lanzar excepción si los datos no son válidos.
        if (convocados == null || convocados.isEmpty()) { // Una sesión necesita al menos un convocado.
            throw new ReglaNegocioException("La sesión debe tener al menos un jugador convocado."); // Se rechaza.
        }
        if (duracionMinutos <= 0) { // La duración debe ser positiva.
            throw new ReglaNegocioException("La duración de la sesión debe ser mayor que cero."); // Se rechaza.
        }
        return new SesionEntrenamiento(idSesion, fechaHora, duracionMinutos, this, instalacion, convocados); // "this" = este entrenador.
    }

    /** Registra la asistencia (y opcionalmente la evaluación) de un jugador en una sesión propia. */
    public void registrarAsistenciaYEvaluacion(SesionEntrenamiento sesion, Socio jugador, EstadoAsistencia estado,
                                               int nota, String comentario) throws ReglaNegocioException {
        if (sesion.getEntrenador() != this) { // Solo el entrenador responsable puede registrar.
            throw new ReglaNegocioException("Solo el entrenador responsable puede registrar la asistencia."); // Se rechaza.
        }
        sesion.registrarAsistencia(jugador, estado, nota, comentario); // Delega en la sesión, que conoce sus reglas.
    }

    /** Registra el marcador final de un partido (interno o de competencia). */
    public void registrarResultadoPartido(Partido partido, int marcadorA, int marcadorB) throws ReglaNegocioException {
        partido.registrarResultado(marcadorA, marcadorB); // El partido valida y actualiza sanciones (polimorfismo).
    }

    @Override // Sobrescribe getTipo de Usuario.
    public String getTipo() { return "ENTRENADOR"; } // Tipo de usuario.

    public Socio getPerfilJugador() { return perfilJugador; } // Devuelve el perfil de jugador (o null).
    public void setPerfilJugador(Socio s) { this.perfilJugador = s; } // Asigna el perfil de jugador.
}
