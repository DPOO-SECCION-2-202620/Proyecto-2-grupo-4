package modelo.usuarios;

import java.util.EnumMap;
import java.util.Map;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;

/**
 * Proyecto 2: persona externa al club o estudiante de la universidad que se registra
 * con login y password para participar en los torneos internos. No es socio: no reserva instalaciones,
 * no entra a equipos y no acumula puntos de fidelidad, pero sí puede ganar y usar bonos.
 */
public class ParticipanteExterno extends Usuario {

    private String documento;                         // Documento de identidad.
    private boolean esEstudiante;                     // true si es estudiante de la universidad.
    private Map<Disciplina, Nivel> nivelesDeclarados; // Nivel que declara en cada disciplina individual.

    public ParticipanteExterno(String id, String nombre, String login, String password, String documento,
                               boolean esEstudiante) {
        super(id, nombre, login, password);
        this.documento = documento;
        this.esEstudiante = esEstudiante;
        this.nivelesDeclarados = new EnumMap<>(Disciplina.class);
    }

    /** Los externos y estudiantes no acumulan puntos de fidelidad. */
    @Override
    public void acumularPuntos(double valorCompra) {
        // No hace nada a propósito.
    }

    /** No reservan instalaciones ni juegan partidos internos del club. */
    @Override
    public boolean puedeUsarInstalacionesYPartidos() {
        return false;
    }

    /** Declara el nivel en una disciplina (al registrarse o después). */
    public void declararNivel(Disciplina disciplina, Nivel nivel) { nivelesDeclarados.put(disciplina, nivel); }

    /** Nivel declarado (null si no lo ha declarado). */
    public Nivel getNivelDeclarado(Disciplina disciplina) { return nivelesDeclarados.get(disciplina); }

    public Map<Disciplina, Nivel> getNivelesDeclarados() { return nivelesDeclarados; }

    @Override
    public String getTipo() { return "EXTERNO"; }

    public String getDocumento() { return documento; }
    public boolean isEstudiante() { return esEstudiante; }

    /** Texto del perfil (se muestra si es estudiante). */
    public String getPerfil() {
        return getNombre() + " (" + getLogin() + ") - documento " + documento
                + (esEstudiante ? " - estudiante de la universidad" : " - participante externo")
                + " - niveles: " + nivelesDeclarados;
    }
}
