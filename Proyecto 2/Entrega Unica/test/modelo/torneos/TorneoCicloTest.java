package modelo.torneos;

import static modelo.torneos.Jugadores.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.Disciplina;
import modelo.enums.EstadoTorneo;
import modelo.enums.Nivel;
import modelo.enums.TipoTorneo;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/** Pruebas unitarias del ciclo del torneo: iniciar, registrar encuentros, finalizar y premios (HU-TOR-ADM-02). */
class TorneoCicloTest {

    private final Socio[] s = socios(6);

    private TorneoIndividual recreativo(int inscritos) throws ReglaNegocioException {
        TorneoIndividual t = new TorneoIndividual("Copa", TipoTorneo.RECREATIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, 8, 0, 0.2, INICIO, CIERRE);
        for (int i = 0; i < inscritos; i++) t.inscribir(uno(s[i]), 1000 - i, false, CIERRE);
        return t;
    }

    private TorneoIndividual competitivo(int inscritos) throws ReglaNegocioException {
        TorneoIndividual t = new TorneoIndividual("Comp", TipoTorneo.COMPETITIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, 8, 50000, 0, INICIO, CIERRE);
        for (int i = 0; i < inscritos; i++) t.inscribir(uno(s[i]), 1000 - i, false, CIERRE);
        return t;
    }

    @Test
    void iniciarCierraLasInscripciones() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(3);
        t.iniciar(HOY);
        assertEquals(EstadoTorneo.EN_CURSO, t.getEstado());
        assertEquals("En curso", t.getEstadoTexto());
        assertThrows(ReglaNegocioException.class, () -> t.iniciar(HOY));
    }

    @Test
    void sinDosInscritosNoSePuedeIniciar() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(1);
        assertThrows(ReglaNegocioException.class, () -> t.iniciar(HOY));
        assertEquals(EstadoTorneo.POR_INICIAR, t.getEstado());
    }

    @Test
    void registrarEncuentroSoloConInscritosYTorneoEnCurso() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(3);
        InscripcionTorneo a = t.getInscripciones().get(0), b = t.getInscripciones().get(1);
        assertThrows(ReglaNegocioException.class, () -> t.registrarEncuentro(a, b, 2, 1, "Semifinal")); // Aún no inicia.
        t.iniciar(HOY);
        EncuentroTorneo e = t.registrarEncuentro(a, b, 2, 1, "Semifinal");
        assertSame(a, e.getGanador());
        assertEquals(1, t.getEncuentros().size());
        assertThrows(ReglaNegocioException.class, () -> t.registrarEncuentro(a, a, 1, 0, ""));
        assertThrows(ReglaNegocioException.class, () -> t.registrarEncuentro(a, null, 1, 0, ""));
        assertThrows(ReglaNegocioException.class, () -> t.registrarEncuentro(a, b, -1, 0, ""));
    }

    @Test
    void finalizarRecreativoDaUnBonoAlGanador() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(3);
        t.iniciar(HOY);
        List<InscripcionTorneo> ins = t.getInscripciones();
        t.finalizar(List.of(ins.get(2), ins.get(0), ins.get(1)), 0.7);
        assertEquals(EstadoTorneo.FINALIZADO, t.getEstado());
        assertSame(ins.get(2), t.getPosiciones().get(0));
        assertEquals(1, s[2].getBonosDisponibles().size());
        assertEquals(0.2, s[2].getBonos().get(0).getPorcentaje(), 1e-9);
        assertTrue(s[0].getBonos().isEmpty());
    }

    @Test
    void finalizarCompetitivoDaElPorcentajeDelRecaudadoAlGanador() throws ReglaNegocioException {
        TorneoIndividual t = competitivo(4); // 4 x 50.000 = 200.000.
        t.iniciar(HOY);
        assertEquals(200000, t.calcularBolsa(), 0.01);
        t.finalizar(List.of(t.getInscripciones().get(1)), 0.7);
        assertEquals(1, t.getPremios().size());
        assertEquals(140000, t.getPremios().get(0).getMonto(), 0.01);
        assertSame(t.getInscripciones().get(1), t.getPremios().get(0).getInscripcion());
    }

    @Test
    void siElGanadorEsEmpleadoElPremioPasaAlSiguiente() throws ReglaNegocioException {
        TorneoIndividual t = competitivo(2);
        Fisioterapeuta emp = empleado("F1");
        InscripcionTorneo insEmp = t.inscribir(uno(emp), -1, false, CIERRE);
        t.iniciar(HOY);
        assertEquals(100000, t.calcularBolsa(), 0.01); // El empleado no paga.
        InscripcionTorneo segundo = t.getInscripciones().get(0);
        t.finalizar(List.of(insEmp, segundo), 0.5);
        Premio p = t.getPremios().get(0);
        assertSame(segundo, p.getInscripcion());
        assertEquals(2, p.getPuesto());
        assertEquals(50000, p.getMonto(), 0.01);
    }

    @Test
    void enDoblesCadaIntegranteGanadorRecibeBono() throws ReglaNegocioException {
        TorneoIndividual t = new TorneoIndividual("Padel Fun", TipoTorneo.RECREATIVO, Disciplina.PADEL, Nivel.INTERMEDIO, true, 4, 0, 0.1, INICIO, CIERRE);
        InscripcionTorneo ganadores = t.inscribir(List.<Usuario>of(s[0], s[1]), 1000, false, CIERRE);
        t.inscribir(List.<Usuario>of(s[2], s[3]), 900, false, CIERRE);
        t.iniciar(HOY);
        t.finalizar(List.of(ganadores), 0.7);
        assertEquals(1, s[0].getBonosDisponibles().size());
        assertEquals(1, s[1].getBonosDisponibles().size());
        assertTrue(s[2].getBonos().isEmpty());
        assertEquals(2, t.getPremios().get(0).getBonos().size());
    }

    @Test
    void noSePuedeFinalizarDosVecesNiAntesDeIniciar() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(2);
        InscripcionTorneo a = t.getInscripciones().get(0);
        assertThrows(ReglaNegocioException.class, () -> t.finalizar(List.of(a), 0.7)); // No ha iniciado.
        t.iniciar(HOY);
        t.finalizar(List.of(a), 0.7);
        assertThrows(ReglaNegocioException.class, () -> t.finalizar(List.of(t.getInscripciones().get(1)), 0.7));
        assertEquals(1, t.getPremios().size());
        assertSame(a, t.getPosiciones().get(0));
    }

    @Test
    void lasPosicionesDebenSerInscripcionesDistintas() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(2);
        t.iniciar(HOY);
        InscripcionTorneo a = t.getInscripciones().get(0);
        InscripcionTorneo ajena = new InscripcionTorneoIndividual(99, HOY, 0, uno(s[5]));
        assertThrows(ReglaNegocioException.class, () -> t.finalizar(List.of(), 0.7));
        assertThrows(ReglaNegocioException.class, () -> t.finalizar(List.of(a, a), 0.7));
        assertThrows(ReglaNegocioException.class, () -> t.finalizar(List.of(ajena), 0.7));
        assertEquals(EstadoTorneo.EN_CURSO, t.getEstado());
    }

    @Test
    void elHistorialMuestraEncuentrosYPosiciones() throws ReglaNegocioException {
        TorneoIndividual t = recreativo(2);
        assertTrue(t.generarHistorial().contains("Inscripciones abiertas"));
        t.iniciar(HOY);
        t.registrarEncuentro(t.getInscripciones().get(0), t.getInscripciones().get(1), 6, 4, "Final");
        t.finalizar(List.of(t.getInscripciones().get(0), t.getInscripciones().get(1)), 0.7);
        String h = t.generarHistorial();
        assertTrue(h.contains("Final: Jugador 1 6 - 4 Jugador 2"), h);
        assertTrue(h.contains("1º Jugador 1"), h);
        assertTrue(h.contains("Finalizado"), h);
    }
}
