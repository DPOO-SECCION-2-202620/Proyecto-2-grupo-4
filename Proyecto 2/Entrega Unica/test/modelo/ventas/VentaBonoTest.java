package modelo.ventas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.torneos.BonoDescuento;
import modelo.usuarios.Entrenador;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;

/** Pruebas unitarias del bono aplicado a ventas (HU-TOR-12). */
class VentaBonoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 25);
    private Socio socio;
    private Articulo balon;
    private BonoDescuento bono;

    @BeforeEach
    void setUp() throws ReglaNegocioException {
        socio = new Socio("S1", "Ana", "ana", "x", LocalDate.of(2000, 1, 1));
        balon = new Articulo("Balón", "Fútbol", 100000);
        balon.agregarInventario(Articulo.TIENDA, 10);
        bono = new BonoDescuento("BONO-COPA-S1", 0.15, "Copa", HOY);
        socio.agregarBono(bono);
    }

    @Test
    void elBonoDescuentaElQuincePorCientoYQuedaUsado() throws ReglaNegocioException {
        VentaTienda v = new VentaTienda(1, HOY, socio);
        v.agregarItem(balon, 1);
        v.aplicarBono(bono);
        v.cerrar();
        assertEquals(15000, v.getDescuento(), 0.01);
        assertEquals((100000 - 15000) * 1.19, v.getTotal(), 0.01);
        assertTrue(bono.isUsado());
        assertTrue(socio.getBonosDisponibles().isEmpty());
    }

    @Test
    void funcionaEnLaCafeteria() throws ReglaNegocioException {
        VentaCafeteria v = new VentaCafeteria(1, HOY, socio);
        v.agregarItem(new Bebida("Jugo", 10000, modelo.enums.TipoBebida.FRIA), 2);
        v.aplicarBono(bono);
        v.calcularTotal();
        assertEquals(3000, v.getDescuento(), 0.01);
    }

    @Test
    void noSeAcumulaConElCodigoDeEmpleado() throws ReglaNegocioException {
        Entrenador emp = new Entrenador("E1", "Emp", "emp", "x");
        VentaTienda v = new VentaTienda(1, HOY, socio);
        v.agregarItem(balon, 1);
        v.aplicarDescuento(new CodigoDescuento("EMP-E1", emp));
        assertThrows(ReglaNegocioException.class, () -> v.aplicarBono(bono));
        assertFalse(bono.isUsado());
    }

    @Test
    void elCodigoNoSeAcumulaConElBono() throws ReglaNegocioException {
        Entrenador emp = new Entrenador("E1", "Emp", "emp", "x");
        VentaTienda v = new VentaTienda(1, HOY, socio);
        v.agregarItem(balon, 1);
        v.aplicarBono(bono);
        assertThrows(ReglaNegocioException.class, () -> v.aplicarDescuento(new CodigoDescuento("EMP-E1", emp)));
    }

    @Test
    void noSeAcumulaConPuntos() throws ReglaNegocioException {
        socio.setPuntosFidelidad(5000);
        VentaTienda v = new VentaTienda(1, HOY, socio);
        v.agregarItem(balon, 1);
        v.usarPuntosFidelidad();
        assertThrows(ReglaNegocioException.class, () -> v.aplicarBono(bono));

        VentaTienda v2 = new VentaTienda(2, HOY, socio);
        v2.agregarItem(balon, 1);
        v2.aplicarBono(bono);
        assertThrows(ReglaNegocioException.class, v2::usarPuntosFidelidad);
    }

    @Test
    void unBonoUsadoNoSePuedeVolverAAplicar() throws ReglaNegocioException {
        bono.usar();
        VentaTienda v = new VentaTienda(1, HOY, socio);
        v.agregarItem(balon, 1);
        assertThrows(ReglaNegocioException.class, () -> v.aplicarBono(bono));
    }

    @Test
    void laMensualidadNoAdmiteBono() {
        VentaMensualidad v = new VentaMensualidad(1, HOY, socio, "2026-11", 150000);
        assertThrows(ReglaNegocioException.class, () -> v.aplicarBono(bono));
    }

    @Test
    void unExternoPuedeUsarSuBonoPeroNoAcumulaPuntos() throws ReglaNegocioException {
        ParticipanteExterno x = new ParticipanteExterno("X1", "Ext", "ext", "x", "123", false);
        BonoDescuento b = new BonoDescuento("BONO-COPA-X1", 0.15, "Copa", HOY);
        x.agregarBono(b);
        VentaTienda v = new VentaTienda(1, HOY, x);
        v.agregarItem(balon, 1);
        v.aplicarBono(b);
        v.cerrar();
        assertTrue(b.isUsado());
        assertEquals(0, x.getPuntosFidelidad(), 0.01);
    }
}
