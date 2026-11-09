package integracion;

import static pruebas.ClubDePrueba.*;

import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.enums.TipoTorneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import pruebas.ClubDePrueba;

/**
 * Agrega torneos directamente a la lista del club (sin pasar por crearTorneo...), para que las pruebas de
 * inscripción no dependan de HU-TOR-ADM-01.
 */
class TorneosDePrueba {

    /** Bono de los recreativos de prueba: 15 %. */
    static double bono(TipoTorneo tipo) {
        return tipo == TipoTorneo.RECREATIVO ? 0.15 : 0;
    }

    /** Tenis intermedio sencillos, sábado (empleados de lunes a sábado están de turno). */
    static TorneoIndividual tenis(ClubDePrueba p, TipoTorneo tipo, int cupos, double tarifa) {
        TorneoIndividual t = new TorneoIndividual("Copa Tenis", tipo, Disciplina.TENIS, Nivel.INTERMEDIO, false, cupos, tarifa, bono(tipo), SABADO, CIERRE);
        p.club.getTorneos().add(t);
        return t;
    }

    /** Tenis intermedio sencillos el domingo (nadie de turno salvo fisio2). */
    static TorneoIndividual tenisDomingo(ClubDePrueba p, TipoTorneo tipo, int cupos, double tarifa) {
        TorneoIndividual t = new TorneoIndividual("Tenis Domingo", tipo, Disciplina.TENIS, Nivel.INTERMEDIO, false, cupos, tarifa, bono(tipo), DOMINGO, CIERRE);
        p.club.getTorneos().add(t);
        return t;
    }

    /** Pádel intermedio dobles. */
    static TorneoIndividual padel(ClubDePrueba p, TipoTorneo tipo, int cupos, double tarifa) {
        TorneoIndividual t = new TorneoIndividual("Pádel", tipo, Disciplina.PADEL, Nivel.INTERMEDIO, true, cupos, tarifa, bono(tipo), DOMINGO, CIERRE);
        p.club.getTorneos().add(t);
        return t;
    }

    /** Fútbol mayores. */
    static TorneoConjunto futbol(ClubDePrueba p, TipoTorneo tipo, int cupos, double tarifa) {
        TorneoConjunto t = new TorneoConjunto("Mini Fútbol", tipo, Deporte.FUTBOL, Categoria.MAYORES, cupos, tarifa, bono(tipo), SABADO, CIERRE);
        p.club.getTorneos().add(t);
        return t;
    }
}
