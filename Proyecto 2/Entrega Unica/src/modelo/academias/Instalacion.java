package modelo.academias;

import excepciones.ReglaNegocioException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.enums.Disciplina;
import modelo.usuarios.Socio;

public abstract class Instalacion {
   public static final String SENCILLOS = "SENCILLOS";
   public static final String DOBLES = "DOBLES";
   private String id;
   private int capacidadMaxima;
   private List<Reserva> reservas;

   public Instalacion(String id, int capacidadMaxima) {
      this.id = id;
      this.capacidadMaxima = capacidadMaxima;
      this.reservas = new ArrayList();
   }

   public abstract Disciplina getDisciplina();

   public abstract boolean admiteJugadores(int var1, String var2);

   public abstract String getTipo();

   public boolean permiteBebidaCaliente() {
      return true;
   }

   public boolean estaOcupada(LocalDateTime inicio) {
      for(Reserva r : this.reservas) {
         if (r.seCruzaCon(inicio)) {
            return true;
         }
      }

      return false;
   }

   public Reserva reservar(int idReserva, Socio socio, LocalDateTime fechaHora, int cantidadJugadores, String modalidad) throws ReglaNegocioException {
      if (!this.admiteJugadores(cantidadJugadores, modalidad)) {
         throw new ReglaNegocioException(this.motivoRechazo(cantidadJugadores, modalidad));
      } else if (this.estaOcupada(fechaHora)) {
         throw new ReglaNegocioException(this.id + " ya está reservada en ese bloque horario.");
      } else if (socio.tieneBebidaCaliente() && !this.permiteBebidaCaliente()) {
         throw new ReglaNegocioException("No se puede reservar " + this.id + " con una bebida caliente: está prohibida en esta instalación.");
      } else {
         Reserva reserva = new Reserva(idReserva, this, socio, fechaHora, cantidadJugadores, modalidad);
         this.reservas.add(reserva);
         return reserva;
      }
   }

   public String motivoRechazo(int cantidad, String modalidad) {
      return this.id + " no admite " + cantidad + " jugadores en modalidad " + modalidad + ".";
   }

   public void agregarReservaCargada(Reserva r) {
      this.reservas.add(r);
   }

   public String getId() {
      return this.id;
   }

   public int getCapacidadMaxima() {
      return this.capacidadMaxima;
   }

   public List<Reserva> getReservas() {
      return this.reservas;
   }

   public String toString() {
      String var10000 = this.getTipo();
      return var10000 + " " + this.id + " (cap. " + this.capacidadMaxima + ", " + this.reservas.size() + " reservas)";
   }
}
