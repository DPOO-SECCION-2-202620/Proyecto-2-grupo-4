package modelo.competencias;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.conjunto.Equipo;
import modelo.conjunto.EstadisticaPartido;
import modelo.usuarios.Socio;

public class InscripcionCompetencia {

    private Competencia competencia;
    private Equipo equipo;
    private List<Socio> nomina;
    private Map<Socio, Integer> amarillasAcumuladas;
    private Set<Socio> suspendidos;

    public InscripcionCompetencia(Competencia competencia, Equipo equipo,
            List<Socio> nomina) {

        this.competencia = competencia;
        this.equipo = equipo;
        this.nomina = new ArrayList<>(nomina);
        this.amarillasAcumuladas = new LinkedHashMap<>();
        this.suspendidos = new LinkedHashSet<>();
    }

    // Revisa si el jugador está en la nómina
    public boolean estaEnNomina(Socio s) {
        return nomina.contains(s);
    }

    // Revisa si puede jugar
    public boolean estaHabilitado(Socio s) {
        return estaEnNomina(s)
                && equipo.buscarInscripcionActiva(s) != null
                && !suspendidos.contains(s);
    }

    // Devuelve los jugadores habilitados
    public List<Socio> getHabilitados() {
        List<Socio> lista = new ArrayList<>();

        for (Socio s : nomina) {
            if (estaHabilitado(s)) {
                lista.add(s);
            }
        }

        return lista;
    }

    // Revisa si hay suficientes jugadores para competir
    public boolean tieneMinimoHabilitados() {
        return getHabilitados().size()
                >= competencia.getDeporte().getMinimoJugadores();
    }

    // Actualiza las sanciones después del partido
    public void actualizarSanciones(List<EstadisticaPartido> estadisticas) {
        suspendidos.clear();

        for (EstadisticaPartido e : estadisticas) {
            Socio jugador = e.getJugador();

            if (!estaEnNomina(jugador)) {
                continue;
            }

            int antes = amarillasAcumuladas.getOrDefault(jugador, 0);
            int despues = antes + e.getAmarillas();

            amarillasAcumuladas.put(jugador, despues);

            // Revisa si completó otro ciclo de dos amarillas
            boolean completoCiclo = despues / 2 > antes / 2;

            if (e.tieneRoja() || completoCiclo) {
                suspendidos.add(jugador);
            }
        }
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public List<Socio> getNomina() {
        return nomina;
    }

    public Map<Socio, Integer> getAmarillasAcumuladas() {
        return amarillasAcumuladas;
    }

    public Set<Socio> getSuspendidos() {
        return suspendidos;
    }

    @Override
    public String toString() {
        return equipo.getNombre() + " en " + competencia.getNombre()
                + " (nómina: " + nomina.size() + ")";
    }
}
