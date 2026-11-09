package modelo.torneos;

import static modelo.torneos.Jugadores.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.usuarios.Socio;

/** Pruebas unitarias de cupos, inscripción y retiro (HU-TOR-SOC-01, SOC-02, SOC-03, EMP-01). */
class TorneoCuposTest {

    private TorneoIndividual torneo; // 10 cupos -> 2 preferenciales y 8 regulares.
    private Socio[] s;

    @BeforeEach
    void setUp() {
        torneo = new TorneoIndividual("Copa", TipoTorneo.RECREATIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, 10, 0, 0.15, INICIO, CIERRE);
        s = socios(12);
    }

    private TorneoIndividual torneoDeCupos(int cupos) {
        return new TorneoIndividual("T" + cupos, TipoTorneo.RECREATIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, cupos, 0, 0.15, INICIO, CIERRE);
    }

    @Test
    void cuposPreferencialesSonEl20PorCientoRedondeadoHaciaArriba() {
        assertEquals(2, torneoDeCupos(10).getCuposPreferenciales());
        assertEquals(2, torneoDeCupos(7).getCuposPreferenciales());
        assertEquals(1, torneoDeCupos(4).getCuposPreferenciales());
        assertEquals(1, torneoDeCupos(2).getCuposPreferenciales());
        assertEquals(3, torneoDeCupos(15).getCuposPreferenciales());
        assertEquals(3, torneoDeCupos(12).getCuposPreferenciales()); // HU-TOR-SOC-03 CA-1: 2,4 -> 3.
    }

    @Test
    void alCrearseTieneTodosLosCuposLibres() {
        assertEquals(2, torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
        assertEquals(8, torneo.getCuposDisponibles(TipoCupo.REGULAR, HOY));
    }

    @Test
    void lasPreferencialesSeLiberanEnLaFechaDeCierre() {
        assertFalse(torneo.preferencialesLiberados(CIERRE.minusDays(1)));
        assertTrue(torneo.preferencialesLiberados(CIERRE));
        assertEquals(0, torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, CIERRE));
        assertEquals(10, torneo.getCuposDisponibles(TipoCupo.REGULAR, CIERRE));
    }

    @Test
    void participanteNoElegibleOcupaCupoRegular() throws ReglaNegocioException {
        InscripcionTorneoIndividual ins = torneo.inscribir(uno(s[0]), 1000, false, HOY);
        assertEquals(TipoCupo.REGULAR, ins.getTipoCupo());
        assertEquals(7, torneo.getCuposDisponibles(TipoCupo.REGULAR, HOY));
        assertEquals(2, torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
    }

    @Test
    void participanteElegibleOcupaCupoPreferencial() throws ReglaNegocioException {
        InscripcionTorneoIndividual ins = torneo.inscribir(uno(s[0]), 1100, true, HOY);
        assertEquals(TipoCupo.PREFERENCIAL, ins.getTipoCupo());
        assertEquals(1, torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
    }

    @Test
    void elegibleSinPreferencialesLibresTomaUnRegular() throws ReglaNegocioException {
        torneo.inscribir(uno(s[0]), 1100, true, HOY);
        torneo.inscribir(uno(s[1]), 1090, true, HOY);
        assertEquals(TipoCupo.REGULAR, torneo.inscribir(uno(s[2]), 1080, true, HOY).getTipoCupo());
    }

    @Test
    void noElegibleEsRechazadoSiSoloQuedanPreferenciales() throws ReglaNegocioException {
        for (int i = 0; i < 8; i++) torneo.inscribir(uno(s[i]), 1000, false, HOY);
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(uno(s[8]), 1000, false, HOY));
        assertTrue(e.getMessage().toLowerCase().contains("reservados"), e.getMessage());
        assertEquals(8, torneo.getInscripciones().size());
    }

    @Test
    void despuesDelCierreLosPreferencialesVaciosSeUsanComoRegulares() throws ReglaNegocioException {
        for (int i = 0; i < 8; i++) torneo.inscribir(uno(s[i]), 1000, false, HOY);
        InscripcionTorneoIndividual ins = torneo.inscribir(uno(s[8]), 1000, false, CIERRE);
        assertEquals(TipoCupo.REGULAR, ins.getTipoCupo());
        assertEquals(9, torneo.getInscripciones().size());
    }

    @Test
    void torneoLlenoRechazaInscripcion() throws ReglaNegocioException {
        for (int i = 0; i < 10; i++) torneo.inscribir(uno(s[i]), 1000, false, CIERRE);
        assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(uno(s[10]), 1000, true, CIERRE));
    }

    @Test
    void noSePuedeInscribirDosVeces() throws ReglaNegocioException {
        torneo.inscribir(uno(s[0]), 1000, false, HOY);
        assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(uno(s[0]), 1000, false, HOY));
        assertEquals(1, torneo.getInscripciones().size());
    }

    @Test
    void enSencillosSeInscribeUnSoloJugador() {
        assertThrows(ReglaNegocioException.class, () -> torneo.inscribir(java.util.List.of(s[0], s[1]), 1000, false, HOY));
    }

    @Test
    void retirarLiberaElCupoRegular() throws ReglaNegocioException {
        torneo.inscribir(uno(s[0]), 1000, false, HOY);
        torneo.retirar(s[0]);
        assertEquals(8, torneo.getCuposDisponibles(TipoCupo.REGULAR, HOY));
        assertNull(torneo.buscarInscripcion(s[0]));
    }

    @Test
    void retirarUnPreferencialLoDevuelveComoPreferencial() throws ReglaNegocioException {
        torneo.inscribir(uno(s[0]), 1100, true, HOY);
        torneo.retirar(s[0]);
        assertEquals(2, torneo.getCuposDisponibles(TipoCupo.PREFERENCIAL, HOY));
    }

    @Test
    void retirarAQuienNoEstaInscritoSeRechaza() {
        assertThrows(ReglaNegocioException.class, () -> torneo.retirar(s[0]));
    }

    @Test
    void recreativoNoCobra() throws ReglaNegocioException {
        assertEquals(0, torneo.inscribir(uno(s[0]), 1000, false, HOY).getTarifaPagada());
    }

    @Test
    void competitivoCobraLaTarifaMenosAEmpleados() throws ReglaNegocioException {
        TorneoIndividual comp = new TorneoIndividual("Comp", TipoTorneo.COMPETITIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, 8, 50000, 0, INICIO, CIERRE);
        assertEquals(50000, comp.inscribir(uno(s[0]), 1000, false, HOY).getTarifaPagada());
        assertEquals(0, comp.inscribir(uno(empleado("F9")), -1, false, HOY).getTarifaPagada());
    }
}
