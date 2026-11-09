package modelo.ventas; // Paquete de ventas.

import java.time.LocalDate;               // Fecha.
import excepciones.ReglaNegocioException; // Excepción de negocio.
import modelo.enums.EstadoSugerencia;     // Estado.
import modelo.usuarios.Empleado;          // Autor.

/** Sugerencia de un empleado para agregar un nuevo artículo de tienda o snack. */
public class Sugerencia { // Clase Sugerencia.

    public static final String ARTICULO = "ARTICULO"; // Tipo: artículo de tienda.
    public static final String SNACK = "SNACK";       // Tipo: snack de cafetería.

    private int id;                   // Identificador.
    private Empleado autor;           // Empleado que sugiere.
    private String tipoProducto;      // ARTICULO o SNACK.
    private String nombreProducto;    // Nombre del producto sugerido.
    private String categoria;         // Categoría (para artículos) o alérgenos separados por coma (para snacks).
    private double precioSugerido;    // Precio propuesto.
    private EstadoSugerencia estado;  // Estado.
    private LocalDate fecha;          // Fecha de la sugerencia.

    public Sugerencia(int id, Empleado autor, String tipoProducto, String nombreProducto, String categoria,
                      double precioSugerido, LocalDate fecha) { // Constructor.
        this.id = id; this.autor = autor; this.tipoProducto = tipoProducto;   // Asigna.
        this.nombreProducto = nombreProducto; this.categoria = categoria;     // Asigna.
        this.precioSugerido = precioSugerido; this.fecha = fecha;             // Asigna.
        this.estado = EstadoSugerencia.PENDIENTE;                             // Nace pendiente.
    }

    /** Aprueba la sugerencia (solo si está pendiente). */
    public void aprobar() throws ReglaNegocioException { // Puede fallar.
        if (estado != EstadoSugerencia.PENDIENTE) throw new ReglaNegocioException("La sugerencia " + id + " ya fue revisada."); // Validación.
        estado = EstadoSugerencia.APROBADA; // Cambia el estado.
    }

    /** Rechaza la sugerencia (solo si está pendiente). */
    public void rechazar() throws ReglaNegocioException { // Puede fallar.
        if (estado != EstadoSugerencia.PENDIENTE) throw new ReglaNegocioException("La sugerencia " + id + " ya fue revisada."); // Validación.
        estado = EstadoSugerencia.RECHAZADA; // Cambia el estado.
    }

    public int getId() { return id; }                         // Getter.
    public Empleado getAutor() { return autor; }              // Getter.
    public String getTipoProducto() { return tipoProducto; }  // Getter.
    public String getNombreProducto() { return nombreProducto; } // Getter.
    public String getCategoria() { return categoria; }        // Getter.
    public double getPrecioSugerido() { return precioSugerido; } // Getter.
    public EstadoSugerencia getEstado() { return estado; }    // Getter.
    public void setEstado(EstadoSugerencia e) { this.estado = e; } // Setter (persistencia).
    public LocalDate getFecha() { return fecha; }             // Getter.

    @Override public String toString() { return String.format("Sugerencia #%d %s '%s' (%s) $%,.0f por %s [%s]", id, tipoProducto, nombreProducto, categoria, precioSugerido, autor.getNombre(), estado); }
}
