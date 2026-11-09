package integracion;

import static org.junit.jupiter.api.Assertions.*;
import static pruebas.ClubDePrueba.*;

import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.EstadoTorneo;
import modelo.enums.Nivel;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.torneos.Torneo;
import pruebas.ClubDePrueba;

/** HU-TOR-ADM-01 · Crear un torneo. */
class HU_TOR_ADM_01_CrearTorneoTest {

    private final ClubDePrueba p = new ClubDePrueba();

    private Torneo tenis(String nombre, TipoTorneo tipo, int cupos, double tarifa, double bono) throws ReglaNegocioException {
        return p.club.crearTorneoIndividual(p.admin, nombre, tipo, Disciplina.TENIS, Nivel.INTERMEDIO, false, cupos, tarifa, bono, SABADO, CIERRE, HOY);
    }

    @Test
    void CA1_seRegistraConInscripcionesAbiertasYCuposPreferenciales() throws ReglaNegocioException {
        Torneo t = tenis("Copa", TipoTorneo.RECREATIVO, 12, 0, 0.15);
        assertEquals(EstadoTorneo.POR_INICIAR, t.getEstado());
        assertTrue(t.estaAbierto());
        assertEquals(3, t.getCuposPreferenciales());
        assertEquals(9, t.getCuposDisponibles(TipoCupo.REGULAR, HOY));
        assertSame(t, p.club.buscarTorneo("Copa"));
    }

    @Test
    void CA2_recreativoConValorDeBonoNoCobraInscripcion() throws ReglaNegocioException {
        Torneo t = tenis("Copa", TipoTorneo.RECREATIVO, 8, 25000, 0.2);
        assertEquals(0, t.getTarifa(), 0.01);
        assertEquals(0.2, t.getPorcentajeBono(), 1e-9);
    }

    @Test
    void CA3_competitivoQuedaConSuTarifa() throws ReglaNegocioException {
        Torneo t = p.club.crearTorneoConjunto(p.admin, "Mini", TipoTorneo.COMPETITIVO, Deporte.FUTBOL, Categoria.MAYORES, 4, 100000, 0, SABADO, CIERRE, HOY);
        assertEquals(100000, t.getTarifa(), 0.01);
    }

    @Test
    void CA4_cuposInvalidosOCompetitivoSinTarifaSeRechazanIndicandoElDato() {
        ReglaNegocioException e1 = assertThrows(ReglaNegocioException.class, () -> tenis("A", TipoTorneo.RECREATIVO, 0, 0, 0.1));
        assertTrue(e1.getMessage().toLowerCase().contains("cupos"), e1.getMessage());
        ReglaNegocioException e2 = assertThrows(ReglaNegocioException.class, () -> tenis("B", TipoTorneo.COMPETITIVO, 8, 0, 0));
        assertTrue(e2.getMessage().toLowerCase().contains("tarifa"), e2.getMessage());
        assertTrue(p.club.getTorneos().isEmpty());
    }

    @Test
    void CA5_variosTorneosActivosConviven() throws ReglaNegocioException {
        Torneo t1 = tenis("Copa", TipoTorneo.RECREATIVO, 4, 0, 0.1);
        t1.setEstado(EstadoTorneo.EN_CURSO);
        tenis("Copa 2", TipoTorneo.COMPETITIVO, 4, 30000, 0);
        assertEquals(2, p.club.getTorneos().size());
    }

    @Test
    void elNombreDebeSerUnico() throws ReglaNegocioException {
        tenis("Copa", TipoTorneo.RECREATIVO, 4, 0, 0.1);
        assertThrows(ReglaNegocioException.class, () -> tenis("copa", TipoTorneo.RECREATIVO, 4, 0, 0.1));
    }
}
