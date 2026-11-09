package modelo.enums; // Paquete de enumeraciones.

/** Nivel asignado a un socio dentro de una disciplina individual. */
public enum Nivel { // El orden de declaración importa: se usa ordinal() para medir "distancia".
    PRINCIPIANTE, // ordinal() = 0
    INTERMEDIO,   // ordinal() = 1
    AVANZADO;     // ordinal() = 2

    /** Dos niveles son compatibles si son iguales o adyacentes (diferencia máxima de 1). */
	public boolean esCompatibleCon(Nivel otro) {
	    return Math.abs(this.ordinal() - otro.ordinal()) <= 1;
	}
}
