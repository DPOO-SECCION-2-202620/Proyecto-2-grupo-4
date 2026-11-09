package consola;

import java.io.File;
import logica.ClubDeportivo;

/**
 * Se encarga de que los datos se carguen y se guarden solos, sin pedirle al usuario
 * el nombre de ningún archivo.
 *
 * - Al iniciar, carga el club desde la carpeta "datos" del proyecto. Si esa carpeta no
 *   existe, primero la crea con los datos de prueba de {@link GeneradorDatosPrueba}.
 * - Al terminar, guarda el club en esos mismos archivos.
 * - Si la carga falló, nunca guarda: guardar un club vacío borraría los datos reales.
 *
 * Se puede usar otra carpeta sin cambiar el código, agregando -Dclub.datos=otraCarpeta en
 * los argumentos de la máquina virtual (VM arguments en Eclipse).
 *
 * El formato de los archivos lo maneja {@link persistencia.PersistenciaClub}.
 */
public class GestorDatos {

    /** Carpeta donde están los archivos de datos. */
    public static final String CARPETA_POR_DEFECTO = "datos";

    private File carpeta;
    private ClubDeportivo club;
    private boolean yaSeGuardo;

    public GestorDatos() {
        String ruta = System.getProperty("club.datos");
        if (ruta == null) {
            ruta = CARPETA_POR_DEFECTO;
        }
        carpeta = new File(ruta);
    }

    /** Devuelve la ruta de la carpeta de datos. */
    public String getRuta() {
        return carpeta.getPath();
    }

    /** Carga el club. Si la carpeta no existe, primero genera los datos de prueba. */
    public ClubDeportivo cargar() throws Exception {
        if (!carpeta.isDirectory()) {
            System.out.println("(No se encontró la carpeta " + carpeta.getAbsolutePath() + ", se crean los datos de prueba.)");
            ClubDeportivo clubDePrueba = GeneradorDatosPrueba.crearClub();
            clubDePrueba.guardar(carpeta.getPath());
        }
        club = ClubDeportivo.cargar(carpeta.getPath());
        System.out.println("Datos cargados de " + carpeta.getAbsolutePath());
        return club;
    }

    /**
     * Guarda el club en los mismos archivos de donde se cargó.
     * Solo guarda una vez por ejecución. Devuelve true si los datos quedaron guardados.
     */
    public boolean guardar() {
        if (club == null) {
            return false; // No se cargó nada, así que no se toca ningún archivo.
        }
        if (yaSeGuardo) {
            return true;
        }
        try {
            club.guardar(carpeta.getPath());
            yaSeGuardo = true;
            System.out.println();
            System.out.println("Cambios guardados en la carpeta " + carpeta.getPath() + ".");
            return true;
        } catch (Exception e) {
            System.out.println();
            System.out.println("[ERROR] No se pudieron guardar los cambios: " + e.getMessage());
            System.out.println("Los archivos anteriores quedaron sin cambios.");
            return false;
        }
    }
}
