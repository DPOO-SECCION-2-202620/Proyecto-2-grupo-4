package logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.academias.Instalacion;
import modelo.academias.Reserva;
import modelo.conjunto.Equipo;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.usuarios.Entrenador;
import pruebas.ClubDePrueba;

/**
 * Pruebas de los métodos de la fachada que usan las consolas para no recorrer
 * las listas del club por su cuenta ni cambiar objetos del modelo directamente.
 */
class ConsultasConsolaTest {

    private ClubDePrueba prueba = new ClubDePrueba();
    private ClubDeportivo club = prueba.club;

    @Test
    void unSocioVeSoloLosEquiposEnLosQueJuega() {
        List<Equipo> equipos = club.getEquiposDeSocio(prueba.socio(1));
        assertEquals(1, equipos.size());
        assertSame(prueba.futbolA, equipos.get(0));

        List<Equipo> ninguno = club.getEquiposDeSocio(prueba.socio(12));
        assertTrue(ninguno.isEmpty());
    }

    @Test
    void laListaDeEntrenadoresNoIncluyeAOtrosEmpleados() {
        List<Entrenador> entrenadores = club.getEntrenadores();
        assertEquals(3, entrenadores.size());
        assertTrue(entrenadores.contains(prueba.ent1));
        assertTrue(entrenadores.contains(prueba.ent2));
        assertTrue(entrenadores.contains(prueba.ent3));
    }

    @Test
    void unSocioVeSoloSusReservas() throws ReglaNegocioException {
        LocalDateTime bloque = LocalDateTime.of(2026, 11, 10, 8, 0);
        Reserva mia = club.reservar(prueba.socio(1), "TEN-1", bloque, 2, Instalacion.SENCILLOS);
        club.reservar(prueba.socio(2), "TEN-2", bloque, 2, Instalacion.SENCILLOS);

        List<Reserva> reservas = club.getReservasDe(prueba.socio(1));
        assertEquals(1, reservas.size());
        assertSame(mia, reservas.get(0));
    }

    @Test
    void noSePuedeReservarParaUnDiaQueYaPaso() {
        LocalDateTime ayer = LocalDateTime.of(2026, 11, 2, 8, 0);
        try {
            club.reservar(prueba.socio(1), "TEN-1", ayer, 2, Instalacion.SENCILLOS, ClubDePrueba.HOY);
            fail("Debió rechazar la reserva en el pasado");
        } catch (ReglaNegocioException e) {
            assertTrue(e.getMessage().contains("ya pasó"), e.getMessage());
        }
        assertTrue(club.getReservasDe(prueba.socio(1)).isEmpty());
    }

    @Test
    void sePuedeReservarParaHoyOParaDespues() throws ReglaNegocioException {
        LocalDateTime hoyALasOcho = LocalDateTime.of(ClubDePrueba.HOY, LocalTime.of(8, 0));
        Reserva reserva = club.reservar(prueba.socio(1), "TEN-1", hoyALasOcho, 2, Instalacion.SENCILLOS, ClubDePrueba.HOY);
        assertEquals(1, club.getReservasDe(prueba.socio(1)).size());
        assertSame(reserva, club.getReservasDe(prueba.socio(1)).get(0));
    }

    @Test
    void unEmpleadoVeSoloSusSolicitudesDeCambio() {
        Turno actual = prueba.ent1.getTurnos().get(0);
        Turno propuesto = new Turno(actual.getDia(), LocalTime.of(8, 0), LocalTime.of(16, 0));
        SolicitudCambioTurno solicitud = club.solicitarCambioTurno(prueba.ent1, actual, propuesto);

        assertEquals(1, club.getSolicitudesDe(prueba.ent1).size());
        assertSame(solicitud, club.getSolicitudesDe(prueba.ent1).get(0));
        assertTrue(club.getSolicitudesDe(prueba.ent2).isEmpty());
    }

    @Test
    void externosYEmpleadosDeclaranSuNivelPorLaFachada() throws ReglaNegocioException {
        club.declararNivel(prueba.externo, Disciplina.PADEL, Nivel.AVANZADO);
        club.declararNivel(prueba.ent1, Disciplina.TENIS_MESA, Nivel.PRINCIPIANTE);
        assertEquals(Nivel.AVANZADO, prueba.externo.getNivelDeclarado(Disciplina.PADEL));
        assertEquals(Nivel.PRINCIPIANTE, prueba.ent1.getNivelDeclarado(Disciplina.TENIS_MESA));
    }

    @Test
    void unSocioNoDeclaraNivelPorqueLoTieneEnLaAcademia() {
        try {
            club.declararNivel(prueba.socio(1), Disciplina.TENIS, Nivel.AVANZADO);
            fail("Debió rechazar que un socio declare su nivel");
        } catch (ReglaNegocioException e) {
            assertTrue(e.getMessage().contains("declaran su nivel"), e.getMessage());
        }
    }

    @Test
    void elNombreRepetidoDeUnTorneoLoRechazaElModelo() throws ReglaNegocioException {
        LocalDate inicio = ClubDePrueba.DOMINGO;
        LocalDate cierre = ClubDePrueba.CIERRE;
        club.crearTorneoIndividual(prueba.admin, "Copa", modelo.enums.TipoTorneo.RECREATIVO, Disciplina.TENIS,
                Nivel.INTERMEDIO, false, 8, 0, 0.1, inicio, cierre, ClubDePrueba.HOY);
        try {
            club.crearTorneoIndividual(prueba.admin, "Copa", modelo.enums.TipoTorneo.RECREATIVO, Disciplina.TENIS,
                    Nivel.INTERMEDIO, false, 8, 0, 0.1, inicio, cierre, ClubDePrueba.HOY);
            fail("Debió rechazar el nombre repetido");
        } catch (ReglaNegocioException e) {
            assertTrue(e.getMessage().contains("Ya existe un torneo"), e.getMessage());
        }
        assertEquals(1, club.getTorneos().size());
    }
}
