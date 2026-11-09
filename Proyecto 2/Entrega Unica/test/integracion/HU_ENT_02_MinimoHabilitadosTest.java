package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.conjunto.Equipo;
import modelo.usuarios.Socio;
import pruebas.ClubDePrueba;

/** HU-ENT-02 · Verificar el mínimo de jugadores habilitados. */
class HU_ENT_02_MinimoHabilitadosTest {

    private final ClubDePrueba p = new ClubDePrueba();
    private int siguiente = 100;

    /** Inscribe n socios nuevos (mayores de edad) en el equipo y los devuelve. */
    private Socio[] agregarJugadores(Equipo e, int n) throws ReglaNegocioException {
        Socio[] nuevos = new Socio[n];
        for (int i = 0; i < n; i++) {
            Socio s = new Socio("S" + siguiente, "Nuevo " + siguiente, "n" + siguiente, "x", LocalDate.of(1999, 5, 5));
            p.club.registrarUsuario(s);
            p.club.inscribirJugadorEnEquipo(s, e.getNombre(), "JUGADOR", siguiente, ClubDePrueba.HOY);
            nuevos[i] = s;
            siguiente++;
        }
        return nuevos;
    }

    @Test
    void CA1_futbolConSieteHabilitadosPuedeDisputarse() throws ReglaNegocioException {
        agregarJugadores(p.futbolA, 4); // 3 que ya tenía + 4 = 7.
        String r = p.club.verificarMinimoHabilitados(p.ent1, "Fútbol A");
        assertTrue(r.contains("PUEDE disputarse"), r);
        assertTrue(r.contains("7 habilitados"), r);
    }

    @Test
    void CA2_baloncestoConCuatroHabilitadosNoPuedeYListaMotivos() throws ReglaNegocioException {
        Socio[] j = agregarJugadores(p.baloncesto, 6);
        p.baloncesto.buscarInscripcionActiva(j[0]).setSuspendidoProximoPartido(true); // Suspendido.
        p.baloncesto.darDeBaja(j[1]);                                                  // Inactivo.
        p.baloncesto.buscarInscripcionActiva(j[2]).setSuspendidoProximoPartido(true); // Suspendido.
        String r = p.club.verificarMinimoHabilitados(p.ent1, "Baloncesto");
        assertTrue(r.contains("NO puede jugarse"), r);
        assertTrue(r.contains("3 habilitados"), r);
        assertTrue(r.contains(j[0].getNombre() + ": suspendido"), r);
        assertTrue(r.contains(j[1].getNombre() + ": inactivo"), r);

        Map<Socio, String> noDisponibles = p.baloncesto.getJugadoresNoDisponibles();
        assertEquals(3, noDisponibles.size());
        assertFalse(noDisponibles.containsKey(j[3]), "Un jugador habilitado no aparece en la lista");
    }

    @Test
    void CA3_unJugadorSancionadoNoCuentaComoHabilitado() throws ReglaNegocioException {
        Socio[] j = agregarJugadores(p.futbolA, 4); // 7 habilitados.
        p.futbolA.buscarInscripcionActiva(j[0]).setSuspendidoProximoPartido(true);
        assertEquals(6, p.futbolA.getJugadoresHabilitados().size());
        assertFalse(p.futbolA.tieneMinimoJugadoresHabilitados());
        assertTrue(p.club.verificarMinimoHabilitados(p.ent1, "Fútbol A").contains("NO puede jugarse"));
    }

    @Test
    void soloElEntrenadorACargoPuedeConsultar() {
        assertThrows(ReglaNegocioException.class, () -> p.club.verificarMinimoHabilitados(p.ent2, "Fútbol A"));
    }
}
