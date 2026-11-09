package pruebas;

import java.time.LocalDate;
import java.time.LocalTime;
import logica.ClubDeportivo;
import modelo.academias.CanchaPadel;
import modelo.academias.CanchaTenis;
import modelo.academias.MesaDeTenisMesa;
import modelo.conjunto.Equipo;
import modelo.entrenamiento.Turno;
import modelo.enums.Categoria;
import modelo.enums.Deporte;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.usuarios.Administrador;
import modelo.usuarios.Entrenador;
import modelo.usuarios.Fisioterapeuta;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;

/**
 * Club pequeño, armado en memoria, para las pruebas (no usa archivos). Así cada prueba empieza
 * desde el mismo estado conocido y no depende de la carpeta "datos".
 *
 * Fechas fijas: HOY = martes 3 de noviembre de 2026; los torneos se juegan el SÁBADO 21 de noviembre
 * (los empleados con turno de lunes a sábado están "de turno") o el DOMINGO 22 (nadie tiene turno).
 */
public class ClubDePrueba {

    public static final LocalDate HOY = LocalDate.of(2026, 11, 3);
    public static final LocalDate SABADO = LocalDate.of(2026, 11, 21);
    public static final LocalDate DOMINGO = LocalDate.of(2026, 11, 22);
    public static final LocalDate CIERRE = LocalDate.of(2026, 11, 14);  // Cierre preferencial.
    public static final LocalDate DESPUES_DEL_CIERRE = LocalDate.of(2026, 11, 15);

    public final ClubDeportivo club = new ClubDeportivo();
    public final Administrador admin = new Administrador("A001", "Admin", "admin", "admin123");
    public final Entrenador ent1 = new Entrenador("E001", "Entrenador Uno", "ent1", "x");
    public final Entrenador ent2 = new Entrenador("E002", "Entrenador Dos", "ent2", "x");
    public final Entrenador ent3 = new Entrenador("E003", "Entrenador Tres", "ent3", "x");   // Sin turnos: nunca está de turno.
    public final Fisioterapeuta fisio1 = new Fisioterapeuta("F001", "Fisio Uno", "fisio1", "x");
    public final Fisioterapeuta fisio2 = new Fisioterapeuta("F002", "Fisio Dos", "fisio2", "x"); // Solo trabaja los domingos.
    public final ParticipanteExterno estudiante = new ParticipanteExterno("X001", "Estudiante Uno", "est1", "x", "100", true);
    public final ParticipanteExterno externo = new ParticipanteExterno("X002", "Externo Dos", "ext2", "x", "200", false);
    public final Socio[] socios = new Socio[12]; // socios[0] = S001 ... socios[11] = S012 (todos mayores de edad).
    public Equipo futbolA, futbolB, futbolC, baloncesto, futbolSub15;

    public ClubDePrueba() {
        try {
            club.registrarUsuario(admin);
            for (int i = 0; i < socios.length; i++) {
                socios[i] = new Socio(String.format("S%03d", i + 1), "Socio " + (i + 1), "s" + (i + 1), "1234", LocalDate.of(2000, 1, 1));
                club.registrarUsuario(socios[i]);
            }
            club.registrarUsuario(ent1);
            club.registrarUsuario(ent2);
            club.registrarUsuario(ent3);
            club.registrarUsuario(fisio1);
            club.registrarUsuario(fisio2);
            club.registrarUsuario(estudiante);
            club.registrarUsuario(externo);
            club.siguienteId("EXTERNO"); club.siguienteId("EXTERNO"); // X001 y X002 ya existen.

            // Turnos de lunes a sábado: 2 entrenadores y 1 fisioterapeuta todo el horario (cobertura mínima exacta).
            for (String dia : club.getDiasAtencion()) {
                ent1.agregarTurno(new Turno(dia, LocalTime.of(6, 0), LocalTime.of(22, 0)));
                ent2.agregarTurno(new Turno(dia, LocalTime.of(6, 0), LocalTime.of(22, 0)));
                fisio1.agregarTurno(new Turno(dia, LocalTime.of(6, 0), LocalTime.of(22, 0)));
            }
            fisio2.agregarTurno(new Turno("DOMINGO", LocalTime.of(8, 0), LocalTime.of(12, 0)));

            // Niveles: los socios 1..8 juegan TENIS INTERMEDIO con ELO 1100, 1090, ..., 1030 (el socio 1 es el mejor).
            for (int i = 0; i < 8; i++) {
                club.inscribirEnDisciplina(socios[i], Disciplina.TENIS, Nivel.INTERMEDIO).setRankingElo(1100 - 10 * i);
            }
            club.inscribirEnDisciplina(socios[8], Disciplina.TENIS, Nivel.AVANZADO);      // Socio 9: otro nivel.
            for (int i = 0; i < 4; i++) club.inscribirEnDisciplina(socios[i], Disciplina.PADEL, Nivel.INTERMEDIO);
            estudiante.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
            externo.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
            ent3.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
            fisio1.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);
            fisio2.declararNivel(Disciplina.TENIS, Nivel.INTERMEDIO);

            // Instalaciones: 2 de tenis, 2 de pádel, 1 mesa.
            club.agregarInstalacion(new CanchaTenis("TEN-1"));
            club.agregarInstalacion(new CanchaTenis("TEN-2"));
            club.agregarInstalacion(new CanchaPadel("PAD-1"));
            club.agregarInstalacion(new CanchaPadel("PAD-2"));
            club.agregarInstalacion(new MesaDeTenisMesa("MESA-1", true));

            // Equipos de fútbol mayores (ent1 dirige A y C, ent2 dirige B) y uno de baloncesto y otro Sub-15.
            futbolA = club.crearEquipo(admin, "Fútbol A", Deporte.FUTBOL, Categoria.MAYORES, 15);
            futbolB = club.crearEquipo(admin, "Fútbol B", Deporte.FUTBOL, Categoria.MAYORES, 15);
            futbolC = club.crearEquipo(admin, "Fútbol C", Deporte.FUTBOL, Categoria.MAYORES, 15);
            baloncesto = club.crearEquipo(admin, "Baloncesto", Deporte.BALONCESTO, Categoria.MAYORES, 12);
            futbolSub15 = club.crearEquipo(admin, "Fútbol Sub-15", Deporte.FUTBOL, Categoria.SUB15, 18);
            futbolA.setEntrenador(ent1);
            futbolC.setEntrenador(ent1);
            futbolB.setEntrenador(ent2);
            baloncesto.setEntrenador(ent1);
            futbolSub15.setEntrenador(ent1);
            for (int i = 0; i < 3; i++) club.inscribirJugadorEnEquipo(socios[i], "Fútbol A", "DEFENSA", i + 1, HOY);
            for (int i = 3; i < 6; i++) club.inscribirJugadorEnEquipo(socios[i], "Fútbol B", "DEFENSA", i + 1, HOY);
            for (int i = 6; i < 9; i++) club.inscribirJugadorEnEquipo(socios[i], "Fútbol C", "DEFENSA", i + 1, HOY);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo armar el club de prueba: " + e.getMessage(), e);
        }
    }

    /** Socio número n (1 = S001). */
    public Socio socio(int n) {
        return socios[n - 1];
    }
}
