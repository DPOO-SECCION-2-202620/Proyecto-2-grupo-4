package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import pruebas.ClubDePrueba;

/** HU-TOR-SOC-02 · Desinscribirme de un torneo. */
class HU_TOR_SOC_02_DesinscribirseTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_seLiberaUnCupoDelMismoTipo() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        assertEquals(TipoCupo.PREFERENCIAL, p.club.inscribirEnTorneoIndividual(p.socio(1), "Copa Tenis", null, HOY).getTipoCupo());
        p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY); // Regular.
        p.club.salirDeTorneo(p.socio(1), "Copa Tenis");
        assertEquals(2, t.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
        p.club.salirDeTorneo(p.socio(8), "Copa Tenis");
        assertEquals(8, t.getCuposDisponibles(TipoCupo.REGULAR, HOY));
        assertTrue(t.getInscripciones().isEmpty());
    }

    @Test
    void CA2_conElTorneoIniciadoNoSePuedeSalir() throws ReglaNegocioException {
        TorneoIndividual t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        p.club.inscribirEnTorneoIndividual(p.socio(8), "Copa Tenis", null, HOY);
        t.setEstado(EstadoTorneo.EN_CURSO);
        assertThrows(ReglaNegocioException.class, () -> p.club.salirDeTorneo(p.socio(8), "Copa Tenis"));
        assertEquals(1, t.getInscripciones().size());
    }

    @Test
    void CA3_sinInscripcionSeRechazaEIndicaQueNoTieneInscripcion() {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 10, 0);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> p.club.salirDeTorneo(p.socio(8), "Copa Tenis"));
        assertTrue(e.getMessage().toLowerCase().contains("no tiene inscripción"), e.getMessage());
    }

    @Test
    void unSocioDelEquipoLoRetira() throws ReglaNegocioException {
        TorneoConjunto t = TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0);
        p.club.inscribirEquipoEnTorneo(p.socio(1), "Mini Fútbol", "Fútbol A", HOY);
        assertThrows(ReglaNegocioException.class, () -> p.club.retirarEquipoDeTorneo(p.socio(4), "Mini Fútbol", "Fútbol A"));
        p.club.retirarEquipoDeTorneo(p.socio(2), "Mini Fútbol", "Fútbol A");
        assertTrue(t.getInscripciones().isEmpty());
    }
}
