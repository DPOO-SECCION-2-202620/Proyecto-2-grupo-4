 
package modelo.competencias;

import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.enums.Categoria;
import modelo.enums.Deporte;

public class CompetenciaFases extends Competencia {

    private List<Fase> fases;
    private int indiceFaseActual;
    private String campeon;

    public CompetenciaFases(String nombre, String entidad, String temporada,
            Deporte deporte, Categoria categoria, int nominaMinima) {

        super(nombre, entidad, temporada, deporte, categoria, nominaMinima);

        fases = new ArrayList<>();
        indiceFaseActual = 0;
        campeon = "";
    }

    // Agrega una fase
    public void agregarFase(Fase f) {
        fases.add(f);
    }

    // Devuelve la fase actual
    public Fase getFaseActual() {
        if (fases.isEmpty()) {
            return null;
        }

        return fases.get(indiceFaseActual);
    }

    // Devuelve el nombre de la fase actual
    public String getNombreFaseActual() {
        Fase f = getFaseActual();

        if (f == null) {
            return "(sin fases)";
        }

        return f.getNombre();
    }

    // Busca los resultados de una fase
    private List<Enfrentamiento> enfrentamientosDe(Fase f) {
        List<Enfrentamiento> lista = new ArrayList<>();

        for (Enfrentamiento e : getEnfrentamientosJugados()) {
            if (e.getFase().equals(f.getNombre())) {
                lista.add(e);
            }
        }

        return lista;
    }

    // Calcula los equipos que pasan a la siguiente fase
    public List<String> calcularClasificados() throws ReglaNegocioException {
        Fase f = getFaseActual();

        if (f == null) {
            throw new ReglaNegocioException(
                    "La competencia no tiene fases definidas.");
        }

        return f.calcularClasificados(
                enfrentamientosDe(f), getDeporte());
    }

    // Cierra la fase y avanza a la siguiente
    public void avanzarFase() throws ReglaNegocioException {
        Fase actual = getFaseActual();

        if (actual == null) {
            throw new ReglaNegocioException(
                    "La competencia no tiene fases definidas.");
        }

        if (actual.isFinalizada()) {
            throw new ReglaNegocioException("La competencia ya terminó.");
        }

        List<String> clasificados = calcularClasificados();
        actual.avanzar();

        if (indiceFaseActual + 1 < fases.size()) {
            indiceFaseActual++;
            getFaseActual().setParticipantes(clasificados);
        } else if (clasificados.size() == 1) {
            campeon = clasificados.get(0);
        }
    }

    @Override
    public String getFormato() {
        return "FASES";
    }

    public List<Fase> getFases() {
        return fases;
    }

    public int getIndiceFaseActual() {
        return indiceFaseActual;
    }

    public void setIndiceFaseActual(int i) {
        this.indiceFaseActual = i;
    }

    public String getCampeon() {
        return campeon;
    }

    public void setCampeon(String c) {
        if (c == null) {
            campeon = "";
        } else {
            campeon = c;
        }
    }
}
