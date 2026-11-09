package persistencia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import logica.ClubDeportivo;

/**
 * Guarda y carga toda la información del club en archivos de texto, uno por tipo de información,
 * dentro de una carpeta que no es la del código fuente.
 *
 * Reparto del trabajo:
 * - {@link EscritorClub} convierte el club en las líneas de cada archivo;
 * - {@link LectorClub} lee los archivos y vuelve a armar el club;
 * - {@link ArchivoTexto} tiene las herramientas para leer, escribir y dar formato;
 * - esta clase organiza el proceso y se asegura de no dejar archivos a medias.
 *
 * Guardado en dos pasos ("todo o nada"):
 * 1. Cada archivo se escribe primero con la extensión .temporal (por ejemplo usuarios.txt.temporal).
 * 2. Solo si TODOS se escribieron bien, cada temporal reemplaza a su archivo definitivo.
 * Si algo falla en el paso 1, se borran los temporales y los archivos que ya existían quedan intactos.
 */
public class PersistenciaClub {

    /** Extensión de los archivos que se escriben antes de reemplazar los definitivos. */
    public static final String EXTENSION_TEMPORAL = ".temporal";

    private List<File> temporales = new ArrayList<>();

    // ---------------------------------------------------------------
    //  Guardar
    // ---------------------------------------------------------------

    /** Guarda el club en la carpeta (la crea si no existe), reemplazando los archivos que ya estaban. */
    public void guardar(ClubDeportivo club, String rutaCarpeta) throws IOException {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + rutaCarpeta);
        }
        temporales.clear();
        try {
            escribirTemporales(club, carpeta);   // Paso 1
            reemplazarDefinitivos();             // Paso 2
        } catch (IOException e) {
            borrarTemporales();
            throw e;
        } catch (RuntimeException e) {
            borrarTemporales();
            throw new IOException("Hay un dato que no se pudo guardar (" + e.getMessage() + ")", e);
        }
    }

    /** Paso 1: escribe todos los archivos como temporales. */
    private void escribirTemporales(ClubDeportivo club, File carpeta) throws IOException {
        EscritorClub escritor = new EscritorClub(club);

        escribir(carpeta, "config.txt", "clave;valores...", escritor.lineasConfiguracion());
        escribir(carpeta, "instalaciones.txt", "tipo;id;techada", escritor.lineasInstalaciones());
        escribir(carpeta, "usuarios.txt", "tipo;id;nombre;login;password;puntos;[SOCIO: fechaNac;instalacionActual;bebidaCaliente"
                + " | ENTRENADOR: idPerfilJugador | EXTERNO: documento;esEstudiante]", escritor.lineasUsuarios());
        escribir(carpeta, "niveles_declarados.txt", "idUsuario;disciplina;nivel", escritor.lineasNivelesDeclarados());
        escribir(carpeta, "bonos.txt", "codigo;idUsuario;porcentaje;torneoOrigen;fechaOtorgado;usado", escritor.lineasBonos());
        escribir(carpeta, "turnos.txt", "idEmpleado;dia;horaInicio;horaFin", escritor.lineasTurnos());
        escribir(carpeta, "codigos_descuento.txt", "codigo;idEmpleado", escritor.lineasCodigosDescuento());

        escribir(carpeta, "equipos.txt", "nombre;deporte;categoria;cupoMaximo;idEntrenador", escritor.lineasEquipos());
        escribir(carpeta, "inscripciones_equipo.txt", "equipo;idSocio;posicion;camiseta;activo;suspendido",
                escritor.lineasInscripcionesEquipo());
        escribir(carpeta, "competencias.txt", "formato;nombre;entidad;temporada;deporte;categoria;nominaMinima;indiceFaseActual;campeon",
                escritor.lineasCompetencias());
        escribir(carpeta, "fases.txt", "competencia;fase;tipo;clasificadosPorGrupo;finalizada;participantes", escritor.lineasFases());
        escribir(carpeta, "grupos.txt", "competencia;fase;grupo;equipos", escritor.lineasGrupos());
        escribir(carpeta, "inscripciones_competencia.txt", "competencia;equipo;nomina;amarillasAcumuladas(id:n);suspendidos",
                escritor.lineasInscripcionesCompetencia());
        escribir(carpeta, "resultados_externos.txt", "competencia;equipoA;equipoB;golesA;golesB;fase;grupo",
                escritor.lineasResultadosExternos());
        escribir(carpeta, "partidos.txt", "INTERNO;id;fecha;local;visitante;marcadorA;marcadorB;jugado"
                + " | COMPETENCIA;id;fecha;competencia;equipo;rival;escenario;fase;grupo;marcadorA;marcadorB;jugado",
                escritor.lineasPartidos());
        escribir(carpeta, "estadisticas_partido.txt", "idPartido;deporte;idSocio;valores...", escritor.lineasEstadisticasPartido());

        escribir(carpeta, "reservas.txt", "id;instalacion;idSocio;fechaHora;cantidadJugadores;modalidad", escritor.lineasReservas());
        escribir(carpeta, "inscripciones_disciplina.txt", "idSocio;disciplina;nivel;rankingElo", escritor.lineasInscripcionesDisciplina());
        escribir(carpeta, "partidos_practica.txt", "id;disciplina;fecha;idsJugadores(ladoA|ladoB);jugado", escritor.lineasPartidosPractica());
        escribir(carpeta, "estadisticas_practica.txt", "idPartido;idSocio;eloAntes;eloDespues;tipo;valores...",
                escritor.lineasEstadisticasPractica());
        escribir(carpeta, "sesiones.txt", "id;idEntrenador;instalacion;fechaHora;duracion;convocados;finalizada", escritor.lineasSesiones());
        escribir(carpeta, "asistencias.txt", "idSesion;idSocio;estado;nota;comentario", escritor.lineasAsistencias());
        escribir(carpeta, "solicitudes_turno.txt", "id;idEmpleado;diaActual;iniActual;finActual;diaProp;iniProp;finProp;estado",
                escritor.lineasSolicitudesTurno());

        escribir(carpeta, "articulos.txt", "nombre;categoria;precio", escritor.lineasArticulos());
        escribir(carpeta, "inventario.txt", "articulo;ubicacion;cantidad", escritor.lineasInventario());
        escribir(carpeta, "productos_cafeteria.txt", "tipo;nombre;precio;[BEBIDA: FRIA/CALIENTE | SNACK: alergenos]",
                escritor.lineasProductosCafeteria());
        escribir(carpeta, "ventas.txt", "id;rubro;fecha;idComprador;subtotal;descuento;puntosRedimidos;impuesto;total;puntosGenerados;"
                + "extra1;extra2;bono", escritor.lineasVentas());
        escribir(carpeta, "items_venta.txt", "idVenta;producto;cantidad;precioUnitario", escritor.lineasItemsVenta());
        escribir(carpeta, "sugerencias.txt", "id;idAutor;tipo;nombre;categoriaOAlergenos;precio;estado;fecha", escritor.lineasSugerencias());

        escribir(carpeta, "torneos.txt", "clase;nombre;tipo;cupos;tarifa;porcentajeBono;fechaInicio;fechaLimitePreferenciales;estado;"
                + "posiciones(ids);[INDIVIDUAL: disciplina;nivel;dobles | CONJUNTO: deporte;categoria;-]", escritor.lineasTorneos());
        escribir(carpeta, "inscripciones_torneo.txt", "torneo;id;fecha;tipoCupo;tarifaPagada;ranking;[EQUIPO;nombreEquipo | JUGADORES;ids]",
                escritor.lineasInscripcionesTorneo());
        escribir(carpeta, "encuentros_torneo.txt", "torneo;id;fase;idInscripcionA;idInscripcionB;marcadorA;marcadorB",
                escritor.lineasEncuentrosTorneo());
        escribir(carpeta, "premios_torneo.txt", "torneo;puesto;idInscripcion;monto;codigosBonos", escritor.lineasPremiosTorneo());
    }

    /** Escribe un archivo temporal y lo anota para el paso 2. */
    private void escribir(File carpeta, String nombreArchivo, String formato, List<String> lineas) throws IOException {
        File temporal = new File(carpeta, nombreArchivo + EXTENSION_TEMPORAL);
        temporales.add(temporal);
        ArchivoTexto.escribir(temporal, formato, lineas);
    }

    /** Paso 2: cada temporal reemplaza a su archivo definitivo. */
    private void reemplazarDefinitivos() throws IOException {
        for (File temporal : temporales) {
            String nombreTemporal = temporal.getName();
            String nombreDefinitivo = nombreTemporal.substring(0, nombreTemporal.length() - EXTENSION_TEMPORAL.length());
            File definitivo = new File(temporal.getParentFile(), nombreDefinitivo);
            Files.move(temporal.toPath(), definitivo.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        temporales.clear();
    }

    /** Borra los temporales de un guardado que no se pudo terminar. */
    private void borrarTemporales() {
        for (File temporal : temporales) {
            if (temporal.isFile()) {
                temporal.delete();
            }
        }
        temporales.clear();
    }

    // ---------------------------------------------------------------
    //  Cargar
    // ---------------------------------------------------------------

    /**
     * Arma el club leyendo los archivos de la carpeta. Si un dato está mal escrito
     * (un número, una fecha o un valor de enum), el error dice en qué archivo está.
     */
    public ClubDeportivo cargar(String rutaCarpeta) throws Exception {
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.isDirectory()) {
            throw new IOException("No existe la carpeta de datos " + carpeta.getAbsolutePath());
        }
        LectorClub lector = new LectorClub(carpeta);
        try {
            return lector.cargarTodo();
        } catch (RuntimeException e) {
            throw new IOException("Hay un dato inválido en el archivo " + lector.getArchivoActual() + " (" + e.getMessage() + ")", e);
        }
    }
}
