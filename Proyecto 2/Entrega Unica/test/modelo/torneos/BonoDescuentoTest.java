package modelo.torneos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;

/** Pruebas unitarias de BonoDescuento (HU-TOR-12). */
class BonoDescuentoTest {

    private BonoDescuento bono = new BonoDescuento("BONO-X-S1", 0.15, "Copa", LocalDate.of(2026, 11, 21));

    @Test
    void calculaElQuincePorCiento() {
        assertEquals(15000, bono.calcularDescuento(100000), 0.01);
        assertEquals(0, bono.calcularDescuento(-5), 0.01);
    }

    @Test
    void soloSePuedeUsarUnaVez() throws ReglaNegocioException {
        assertTrue(bono.estaDisponible());
        bono.usar();
        assertFalse(bono.estaDisponible());
        assertThrows(ReglaNegocioException.class, bono::usar);
    }

    @Test
    void elCodigoEsLegible() {
        assertEquals("BONO-COPATENIS2026-S001", BonoDescuento.generarCodigo("Copa Tenis 2026", "S001"));
    }
}
