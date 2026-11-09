package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.TipoTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.Torneo;
import pruebas.ClubDePrueba;

/** HU-TOR-EMP-01 · Inscribirme en un torneo individual (empleado). */
class HU_TOR_EMP_01_EmpleadosTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_unEmpleadoSinTurnoQuedaInscrito() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0); // Sábado.
        p.club.inscribirEnTorneoIndividual(p.ent3, "Copa Tenis", null, HOY);   // ent3 no tiene turnos.
        p.club.inscribirEnTorneoIndividual(p.fisio2, "Copa Tenis", null, HOY); // fisio2 solo trabaja domingos.
        assertEquals(2, t.getInscripciones().size());
    }

    @Test
    void CA2_enUnCompetitivoNoPagaNiSumaAlRecaudado() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.COMPETITIVO, 8, 50000);
        InscripcionTorneo ins = p.club.inscribirEnTorneoIndividual(p.ent3, "Copa Tenis", null, HOY);
        p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY);
        assertEquals(0, ins.getTarifaPagada(), 0.01);
        assertEquals(50000, t.calcularBolsa(), 0.01);
    }

    @Test
    void CA3_siEstaDeTurnoElDiaDelTorneoSeRechaza() {
        TorneosDePrueba.tenis(p, TipoTorneo.COMPETITIVO, 8, 50000);   // Sábado: fisio1 trabaja.
        TorneosDePrueba.tenisDomingo(p, TipoTorneo.RECREATIVO, 8, 0);  // Domingo: fisio2 trabaja.
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirEnTorneoIndividual(p.fisio1, "Copa Tenis", null, HOY));
        assertTrue(e.getMessage().toLowerCase().contains("turno"), e.getMessage());
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEnTorneoIndividual(p.fisio2, "Tenis Domingo", null, HOY));
    }

    @Test
    void CA4_noParticipaEnTorneosDeConjunto() {
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirEnTorneoIndividual(p.ent3, "Mini Fútbol", null, HOY));
        assertTrue(e.getMessage().toLowerCase().contains("individuales"), e.getMessage());
        assertThrows(ReglaNegocioException.class, () -> p.club.inscribirEquipoEnTorneo(p.fisio1, "Mini Fútbol", "Fútbol A", HOY));
    }

    @Test
    void tieneTurnoElUsaElDiaDeLaSemana() {
        assertTrue(p.fisio1.tieneTurnoEl(SABADO));
        assertFalse(p.fisio1.tieneTurnoEl(DOMINGO));
        assertTrue(p.fisio2.tieneTurnoEl(DOMINGO));
        assertFalse(p.ent3.tieneTurnoEl(SABADO));
    }
}
