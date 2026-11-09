package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Premio;
import modelo.torneos.Torneo;
import modelo.usuarios.Socio;
import pruebas.ClubDePrueba;

/** HU-TOR-ADM-02 · Finalizar un torneo y asignar premios. */
class HU_TOR_ADM_02_FinalizarTest {

    private final ClubDePrueba p = new ClubDePrueba();

    private int id(Torneo t, modelo.usuarios.Usuario u) {
        return t.buscarInscripcion(u).getId();
    }

    @Test
    void CA1_recreativoFinalizaYDaUnBonoACadaGanador() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0); // Bono de 15 %.
        p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", DESPUES_DEL_CIERRE);
        p.club.inscribirEquipoEnTorneo(p.socio(4), "Mini Fútbol", "Fútbol B", DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Mini Fútbol", HOY);
        p.club.finalizarTorneo(p.admin, "Mini Fútbol", List.of(id(t, p.socio(1)), id(t, p.socio(4))));
        assertEquals(EstadoTorneo.FINALIZADO, t.getEstado());
        for (Socio s : p.futbolA.getJugadoresActivos()) {
            assertEquals(1, s.getBonosDisponibles().size(), s.getNombre());
            assertEquals(0.15, s.getBonos().get(0).getPorcentaje(), 1e-9);
        }
        for (Socio s : p.futbolB.getJugadoresActivos()) assertTrue(s.getBonos().isEmpty());
    }

    @Test
    void CA2_competitivoDaAlGanadorElPorcentajeDelRecaudado() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.COMPETITIVO, 8, 50000);
        for (int i = 1; i <= 4; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(id(t, p.socio(3)), id(t, p.socio(1))));
        Premio premio = t.getPremios().get(0);
        assertTrue(premio.getInscripcion().incluye(p.socio(3)));
        assertEquals(200000 * 0.70, premio.getMonto(), 0.01); // 70 % por defecto (config.txt).
    }

    @Test
    void CA2_elPorcentajeSeConfigura() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.COMPETITIVO, 8, 50000);
        for (int i = 1; i <= 2; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.setPorcentajePremioCompetitivo(0.5);
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(id(t, p.socio(2))));
        assertEquals(50000, t.getPremios().get(0).getMonto(), 0.01);
    }

    @Test
    void CA3_siGanaUnEmpleadoElPremioPasaAlSiguienteNoEmpleado() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenisDomingo(p, TipoTorneo.COMPETITIVO, 8, 50000);
        p.club.inscribirEnTorneoIndividual(p.ent3, "Tenis Domingo", null, DESPUES_DEL_CIERRE);   // Empleado: no paga.
        for (int i = 1; i <= 2; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Tenis Domingo", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Tenis Domingo", HOY);
        p.club.finalizarTorneo(p.admin, "Tenis Domingo", List.of(id(t, p.ent3), id(t, p.socio(2)), id(t, p.socio(1))));
        assertEquals(1, t.getPremios().size());
        Premio premio = t.getPremios().get(0);
        assertTrue(premio.getInscripcion().incluye(p.socio(2)));
        assertEquals(2, premio.getPuesto());
        assertEquals(100000 * 0.70, premio.getMonto(), 0.01);
    }

    @Test
    void CA4_unTorneoFinalizadoNoSePuedeFinalizarDeNuevo() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0);
        for (int i = 1; i <= 2; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(id(t, p.socio(1))));
        List<Premio> antes = List.copyOf(t.getPremios());
        assertThrows(ReglaNegocioException.class, () -> p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(id(t, p.socio(2)))));
        assertEquals(antes, t.getPremios());
        assertEquals(1, p.socio(1).getBonos().size());
        assertTrue(p.socio(2).getBonos().isEmpty());
    }

    @Test
    void lasPosicionesDebenSerDeInscritos() throws ReglaNegocioException {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0);
        for (int i = 1; i <= 2; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        assertThrows(ReglaNegocioException.class, () -> p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(99)));
    }

    @Test
    void unEmpleadoGanadorDeUnRecreativoSiRecibeElBono() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenisDomingo(p, TipoTorneo.RECREATIVO, 8, 0);
        p.club.inscribirEnTorneoIndividual(p.ent3, "Tenis Domingo", null, DESPUES_DEL_CIERRE);
        p.club.inscribirEnTorneoIndividual(p.socio(1), "Tenis Domingo", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Tenis Domingo", HOY);
        InscripcionTorneo emp = t.buscarInscripcion(p.ent3);
        p.club.finalizarTorneo(p.admin, "Tenis Domingo", List.of(emp.getId()));
        assertEquals(1, p.ent3.getBonosDisponibles().size());
    }
}
