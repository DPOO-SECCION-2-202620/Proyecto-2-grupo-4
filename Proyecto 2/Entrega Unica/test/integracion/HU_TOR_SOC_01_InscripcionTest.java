package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import pruebas.ClubDePrueba;

/** HU-TOR-SOC-01 · Inscribirme en un torneo. */
class HU_TOR_SOC_01_InscripcionTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_individualConNivelCompatibleDescuentaUnCupo() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        InscripcionTorneo ins = p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY);
        assertEquals(TipoCupo.REGULAR, ins.getTipoCupo());
        assertEquals(7, t.getCuposDisponibles(TipoCupo.REGULAR, HOY));
    }

    @Test
    void CA1_enDoblesLaParejaOcupaUnSoloCupo() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.padel(p, TipoTorneo.RECREATIVO, 4, 0);
        InscripcionTorneo ins = p.club.inscribirEnTorneoIndividual(p.socio(1), "Pádel", "s2", HOY);
        assertEquals(2, ins.getIntegrantes().size());
        assertEquals(1, t.getInscripciones().size());
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.socio(3), "Pádel", null, HOY)); // Sin pareja.
    }

    @Test
    void CA1_unEstudianteTambienPuedeInscribirse() throws ReglaNegocioException {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        assertNotNull(p.club.inscribirEnTorneoIndividual(p.estudiante, "Copa Tenis", null, HOY));
    }

    @Test
    void CA2_unSocioInscribeASuEquipoCompleto() throws ReglaNegocioException {
        TorneoConjunto t = TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        InscripcionTorneo ins = p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", HOY);
        assertEquals("Fútbol A", ins.getNombre());
        assertEquals(1, t.getInscripciones().size());
        assertTrue(ins.incluye(p.socio(2)), "Todo el equipo queda inscrito");
    }

    @Test
    void CA2_unSocioQueNoEsDelEquipoNoPuedeInscribirlo() {
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol B", HOY));
    }

    @Test
    void CA3_competitivoDejaConstanciaDeLaTarifaYRecreativoNoCobra() throws ReglaNegocioException {
        TorneosDePrueba.tenis(p, TipoTorneo.COMPETITIVO, 10, 30000);
        assertEquals(30000, p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY).getTarifaPagada(), 0.01);
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        assertEquals(0, p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", HOY).getTarifaPagada(), 0.01);
    }

    @Test
    void CA4_sinCuposYaIniciadoONivelDistintoSeRechaza() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 2, 0); // 1 preferencial + 1 regular.
        p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.socio(7), "Copa Tenis", null, HOY)); // Sin cupos regulares.
        ReglaNegocioException nivel = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirEnTorneoIndividual(p.socio(9), "Copa Tenis", null, HOY)); // AVANZADO.
        assertTrue(nivel.getMessage().toLowerCase().contains("nivel"), nivel.getMessage());
        t.setEstado(EstadoTorneo.EN_CURSO);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.socio(1), "Copa Tenis", null, HOY));
    }

    @Test
    void CA4_equipoDeOtraCategoriaSeRechaza() {
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEquipoEnTorneo(p.ent1, "Mini Fútbol", "Fútbol Sub-15", HOY));
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEquipoEnTorneo(p.ent1, "Mini Fútbol", "Baloncesto", HOY));
    }

    @Test
    void CA5_noSePuedeInscribirDosVeces() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY));
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", HOY);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEquipoEnTorneo(p.socio(2), "Mini Fútbol", "Fútbol A", HOY));
        assertEquals(1, t.getInscripciones().size());
    }

    @Test
    void elAdministradorNoParticipa() {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.admin, "Copa Tenis", null, HOY));
    }
}
