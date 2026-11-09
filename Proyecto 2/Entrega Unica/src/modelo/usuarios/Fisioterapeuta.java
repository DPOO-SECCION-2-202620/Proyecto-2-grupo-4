package modelo.usuarios; // Paquete de usuarios.

import java.util.ArrayList;                        // Lista dinámica.
import java.util.List;                             // Interfaz de lista.
import modelo.entrenamiento.AsistenciaEvaluacion;  // Registro de asistencia.
import modelo.entrenamiento.SesionEntrenamiento;   // Sesión de entrenamiento.
import modelo.enums.EstadoAsistencia;              // Estado de asistencia.

/** Fisioterapeuta: consulta la carga física de los jugadores. */
public class Fisioterapeuta extends Empleado { // Fisioterapeuta ES UN Empleado.

    public Fisioterapeuta(String id, String nombre, String login, String password) { // Constructor.
        super(id, nombre, login, password); // Inicializa la parte común.
    }

    /**
     * Devuelve las sesiones finalizadas a las que el jugador realmente asistió (a tiempo o tarde).
     * Esa es su "carga física".
     */
    public List<SesionEntrenamiento> consultarCargaFisica(Socio jugador, List<SesionEntrenamiento> todasLasSesiones) {
        List<SesionEntrenamiento> resultado = new ArrayList<>();
        for (SesionEntrenamiento s : todasLasSesiones) {
            AsistenciaEvaluacion a = s.buscarAsistencia(jugador);
            if (a != null && a.getEstado() != EstadoAsistencia.FALTO) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    /** Suma los minutos de entrenamiento efectivos del jugador. */
    public int calcularMinutosDeCarga(Socio jugador, List<SesionEntrenamiento> todasLasSesiones) {
        int total = 0; // Acumulador de minutos.
        for (SesionEntrenamiento s : consultarCargaFisica(jugador, todasLasSesiones)) { // Solo sesiones con asistencia.
            total += s.getDuracion(); // Suma la duración de cada sesión.
        }
        return total; // Devuelve el total de minutos.
    }

    @Override // Sobrescribe getTipo.
    public String getTipo() { return "FISIOTERAPEUTA"; } // Tipo de usuario.
}
