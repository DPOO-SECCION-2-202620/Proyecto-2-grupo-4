package modelo.academias; // Paquete de academias.

import modelo.enums.Disciplina; // Disciplina.

/** Mesa de tenis de mesa: 2 o 4 jugadores. Si es techada no se permiten bebidas calientes. */
public class MesaDeTenisMesa extends Instalacion { // Hereda de Instalacion.

    private boolean techada; // Si la mesa está bajo techo.

    public MesaDeTenisMesa(String id, boolean techada) { // Constructor.
        super(id, 4);          // Capacidad 4.
        this.techada = techada; // Guarda si es techada.
    }

    @Override public Disciplina getDisciplina() { return Disciplina.TENIS_MESA; } // Disciplina.
    @Override public String getTipo() { return "MESA_TENIS_MESA"; }               // Tipo.
    @Override public boolean permiteBebidaCaliente() { return !techada; }         // Techada => no se permite.

    @Override // Regla de cantidad.

    public boolean admiteJugadores(int cantidad, String modalidad) {
        return (cantidad == 2 && SENCILLOS.equals(modalidad))
            || (cantidad == 4 && DOBLES.equals(modalidad));
    }

    public boolean isTechada() { return techada; } // Getter.
}
