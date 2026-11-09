package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoTorneo;
import modelo.torneos.Torneo;
import pruebas.ClubDePrueba;

/** HU-TOR-SOC-05 · Consultar torneos y su historial (socio). Usa las mismas consultas de HU-TOR-ADM-03. */
class HU_TOR_SOC_05_ConsultaTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_cadaTorneoConSuEstado() {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0).setEstado(EstadoTorneo.EN_CURSO);
        TorneosDePrueba.padel(p, TipoTorneo.RECREATIVO, 4, 0).setEstado(EstadoTorneo.FINALIZADO);
        assertEquals(1, p.club.consultarTorneos(EstadoTorneo.FINALIZADO).size());
        assertTrue(p.club.resumenTorneos(EstadoTorneo.EN_CURSO).contains("Copa Tenis"));
        assertFalse(p.club.resumenTorneos(EstadoTorneo.EN_CURSO).contains("Pádel"));
    }

    @Test
    void CA2_historialEnCualquierEstado() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0);
        p.club.inscribirEnTorneoIndividual(p.socio(1), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.inscribirEnTorneoIndividual(p.socio(2), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        assertTrue(p.club.consultarHistorialTorneo("Copa Tenis").contains("Inscripciones abiertas"));
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(t.buscarInscripcion(p.socio(2)).getId()));
        String h = p.club.consultarHistorialTorneo("Copa Tenis");
        assertTrue(h.contains("Socio 1") && h.contains("1º Socio 2"), h);
    }

    @Test
    void CA3_sinTorneosIndicaQueNoHay() {
        assertTrue(p.club.resumenTorneos(null).startsWith("No hay torneos"));
        assertTrue(p.club.resumenTorneos(EstadoTorneo.FINALIZADO).startsWith("No hay torneos"));
    }
}
