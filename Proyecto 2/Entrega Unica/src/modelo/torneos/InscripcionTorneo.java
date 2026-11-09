package modelo.torneos;

import java.time.LocalDate;
import java.util.List;
import modelo.enums.TipoCupo;
import modelo.usuarios.Empleado;
import modelo.usuarios.Usuario;

/**
 * Inscripción en un torneo: puede ser de un jugador, de una pareja (dobles) o de un equipo.
 * Cada inscripción ocupa UN cupo del torneo.
 */
public abstract class InscripcionTorneo {

    private int id;              // Id dentro del torneo (1, 2, 3...).
    private LocalDate fecha;     // Fecha de inscripción.
    private TipoCupo tipoCupo;   // PREFERENCIAL o REGULAR (lo asigna el torneo).
    private double tarifaPagada; // Lo que pagó (0 en recreativos y para empleados).
    private double ranking;      // Ranking con el que se inscribió (-1 = sin ranking, ej. externos).

    public InscripcionTorneo(int id, LocalDate fecha, double ranking) {
        this.id = id;
        this.fecha = fecha;
        this.ranking = ranking;
        this.tipoCupo = TipoCupo.REGULAR; // Valor por defecto: el torneo lo cambia al inscribir.
        this.tarifaPagada = 0;
    }

    /** Usuarios que juegan bajo esta inscripción (1 jugador, 2 en dobles o los jugadores del equipo). */
    public abstract List<Usuario> getIntegrantes();

    /** Nombre que se muestra en el historial (jugador, "A / B" o nombre del equipo). */
    public abstract String getNombre();

    /** ¿Este usuario juega bajo esta inscripción? */
    public boolean incluye(Usuario u) {
        for (Usuario integrante : getIntegrantes()) {
            if (integrante == u) return true;
        }
        return false;
    }

    /** true si TODOS los integrantes son empleados (para la tarifa 0 y el premio "no aplica"). */
    public boolean esDeEmpleado() {
        List<Usuario> integrantes = getIntegrantes();
        if (integrantes.isEmpty()) return false;
        for (Usuario u : integrantes) {
            if (!(u instanceof Empleado)) return false;
        }
        return true;
    }

    /** ¿Tiene ranking? (los externos y estudiantes no tienen) */
    public boolean tieneRanking() { return ranking >= 0; }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public TipoCupo getTipoCupo() { return tipoCupo; }
    public void setTipoCupo(TipoCupo tipoCupo) { this.tipoCupo = tipoCupo; }
    public double getTarifaPagada() { return tarifaPagada; }
    public void setTarifaPagada(double tarifaPagada) { this.tarifaPagada = tarifaPagada; }
    public double getRanking() { return ranking; }

    @Override
    public String toString() {
        return String.format("#%d %s [%s, ranking %s, pagó $%,.0f]", id, getNombre(), tipoCupo,
                tieneRanking() ? String.format("%.0f", ranking) : "sin ranking", tarifaPagada);
    }
}
