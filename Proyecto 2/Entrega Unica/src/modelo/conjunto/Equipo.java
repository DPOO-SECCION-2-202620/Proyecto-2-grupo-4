
package modelo.conjunto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import excepciones.ReglaNegocioException;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Socio;

public class Equipo {

    private String nombre;
    private Deporte deporte;
    private Categoria categoria;
    private int cupoMaximo;
    private List<InscripcionEquipo> inscripciones;
    private Entrenador entrenador; // Proyecto 2: entrenador a cargo (HU-ENT-02 y torneos de conjunto).

    // Crea un equipo
    public Equipo(String nombre, Deporte deporte, Categoria categoria, int cupoMaximo) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.categoria = categoria;
        this.cupoMaximo = cupoMaximo;
        this.inscripciones = new ArrayList<>();
    }

    // Inscribe un jugador en el equipo
    public InscripcionEquipo inscribirJugador(Socio jugador, String posicion,
            int numeroCamiseta, LocalDate fechaReferencia) throws ReglaNegocioException {

        if (buscarInscripcionActiva(jugador) != null) {
            throw new ReglaNegocioException(jugador.getNombre() + " ya está inscrito en " + nombre + ".");
        }

        if (contarActivos() >= cupoMaximo) {
            throw new ReglaNegocioException("El equipo " + nombre + " ya alcanzó su cupo máximo (" + cupoMaximo + ").");
        }

        int edad = jugador.calcularEdad(fechaReferencia);

        if (!categoria.admiteEdad(edad)) {
            throw new ReglaNegocioException(jugador.getNombre() + " tiene " + edad
                    + " años y no corresponde a la categoría " + categoria + ".");
        }

        if (numeroCamiseta <= 0) {
            throw new ReglaNegocioException("El número de camiseta debe ser positivo.");
        }

        for (InscripcionEquipo i : inscripciones) {
            if (i.isActivo() && i.getNumeroCamiseta() == numeroCamiseta) {
                throw new ReglaNegocioException("El dorsal " + numeroCamiseta
                        + " ya está asignado a " + nombre + ". Elija un dorsal libre.");
            }
        }

        InscripcionEquipo nueva = new InscripcionEquipo(this, jugador, posicion, numeroCamiseta);
        inscripciones.add(nueva);
        return nueva;
    }

    // Agrega una inscripción cargada desde un archivo
    public void agregarInscripcionCargada(InscripcionEquipo i) {
        inscripciones.add(i);
    }

    // Da de baja a un jugador
    public void darDeBaja(Socio jugador) throws ReglaNegocioException {
        InscripcionEquipo i = buscarInscripcionActiva(jugador);

        if (i == null) {
            throw new ReglaNegocioException(jugador.getNombre() + " no está activo en " + nombre + ".");
        }

        i.setActivo(false);
    }

    // Busca la inscripción activa de un jugador
    public InscripcionEquipo buscarInscripcionActiva(Socio jugador) {
        for (InscripcionEquipo i : inscripciones) {
            if (i.isActivo() && i.getJugador() == jugador) {
                return i;
            }
        }
        return null;
    }

    // Cuenta los jugadores activos
    public int contarActivos() {
        int n = 0;

        for (InscripcionEquipo i : inscripciones) {
            if (i.isActivo()) {
                n++;
            }
        }

        return n;
    }

    // Devuelve los jugadores activos
    public List<Socio> getJugadoresActivos() {
        List<Socio> lista = new ArrayList<>();

        for (InscripcionEquipo i : inscripciones) {
            if (i.isActivo()) {
                lista.add(i.getJugador());
            }
        }

        return lista;
    }

    // Devuelve los jugadores habilitados
    public List<Socio> getJugadoresHabilitados() {
        List<Socio> lista = new ArrayList<>();

        for (InscripcionEquipo i : inscripciones) {
            if (i.estaHabilitado()) {
                lista.add(i.getJugador());
            }
        }

        return lista;
    }

    // Revisa si un jugador puede jugar
    public boolean estaHabilitado(Socio jugador) {
        InscripcionEquipo i = buscarInscripcionActiva(jugador);
        return i != null && i.estaHabilitado();
    }

    // Revisa si hay suficientes jugadores habilitados
    public boolean tieneMinimoJugadoresHabilitados() {
        return getJugadoresHabilitados().size() >= deporte.getMinimoJugadores();
    }

    /**
     * Proyecto 2 (HU-ENT-02 CA-2): jugadores del equipo que NO están disponibles, con su motivo.
     * La clave es el jugador y el valor el motivo ("suspendido por tarjetas" o "inactivo").
     * Se usa un LinkedHashMap para conservar el orden de inscripción.
     */
    public java.util.Map<Socio, String> getJugadoresNoDisponibles() {
    	      Map<Socio, String> m = new LinkedHashMap();

    	      for(InscripcionEquipo i : this.inscripciones) {
    	         if (!i.isActivo()) {
    	            m.put(i.getJugador(), "inactivo");
    	         } else if (i.isSuspendidoProximoPartido()) {
    	            m.put(i.getJugador(), "suspendido por tarjetas");
    	         }
    	      }

    	      return m;
    	   }
    

    // Actualiza las suspensiones después de un partido
    public void actualizarSuspensiones(List<EstadisticaPartido> estadisticasDelPartido) {
        for (InscripcionEquipo i : inscripciones) {
            i.setSuspendidoProximoPartido(false);
        }

        for (EstadisticaPartido e : estadisticasDelPartido) {
            InscripcionEquipo i = buscarInscripcionActiva(e.getJugador());

            if (i != null && e.quedaSuspendido()) {
                i.setSuspendidoProximoPartido(true);
            }
        }
    }

    // Getters
    /** Entrenador a cargo del equipo (null si no tiene). */
    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public String getNombre() {
        return nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<InscripcionEquipo> getInscripciones() {
        return inscripciones;
    }

    @Override
    public String toString() {
        return nombre + " (" + deporte + ", " + categoria + ") "
                + contarActivos() + "/" + cupoMaximo;
    }
}
