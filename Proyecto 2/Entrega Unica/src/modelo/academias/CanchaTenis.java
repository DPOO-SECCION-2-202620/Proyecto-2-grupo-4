package modelo.academias; // Paquete de academias.

import modelo.enums.Disciplina; // Disciplina.

/** Cancha de tenis: 2 jugadores (sencillos) o 4 (dobles). */
public class CanchaTenis extends Instalacion { // Hereda de Instalacion.

    public CanchaTenis(String id) { super(id, 4); } // Capacidad máxima 4.

    @Override public Disciplina getDisciplina() { return Disciplina.TENIS; } // Disciplina tenis.
    @Override public String getTipo() { return "CANCHA_TENIS"; }             // Tipo.

    @Override // Regla de cantidad.

    public boolean admiteJugadores(int cantidad, String modalidad) {
        return (cantidad == 2 && SENCILLOS.equals(modalidad))
            || (cantidad == 4 && DOBLES.equals(modalidad));
    }
}
