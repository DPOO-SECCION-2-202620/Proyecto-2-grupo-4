package modelo.torneos;

import java.time.LocalDate;
import java.util.List;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;

/** Utilidades para las pruebas unitarias de torneos (sin club). */
class Jugadores {

    static final LocalDate HOY = LocalDate.of(2026, 11, 3);
    static final LocalDate INICIO = LocalDate.of(2026, 11, 21);
    static final LocalDate CIERRE = LocalDate.of(2026, 11, 14);

    /** Crea n socios S1..Sn. */
    static Socio[] socios(int n) {
        Socio[] s = new Socio[n];
        for (int i = 0; i < n; i++) s[i] = new Socio("S" + (i + 1), "Jugador " + (i + 1), "j" + (i + 1), "x", LocalDate.of(2000, 1, 1));
        return s;
    }

    static Fisioterapeuta empleado(String id) {
        return new Fisioterapeuta(id, "Empleado " + id, id.toLowerCase(), "x");
    }

    static List<Usuario> uno(Usuario u) {
        return List.of(u);
    }
}
