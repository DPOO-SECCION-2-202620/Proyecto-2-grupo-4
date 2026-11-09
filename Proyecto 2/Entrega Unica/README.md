# Club Deportivo Séneca — Proyecto 2, entrega única (DPOO, Grupo 04)

Este proyecto parte del código del Proyecto 1 (Entrega 2) y está alineado con el documento de historias de usuario del grupo (Samuel Beltrán, Rafael Mora y Daniela Sánchez). Cubre:

- **Historias del banco oficial:** HU-ADM-02, HU-ENT-02, HU-SOC-01 y HU-SOC-02.
- **Historias de torneos:** HU-TOR-ADM-01 a 03, HU-TOR-SOC-01 a 05 y HU-TOR-EMP-01.
- **Interfaces:** 3 consolas con inicio de sesión.
- **Pruebas:** JUnit 5.

## Cómo importarlo en Eclipse
1. Descomprima el `.zip` dentro de `Proyecto 2/Entrega única/` en el repositorio.
2. En Eclipse vaya a **File > Import > General > Existing Projects into Workspace** y elija la carpeta `ClubDeportivoSenecaP2`.
3. Requiere Java 17 o superior. JUnit 5 ya está en el *build path*. Si Eclipse no lo encuentra, haga clic derecho en el proyecto y vaya a *Build Path > Add Libraries > JUnit > JUnit 5*.
4. Si las tildes se ven mal en la consola, vaya a *Run > Run Configurations > Common > Encoding: UTF-8*.

## Cómo ejecutarlo
Las tres aplicaciones están en el paquete `consola`, cada una con su propio `main`:

| Programa | Usuarios | Login de prueba |
|---|---|---|
| `ConsolaAdministrador` | Administrador | `admin` / `admin123` |
| `ConsolaParticipante` | Socios, estudiantes y externos (al iniciar puede crear una cuenta) | `s001` / `1234`, `jospina` / `1234` (estudiante), `rvelez` / `1234` (externa) |
| `ConsolaEntrenador` | Entrenadores y fisioterapeutas | `cmendez` / `ent123`, `prios` / `fis123` |

- Al iniciar, cada consola **carga** los archivos de `datos/` y al salir **guarda** en esos mismos archivos (guardado "todo o nada": si algo falla, los archivos anteriores quedan intactos).
- Opcional (VM arguments): `-Dclub.datos=otraCarpeta` usa otra carpeta de datos; `-Dclub.hoy=aaaa-mm-dd` simula la fecha de hoy.
- El diseño de la interfaz y de la persistencia está en [`DISENO_CONSOLA_Y_PERSISTENCIA.md`](DISENO_CONSOLA_Y_PERSISTENCIA.md).
- `GeneradorDatosPrueba` regenera `datos/` con los datos de prueba originales.

## Qué hace cada consola, por historia
| Historia | Consola y opción |
|---|---|
| HU-ADM-02 Revisar cambios de turno | Administrador > Turnos del personal |
| HU-ENT-02 Mínimo de jugadores habilitados | Entrenador > Revisar el mínimo de jugadores habilitados |
| HU-SOC-01 Reservar instalación | Participante (socio) > Reservar una instalación |
| HU-SOC-02 Inscribirse en un equipo | Participante (socio) > Inscribirme en un equipo de conjunto |
| HU-TOR-ADM-01 Crear torneo | Administrador > Torneos > Crear torneo |
| HU-TOR-ADM-02 Finalizar y asignar premios | Administrador > Torneos > Iniciar / Registrar encuentro / Finalizar |
| HU-TOR-ADM-03 y HU-TOR-SOC-05 Consultar | Las tres consolas > Torneos > Consultar / Ver historial |
| HU-TOR-SOC-01 Inscribirme | Participante > Torneos > Inscribirme (individual) o Inscribir a mi equipo |
| HU-TOR-SOC-02 Salirme del torneo | Participante > Torneos > Salirme / Retirar a mi equipo |
| HU-TOR-SOC-03 Cupo preferencial | Automático al inscribirse |
| HU-TOR-SOC-04 Usar el bono | Participante > Tienda, cafetería y bonos |
| HU-TOR-EMP-01 Empleado en torneo individual | Entrenador > Torneos > Inscribirme en un torneo individual |

## Pruebas (JUnit 5)
Haga clic derecho en la carpeta `test` y elija **Run As > JUnit Test**.

| Carpeta | Qué contiene |
|---|---|
| `test/modelo/torneos`, `test/modelo/ventas` | Pruebas unitarias: cupos, validación, ciclo del torneo (iniciar, encuentros, finalizar y premios), bonos y ventas con bono |
| `test/integracion` | Una clase por historia (`HU_ENT_02_...`, `HU_TOR_ADM_01_...`, `HU_TOR_SOC_03_...`). Cada método se llama como su criterio (`CA1_...`) |
| `test/persistencia` | Pruebas de guardar y cargar la información nueva |
| `test/logica` | Pruebas de los métodos de la fachada que usan las consolas (`ConsultasConsolaTest`) |
| `test/pruebas/ClubDePrueba` | Club pequeño en memoria con fechas fijas para las pruebas |

**Estado actual: pasan las 125 pruebas** (las 116 de las historias y las clases del modelo, más las 9 de `ConsultasConsolaTest`).

## Métodos implementados por historia
Esta tabla dice dónde está cada método que se implementó para el Proyecto 2 y qué prueba lo verifica.

| # | Archivo y método | Historia | Prueba que lo verifica |
|---|---|---|---|
| 1 | `Equipo.inscribirJugador` (mensaje del dorsal), `ClubDeportivo.inscribirJugadorEnEquipo` (mensaje) | HU-SOC-02 | `HU_SOC_02_...` |
| 2 | `ClubDeportivo.reservar` (mensaje), `CanchaPadel` + `Instalacion.motivoRechazo` | HU-SOC-01 | `HU_SOC_01_...` |
| 3 | `Equipo.getJugadoresNoDisponibles` | HU-ENT-02 | `HU_ENT_02_...` |
| 4 | `Torneo`: `getCuposPreferenciales`, `preferencialesLiberados`, `getCuposDisponibles` | HU-TOR-SOC-03 | `TorneoCuposTest` |
| 5 | `Torneo.registrarInscripcion`, `TorneoIndividual.inscribir`, `TorneoConjunto.inscribirEquipo` | HU-TOR-SOC-01 | `TorneoCuposTest` |
| 6 | `Torneo.retirar` | HU-TOR-SOC-02 | `TorneoCuposTest` |
| 7 | `Torneo.validarDatos`, `TorneoIndividual.validarDatosEspecificos`, `ClubDeportivo.registrarTorneo` | HU-TOR-ADM-01 | `TorneoValidacionTest`, `HU_TOR_ADM_01_...` |
| 8 | `Torneo.iniciar`, `Torneo.registrarEncuentro` | inicio del torneo y HU-TOR-ADM-03 | `TorneoCicloTest` |
| 9 | `Torneo.calcularBolsa`, `Torneo.finalizar` | HU-TOR-ADM-02 | `TorneoCicloTest` |
| 10 | `Empleado.tieneTurnoEl` | HU-TOR-EMP-01 | `HU_TOR_EMP_01_...` |
| 11 | `ClubDeportivo`: `calcularRankingIndividual`, `calcularPuntosInternos`, `rankingsDeCategoria` (×2), `esElegiblePreferencial` | HU-TOR-SOC-03 | `HU_TOR_SOC_03_...` |
| 12 | `ClubDeportivo.inscribirEnTorneoIndividual`, `puedeGestionarEquipo`, `inscribirEquipoEnTorneo` | HU-TOR-SOC-01, EMP-01 | `HU_TOR_SOC_01/02/03_...`, `HU_TOR_EMP_01_...`, `HU_TOR_ADM_02/03_...` |
| 13 | `ClubDeportivo.registrarExterno` | registro de externos | `RegistroExternoTest` |
| 14 | `BonoDescuento.usar`, `calcularDescuento`; `Venta.aplicarBono`, `calcularDescuentoBono`, `usarPuntosFidelidad`; `VentaTienda.aplicarDescuento`, `admiteBono` | HU-TOR-SOC-04 | `BonoDescuentoTest`, `VentaBonoTest`, `HU_TOR_SOC_04_...` |

## Decisiones y supuestos del grupo (complementan la sección 3.5 de las HU)
- **Ciclo del torneo:** se crea con inscripciones abiertas. El administrador lo **inicia** (se cierran las inscripciones; hacen falta al menos 2 inscritos), registra los **encuentros** jugados (para el historial) y lo **finaliza** registrando las posiciones a mano.
- **Premio en efectivo:** un porcentaje del total recaudado configurable en `config.txt` (`PREMIO;0.7` = 70 %), solo para el ganador. Si el ganador es empleado, el premio pasa al siguiente participante que no sea empleado.
- **Bono:** el administrador define su valor (% de descuento) al crear el torneo recreativo. Lo recibe cada integrante del primer puesto (en dobles o equipos, todos), también si es empleado. Es de uso único y no se acumula con el código de empleado ni con los puntos.
- **Ranking de equipos:** puntos en partidos internos (victoria 3, empate 1). Ranking individual: ELO de la disciplina. "Los mejores" son los N primeros, con N = cupos preferenciales.
- **Externos y estudiantes:** se registran ellos mismos como `ParticipanteExterno` desde la consola de participantes. Esto resuelve el "falta definir quién los registra" del documento. No reservan instalaciones, no entran a equipos y no acumulan puntos.
- **Inscribir un equipo:** lo hace un socio activo del equipo (HU-TOR-SOC-01 CA-2) o su entrenador.
- **Empleados:** el torneo se juega en un solo día; un empleado está de turno si tiene un turno ese día de la semana. Una inscripción es "de empleado" (tarifa 0) si **todos** sus integrantes son empleados.
- **Otras reglas que se conservan:** el pádel siempre en dobles, el nombre del torneo es único y la fecha límite de los preferenciales es anterior al inicio.

## Estructura
```
src/
  consola/        ConsolaBase, Menu, LectorEntrada, EntradaTerminadaException, GestorDatos, FlujosTorneo,
                  ConsolaAdministrador, ConsolaParticipante, ConsolaEntrenador, GeneradorDatosPrueba
  modelo/torneos/ Torneo, TorneoIndividual, TorneoConjunto, InscripcionTorneo (+Individual, +Equipo), EncuentroTorneo, Premio, BonoDescuento
  modelo/enums/   + TipoTorneo, EstadoTorneo, TipoCupo
  modelo/usuarios/ + ParticipanteExterno; Usuario tiene bonos; Empleado tiene niveles declarados y tieneTurnoEl
  modelo/conjunto/ Equipo tiene entrenador y getJugadoresNoDisponibles (HU-ENT-02)
  modelo/ventas/   Venta tiene bonoAplicado (no acumulable)
  logica/          ClubDeportivo: torneos, rankings, externos, bonos, HU-ENT-02, % del premio y consultas para las consolas
  persistencia/    PersistenciaClub, EscritorClub, LectorClub, ArchivoTexto
                   + torneos.txt, inscripciones_torneo.txt, encuentros_torneo.txt, premios_torneo.txt, bonos.txt,
                     niveles_declarados.txt; config.txt (PREMIO), equipos.txt y ventas.txt tienen campos nuevos
test/              Pruebas JUnit 5
datos/             Datos de prueba
```
