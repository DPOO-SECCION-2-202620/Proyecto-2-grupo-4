package persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import logica.ClubDeportivo;
import modelo.entrenamiento.AsistenciaEvaluacion;
import modelo.academias.EstadisticaPractica;
import modelo.academias.EstadisticaTenisMesa;
import modelo.academias.EstadisticaTenisPadel;
import modelo.academias.InscripcionDisciplina;
import modelo.academias.Instalacion;
import modelo.academias.MesaDeTenisMesa;
import modelo.academias.PartidoPractica;
import modelo.academias.Reserva;
import modelo.competencias.Competencia;
import modelo.competencias.CompetenciaFases;
import modelo.competencias.Enfrentamiento;
import modelo.competencias.Fase;
import modelo.competencias.Grupo;
import modelo.competencias.InscripcionCompetencia;
import modelo.conjunto.Equipo;
import modelo.conjunto.EstadisticaBaloncesto;
import modelo.conjunto.EstadisticaFutbol;
import modelo.conjunto.EstadisticaPartido;
import modelo.conjunto.EstadisticaVoleibol;
import modelo.conjunto.InscripcionEquipo;
import modelo.conjunto.Partido;
import modelo.conjunto.PartidoCompetencia;
import modelo.conjunto.PartidoInterno;
import modelo.entrenamiento.SesionEntrenamiento;
import modelo.entrenamiento.SolicitudCambioTurno;
import modelo.entrenamiento.Turno;
import modelo.enums.Disciplina;
import modelo.enums.Nivel;
import modelo.torneos.BonoDescuento;
import modelo.torneos.EncuentroTorneo;
import modelo.torneos.InscripcionTorneo;
import modelo.torneos.InscripcionTorneoEquipo;
import modelo.torneos.Premio;
import modelo.torneos.Torneo;
import modelo.torneos.TorneoConjunto;
import modelo.torneos.TorneoIndividual;
import modelo.usuarios.Empleado;
import modelo.usuarios.Entrenador;
import modelo.usuarios.ParticipanteExterno;
import modelo.usuarios.Socio;
import modelo.usuarios.Usuario;
import modelo.ventas.Articulo;
import modelo.ventas.Bebida;
import modelo.ventas.CodigoDescuento;
import modelo.ventas.InventarioUbicacion;
import modelo.ventas.ItemVentaCafeteria;
import modelo.ventas.ItemVentaTienda;
import modelo.ventas.ProductoCafeteria;
import modelo.ventas.Snack;
import modelo.ventas.Sugerencia;
import modelo.ventas.Venta;
import modelo.ventas.VentaCafeteria;
import modelo.ventas.VentaMensualidad;
import modelo.ventas.VentaTienda;

/**
 * Convierte la información del club en las líneas de texto de cada archivo.
 *
 * Hay un método por archivo y cada uno devuelve la lista de líneas que se van a escribir.
 * Las referencias entre objetos se guardan con su identificador (id del usuario, nombre del
 * equipo, etc.) para poder reconstruirlas al cargar.
 */
public class EscritorClub {

    private static final String SEP = ArchivoTexto.SEPARADOR;
    private static final String VACIO = ArchivoTexto.VACIO;

    private ClubDeportivo club;

    public EscritorClub(ClubDeportivo club) {
        this.club = club;
    }

    // Atajos para que las líneas se lean más fácil.
    private static String limpiar(String texto) {
        return ArchivoTexto.limpiar(texto);
    }

    private static String unir(List<String> lista) {
        return ArchivoTexto.unirLista(lista);
    }

    private static String marcarVacio(String texto) {
        return ArchivoTexto.marcarVacio(texto);
    }

    // ---------------------------------------------------------------
    //  Configuración, instalaciones y usuarios
    // ---------------------------------------------------------------

    /** config.txt: días, horario, máximos por disciplina, contadores y % del premio. */
    public List<String> lineasConfiguracion() {
        List<String> lineas = new ArrayList<>();
        lineas.add("DIAS;" + String.join(ArchivoTexto.SEPARADOR_LISTA, club.getDiasAtencion()));
        lineas.add("HORARIO;" + club.getHoraApertura() + SEP + club.getHoraCierre());
        for (Map.Entry<Disciplina, Integer> maximo : club.getMaxInstalacionesSimultaneas().entrySet()) {
            lineas.add("MAX;" + maximo.getKey() + SEP + maximo.getValue());
        }
        for (Map.Entry<String, Integer> contador : club.getContadores().entrySet()) {
            lineas.add("CONTADOR;" + contador.getKey() + SEP + contador.getValue());
        }
        lineas.add("PREMIO;" + club.getPorcentajePremioCompetitivo());
        return lineas;
    }

    /** instalaciones.txt */
    public List<String> lineasInstalaciones() {
        List<String> lineas = new ArrayList<>();
        for (Instalacion instalacion : club.getInstalaciones()) {
            boolean techada = false;
            if (instalacion instanceof MesaDeTenisMesa) {
                techada = ((MesaDeTenisMesa) instalacion).isTechada();
            }
            lineas.add(instalacion.getTipo() + SEP + instalacion.getId() + SEP + techada);
        }
        return lineas;
    }

    /** usuarios.txt: campos comunes y, al final, los propios de cada tipo de usuario. */
    public List<String> lineasUsuarios() {
        List<String> lineas = new ArrayList<>();
        for (Usuario u : club.getUsuarios()) {
            String linea = u.getTipo() + SEP + u.getId() + SEP + limpiar(u.getNombre()) + SEP + limpiar(u.getLogin())
                    + SEP + limpiar(u.getPassword()) + SEP + u.getPuntosFidelidad();
            if (u instanceof Socio) {
                Socio socio = (Socio) u;
                String instalacion = VACIO;
                if (socio.getInstalacionActual() != null) {
                    instalacion = socio.getInstalacionActual().getId();
                }
                linea = linea + SEP + socio.getFechaDeNacimiento() + SEP + instalacion + SEP + socio.tieneBebidaCaliente();
            } else if (u instanceof Entrenador) {
                Socio perfil = ((Entrenador) u).getPerfilJugador();
                String idPerfil = VACIO;
                if (perfil != null) {
                    idPerfil = perfil.getId();
                }
                linea = linea + SEP + idPerfil;
            } else if (u instanceof ParticipanteExterno) {
                ParticipanteExterno externo = (ParticipanteExterno) u;
                linea = linea + SEP + limpiar(externo.getDocumento()) + SEP + externo.isEstudiante();
            }
            lineas.add(linea);
        }
        return lineas;
    }

    /** niveles_declarados.txt: niveles que declararon los externos y los empleados. */
    public List<String> lineasNivelesDeclarados() {
        List<String> lineas = new ArrayList<>();
        for (Usuario u : club.getUsuarios()) {
            Map<Disciplina, Nivel> niveles = null;
            if (u instanceof ParticipanteExterno) {
                niveles = ((ParticipanteExterno) u).getNivelesDeclarados();
            } else if (u instanceof Empleado) {
                niveles = ((Empleado) u).getNivelesDeclarados();
            }
            if (niveles != null) {
                for (Map.Entry<Disciplina, Nivel> nivel : niveles.entrySet()) {
                    lineas.add(u.getId() + SEP + nivel.getKey() + SEP + nivel.getValue());
                }
            }
        }
        return lineas;
    }

    /** bonos.txt: bonos de descuento ganados en torneos recreativos. */
    public List<String> lineasBonos() {
        List<String> lineas = new ArrayList<>();
        for (Usuario u : club.getUsuarios()) {
            for (BonoDescuento bono : u.getBonos()) {
                lineas.add(bono.getCodigo() + SEP + u.getId() + SEP + bono.getPorcentaje() + SEP + limpiar(bono.getTorneoOrigen())
                        + SEP + bono.getFechaOtorgado() + SEP + bono.isUsado());
            }
        }
        return lineas;
    }

    /** turnos.txt */
    public List<String> lineasTurnos() {
        List<String> lineas = new ArrayList<>();
        for (Empleado empleado : club.getEmpleados()) {
            for (Turno turno : empleado.getTurnos()) {
                lineas.add(empleado.getId() + SEP + turno.getDia() + SEP + turno.getHoraInicio() + SEP + turno.getHoraFin());
            }
        }
        return lineas;
    }

    /** codigos_descuento.txt */
    public List<String> lineasCodigosDescuento() {
        List<String> lineas = new ArrayList<>();
        for (CodigoDescuento codigo : club.getCodigosDescuento()) {
            lineas.add(codigo.getCodigo() + SEP + codigo.getDueno().getId());
        }
        return lineas;
    }

    // ---------------------------------------------------------------
    //  Equipos y competencias
    // ---------------------------------------------------------------

    /** equipos.txt */
    public List<String> lineasEquipos() {
        List<String> lineas = new ArrayList<>();
        for (Equipo equipo : club.getEquipos()) {
            String idEntrenador = VACIO;
            if (equipo.getEntrenador() != null) {
                idEntrenador = equipo.getEntrenador().getId();
            }
            lineas.add(limpiar(equipo.getNombre()) + SEP + equipo.getDeporte() + SEP + equipo.getCategoria() + SEP
                    + equipo.getCupoMaximo() + SEP + idEntrenador);
        }
        return lineas;
    }

    /** inscripciones_equipo.txt */
    public List<String> lineasInscripcionesEquipo() {
        List<String> lineas = new ArrayList<>();
        for (Equipo equipo : club.getEquipos()) {
            for (InscripcionEquipo inscripcion : equipo.getInscripciones()) {
                lineas.add(limpiar(equipo.getNombre()) + SEP + inscripcion.getJugador().getId() + SEP + limpiar(inscripcion.getPosicion())
                        + SEP + inscripcion.getNumeroCamiseta() + SEP + inscripcion.isActivo() + SEP + inscripcion.isSuspendidoProximoPartido());
            }
        }
        return lineas;
    }

    /** competencias.txt: las de fases guardan además la fase actual y el campeón. */
    public List<String> lineasCompetencias() {
        List<String> lineas = new ArrayList<>();
        for (Competencia competencia : club.getCompetencias()) {
            String datosDeFases = VACIO + SEP + VACIO;
            if (competencia instanceof CompetenciaFases) {
                CompetenciaFases conFases = (CompetenciaFases) competencia;
                String campeon = VACIO;
                if (!conFases.getCampeon().isEmpty()) {
                    campeon = limpiar(conFases.getCampeon());
                }
                datosDeFases = conFases.getIndiceFaseActual() + SEP + campeon;
            }
            lineas.add(competencia.getFormato() + SEP + limpiar(competencia.getNombre()) + SEP + limpiar(competencia.getEntidadOrganizadora())
                    + SEP + limpiar(competencia.getTemporada()) + SEP + competencia.getDeporte() + SEP + competencia.getCategoria()
                    + SEP + competencia.getNominaMinima() + SEP + datosDeFases);
        }
        return lineas;
    }

    /** fases.txt */
    public List<String> lineasFases() {
        List<String> lineas = new ArrayList<>();
        for (Competencia competencia : club.getCompetencias()) {
            if (competencia instanceof CompetenciaFases) {
                for (Fase fase : ((CompetenciaFases) competencia).getFases()) {
                    lineas.add(limpiar(competencia.getNombre()) + SEP + limpiar(fase.getNombre()) + SEP + fase.getTipo() + SEP
                            + fase.getClasificadosPorGrupo() + SEP + fase.isFinalizada() + SEP + unir(fase.getParticipantes()));
                }
            }
        }
        return lineas;
    }

    /** grupos.txt */
    public List<String> lineasGrupos() {
        List<String> lineas = new ArrayList<>();
        for (Competencia competencia : club.getCompetencias()) {
            if (competencia instanceof CompetenciaFases) {
                for (Fase fase : ((CompetenciaFases) competencia).getFases()) {
                    for (Grupo grupo : fase.getGrupos()) {
                        lineas.add(limpiar(competencia.getNombre()) + SEP + limpiar(fase.getNombre()) + SEP + limpiar(grupo.getNombre())
                                + SEP + unir(grupo.getEquipos()));
                    }
                }
            }
        }
        return lineas;
    }

    /** inscripciones_competencia.txt: nómina, amarillas acumuladas (id:cantidad) y suspendidos. */
    public List<String> lineasInscripcionesCompetencia() {
        List<String> lineas = new ArrayList<>();
        for (Competencia competencia : club.getCompetencias()) {
            for (InscripcionCompetencia inscripcion : competencia.getInscripciones()) {
                List<String> nomina = new ArrayList<>();
                for (Socio socio : inscripcion.getNomina()) {
                    nomina.add(socio.getId());
                }
                List<String> amarillas = new ArrayList<>();
                for (Map.Entry<Socio, Integer> amarilla : inscripcion.getAmarillasAcumuladas().entrySet()) {
                    amarillas.add(amarilla.getKey().getId() + ":" + amarilla.getValue());
                }
                List<String> suspendidos = new ArrayList<>();
                for (Socio socio : inscripcion.getSuspendidos()) {
                    suspendidos.add(socio.getId());
                }
                lineas.add(limpiar(competencia.getNombre()) + SEP + limpiar(inscripcion.getEquipo().getNombre()) + SEP + unir(nomina)
                        + SEP + unir(amarillas) + SEP + unir(suspendidos));
            }
        }
        return lineas;
    }

    /** resultados_externos.txt: partidos entre otros equipos de la competencia. */
    public List<String> lineasResultadosExternos() {
        List<String> lineas = new ArrayList<>();
        for (Competencia competencia : club.getCompetencias()) {
            for (Enfrentamiento e : competencia.getResultadosExternos()) {
                lineas.add(limpiar(competencia.getNombre()) + SEP + limpiar(e.getEquipoA()) + SEP + limpiar(e.getEquipoB()) + SEP
                        + e.getGolesA() + SEP + e.getGolesB() + SEP + marcarVacio(e.getFase()) + SEP + marcarVacio(e.getGrupo()));
            }
        }
        return lineas;
    }

    /** partidos.txt: internos y de competencia. El resultado va en los tres últimos campos. */
    public List<String> lineasPartidos() {
        List<String> lineas = new ArrayList<>();
        for (Partido partido : club.getTodosLosPartidos()) {
            String resultado = SEP + partido.getMarcadorA() + SEP + partido.getMarcadorB() + SEP + partido.isJugado();
            if (partido instanceof PartidoInterno) {
                PartidoInterno interno = (PartidoInterno) partido;
                lineas.add("INTERNO;" + partido.getId() + SEP + partido.getFecha() + SEP + limpiar(interno.getEquipoLocal().getNombre())
                        + SEP + limpiar(interno.getEquipoVisitante().getNombre()) + resultado);
            } else {
                PartidoCompetencia deCompetencia = (PartidoCompetencia) partido;
                lineas.add("COMPETENCIA;" + partido.getId() + SEP + partido.getFecha() + SEP
                        + limpiar(deCompetencia.getCompetencia().getNombre()) + SEP + limpiar(deCompetencia.getEquipo().getNombre())
                        + SEP + limpiar(deCompetencia.getInstitucionRival()) + SEP + limpiar(deCompetencia.getEscenario())
                        + SEP + marcarVacio(deCompetencia.getFase()) + SEP + marcarVacio(deCompetencia.getGrupo()) + resultado);
            }
        }
        return lineas;
    }

    /** estadisticas_partido.txt */
    public List<String> lineasEstadisticasPartido() {
        List<String> lineas = new ArrayList<>();
        for (Partido partido : club.getTodosLosPartidos()) {
            for (EstadisticaPartido estadistica : partido.getEstadisticas()) {
                lineas.add(partido.getId() + SEP + textoEstadistica(estadistica));
            }
        }
        return lineas;
    }

    /** Escribe una estadística de partido según su deporte. */
    private String textoEstadistica(EstadisticaPartido estadistica) {
        String idJugador = estadistica.getJugador().getId();
        if (estadistica instanceof EstadisticaFutbol) {
            EstadisticaFutbol futbol = (EstadisticaFutbol) estadistica;
            return "FUTBOL;" + idJugador + SEP + futbol.getGoles() + SEP + futbol.getAsistencias() + SEP + futbol.getAmarillas()
                    + SEP + futbol.tieneRoja();
        }
        if (estadistica instanceof EstadisticaBaloncesto) {
            EstadisticaBaloncesto baloncesto = (EstadisticaBaloncesto) estadistica;
            return "BALONCESTO;" + idJugador + SEP + baloncesto.getPuntos() + SEP + baloncesto.getRebotes();
        }
        EstadisticaVoleibol voleibol = (EstadisticaVoleibol) estadistica;
        return "VOLEIBOL;" + idJugador + SEP + voleibol.getAces() + SEP + voleibol.getBloqueos();
    }

    // ---------------------------------------------------------------
    //  Academias y entrenamiento
    // ---------------------------------------------------------------

    /** reservas.txt */
    public List<String> lineasReservas() {
        List<String> lineas = new ArrayList<>();
        for (Instalacion instalacion : club.getInstalaciones()) {
            for (Reserva reserva : instalacion.getReservas()) {
                lineas.add(reserva.getId() + SEP + instalacion.getId() + SEP + reserva.getSocioResponsable().getId() + SEP
                        + reserva.getFechaHora() + SEP + reserva.getCantidadJugadores() + SEP + reserva.getModalidad());
            }
        }
        return lineas;
    }

    /** inscripciones_disciplina.txt */
    public List<String> lineasInscripcionesDisciplina() {
        List<String> lineas = new ArrayList<>();
        for (InscripcionDisciplina inscripcion : club.getInscripcionesDisciplina()) {
            lineas.add(inscripcion.getSocio().getId() + SEP + inscripcion.getDisciplina() + SEP + inscripcion.getNivel() + SEP
                    + inscripcion.getRankingElo());
        }
        return lineas;
    }

    /** partidos_practica.txt: los jugadores van en orden (primero el lado A y luego el lado B). */
    public List<String> lineasPartidosPractica() {
        List<String> lineas = new ArrayList<>();
        for (PartidoPractica partido : club.getPartidosPractica()) {
            List<String> idsJugadores = new ArrayList<>();
            for (InscripcionDisciplina jugador : partido.getJugadores()) {
                idsJugadores.add(jugador.getSocio().getId());
            }
            lineas.add(partido.getId() + SEP + partido.getDisciplina() + SEP + partido.getFecha() + SEP + unir(idsJugadores)
                    + SEP + partido.isJugado());
        }
        return lineas;
    }

    /** estadisticas_practica.txt */
    public List<String> lineasEstadisticasPractica() {
        List<String> lineas = new ArrayList<>();
        for (PartidoPractica partido : club.getPartidosPractica()) {
            for (EstadisticaPractica estadistica : partido.getEstadisticas()) {
                String valores;
                if (estadistica instanceof EstadisticaTenisPadel) {
                    EstadisticaTenisPadel tenis = (EstadisticaTenisPadel) estadistica;
                    valores = "TENIS_PADEL;" + estadistica.getSetsGanados() + SEP + tenis.getGamesGanados() + SEP + tenis.getAces()
                            + SEP + tenis.getErroresNoForzados();
                } else {
                    EstadisticaTenisMesa mesa = (EstadisticaTenisMesa) estadistica;
                    valores = "TENIS_MESA;" + estadistica.getSetsGanados() + SEP + mesa.getPuntosTotales();
                }
                lineas.add(partido.getId() + SEP + estadistica.getJugador().getId() + SEP + estadistica.getEloAntes() + SEP
                        + estadistica.getEloDespues() + SEP + valores);
            }
        }
        return lineas;
    }

    /** sesiones.txt */
    public List<String> lineasSesiones() {
        List<String> lineas = new ArrayList<>();
        for (SesionEntrenamiento sesion : club.getSesiones()) {
            List<String> convocados = new ArrayList<>();
            for (Socio socio : sesion.getConvocados()) {
                convocados.add(socio.getId());
            }
            String instalacion = VACIO;
            if (sesion.getInstalacion() != null) {
                instalacion = sesion.getInstalacion().getId();
            }
            lineas.add(sesion.getId() + SEP + sesion.getEntrenador().getId() + SEP + instalacion + SEP + sesion.getFechaHora()
                    + SEP + sesion.getDuracion() + SEP + unir(convocados) + SEP + sesion.isFinalizada());
        }
        return lineas;
    }

    /** asistencias.txt */
    public List<String> lineasAsistencias() {
        List<String> lineas = new ArrayList<>();
        for (SesionEntrenamiento sesion : club.getSesiones()) {
            for (AsistenciaEvaluacion asistencia : sesion.getAsistencias()) {
                lineas.add(sesion.getId() + SEP + asistencia.getJugador().getId() + SEP + asistencia.getEstado() + SEP
                        + asistencia.getNota() + SEP + marcarVacio(limpiar(asistencia.getComentario())));
            }
        }
        return lineas;
    }

    /** solicitudes_turno.txt: turno actual, turno propuesto y estado. */
    public List<String> lineasSolicitudesTurno() {
        List<String> lineas = new ArrayList<>();
        for (SolicitudCambioTurno solicitud : club.getSolicitudesTurno()) {
            Turno actual = solicitud.getTurnoActual();
            Turno propuesto = solicitud.getTurnoPropuesto();
            lineas.add(solicitud.getId() + SEP + solicitud.getEmpleado().getId()
                    + SEP + actual.getDia() + SEP + actual.getHoraInicio() + SEP + actual.getHoraFin()
                    + SEP + propuesto.getDia() + SEP + propuesto.getHoraInicio() + SEP + propuesto.getHoraFin()
                    + SEP + solicitud.getEstado());
        }
        return lineas;
    }

    // ---------------------------------------------------------------
    //  Tienda, cafetería y ventas
    // ---------------------------------------------------------------

    /** articulos.txt */
    public List<String> lineasArticulos() {
        List<String> lineas = new ArrayList<>();
        for (Articulo articulo : club.getArticulos()) {
            lineas.add(limpiar(articulo.getNombre()) + SEP + limpiar(articulo.getCategoria()) + SEP + articulo.getPrecio());
        }
        return lineas;
    }

    /** inventario.txt */
    public List<String> lineasInventario() {
        List<String> lineas = new ArrayList<>();
        for (Articulo articulo : club.getArticulos()) {
            for (InventarioUbicacion ubicacion : articulo.getInventario()) {
                lineas.add(limpiar(articulo.getNombre()) + SEP + limpiar(ubicacion.getUbicacion()) + SEP + ubicacion.getCantidad());
            }
        }
        return lineas;
    }

    /** productos_cafeteria.txt: las bebidas guardan si son frías o calientes y los snacks sus alérgenos. */
    public List<String> lineasProductosCafeteria() {
        List<String> lineas = new ArrayList<>();
        for (ProductoCafeteria producto : club.getProductosCafeteria()) {
            String extra;
            if (producto instanceof Bebida) {
                extra = ((Bebida) producto).getTipoBebida().toString();
            } else {
                extra = unir(((Snack) producto).getAlergenos());
            }
            lineas.add(producto.getTipo() + SEP + limpiar(producto.getNombre()) + SEP + producto.getPrecio() + SEP + extra);
        }
        return lineas;
    }

    /** ventas.txt: valores de la venta, dos campos propios de cada rubro y el bono usado. */
    public List<String> lineasVentas() {
        List<String> lineas = new ArrayList<>();
        for (Venta venta : club.getVentas()) {
            String extra;
            if (venta instanceof VentaTienda) {
                CodigoDescuento codigo = ((VentaTienda) venta).getDescuentoAplicado();
                String textoCodigo = VACIO;
                if (codigo != null) {
                    textoCodigo = codigo.getCodigo();
                }
                extra = textoCodigo + SEP + VACIO;
            } else if (venta instanceof VentaCafeteria) {
                extra = ((VentaCafeteria) venta).getPropina() + SEP + VACIO;
            } else {
                VentaMensualidad mensualidad = (VentaMensualidad) venta;
                extra = limpiar(mensualidad.getPeriodo()) + SEP + mensualidad.getValor();
            }
            String bono = VACIO;
            if (venta.getBonoAplicado() != null) {
                bono = venta.getBonoAplicado().getCodigo();
            }
            lineas.add(venta.getId() + SEP + venta.getRubro() + SEP + venta.getFecha() + SEP + venta.getComprador().getId()
                    + SEP + venta.getSubtotal() + SEP + venta.getDescuento() + SEP + venta.getPuntosRedimidos()
                    + SEP + venta.getImpuesto() + SEP + venta.getTotal() + SEP + venta.getPuntosFidelidadGenerados()
                    + SEP + extra + SEP + bono);
        }
        return lineas;
    }

    /** items_venta.txt: productos de las ventas de tienda y cafetería. */
    public List<String> lineasItemsVenta() {
        List<String> lineas = new ArrayList<>();
        for (Venta venta : club.getVentas()) {
            if (venta instanceof VentaTienda) {
                for (ItemVentaTienda item : ((VentaTienda) venta).getItems()) {
                    lineas.add(venta.getId() + SEP + limpiar(item.getArticulo().getNombre()) + SEP + item.getCantidad()
                            + SEP + item.getPrecioUnitario());
                }
            } else if (venta instanceof VentaCafeteria) {
                for (ItemVentaCafeteria item : ((VentaCafeteria) venta).getItems()) {
                    lineas.add(venta.getId() + SEP + limpiar(item.getProducto().getNombre()) + SEP + item.getCantidad()
                            + SEP + item.getPrecioUnitario());
                }
            }
        }
        return lineas;
    }

    /** sugerencias.txt */
    public List<String> lineasSugerencias() {
        List<String> lineas = new ArrayList<>();
        for (Sugerencia sugerencia : club.getSugerencias()) {
            lineas.add(sugerencia.getId() + SEP + sugerencia.getAutor().getId() + SEP + sugerencia.getTipoProducto() + SEP
                    + limpiar(sugerencia.getNombreProducto()) + SEP + marcarVacio(limpiar(sugerencia.getCategoria()))
                    + SEP + sugerencia.getPrecioSugerido() + SEP + sugerencia.getEstado() + SEP + sugerencia.getFecha());
        }
        return lineas;
    }

    // ---------------------------------------------------------------
    //  Torneos (Proyecto 2)
    // ---------------------------------------------------------------

    /** torneos.txt: datos comunes, posiciones finales y datos de la disciplina o del deporte. */
    public List<String> lineasTorneos() {
        List<String> lineas = new ArrayList<>();
        for (Torneo torneo : club.getTorneos()) {
            List<String> posiciones = new ArrayList<>();
            for (InscripcionTorneo inscripcion : torneo.getPosiciones()) {
                posiciones.add(String.valueOf(inscripcion.getId()));
            }
            String comun = limpiar(torneo.getNombre()) + SEP + torneo.getTipo() + SEP + torneo.getCupos() + SEP + torneo.getTarifa()
                    + SEP + torneo.getPorcentajeBono() + SEP + torneo.getFechaInicio() + SEP + torneo.getFechaCierrePreferencial()
                    + SEP + torneo.getEstado() + SEP + unir(posiciones);
            if (torneo instanceof TorneoIndividual) {
                TorneoIndividual individual = (TorneoIndividual) torneo;
                lineas.add("INDIVIDUAL;" + comun + SEP + individual.getDisciplina() + SEP + individual.getNivel() + SEP + individual.isDobles());
            } else {
                TorneoConjunto conjunto = (TorneoConjunto) torneo;
                lineas.add("CONJUNTO;" + comun + SEP + conjunto.getDeporte() + SEP + conjunto.getCategoria() + SEP + VACIO);
            }
        }
        return lineas;
    }

    /** inscripciones_torneo.txt: al final va el equipo inscrito o los ids de los jugadores. */
    public List<String> lineasInscripcionesTorneo() {
        List<String> lineas = new ArrayList<>();
        for (Torneo torneo : club.getTorneos()) {
            for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
                String quien;
                if (inscripcion instanceof InscripcionTorneoEquipo) {
                    quien = "EQUIPO;" + limpiar(((InscripcionTorneoEquipo) inscripcion).getEquipo().getNombre());
                } else {
                    List<String> ids = new ArrayList<>();
                    for (Usuario jugador : inscripcion.getIntegrantes()) {
                        ids.add(jugador.getId());
                    }
                    quien = "JUGADORES;" + unir(ids);
                }
                lineas.add(limpiar(torneo.getNombre()) + SEP + inscripcion.getId() + SEP + inscripcion.getFecha() + SEP
                        + inscripcion.getTipoCupo() + SEP + inscripcion.getTarifaPagada() + SEP + inscripcion.getRanking() + SEP + quien);
            }
        }
        return lineas;
    }

    /** encuentros_torneo.txt */
    public List<String> lineasEncuentrosTorneo() {
        List<String> lineas = new ArrayList<>();
        for (Torneo torneo : club.getTorneos()) {
            for (EncuentroTorneo encuentro : torneo.getEncuentros()) {
                lineas.add(limpiar(torneo.getNombre()) + SEP + encuentro.getId() + SEP + marcarVacio(limpiar(encuentro.getFase()))
                        + SEP + encuentro.getLadoA().getId() + SEP + encuentro.getLadoB().getId()
                        + SEP + encuentro.getMarcadorA() + SEP + encuentro.getMarcadorB());
            }
        }
        return lineas;
    }

    /** premios_torneo.txt: puesto, inscripción ganadora, dinero y códigos de los bonos entregados. */
    public List<String> lineasPremiosTorneo() {
        List<String> lineas = new ArrayList<>();
        for (Torneo torneo : club.getTorneos()) {
            for (Premio premio : torneo.getPremios()) {
                List<String> codigos = new ArrayList<>();
                for (BonoDescuento bono : premio.getBonos()) {
                    codigos.add(bono.getCodigo());
                }
                lineas.add(limpiar(torneo.getNombre()) + SEP + premio.getPuesto() + SEP + premio.getInscripcion().getId()
                        + SEP + premio.getMonto() + SEP + unir(codigos));
            }
        }
        return lineas;
    }
}
