package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import logica.ClubDeportivo;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.EstadoTorneo;
import modelo.enums.Nivel;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.torneos.BonoDescuento;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.InscripcionTorneoEquipo;
import modelo.torneos.InscripcionTorneoIndividual;
import modelo.torneos.Premio;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Usuario;
import pruebas.ClubDePrueba;

/**
 * Guardar -> cargar conserva la información nueva del Proyecto 2 (torneos, inscripciones, encuentros, posiciones,
 * premios, bonos, externos, niveles declarados, entrenador de cada equipo y % del premio). El estado se arma con
 * los métodos "Cargado" para no depender de la lógica que todavía está marcada con TODO.
 */
class PersistenciaProyecto2Test {

    @Test
    void guardarYCargarConservaTorneosBonosYExternos(@TempDir Path carpeta) throws Exception {
        ClubDePrueba p = new ClubDePrueba();
        TorneoIndividual t = new TorneoIndividual("Copa", TipoTorneo.RECREATIVO, Disciplina.TENIS, Nivel.INTERMEDIO, false, 4, 0, 0.2,
                ClubDePrueba.SABADO, ClubDePrueba.CIERRE);
        InscripcionTorneo a = new InscripcionTorneoIndividual(1, ClubDePrueba.HOY, 1100, List.<Usuario>of(p.socio(1)));
        InscripcionTorneo b = new InscripcionTorneoIndividual(2, ClubDePrueba.HOY, -1, List.<Usuario>of(p.estudiante));
        a.setTipoCupo(TipoCupo.PREFERENCIAL);
        t.agregarInscripcionCargada(a);
        t.agregarInscripcionCargada(b);
        t.agregarEncuentroCargado(new EncuentroTorneo(1, "Final", a, b, 3, 1));
        t.agregarPosicionCargada(a);
        t.agregarPosicionCargada(b);
        BonoDescuento bono = new BonoDescuento(BonoDescuento.generarCodigo("Copa", "S001"), 0.2, "Copa", ClubDePrueba.SABADO);
        p.socio(1).agregarBono(bono);
        Premio premio = new Premio(1, a, 0);
        premio.agregarBono(bono);
        t.agregarPremioCargado(premio);
        t.setEstado(EstadoTorneo.FINALIZADO);
        p.club.getTorneos().add(t);

        TorneoConjunto tc = new TorneoConjunto("Mini", TipoTorneo.COMPETITIVO, Deporte.FUTBOL, Categoria.MAYORES, 4, 100000, 0,
                ClubDePrueba.SABADO, ClubDePrueba.CIERRE);
        InscripcionTorneoEquipo ie = new InscripcionTorneoEquipo(1, ClubDePrueba.HOY, 3, p.futbolA);
        ie.setTarifaPagada(100000);
        tc.agregarInscripcionCargada(ie);
        tc.agregarPremioCargado(new Premio(1, ie, 70000));
        p.club.getTorneos().add(tc);
        p.fisio1.declararNivel(Disciplina.PADEL, Nivel.AVANZADO);
        p.club.setPorcentajePremioCompetitivo(0.55);

        p.club.guardar(carpeta.toString());
        ClubDeportivo c = ClubDeportivo.cargar(carpeta.toString());

        Torneo t2 = c.buscarTorneo("Copa");
        assertEquals(EstadoTorneo.FINALIZADO, t2.getEstado());
        assertEquals(t.generarHistorial(), t2.generarHistorial());
        assertEquals(0.2, t2.getPorcentajeBono(), 1e-9);
        assertEquals(TipoCupo.PREFERENCIAL, t2.buscarInscripcion(1).getTipoCupo());
        assertEquals(2, t2.getPosiciones().size());
        assertSame(t2.buscarInscripcion(1), t2.getPosiciones().get(0));
        assertTrue(t2.getPremios().get(0).getBonos().get(0) == c.buscarUsuario("S001").getBonos().get(0),
                "El bono del premio y el del usuario deben ser el mismo objeto");

        Torneo tc2 = c.buscarTorneo("Mini");
        assertEquals(100000, tc2.getInscripciones().get(0).getTarifaPagada(), 0.01);
        assertEquals("Fútbol A", tc2.getInscripciones().get(0).getNombre());
        assertEquals(70000, tc2.getPremios().get(0).getMonto(), 0.01);

        ParticipanteExterno est = (ParticipanteExterno) c.buscarUsuario("X001");
        assertTrue(est.isEstudiante());
        assertEquals(Nivel.INTERMEDIO, est.getNivelDeclarado(Disciplina.TENIS));
        assertEquals(Nivel.AVANZADO, ((modelo.usuarios.Empleado) c.buscarUsuario("F001")).getNivelDeclarado(Disciplina.PADEL));
        assertEquals("E001", c.buscarEquipo("Fútbol A").getEntrenador().getId());
        assertEquals(0.55, c.getPorcentajePremioCompetitivo(), 1e-9);
    }

    @Test
    void losDatosDePruebaDelProyectoSeCarganYSeGuardanSinCambios(@TempDir Path carpeta) throws Exception {
        ClubDeportivo c = ClubDeportivo.cargar("datos");
        assertEquals(4, c.getTorneos().size());
        assertNotNull(c.buscarUsuarioPorLogin("jospina"));
        c.guardar(carpeta.toString());
        ClubDeportivo otra = ClubDeportivo.cargar(carpeta.toString());
        assertEquals(c.getUsuarios().size(), otra.getUsuarios().size());
        assertEquals(c.getTorneos().size(), otra.getTorneos().size());
    }
}
