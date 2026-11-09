package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.EstadoSolicitud;
import pruebas.ClubDePrueba;

/** HU-ADM-02 · Revisar solicitudes de cambio de turno. */
class HU_ADM_02_CambioTurnoTest {

    private final ClubDePrueba p = new ClubDePrueba();

    private Turno lunes() { return new Turno("LUNES", LocalTime.of(6, 0), LocalTime.of(22, 0)); }
    private Turno domingo() { return new Turno("DOMINGO", LocalTime.of(6, 0), LocalTime.of(22, 0)); }

    @Test
    void CA1_siSeMantieneLaCoberturaElCambioSeRegistra() throws ReglaNegocioException {
        p.ent3.agregarTurno(lunes()); // Tercer entrenador el lunes: sobra uno.
        SolicitudCambioTurno s = p.club.solicitarCambioTurno(p.ent3, lunes(), domingo());
        assertTrue(p.club.aprobarCambioTurno(p.admin, s));
        assertEquals(EstadoSolicitud.APROBADA, s.getEstado());
        assertTrue(p.ent3.getTurnos().stream().anyMatch(t -> t.getDia().equals("DOMINGO")));
    }

    @Test
    void CA2_siQuedanMenosDeDosEntrenadoresSeRechaza() throws ReglaNegocioException {
        SolicitudCambioTurno s = p.club.solicitarCambioTurno(p.ent1, lunes(), domingo());
        assertFalse(p.club.aprobarCambioTurno(p.admin, s));
        assertEquals(EstadoSolicitud.RECHAZADA, s.getEstado());
        assertTrue(p.ent1.getTurnos().stream().anyMatch(t -> t.getDia().equals("LUNES")), "El turno no debe cambiar");
    }

    @Test
    void CA2_siQuedaSinFisioterapeutaSeRechaza() throws ReglaNegocioException {
        SolicitudCambioTurno s = p.club.solicitarCambioTurno(p.fisio1, lunes(), domingo());
        assertFalse(p.club.aprobarCambioTurno(p.admin, s));
    }
}
