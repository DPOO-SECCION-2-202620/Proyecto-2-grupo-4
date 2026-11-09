package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.academias.Instalacion;
import modelo.academias.Reserva;
import pruebas.ClubDePrueba;

/** HU-SOC-01 · Reservar una instalación para práctica individual. */
class HU_SOC_01_ReservaTest {

    private final ClubDePrueba p = new ClubDePrueba();
    private final LocalDateTime bloque = LocalDateTime.of(2026, 11, 10, 8, 0);

    @Test
    void CA1_hayCupoSimultaneoYLaReservaSeAsociaALaInstalacion() throws ReglaNegocioException {
        Reserva r = p.club.reservar(p.socio(1), "TEN-1", bloque, 2, Instalacion.SENCILLOS);
        assertSame(p.club.buscarInstalacion("TEN-1"), r.getInstalacion());
        assertTrue(p.club.buscarInstalacion("TEN-1").getReservas().contains(r));
    }

    @Test
    void CA2_sinDisponibilidadSeRechazaEInforma() throws ReglaNegocioException {
        p.club.setMaxInstalacionesSimultaneas(modelo.enums.Disciplina.TENIS, 1);
        p.club.reservar(p.socio(1), "TEN-1", bloque, 2, Instalacion.SENCILLOS);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.reservar(p.socio(2), "TEN-2", bloque, 2, Instalacion.SENCILLOS));
        assertTrue(e.getMessage().toLowerCase().contains("no hay disponibilidad"), e.getMessage());
    }

    @Test
    void CA3_padelParaDosJugadoresSeRechazaPorqueEsDobles() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.reservar(p.socio(1), "PAD-1", bloque, 2, Instalacion.SENCILLOS));
        assertTrue(e.getMessage().toLowerCase().contains("dobles"), e.getMessage());
    }
}
