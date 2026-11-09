package modelo.torneos;

import static modelo.torneos.Jugadores.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.enums.TipoTorneo;

/** Pruebas unitarias de Torneo.validarDatos (HU-TOR-ADM-01). */
class TorneoValidacionTest {

    private TorneoIndividual individual(TipoTorneo tipo, Disciplina d, boolean dobles, int cupos, double tarifa, double bono,
                                        LocalDate inicio, LocalDate cierre) {
        return new TorneoIndividual("T", tipo, d, Nivel.INTERMEDIO, dobles, cupos, tarifa, bono, inicio, cierre);
    }

    private void rechazaCon(TorneoIndividual t, String palabra) {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> t.validarDatos(HOY));
        assertTrue(e.getMessage().toLowerCase().contains(palabra), "El mensaje debe indicar el dato inválido: " + e.getMessage());
    }

    @Test
    void recreativoValidoQuedaSinTarifaYConSuBono() throws ReglaNegocioException {
        TorneoIndividual t = individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 10, 30000, 0.2, INICIO, CIERRE);
        t.validarDatos(HOY);
        assertEquals(0, t.getTarifa());
        assertEquals(0.2, t.getPorcentajeBono(), 1e-9);
    }

    @Test
    void recreativoSinValorDeBonoSeRechaza() {
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 10, 0, 0, INICIO, CIERRE), "bono");
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 10, 0, 1.5, INICIO, CIERRE), "bono");
    }

    @Test
    void competitivoConTarifaEsValidoYNoDaBono() throws ReglaNegocioException {
        TorneoIndividual t = individual(TipoTorneo.COMPETITIVO, Disciplina.TENIS, false, 8, 50000, 0.15, INICIO, CIERRE);
        t.validarDatos(HOY);
        assertEquals(50000, t.getTarifa());
        assertEquals(0, t.getPorcentajeBono());
    }

    @Test
    void competitivoSinTarifaSeRechaza() {
        rechazaCon(individual(TipoTorneo.COMPETITIVO, Disciplina.TENIS, false, 10, 0, 0, INICIO, CIERRE), "tarifa");
    }

    @Test
    void cuposCeroONegativosSeRechazan() {
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 0, 0, 0.1, INICIO, CIERRE), "cupos");
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, -3, 0, 0.1, INICIO, CIERRE), "cupos");
    }

    @Test
    void unSoloCupoEsValido() throws ReglaNegocioException {
        individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 1, 0, 0.1, INICIO, CIERRE).validarDatos(HOY);
    }

    @Test
    void fechasInvalidasSeRechazan() {
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 8, 0, 0.1, HOY.minusDays(1), HOY.minusDays(5)), "fecha");
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.TENIS, false, 8, 0, 0.1, INICIO, INICIO), "fecha");
    }

    @Test
    void padelEnSencillosSeRechaza() {
        rechazaCon(individual(TipoTorneo.RECREATIVO, Disciplina.PADEL, false, 8, 0, 0.1, INICIO, CIERRE), "dobles");
    }

    @Test
    void torneoDeConjuntoValido() throws ReglaNegocioException {
        new TorneoConjunto("F", TipoTorneo.COMPETITIVO, Deporte.FUTBOL, Categoria.MAYORES, 4, 100000, 0, INICIO, CIERRE).validarDatos(HOY);
    }
}
