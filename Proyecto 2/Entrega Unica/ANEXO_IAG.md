# Anexo: uso de IA generativa (Nivel 3)

## Proyecto 2 — entrega única

**Herramienta:** Claude (Anthropic). **Fecha:** 3 de noviembre de 2026.


### 1. Uso de la herramienta

Claude se utilizó como herramienta de apoyo durante la construcción y organización inicial del proyecto. Su principal aporte fue generar una estructura base que facilitara el desarrollo posterior por parte del grupo.

En particular, la herramienta apoyó en:

- **Estructura inicial del código:** creación de clases, atributos, constructores, getters, setters y enumeraciones.
- **Estructura de las interfaces de usuario:** código base de `Scanner`, validación de entradas, menús y las tres aplicaciones de consola. Estas consolas se diseñaron para interactuar con la fachada `ClubDeportivo`.
- **Preparación de pruebas:** generación de pruebas JUnit iniciales y datos de prueba, siguiendo un enfoque de desarrollo guiado por pruebas (TDD).
- **Documentación del código:** inclusión de comentarios `TODO` con indicaciones para orientar la implementación de los métodos pendientes.

### 2. Trabajo desarrollado por el grupo

El grupo se encargó de las decisiones de diseño y de la implementación de la lógica principal del proyecto. En particular, se desarrollaron los 36 métodos inicialmente identificados mediante comentarios `TODO`.

Por tanto, Claude proporcionó una estructura inicial y una guía para el desarrollo, mientras que el grupo asumió la implementación de los métodos de lógica de negocio y las decisiones necesarias para adaptarlos a los requerimientos del proyecto.

La ubicación de los métodos implementados se encuentra documentada en el archivo README del proyecto.

### 3. Prompts utilizados

A continuación, se presentan los principales prompts y solicitudes realizados durante el proceso:

1. "Este es el archivo para la segunda parte de mi proyecto club deportivo seneca,  ayúdame a dividir el trabajo en 3 y a hacer una lista de que debemos completar y quien se encarga de que", junto con el PDF del banco oficial de historias de usuario, el PDF del enunciado del Proyecto 2 y el documento de historias de usuario redactado por el grupo.

2."Ahora hazme un archivo de Eclipse nuevo con la base/esqueleto para el código de mi entrega basado en el último. Indicame con //Todo los metodos que debemos completar y una lista de su orden de implementacion".


### 4. Archivos proporcionados

Para el desarrollo y la adaptación del proyecto se proporcionaron los siguientes archivos:

- `Banco_Oficial_Historias_de_Usuario_CDS.pdf`.
- Documento de historias de usuario elaborado por el grupo.
- `Proyecto_2_ClubDeportivoSéneca.pdf`, correspondiente al enunciado del proyecto.
- Archivo del proyecto anterior y su UML




---

## Proyecto 1 — Entrega 2 (histórico)

**Herramienta:** Claude (Anthropic). **Fecha:** 11 de octubre de 2026.

**Qué generó la herramienta:** la estructura del proyecto a partir del UML del grupo: clases, atributos, constructores, getters y setters, enums, la plantilla de persistencia en archivos, los datos de prueba y los programas de demostración. También generó instrucciones paso a paso (comentarios TODO) para los métodos de lógica de negocio.

**Qué implementó el grupo:** los métodos marcados con TODO (lógica de negocio).

**Prompt utilizado (textual):**
1. "ayudame a hacer la estructura de mi proyecto con base en mi uml en java para comprimido para descargar sin las funciones sino con los espacios de cada metodo marcados con //TODO para completar  e instrucciones paso a paso del algoritmo que debo implementar, ademas ayudame a hacer las pruebas de mi sistema y genera los datos de prueba para llevarlas a cabp"

**Archivo entregado a la herramienta:** `UMLentrega1Proyecto_corregido.uxf`
