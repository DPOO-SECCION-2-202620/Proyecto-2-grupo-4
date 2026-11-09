
package modelo.usuarios;

import java.time.LocalDate;
import java.time.Period;
import modelo.academias.Instalacion;

public class Socio extends Usuario {

    private LocalDate fechaDeNacimiento;
    private Instalacion instalacionActual;
    private boolean tieneBebidaCaliente;

    // Crea un socio
    public Socio(String id, String nombre, String login, String password, LocalDate fechaDeNacimiento) {
        super(id, nombre, login, password);
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.instalacionActual = null;
        this.tieneBebidaCaliente = false;
    }

    // Calcula la edad actual
    public int calcularEdad() {
        return calcularEdad(LocalDate.now());
    }

    // Calcula la edad en una fecha
    public int calcularEdad(LocalDate fechaReferencia) {
        return Period.between(fechaDeNacimiento, fechaReferencia).getYears();
    }

    // Devuelve el tipo de usuario
    @Override
    public String getTipo() {
        return "SOCIO";
    }

    // Getters y setters
    public LocalDate getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public Instalacion getInstalacionActual() {
        return instalacionActual;
    }

    public void setInstalacionActual(Instalacion i) {
        this.instalacionActual = i;
    }

    public boolean tieneBebidaCaliente() {
        return tieneBebidaCaliente;
    }

    public void setTieneBebidaCaliente(boolean b) {
        this.tieneBebidaCaliente = b;
    }
}

