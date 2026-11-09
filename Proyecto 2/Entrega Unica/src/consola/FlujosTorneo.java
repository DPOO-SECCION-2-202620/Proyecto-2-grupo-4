package consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import logica.ClubDeportivo;
import modelo.conjunto.Equipo;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoCupo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Usuario;

/**
 * Pasos de consola sobre torneos que se repiten en varias aplicaciones. En lugar de copiar
 * el mismo código en cada consola, todas usan esta clase:
 * - consultar torneos y ver su historial (las tres consolas);
 * - inscribirse o salirse de un torneo individual (participantes y empleados);
 * - inscribir o retirar un equipo (socios y entrenadores).
 *
 * Esta clase no tiene reglas del negocio. Solo muestra las opciones que tienen sentido, pide
 * los datos, llama a la fachada {@link ClubDeportivo} y muestra el resultado. Las reglas de fondo
 * (cupos, nivel, turno del empleado, estado del torneo...) las revisa el modelo, que lanza
 * ReglaNegocioException si algo no se cumple.
 */
public class FlujosTorneo {

    private ClubDeportivo club;
    private LectorEntrada lector;

    public FlujosTorneo(ClubDeportivo club, LectorEntrada lector) {
        this.club = club;
        this.lector = lector;
    }

    // ---------------------------------------------------------------
    //  Consultas (HU-TOR-ADM-03 y HU-TOR-SOC-05)
    // ---------------------------------------------------------------

    /** Muestra los torneos con su estado. Se puede filtrar por un estado. */
    public void consultarTorneos() {
        EstadoTorneo filtro = null;
        if (!club.getTorneos().isEmpty() && lector.leerSiNo("¿Quiere ver solo los torneos de un estado?")) {
            filtro = lector.elegirObligatorio("Estado", EstadoTorneo.values());
        }
        System.out.println();
        System.out.println("--- Torneos ---");
        ConsolaBase.mostrarTexto(club.resumenTorneos(filtro).trim());
    }

    /** Muestra el historial completo de un torneo (encuentros, participantes y posiciones). */
    public void verHistorial() throws ReglaNegocioException {
        Torneo torneo = elegirTorneo(null);
        if (torneo != null) {
            System.out.println();
            System.out.println(club.consultarHistorialTorneo(torneo.getNombre()));
        }
    }

    /** Muestra los torneos en los que participa el usuario. */
    public void verMisTorneos(Usuario usuario) {
        ConsolaBase.mostrarLista("Mis torneos", club.consultarTorneosDe(usuario));
    }

    /** Muestra los torneos de un estado (o todos si estado es null) y devuelve el que se elija. */
    public Torneo elegirTorneo(EstadoTorneo estado) {
        return lector.elegir("Torneo", club.consultarTorneos(estado));
    }

    // ---------------------------------------------------------------
    //  Torneos individuales (HU-TOR-SOC-01, SOC-02 y EMP-01)
    // ---------------------------------------------------------------

    /** Inscribe al usuario (y a su pareja si el torneo es de dobles) en un torneo individual abierto. */
    public void inscribirmeEnIndividual(Usuario usuario, LocalDate hoy) throws ReglaNegocioException {
        List<Torneo> abiertos = new ArrayList<>();
        for (Torneo torneo : club.consultarTorneos(EstadoTorneo.POR_INICIAR)) {
            if (torneo instanceof TorneoIndividual && torneo.buscarInscripcion(usuario) == null) {
                abiertos.add(torneo);
            }
        }
        if (abiertos.isEmpty()) {
            System.out.println("  No hay torneos individuales abiertos en los que no esté inscrito.");
            return;
        }
        TorneoIndividual torneo = (TorneoIndividual) lector.elegir("Torneo individual", abiertos);
        if (torneo == null) {
            return;
        }

        String usuarioPareja = null;
        if (torneo.isDobles()) {
            usuarioPareja = lector.leerNombreUsuario("Usuario de su pareja");
            if (usuarioPareja.equalsIgnoreCase(usuario.getLogin())) {
                System.out.println("  [RECHAZADO] Su pareja tiene que ser otra persona.");
                return;
            }
        }
        InscripcionTorneo inscripcion = club.inscribirEnTorneoIndividual(usuario, torneo.getNombre(), usuarioPareja, hoy);
        System.out.printf("  [HECHO] Quedó inscrito en %s con cupo %s. Valor a pagar: $%,.0f%n",
                torneo.getNombre(), inscripcion.getTipoCupo(), inscripcion.getTarifaPagada());
        mostrarCuposLibres(torneo, hoy);
    }

    /** Saca al usuario de un torneo individual que todavía no ha empezado (el cupo queda libre). */
    public void salirmeDeIndividual(Usuario usuario, LocalDate hoy) throws ReglaNegocioException {
        List<Torneo> misTorneos = new ArrayList<>();
        for (Torneo torneo : club.consultarTorneosDe(usuario)) {
            if (torneo instanceof TorneoIndividual && torneo.getEstado() == EstadoTorneo.POR_INICIAR) {
                misTorneos.add(torneo);
            }
        }
        if (misTorneos.isEmpty()) {
            System.out.println("  No está inscrito en torneos individuales que no hayan empezado.");
            return;
        }
        Torneo torneo = lector.elegir("Torneo", misTorneos);
        if (torneo == null || !lector.confirmar("salirse de " + torneo.getNombre())) {
            return;
        }
        club.salirDeTorneo(usuario, torneo.getNombre());
        System.out.println("  [HECHO] Ya no está inscrito en " + torneo.getNombre() + ".");
        mostrarCuposLibres(torneo, hoy);
    }

    // ---------------------------------------------------------------
    //  Torneos de conjunto (HU-TOR-SOC-01 CA-2 y HU-TOR-SOC-02)
    // ---------------------------------------------------------------

    /** Inscribe uno de los equipos del usuario en un torneo de conjunto del mismo deporte y categoría. */
    public void inscribirEquipo(Usuario usuario, List<Equipo> misEquipos, LocalDate hoy) throws ReglaNegocioException {
        if (misEquipos.isEmpty()) {
            System.out.println("  No tiene equipos para inscribir.");
            return;
        }
        Equipo equipo = lector.elegir("Equipo", misEquipos);
        if (equipo == null) {
            return;
        }

        List<Torneo> torneosPosibles = new ArrayList<>();
        for (Torneo torneo : club.consultarTorneos(EstadoTorneo.POR_INICIAR)) {
            if (torneo instanceof TorneoConjunto) {
                TorneoConjunto torneoConjunto = (TorneoConjunto) torneo;
                boolean mismoDeporte = torneoConjunto.getDeporte() == equipo.getDeporte();
                boolean mismaCategoria = torneoConjunto.getCategoria() == equipo.getCategoria();
                boolean noInscrito = torneoConjunto.buscarInscripcionDeEquipo(equipo) == null;
                if (mismoDeporte && mismaCategoria && noInscrito) {
                    torneosPosibles.add(torneo);
                }
            }
        }
        if (torneosPosibles.isEmpty()) {
            System.out.println("  No hay torneos abiertos de " + equipo.getDeporte() + " " + equipo.getCategoria()
                    + " en los que " + equipo.getNombre() + " no esté inscrito.");
            return;
        }
        Torneo torneo = lector.elegir("Torneo de conjunto", torneosPosibles);
        if (torneo == null) {
            return;
        }
        InscripcionTorneo inscripcion = club.inscribirEquipoEnTorneo(usuario, torneo.getNombre(), equipo.getNombre(), hoy);
        System.out.printf("  [HECHO] %s quedó inscrito en %s (cupo %s). Valor a pagar: $%,.0f%n",
                equipo.getNombre(), torneo.getNombre(), inscripcion.getTipoCupo(), inscripcion.getTarifaPagada());
        mostrarCuposLibres(torneo, hoy);
    }

    /** Retira uno de los equipos del usuario de un torneo que todavía no ha empezado. */
    public void retirarEquipo(Usuario usuario, List<Equipo> misEquipos, LocalDate hoy) throws ReglaNegocioException {
        if (misEquipos.isEmpty()) {
            System.out.println("  No tiene equipos para retirar.");
            return;
        }
        Equipo equipo = lector.elegir("Equipo", misEquipos);
        if (equipo == null) {
            return;
        }

        List<Torneo> torneosDelEquipo = new ArrayList<>();
        for (Torneo torneo : club.consultarTorneos(EstadoTorneo.POR_INICIAR)) {
            if (torneo instanceof TorneoConjunto && ((TorneoConjunto) torneo).buscarInscripcionDeEquipo(equipo) != null) {
                torneosDelEquipo.add(torneo);
            }
        }
        if (torneosDelEquipo.isEmpty()) {
            System.out.println("  " + equipo.getNombre() + " no está inscrito en torneos que no hayan empezado.");
            return;
        }
        Torneo torneo = lector.elegir("Torneo", torneosDelEquipo);
        if (torneo == null || !lector.confirmar("retirar a " + equipo.getNombre() + " de " + torneo.getNombre())) {
            return;
        }
        club.retirarEquipoDeTorneo(usuario, torneo.getNombre(), equipo.getNombre());
        System.out.println("  [HECHO] " + equipo.getNombre() + " ya no está inscrito en " + torneo.getNombre() + ".");
        mostrarCuposLibres(torneo, hoy);
    }

    /** Muestra cuántos cupos regulares y preferenciales quedan libres en un torneo. */
    private void mostrarCuposLibres(Torneo torneo, LocalDate hoy) {
        int regulares = torneo.getCuposDisponibles(TipoCupo.REGULAR, hoy);
        int preferenciales = torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, hoy);
        System.out.println("  Cupos libres en " + torneo.getNombre() + ": " + regulares + " regulares y "
                + preferenciales + " preferenciales.");
    }
}
