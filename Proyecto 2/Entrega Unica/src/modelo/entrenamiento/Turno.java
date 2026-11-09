package modelo.entrenamiento; // Paquete de entrenamiento y turnos.

import java.time.LocalTime; // Hora del día.

/** Turno semanal de un empleado: un día y un rango de horas. */
public class Turno { // Clase Turno.

    private String dia;          // Día de la semana en mayúsculas sin tilde ("LUNES", "MIERCOLES"...).
    private LocalTime horaInicio; // Hora de inicio (incluida).
    private LocalTime horaFin;    // Hora de fin (excluida).

    public Turno(String dia, LocalTime horaInicio, LocalTime horaFin) { // Constructor.
        this.dia = dia.toUpperCase(); // Normaliza a mayúsculas.
        this.horaInicio = horaInicio; // Guarda inicio.
        this.horaFin = horaFin;       // Guarda fin.
    }

    /** ¿El turno cubre ese día a esa hora? (inicio <= hora < fin) */
    public boolean cubre(String dia, LocalTime hora) {
        return this.dia.equalsIgnoreCase(dia)
            && !hora.isBefore(horaInicio)
            && hora.isBefore(horaFin);
    }

    /** Dos turnos son "iguales" si tienen el mismo día y horas (para buscar el turno actual). */
    public boolean mismoHorario(Turno otro) { // Recibe otro turno.
        return dia.equals(otro.dia) && horaInicio.equals(otro.horaInicio) && horaFin.equals(otro.horaFin); // Compara campos.
    }

    /**
     * Proyecto 2: nombre del día de la semana de una fecha, con el mismo formato de los turnos
     * ("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO").
     */
    public static String nombreDia(java.time.LocalDate fecha) {
        String[] nombres = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO" };
        return nombres[fecha.getDayOfWeek().getValue() - 1]; // getValue(): lunes = 1 ... domingo = 7.
    }

    public String getDia() { return dia; }                // Getter.
    public LocalTime getHoraInicio() { return horaInicio; } // Getter.
    public LocalTime getHoraFin() { return horaFin; }     // Getter.

    @Override public String toString() { return dia + " " + horaInicio + "-" + horaFin; } // Ej: "LUNES 06:00-14:00".
}
