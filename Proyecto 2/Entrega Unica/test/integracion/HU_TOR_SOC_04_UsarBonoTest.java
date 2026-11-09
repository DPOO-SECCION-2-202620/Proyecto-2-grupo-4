package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import excepciones.ReglaNegocioException;
import logica.ClubDeportivo;
import modelo.torneos.BonoDescuento;
import modelo.ventas.Articulo;
import modelo.ventas.VentaTienda;
import pruebas.ClubDePrueba;

/** HU-TOR-SOC-04 · Guardar y usar el bono de un torneo recreativo. */
class HU_TOR_SOC_04_UsarBonoTest {

    private final ClubDePrueba p = new ClubDePrueba();
    private BonoDescuento bono;

    @BeforeEach
    void setUp() throws ReglaNegocioException {
        Articulo raqueta = new Articulo("Raqueta", "Tenis", 200000);
        raqueta.agregarInventario(Articulo.TIENDA, 5);
        p.club.agregarArticulo(raqueta);
        bono = new BonoDescuento("BONO-COPA-S001", 0.15, "Copa", ClubDePrueba.HOY);
        p.socio(1).agregarBono(bono);
    }

    private VentaTienda venta() throws ReglaNegocioException {
        VentaTienda v = p.club.iniciarVentaTienda(p.socio(1), ClubDePrueba.HOY);
        v.agregarItem(p.club.buscarArticulo("Raqueta"), 1);
        return v;
    }

    @Test
    void CA1_seAplicaElDescuentoYElBonoQuedaUsado() throws ReglaNegocioException {
        VentaTienda v = venta();
        p.club.aplicarBono(v, "BONO-COPA-S001");
        p.club.confirmarVenta(v);
        assertEquals(30000, v.getDescuento(), 0.01);
        assertTrue(bono.isUsado());
    }

    @Test
    void CA2_conOtroDescuentoSeRechazaYLaCompraConservaElOtro() throws ReglaNegocioException {
        VentaTienda v = venta();
        v.aplicarDescuento(p.club.buscarCodigoDeEmpleado(p.ent1)); // 8 % para un socio.
        assertThrows(ReglaNegocioException.class, () -> p.club.aplicarBono(v, "BONO-COPA-S001"));
        v.calcularTotal();
        assertEquals(200000 * 0.08, v.getDescuento(), 0.01);
        assertFalse(bono.isUsado());

        p.socio(1).setPuntosFidelidad(1000);
        VentaTienda v2 = venta();
        v2.usarPuntosFidelidad();
        assertThrows(ReglaNegocioException.class, () -> p.club.aplicarBono(v2, "BONO-COPA-S001"));
    }

    @Test
    void CA3_unBonoUsadoSeRechaza() throws ReglaNegocioException {
        VentaTienda v = venta();
        p.club.aplicarBono(v, "BONO-COPA-S001");
        p.club.confirmarVenta(v);
        assertThrows(ReglaNegocioException.class, () -> p.club.aplicarBono(venta(), "BONO-COPA-S001"));
    }

    @Test
    void CA4_losBonosSeConsultanConValorYEstadoYSeGuardan(@TempDir Path carpeta) throws Exception {
        assertTrue(bono.toString().contains("15 %") && bono.toString().contains("DISPONIBLE"), bono.toString());
        p.club.guardar(carpeta.toString());
        ClubDeportivo cargado = ClubDeportivo.cargar(carpeta.toString());
        BonoDescuento b = cargado.buscarUsuario("S001").buscarBono("BONO-COPA-S001");
        assertNotNull(b);
        assertTrue(b.estaDisponible());
        VentaTienda v = venta();
        p.club.aplicarBono(v, "BONO-COPA-S001");
        p.club.confirmarVenta(v);
        assertTrue(bono.toString().contains("USADO"));
    }
}
