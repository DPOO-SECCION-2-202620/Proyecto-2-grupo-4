package integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import excepciones.ReglaNegocioException;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.usuarios.ParticipanteExterno;
import pruebas.ClubDePrueba;

/**
 * Registro de externos y estudiantes (supuesto 3.5 de las HU: "pueden participar ... falta definir quién los
 * registra"). El grupo decidió que se registran solos con un usuario ParticipanteExterno.
 */
class RegistroExternoTest {

    private final ClubDePrueba p = new ClubDePrueba();
    private final Map<Disciplina, Nivel> niveles = new EnumMap<>(Disciplina.class);

    @Test
    void cuentaNuevaPuedeAutenticarse() throws ReglaNegocioException {
        niveles.put(Disciplina.TENIS, Nivel.PRINCIPIANTE);
        ParticipanteExterno x = p.club.registrarExterno("Nueva", "555", "nueva", "clave", false, niveles);
        assertSame(x, p.club.autenticar("nueva", "clave"));
        assertEquals("X003", x.getId());
        assertEquals(Nivel.PRINCIPIANTE, x.getNivelDeclarado(Disciplina.TENIS));
    }

    @Test
    void loginRepetidoODatosIncompletosSeRechazan() {
        assertThrows(ReglaNegocioException.class, () -> p.club.registrarExterno("Otra", "556", "s1", "clave", false, niveles));
        assertThrows(ReglaNegocioException.class, () -> p.club.registrarExterno("Otra", "556", "otra", "  ", false, niveles));
        assertThrows(ReglaNegocioException.class, () -> p.club.registrarExterno("", "556", "otra", "clave", false, niveles));
        assertNull(p.club.buscarUsuarioPorLogin("otra"));
    }

    @Test
    void elPerfilMuestraSiEsEstudiante() throws ReglaNegocioException {
        ParticipanteExterno x = p.club.registrarExterno("Estudiante", "557", "est9", "clave", true, niveles);
        assertTrue(x.isEstudiante());
        assertTrue(x.getPerfil().contains("estudiante"));
    }
}
