package modelo.usuarios; // Paquete de usuarios.

import java.time.LocalTime;            // Hora del día sin fecha.
import java.util.ArrayList;            // Implementación de lista dinámica.
import java.util.List;                 // Interfaz de lista.
import modelo.entrenamiento.Turno;     // Turno semanal de trabajo.
import java.time.LocalDate;            // Fecha (Proyecto 2: día del torneo).
import java.util.EnumMap;              // Mapa con claves de enum.
import java.util.Map;                  // Mapa.
import modelo.enums.Disciplina;        // Disciplina individual.
import modelo.enums.Nivel;             // Nivel declarado.

/**
 * Empleado del club (entrenador o fisioterapeuta). Tiene turnos semanales
 * y puede tener un código de descuento para la tienda.
 */
public abstract class Empleado extends Usuario { // Abstracta: solo existen Entrenador y Fisioterapeuta.

    private List<Turno> turnos; // Turnos semanales asignados al empleado.
    private Map<Disciplina, Nivel> nivelesDeclarados; // Proyecto 2: nivel con el que juega torneos individuales.

    public Empleado(String id, String nombre, String login, String password) { // Constructor.
        super(id, nombre, login, password); // Inicializa la parte de Usuario.
        this.turnos = new ArrayList<>();    // Empieza sin turnos.
        this.nivelesDeclarados = new EnumMap<>(Disciplina.class); // Sin niveles declarados.
    }

    /** Agrega un turno al empleado. */
    public void agregarTurno(Turno t) { // Recibe el turno.
        turnos.add(t); // Lo agrega a la lista.
    }

    /** Indica si el empleado está trabajando en un día y hora dados. */
    public boolean estaDisponible(String dia, LocalTime hora) {
        for (Turno t : turnos) {
            if (t.cubre(dia, hora)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Proyecto 2 (HU-TOR-EMP-01 CA-3): ¿el empleado tiene algún turno el día de la semana de esa fecha?
     * Los torneos se juegan en un solo día, así que basta con revisar el día de la semana.
     */
    
    public boolean tieneTurnoEl(LocalDate fecha) {
    	String dia = Turno.nombreDia(fecha);

        for(Turno t : this.turnos) {
           if (t.getDia().equals(dia)) {
              return true;
           }
        }

        return false;
     }

    /** Proyecto 2: declara el nivel con el que el empleado juega una disciplina en torneos. */
    public void declararNivel(Disciplina disciplina, Nivel nivel) { nivelesDeclarados.put(disciplina, nivel); }

    /** Nivel declarado en una disciplina (null si no lo ha declarado). */
    public Nivel getNivelDeclarado(Disciplina disciplina) { return nivelesDeclarados.get(disciplina); }

    public Map<Disciplina, Nivel> getNivelesDeclarados() { return nivelesDeclarados; }

    public List<Turno> getTurnos() { return turnos; } // Devuelve la lista de turnos (se modifica al aprobar cambios).
}
