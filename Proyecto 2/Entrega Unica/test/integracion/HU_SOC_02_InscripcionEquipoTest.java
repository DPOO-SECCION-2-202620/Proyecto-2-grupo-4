package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.conjunto.InscripcionEquipo;
import modelo.usuarios.Socio;
import pruebas.ClubDePrueba;

/** HU-SOC-02 · Inscribirse en un equipo de conjunto. */
class HU_SOC_02_InscripcionEquipoTest {

    private final ClubDePrueba p = new ClubDePrueba();

    @Test
    void CA1_seRegistraConDorsalYPosicionYDescuentaUnCupo() throws ReglaNegocioException {
        int antes = p.baloncesto.contarActivos();
        p.club.inscribirJugadorEnEquipo(p.socio(10), "Baloncesto", "BASE", 7, ClubDePrueba.HOY);
        InscripcionEquipo i = p.baloncesto.buscarInscripcionActiva(p.socio(10));
        assertEquals(7, i.getNumeroCamiseta());
        assertEquals("BASE", i.getPosicion());
        assertEquals(antes + 1, p.baloncesto.contarActivos());
    }

    @Test
    void CA2_edadQueNoCorrespondeALaCategoriaSeRechaza() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirJugadorEnEquipo(p.socio(10), "Fútbol Sub-15", "DELANTERO", 9, ClubDePrueba.HOY));
        assertTrue(e.getMessage().toLowerCase().contains("categoría"), e.getMessage());
    }

    @Test
    void CA3_dorsalOcupadoSeRechazaYPideUnoLibre() throws ReglaNegocioException {
        p.club.inscribirJugadorEnEquipo(p.socio(10), "Baloncesto", "BASE", 7, ClubDePrueba.HOY);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirJugadorEnEquipo(p.socio(11), "Baloncesto", "ALERO", 7, ClubDePrueba.HOY));
        assertTrue(e.getMessage().toLowerCase().contains("dorsal libre"), e.getMessage());
    }

    @Test
    void CA4_soloUnEquipoPorDisciplina() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> p.club.inscribirJugadorEnEquipo(p.socio(1), "Fútbol B", "DELANTERO", 20, ClubDePrueba.HOY));
        assertTrue(e.getMessage().toLowerCase().contains("único equipo por disciplina"), e.getMessage());
    }

    @Test
    void unSocioSub15SiPuedeEntrarAlEquipoSub15() throws ReglaNegocioException {
        Socio nino = new Socio("S099", "Niño", "nino", "x", LocalDate.of(2012, 6, 1));
        p.club.registrarUsuario(nino);
        p.club.inscribirJugadorEnEquipo(nino, "Fútbol Sub-15", "DELANTERO", 9, ClubDePrueba.HOY);
        assertNotNull(p.futbolSub15.buscarInscripcionActiva(nino));
    }
}
