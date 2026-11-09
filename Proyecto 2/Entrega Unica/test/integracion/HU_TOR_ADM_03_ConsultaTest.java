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

/** HU-TOR-ADM-03 · Consultar el estado y el historial de los torneos (administrador). */
class HU_TOR_ADM_03_ConsultaTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_muestraCadaTorneoConSuEstado() {
        TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0).setEstado(EstadoTorneo.EN_CURSO);
        TorneosDePrueba.futbol(p, TipoTorneo.RECREATIVO, 4, 0).setEstado(EstadoTorneo.FINALIZADO);
        String r = p.club.resumenTorneos(null);
        assertTrue(r.contains("Copa Tenis") && r.contains("En curso"), r);
        assertTrue(r.contains("Mini Fútbol") && r.contains("Finalizado"), r);
        assertEquals(1, p.club.consultarTorneos(EstadoTorneo.EN_CURSO).size());
    }

    @Test
    void CA2_elHistorialMuestraEncuentrosParticipantesYPosiciones() throws ReglaNegocioException {
        Torneo t = TorneosDePrueba.tenis(p, TipoTorneo.RECREATIVO, 8, 0);
        for (int i = 1; i <= 3; i++) p.club.inscribirEnTorneoIndividual(p.socio(i), "Copa Tenis", null, DESPUES_DEL_CIERRE);
        p.club.iniciarTorneo(p.admin, "Copa Tenis", HOY);
        int a = t.buscarInscripcion(p.socio(1)).getId(), b = t.buscarInscripcion(p.socio(2)).getId();
        p.club.registrarEncuentroTorneo(p.admin, "Copa Tenis", a, b, 6, 3, "Semifinal");
        String enCurso = p.club.consultarHistorialTorneo("Copa Tenis");
        assertTrue(enCurso.contains("Socio 3"), enCurso);
        assertTrue(enCurso.contains("Semifinal: Socio 1 6 - 3 Socio 2"), enCurso);
        p.club.finalizarTorneo(p.admin, "Copa Tenis", List.of(a, b));
        String fin = p.club.consultarHistorialTorneo("Copa Tenis");
        assertTrue(fin.contains("1º Socio 1") && fin.contains("2º Socio 2"), fin);
    }

    @Test
    void CA3_sinTorneosIndicaQueNoHay() {
        assertEquals("No hay torneos registrados.", p.club.resumenTorneos(null));
    }

    @Test
    void unTorneoQueNoExisteSeInforma() {
        assertThrows(ReglaNegocioException.class, () -> p.club.consultarHistorialTorneo("No existe"));
    }
}
