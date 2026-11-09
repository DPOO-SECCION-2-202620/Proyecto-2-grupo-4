package modelo.usuarios;

import java.util.ArrayList;
import java.util.List;
import modelo.torneos.BonoDescuento;

public abstract class Usuario {

    // Porcentaje que se acumula en puntos
    public static final double PORCENTAJE_PUNTOS = 0.02;

    private String id;
    private String nombre;
    private String login;
    private String password;
    private double puntosFidelidad;
    private List<BonoDescuento> bonos; // Proyecto 2: bonos ganados en torneos recreativos.

    // Crea un usuario
    public Usuario(String id, String nombre, String login, String password) {
        this.id = id;
        this.nombre = nombre;
        this.login = login;
        this.password = password;
        this.puntosFidelidad = 0;
        this.bonos = new ArrayList<>();
    }

    // Verifica los datos de inicio de sesion
    public boolean autenticar(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }

    // Acumula puntos por una compra
    public void acumularPuntos(double valorCompra) {
        if (valorCompra > 0) {
            puntosFidelidad += valorCompra * PORCENTAJE_PUNTOS;
        }
    }

    // Usa puntos como descuento
    public double redimirPuntos(double maximo) {
        double aRedimir = Math.min(puntosFidelidad, Math.max(0, maximo));
        puntosFidelidad -= aRedimir;
        return aRedimir;
    }

    // Indica si puede usar instalaciones y partidos
    public boolean puedeUsarInstalacionesYPartidos() {
        return true;
    }

    // Devuelve el tipo de usuario
    public abstract String getTipo();

    // ---------------- Proyecto 2: bonos de torneos ----------------

    /** Guarda un bono ganado en un torneo recreativo. */
    public void agregarBono(BonoDescuento bono) {
        bonos.add(bono);
    }

    /** Todos los bonos (usados y sin usar). */
    public List<BonoDescuento> getBonos() {
        return bonos;
    }

    /** Bonos que todavía se pueden usar. */
    public List<BonoDescuento> getBonosDisponibles() {
        List<BonoDescuento> lista = new ArrayList<>();
        for (BonoDescuento b : bonos) if (!b.isUsado()) lista.add(b);
        return lista;
    }

    /** Busca un bono propio por código (null si no lo tiene). */
    public BonoDescuento buscarBono(String codigo) {
        for (BonoDescuento b : bonos) if (b.getCodigo().equalsIgnoreCase(codigo)) return b;
        return null;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(double p) {
        this.puntosFidelidad = p;
    }

    @Override
    public String toString() {
        return getTipo() + " " + nombre + " (" + id + ")";
    }
}