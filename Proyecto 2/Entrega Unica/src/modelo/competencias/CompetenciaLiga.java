
package modelo.competencias;

import java.util.ArrayList;
import java.util.List;
import modelo.enums.Categoria;
import modelo.enums.Deporte;

// Competencia donde todos los equipos juegan entre sí
public class CompetenciaLiga extends Competencia {

    private List<PosicionTabla> tabla;

    public CompetenciaLiga(String nombre, String entidad, String temporada,
            Deporte deporte, Categoria categoria, int nominaMinima) {

        super(nombre, entidad, temporada, deporte, categoria, nominaMinima);
        tabla = new ArrayList<>();
    }

    // Calcula los puntos de los equipos
    public void calcularPuntos() {
        tabla = PosicionTabla.construirTabla(
                getNombresEquipos(),
                getEnfrentamientosJugados(),
                getDeporte(),
                false);
    }

    // Calcula y devuelve la tabla de posiciones
    public List<PosicionTabla> calcularTablaPosiciones() {
        calcularPuntos();
        return tabla;
    }

    // Indica el formato de la competencia
    @Override
    public String getFormato() {
        return "LIGA";
    }
}
