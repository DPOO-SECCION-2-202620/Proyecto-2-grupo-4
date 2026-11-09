package modelo.torneos; // Paquete de torneos internos (Proyecto 2).

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import excepciones.ReglaNegocioException;
import modelo.enums.EstadoTorneo;
import modelo.enums.TipoCupo;
import modelo.enums.TipoTorneo;
import modelo.usuarios.Usuario;

/**
 * Torneo interno. Contiene la lógica común a torneos individuales y de conjunto: cupos (con el 20 %
 * preferencial), inscripción y retiro, encuentros, posiciones finales y premios.
 * Las subclases solo validan QUIÉN puede inscribirse.
 *
 * Ciclo de vida: POR_INICIAR (inscripciones abiertas) --iniciar()--> EN_CURSO --finalizar(posiciones)--> FINALIZADO.
 *
 * Historias: HU-TOR-ADM-01 (crear), HU-TOR-ADM-02 (finalizar y premios), HU-TOR-ADM-03 y HU-TOR-SOC-05 (consultar),
 * HU-TOR-SOC-01 (inscribirse), HU-TOR-SOC-02 (desinscribirse), HU-TOR-SOC-03 (cupos preferenciales),
 * HU-TOR-EMP-01 (empleados).
 */
public abstract class Torneo {

    public static final double PORCENTAJE_PREFERENCIAL = 0.20; // 20 % de los cupos.

    private String nombre;                          // Nombre único del torneo.
    private TipoTorneo tipo;                        // RECREATIVO o COMPETITIVO.
    private int cupos;                              // Número total de cupos (inscripciones).
    private double tarifa;                          // Tarifa de inscripción (0 en recreativos).
    private double porcentajeBono;                  // Valor del bono del recreativo (0.15 = 15 %); 0 en competitivos.
    private LocalDate fechaInicio;                  // Día en que se juega (un solo día).
    private LocalDate fechaCierrePreferencial;      // Fecha límite de los cupos preferenciales.
    private EstadoTorneo estado;                    // Estado actual.
    private List<InscripcionTorneo> inscripciones;  // Inscritos (composición).
    private List<EncuentroTorneo> encuentros;       // Encuentros registrados (composición).
    private List<InscripcionTorneo> posiciones;     // Posiciones finales en orden (1.º, 2.º, ...).
    private List<Premio> premios;                   // Premios entregados al finalizar (composición).
    private int ultimoIdInscripcion;                // Para generar ids de inscripción.
    private int ultimoIdEncuentro;                  // Para generar ids de encuentro.

    public Torneo(String nombre, TipoTorneo tipo, int cupos, double tarifa, double porcentajeBono, LocalDate fechaInicio,
                  LocalDate fechaCierrePreferencial) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.cupos = cupos;
        this.tarifa = tarifa;
        this.porcentajeBono = porcentajeBono;
        this.fechaInicio = fechaInicio;
        this.fechaCierrePreferencial = fechaCierrePreferencial;
        this.estado = EstadoTorneo.POR_INICIAR;
        this.inscripciones = new ArrayList<>();
        this.encuentros = new ArrayList<>();
        this.posiciones = new ArrayList<>();
        this.premios = new ArrayList<>();
    }

    // =====================================================================
    //                 MÉTODOS ABSTRACTOS / GANCHOS PARA SUBCLASES
    // =====================================================================

    /** Descripción del deporte/disciplina y categoría/nivel (ej. "TENIS INTERMEDIO dobles"). */
    public abstract String getDescripcion();

    /** true para torneos de disciplina individual. */
    public abstract boolean esIndividual();

    /** Validaciones de creación propias de cada subclase (ej. pádel siempre en dobles). */
    protected abstract void validarDatosEspecificos() throws ReglaNegocioException;

    // =====================================================================
    //                        CREACIÓN (HU-TOR-ADM-01)
    // =====================================================================

    /**
     * Valida los datos del torneo al crearlo (HU-TOR-ADM-01 CA-1 a CA-4). La llama ClubDeportivo.registrarTorneo.
     */
    public void validarDatos(LocalDate hoy) throws ReglaNegocioException {
    	if (this.nombre != null && !this.nombre.isBlank()) {
            if (this.cupos <= 0) {
               throw new ReglaNegocioException("El número de cupos debe ser mayor que cero.");
            } else {
               if (this.tipo == TipoTorneo.COMPETITIVO) {
                  if (this.tarifa <= (double)0.0F) {
                     throw new ReglaNegocioException("La tarifa de inscripción es obligatoria en un torneo competitivo.");
                  }

                  this.porcentajeBono = (double)0.0F;
               } else {
                  this.tarifa = (double)0.0F;
                  if (this.porcentajeBono <= (double)0.0F || this.porcentajeBono > (double)1.0F) {
                     throw new ReglaNegocioException("El valor del bono debe estar entre 1 % y 100 %.");
                  }
               }

               if (this.fechaInicio.isBefore(hoy)) {
                  throw new ReglaNegocioException("La fecha de inicio " + String.valueOf(this.fechaInicio) + " ya pasó.");
               } else if (!this.fechaCierrePreferencial.isBefore(this.fechaInicio)) {
                  throw new ReglaNegocioException("La fecha límite de los cupos preferenciales debe ser anterior a la de inicio.");
               } else {
                  this.validarDatosEspecificos();
               }
            }
         } else {
            throw new ReglaNegocioException("El torneo debe tener nombre.");
         }
    }

    // =====================================================================
    //                        CUPOS (HU-TOR-SOC-03)
    // =====================================================================

    /**
     * Cupos preferenciales: 20 % de los cupos redondeado HACIA ARRIBA (HU-TOR-SOC-03 CA-1).
     * Ejemplos: 12 cupos -> 3 (2,4 -> 3); 10 cupos -> 2; 4 cupos -> 1.
     */
    public int getCuposPreferenciales() {
        return (int)Math.ceil((double)this.cupos * 0.2 - 1.0E-9);
     }

    /** Cuántas inscripciones ocupan cupos de un tipo. */
    public int contarInscripciones(TipoCupo tipoCupo) {
        int n = 0;
        for (InscripcionTorneo i : inscripciones) if (i.getTipoCupo() == tipoCupo) n++;
        return n;
    }

    /** ¿Ya llegó la fecha límite de los cupos preferenciales? (HU-TOR-SOC-03 CA-4) */
    public boolean preferencialesLiberados(LocalDate hoy) {
    	return !hoy.isBefore(this.fechaCierrePreferencial);
    }

    /**
     * Cupos LIBRES de un tipo en una fecha (HU-TOR-SOC-02 y HU-TOR-SOC-03). Cuando llega la fecha límite, los
     * preferenciales vacíos se "liberan": se cuentan como regulares (CA-4).
     */
    public int getCuposDisponibles(TipoCupo tipoCupo, LocalDate hoy) {
    	int pref = this.getCuposPreferenciales();
        if (this.preferencialesLiberados(hoy)) {
           return tipoCupo == TipoCupo.PREFERENCIAL ? 0 : Math.max(0, this.cupos - this.inscripciones.size());
        } else {
           return tipoCupo == TipoCupo.PREFERENCIAL ? Math.max(0, pref - this.contarInscripciones(TipoCupo.PREFERENCIAL)) : Math.max(0, this.cupos - pref - this.contarInscripciones(TipoCupo.REGULAR));
        }
    }

    /** ¿Se aceptan inscripciones y retiros? */
    public boolean estaAbierto() {
        return estado == EstadoTorneo.POR_INICIAR;
    }

    // =====================================================================
    //              INSCRIPCIÓN Y RETIRO (HU-TOR-SOC-01, SOC-02, EMP-01)
    // =====================================================================

    /** Siguiente id de inscripción (lo usan las subclases al crear la inscripción). */
    protected int nuevoIdInscripcion() {
        return ultimoIdInscripcion + 1;
    }

    /**
     * Registra una inscripción ya creada por la subclase: asigna el tipo de cupo y la tarifa.
     * @param elegible true si el participante está entre los N mejores del ranking (lo calcula ClubDeportivo).
     * @return el tipo de cupo asignado.
     */
    protected TipoCupo registrarInscripcion(InscripcionTorneo ins, boolean elegible, LocalDate hoy)
            throws ReglaNegocioException {
    	if (!this.estaAbierto()) {
            throw new ReglaNegocioException("Las inscripciones de " + this.nombre + " están cerradas: el torneo ya inició.");
         } else {
            TipoCupo t;
            if (elegible && this.getCuposDisponibles(TipoCupo.PREFERENCIAL, hoy) > 0) {
               t = TipoCupo.PREFERENCIAL;
            } else {
               if (this.getCuposDisponibles(TipoCupo.REGULAR, hoy) <= 0) {
                  if (this.getCuposDisponibles(TipoCupo.PREFERENCIAL, hoy) > 0) {
                     throw new ReglaNegocioException("Los cupos restantes de " + this.nombre + " están reservados para los mejores del ranking.");
                  }

                  throw new ReglaNegocioException("No hay cupos disponibles en " + this.nombre + ".");
               }

               t = TipoCupo.REGULAR;
            }

            ins.setTipoCupo(t);
            ins.setTarifaPagada(this.tipo == TipoTorneo.COMPETITIVO && !ins.esDeEmpleado() ? this.tarifa : (double)0.0F);
            this.inscripciones.add(ins);
            this.ultimoIdInscripcion = ins.getId();
            return t;
         }
    }

    /** Busca la inscripción en la que juega un usuario (null si no está inscrito). */
    public InscripcionTorneo buscarInscripcion(Usuario u) {
        for (InscripcionTorneo i : inscripciones) if (i.incluye(u)) return i;
        return null;
    }

    /** Busca una inscripción por id (null si no existe). */
    public InscripcionTorneo buscarInscripcion(int id) {
        for (InscripcionTorneo i : inscripciones) if (i.getId() == id) return i;
        return null;
    }

    /** Retira la inscripción en la que juega un usuario (HU-TOR-SOC-02). */
    public InscripcionTorneo retirar(Usuario u) throws ReglaNegocioException {
        return retirar(buscarInscripcion(u));
    }

    /**
     * Retira una inscripción y libera su cupo (HU-TOR-SOC-02). ins puede ser null (no estaba inscrito).
     */
    public InscripcionTorneo retirar(InscripcionTorneo ins) throws ReglaNegocioException {
        if (!this.estaAbierto()) {
           throw new ReglaNegocioException("No se puede salir de " + this.nombre + ": el torneo ya inició.");
        } else if (ins != null && this.inscripciones.contains(ins)) {
           this.inscripciones.remove(ins);
           return ins;
        } else {
           throw new ReglaNegocioException("No tiene inscripción en " + this.nombre + ".");
        }
     }

    // =====================================================================
    //              INICIO, ENCUENTROS Y FINALIZACIÓN (HU-TOR-ADM-02)
    // =====================================================================

    public void iniciar(LocalDate hoy) throws ReglaNegocioException {
        if (!this.estaAbierto()) {
           throw new ReglaNegocioException(this.nombre + " ya fue iniciado.");
        } else if (this.inscripciones.size() < 2) {
           throw new ReglaNegocioException("Se necesitan al menos 2 inscritos para iniciar " + this.nombre + ".");
        } else {
           this.estado = EstadoTorneo.EN_CURSO;
        }
     }

    /**
     * Registra un encuentro jugado entre dos inscritos, para el historial (HU-TOR-ADM-03 CA-2).
     * @param fase texto libre, ej. "Semifinal" (puede ser vacío).
     */
    
    public EncuentroTorneo registrarEncuentro(InscripcionTorneo a, InscripcionTorneo b, int marcadorA, int marcadorB, String fase) throws ReglaNegocioException {
        if (this.estado != EstadoTorneo.EN_CURSO) {
           throw new ReglaNegocioException("Solo se registran encuentros de un torneo en curso.");
        } else if (a != null && b != null && this.inscripciones.contains(a) && this.inscripciones.contains(b)) {
           if (a == b) {
              throw new ReglaNegocioException("Un participante no puede jugar contra sí mismo.");
           } else if (marcadorA >= 0 && marcadorB >= 0) {
              ++this.ultimoIdEncuentro;
              EncuentroTorneo e = new EncuentroTorneo(this.ultimoIdEncuentro, fase, a, b, marcadorA, marcadorB);
              this.encuentros.add(e);
              return e;
           } else {
              throw new ReglaNegocioException("Los marcadores no pueden ser negativos.");
           }
        } else {
           throw new ReglaNegocioException("Los dos participantes deben estar inscritos en " + this.nombre + ".");
        }
     }

    /**
     * Finaliza el torneo con las posiciones que registra el administrador y entrega los premios (HU-TOR-ADM-02).
     * @param posicionesFinales inscripciones en orden: la primera es el 1.º puesto (al menos una).
     * @param porcentajePremio  porcentaje del total recaudado para el premio en efectivo (configuración del club).
     */
    
    public void finalizar(List<InscripcionTorneo> posicionesFinales, double porcentajePremio)
            throws ReglaNegocioException {

        if (this.estado == EstadoTorneo.FINALIZADO) {
            throw new ReglaNegocioException(this.nombre + " ya está finalizado.");
        }

        if (this.estado != EstadoTorneo.EN_CURSO) {
            throw new ReglaNegocioException(this.nombre + " no ha iniciado.");
        }

        if (posicionesFinales == null || posicionesFinales.isEmpty()) {
            throw new ReglaNegocioException("Debe registrar al menos el primer puesto.");
        }

        // Verificar que todas las inscripciones pertenezcan al torneo
        // y que no se repita ninguna posición.
        List<InscripcionTorneo> vistos = new ArrayList<>();

        for (InscripcionTorneo inscripcion : posicionesFinales) {

            if (!this.inscripciones.contains(inscripcion)) {
                throw new ReglaNegocioException(
                    "Las posiciones deben ser inscripciones distintas de " + this.nombre + "."
                );
            }

            if (vistos.contains(inscripcion)) {
                throw new ReglaNegocioException(
                    "Las posiciones deben ser inscripciones distintas de " + this.nombre + "."
                );
            }

            vistos.add(inscripcion);
        }

        // Guardar las posiciones finales.
        this.posiciones.clear();
        this.posiciones.addAll(posicionesFinales);

        this.estado = EstadoTorneo.FINALIZADO;

        // Si el torneo es recreativo, entregar bonos al ganador.
        if (this.tipo == TipoTorneo.RECREATIVO) {

            InscripcionTorneo ganador = posicionesFinales.get(0);
            Premio premio = new Premio(1, ganador, 0.0);

            for (Usuario usuario : ganador.getIntegrantes()) {

                String codigo = BonoDescuento.generarCodigo(this.nombre, usuario.getId());

                BonoDescuento bono = new BonoDescuento(
                    codigo,
                    this.porcentajeBono,
                    this.nombre,
                    this.fechaInicio
                );

                usuario.agregarBono(bono);
                premio.agregarBono(bono);
            }

            this.premios.add(premio);

        } else {

            // Calcular el dinero disponible para los premios.
            double monto = this.calcularBolsa() * porcentajePremio;

            // Entregar el premio al primer participante que no sea empleado.
            for (int i = 0; i < posicionesFinales.size(); i++) {

                InscripcionTorneo inscripcion = posicionesFinales.get(i);

                if (!inscripcion.esDeEmpleado()) {

                    Premio premio = new Premio(i + 1, inscripcion, monto);
                    this.premios.add(premio);

                    break;
                }
            }
        }
    }

    /** Total recaudado: suma de las tarifas pagadas (los empleados pagan 0) (HU-TOR-EMP-01 CA-2). */
    public double calcularBolsa() {
        double total = 0.0;

        for (InscripcionTorneo inscripcion : this.inscripciones) {
            total = total + inscripcion.getTarifaPagada();
        }

        return total;
    }

    // =====================================================================
    //                    CONSULTA (HU-TOR-ADM-03 y HU-TOR-SOC-05)
    // =====================================================================

    /** Estado en palabras: "Inscripciones abiertas", "En curso" o "Finalizado". */
    public String getEstadoTexto() {
        switch (estado) {
            case POR_INICIAR: return "Inscripciones abiertas";
            case EN_CURSO: return "En curso";
            default: return "Finalizado";
        }
    }

    /** Historial detallado: participantes, encuentros, posiciones y premios (sirve en cualquier estado). */
    public String generarHistorial() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(nombre).append(" ===\n");
        sb.append(String.format("%s | %s | %s | Juega el %s | Fecha límite preferenciales %s%n", getDescripcion(), tipo,
                getEstadoTexto(), fechaInicio, fechaCierrePreferencial));
        sb.append(String.format("Cupos: %d | Inscritos: %d | Tarifa: $%,.0f%s%n", cupos, inscripciones.size(), tarifa,
                tipo == TipoTorneo.RECREATIVO ? String.format(" | Bono para el ganador: %.0f %%", porcentajeBono * 100) : ""));
        sb.append("Participantes:\n");
        if (inscripciones.isEmpty()) sb.append("  (ninguno)\n");
        for (InscripcionTorneo i : inscripciones) sb.append("  ").append(i).append("\n");
        sb.append("Encuentros:\n");
        if (encuentros.isEmpty()) sb.append("  (sin encuentros registrados)\n");
        for (EncuentroTorneo e : encuentros) sb.append("  ").append(e).append("\n");
        sb.append("Posiciones:\n");
        if (posiciones.isEmpty()) sb.append("  (aún no hay posiciones registradas)\n");
        for (int k = 0; k < posiciones.size(); k++) sb.append("  ").append(k + 1).append("º ").append(posiciones.get(k).getNombre()).append("\n");
        if (!premios.isEmpty()) {
            sb.append("Premios:\n");
            for (Premio p : premios) sb.append("  ").append(p.getDescripcion()).append("\n");
        }
        return sb.toString();
    }

    // =====================================================================
    //                    PERSISTENCIA (carga desde archivos)
    // =====================================================================

    public void setEstado(EstadoTorneo estado) { this.estado = estado; }

    public void agregarInscripcionCargada(InscripcionTorneo i) {
        inscripciones.add(i);
        ultimoIdInscripcion = Math.max(ultimoIdInscripcion, i.getId());
    }

    public void agregarEncuentroCargado(EncuentroTorneo e) {
        encuentros.add(e);
        ultimoIdEncuentro = Math.max(ultimoIdEncuentro, e.getId());
    }

    public void agregarPosicionCargada(InscripcionTorneo i) { posiciones.add(i); }

    public void agregarPremioCargado(Premio p) { premios.add(p); }

    // =====================================================================
    //                              GETTERS
    // =====================================================================

    public String getNombre() { return nombre; }
    public TipoTorneo getTipo() { return tipo; }
    public int getCupos() { return cupos; }
    public double getTarifa() { return tarifa; }
    public double getPorcentajeBono() { return porcentajeBono; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaCierrePreferencial() { return fechaCierrePreferencial; }
    public EstadoTorneo getEstado() { return estado; }
    public List<InscripcionTorneo> getInscripciones() { return inscripciones; }
    public List<EncuentroTorneo> getEncuentros() { return encuentros; }
    public List<InscripcionTorneo> getPosiciones() { return posiciones; }
    public List<Premio> getPremios() { return premios; }

    @Override
    public String toString() {
        return String.format("%-28s %-32s %-11s %-22s %2d/%2d inscritos  juega %s", nombre, getDescripcion(), tipo, getEstadoTexto(),
                inscripciones.size(), cupos, fechaInicio);
    }
}
