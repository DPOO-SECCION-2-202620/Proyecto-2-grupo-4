package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.conjunto.PartidoInterno;
import modelo.enums.Disciplina;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import pruebas.ClubDePrueba;

/** HU-TOR-SOC-03 · Acceder a un cupo preferencial. */
class HU_TOR_SOC_03_CuposPreferencialesTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_doceCuposReservanTresPreferenciales() {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 12, 0);
        assertEquals(3, t.getCuposPreferenciales());
        assertEquals(3, t.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
        assertEquals(9, t.getCuposDisponibles(TipoCupo.REGULAR, HOY));
    }

    @Test
    void CA2_elMejorDelRankingEntraEnPreferencialAunqueLosRegularesEstenLlenos() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 4, 0); // 1 preferencial + 3 regulares.
        for (int n = 6; n <= 8; n++) p.club.inscribirEnTorneoIndividual(p.socio(n), "Copa Tenis", null, HOY);
        assertEquals(0, t.getCuposDisponibles(TipoCupo.REGULAR, HOY));
        assertEquals(TipoCupo.PREFERENCIAL, p.club.inscribirEnTorneoIndividual(p.socio(1), "Copa Tenis", null, HOY).getTipoCupo());
    }

    @Test
    void CA3_quienNoEstaEntreLosMejoresNoOcupaUnPreferencial() throws ReglaNegocioException {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 4, 0);
        for (int n = 6; n <= 8; n++) {
            assertEquals(TipoCupo.REGULAR, p.club.inscribirEnTorneoIndividual(p.socio(n), "Copa Tenis", null, HOY).getTipoCupo());
        }
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirEnTorneoIndividual(p.socio(5), "Copa Tenis", null, HOY));
        assertTrue(e.getMessage().toLowerCase().contains("reservados"), e.getMessage());
    }

    @Test
    void CA4_enLaFechaLimiteLosPreferencialesVaciosSeLiberanComoRegulares() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 4, 0);
        for (int n = 6; n <= 8; n++) p.club.inscribirEnTorneoIndividual(p.socio(n), "Copa Tenis", null, HOY);
        assertEquals(1, t.getCuposDisponibles(TipoCupo.REGULAR, CIERRE));
        assertEquals(0, t.getCuposDisponibles(TipoCupo.PREFERENCIAL, CIERRE));
        assertEquals(TipoCupo.REGULAR, p.club.inscribirEnTorneoIndividual(p.socio(5), "Copa Tenis", null, CIERRE).getTipoCupo());
    }

    @Test
    void losExternosNoTienenRankingYNuncaTomanPreferencial() throws ReglaNegocioException {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        assertEquals(TipoCupo.REGULAR, p.club.inscribirEnTorneoIndividual(p.estudiante, "Copa Tenis", null, HOY).getTipoCupo());
        assertEquals(-1, p.club.calcularRankingIndividual(p.estudiante, Disciplina.TENIS), 0.01);
    }

    @Test
    void elegibilidadSegunElCorteDelRanking() {
        List<Double> rankings = List.of(1000.0, 1200.0, 900.0, 1100.0);
        assertTrue(p.club.esElegiblePreferencial(1100, rankings, 2));
        assertFalse(p.club.esElegiblePreferencial(1000, rankings, 2));
        assertFalse(p.club.esElegiblePreferencial(-1, rankings, 2));
        assertTrue(p.club.esElegiblePreferencial(10, List.of(), 1));
    }

    @Test
    void enEquiposElRankingSonLosPuntosEnPartidosInternos() throws ReglaNegocioException {
        PartidoInterno ab = p.club.programarPartidoInterno(HOY.minusDays(10), "Fútbol A", "Fútbol B");
        ab.fijarResultadoCargado(2, 1, true); // Gana A.
        PartidoInterno bc = p.club.programarPartidoInterno(HOY.minusDays(5), "Fútbol B", "Fútbol C");
        bc.fijarResultadoCargado(1, 1, true); // Empate.
        assertEquals(3, p.club.calcularPuntosInternos(p.futbolA), 0.01);
        assertEquals(1, p.club.calcularPuntosInternos(p.futbolB), 0.01);
        TorneoConjunto t = TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0); // 1 preferencial.
        assertEquals(TipoCupo.REGULAR, p.club.inscribirEquipoEnTorneo(p.socio(7), "Mini Fútbol", "Fútbol C", HOY).getTipoCupo());
        assertEquals(TipoCupo.PREFERENCIAL, p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", HOY).getTipoCupo());
        assertEquals(2, t.getInscripciones().size());
    }
}
