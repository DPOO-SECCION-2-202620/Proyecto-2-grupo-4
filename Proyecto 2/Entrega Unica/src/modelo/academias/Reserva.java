package modelo.academias; // Paquete de academias de raqueta.

import java.time.LocalDateTime; // Fecha y hora.
import modelo.usuarios.Socio;   // Socio responsable.

/** Reserva de una instalación por un bloque horario. */
public class Reserva { // Clase Reserva.

    /** Duración fija de un bloque de reserva (supuesto de diseño: 1 hora). */
    public static final int DURACION_BLOQUE_MINUTOS = 60; // Constante.

    private int id;                   // Identificador de la reserva.
    private Instalacion instalacion;  // Instalación reservada.
    private Socio socioResponsable;   // Socio que hizo la reserva.
    private LocalDateTime fechaHora;  // Inicio del bloque.
    private int cantidadJugadores;    // Cuántos jugarán.
    private String modalidad;         // "SENCILLOS" o "DOBLES".

    public Reserva(int id, Instalacion instalacion, Socio socioResponsable, LocalDateTime fechaHora,
                   int cantidadJugadores, String modalidad) { // Constructor.
        this.id = id;                               // Guarda el id.
        this.instalacion = instalacion;             // Guarda la instalación.
        this.socioResponsable = socioResponsable;   // Guarda el responsable.
        this.fechaHora = fechaHora;                 // Guarda el inicio.
        this.cantidadJugadores = cantidadJugadores; // Guarda la cantidad.
        this.modalidad = modalidad;                 // Guarda la modalidad.
    }

    /** Hora en que termina el bloque. */
    public LocalDateTime getFin() { return fechaHora.plusMinutes(DURACION_BLOQUE_MINUTOS); } // Inicio + 60 min.

    /** ¿Este bloque se cruza con otro bloque que empieza en "inicio"? (intervalos [ini, fin) ) */
    public boolean seCruzaCon(LocalDateTime inicio) {
        LocalDateTime finOtro = inicio.plusMinutes(DURACION_BLOQUE_MINUTOS);
        return fechaHora.isBefore(finOtro) && inicio.isBefore(getFin());
    }

    public int getId() { return id; }                              // Getter.
    public Instalacion getInstalacion() { return instalacion; }    // Getter.
    public Socio getSocioResponsable() { return socioResponsable; } // Getter.
    public LocalDateTime getFechaHora() { return fechaHora; }      // Getter.
    public int getCantidadJugadores() { return cantidadJugadores; } // Getter.
    public String getModalidad() { return modalidad; }             // Getter.

    @Override // Texto.
    public String toString() { // Ej: "Reserva #1 TEN-1 2026-10-12T08:00 (2 jug., SENCILLOS) por Ana".
        return "Reserva #" + id + " " + instalacion.getId() + " " + fechaHora + " (" + cantidadJugadores + " jug., " + modalidad + ") por " + socioResponsable.getNombre();
    }
}
