package modelo.competencias;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import excepciones.ReglaNegocioException;
import modelo.conjunto.Equipo;
import modelo.conjunto.EstadisticaPartido;
import modelo.conjunto.PartidoCompetencia;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.usuarios.Socio;

public abstract class Competencia {

    private String nombre;
    private String entidadOrganizadora;
    private String temporada;
    private Deporte deporte;
    private Categoria categoria;
    private int nominaMinima;
    private List<InscripcionCompetencia> inscripciones;
    private List<PartidoCompetencia> partidos;
    private List<Enfrentamiento> resultadosExternos;

    public Competencia(String nombre, String entidadOrganizadora,
            String temporada, Deporte deporte, Categoria categoria,
            int nominaMinima) {

        this.nombre = nombre;
        this.entidadOrganizadora = entidadOrganizadora;
        this.temporada = temporada;
        this.deporte = deporte;
        this.categoria = categoria;
        this.nominaMinima = nominaMinima;

        inscripciones = new ArrayList<>();
        partidos = new ArrayList<>();
        resultadosExternos = new ArrayList<>();
    }

    // Inscribe un equipo si cumple los requisitos
    public InscripcionCompetencia inscribirEquipo(
            Equipo equipo, List<Socio> nomina)
            throws ReglaNegocioException {

        if (buscarInscripcion(equipo) != null) {
            throw new ReglaNegocioException(
                    equipo.getNombre() + " ya está inscrito en " + nombre + ".");
        }

        if (equipo.getDeporte() != deporte) {
            throw new ReglaNegocioException(
                    "La competencia es de " + deporte
                    + " y el equipo de " + equipo.getDeporte() + ".");
        }

        if (equipo.getCategoria() != categoria) {
            throw new ReglaNegocioException(
                    "La competencia solo admite la categoría " + categoria + ".");
        }

        if (nomina.size() < nominaMinima) {
            throw new ReglaNegocioException(
                    "La nómina tiene " + nomina.size()
                    + " jugadores y el mínimo es " + nominaMinima + ".");
        }

        for (Socio s : nomina) {
            if (!equipo.estaHabilitado(s)) {
                throw new ReglaNegocioException(
                        s.getNombre() + " no está habilitado en "
                        + equipo.getNombre() + ".");
            }
        }

        InscripcionCompetencia ins =
                new InscripcionCompetencia(this, equipo, nomina);

        inscripciones.add(ins);
        return ins;
    }

    // Agrega una inscripción cargada desde archivo
    public void agregarInscripcionCargada(InscripcionCompetencia i) {
        inscripciones.add(i);
    }

    // Busca la inscripción de un equipo
    public InscripcionCompetencia buscarInscripcion(Equipo equipo) {
        for (InscripcionCompetencia i : inscripciones) {
            if (i.getEquipo() == equipo) {
                return i;
            }
        }

        return null;
    }

    // Agrega un partido oficial
    public void agregarPartido(PartidoCompetencia p) {
        partidos.add(p);
    }

    // Agrega un resultado externo
    public void registrarResultadoExterno(Enfrentamiento e) {
        resultadosExternos.add(e);
    }

    // Devuelve todos los resultados conocidos
    public List<Enfrentamiento> getEnfrentamientosJugados() {
        List<Enfrentamiento> lista = new ArrayList<>();

        for (PartidoCompetencia p : partidos) {
            if (p.isJugado()) {
                lista.add(p.comoEnfrentamiento());
            }
        }

        lista.addAll(resultadosExternos);
        return lista;
    }

    // Devuelve los nombres de los equipos de la competencia
    public List<String> getNombresEquipos() {
        List<String> nombres = new ArrayList<>();

        for (InscripcionCompetencia i : inscripciones) {
            agregarSinRepetir(nombres, i.getEquipo().getNombre());
        }

        for (Enfrentamiento e : getEnfrentamientosJugados()) {
            agregarSinRepetir(nombres, e.getEquipoA());
            agregarSinRepetir(nombres, e.getEquipoB());
        }

        for (PartidoCompetencia p : partidos) {
            agregarSinRepetir(nombres, p.getInstitucionRival());
        }

        return nombres;
    }

    // Agrega un nombre solo si no existe
    protected static void agregarSinRepetir(
            List<String> lista, String valor) {

        if (!lista.contains(valor)) {
            lista.add(valor);
        }
    }

    // Calcula la tabla de goleadores o jugadores con más puntos
    public List<Map.Entry<Socio, Integer>> calcularTablaGoleadores() {
        Map<Socio, Integer> totales = new LinkedHashMap<>();

        for (PartidoCompetencia p : partidos) {
            for (EstadisticaPartido e : p.getEstadisticas()) {
                Socio jugador = e.getJugador();
                int antes = totales.getOrDefault(jugador, 0);
                totales.put(jugador, antes + e.getValorPrincipal());
            }
        }

        List<Map.Entry<Socio, Integer>> lista = new ArrayList<>(totales.entrySet());
        lista.sort((a, b) -> b.getValue() - a.getValue());
        return lista;
    }


    // Busca al jugador con mayor valoración acumulada
    public Socio calcularJugadorMasDestacado() {
        Map<Socio, Double> totales = new LinkedHashMap<>();

        for (PartidoCompetencia p : partidos) {
            for (EstadisticaPartido e : p.getEstadisticas()) {
                Socio jugador = e.getJugador();
                double antes = totales.getOrDefault(jugador, 0.0);
                totales.put(jugador, antes + e.calcularValoracion());
            }
        }

        Socio mejor = null;
        double mejorValor = -1000000;

        for (Socio jugador : totales.keySet()) {
            double valor = totales.get(jugador);
            if (valor > mejorValor) {
                mejorValor = valor;
                mejor = jugador;
            }
        }

        return mejor;
    }

    // Cada subclase define su formato
    public abstract String getFormato();

    public String getNombre() {
        return nombre;
    }

    public String getEntidadOrganizadora() {
        return entidadOrganizadora;
    }

    public String getTemporada() {
        return temporada;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getNominaMinima() {
        return nominaMinima;
    }

    public List<InscripcionCompetencia> getInscripciones() {
        return inscripciones;
    }

    public List<PartidoCompetencia> getPartidos() {
        return partidos;
    }

    public List<Enfrentamiento> getResultadosExternos() {
        return resultadosExternos;
    }

    @Override
    public String toString() {
        return nombre + " " + temporada + " (" + getFormato()
                + ", " + deporte + ", " + categoria
                + ") - Organiza: " + entidadOrganizadora;
    }
}
