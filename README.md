# Congreso: personas, países e inscripciones

Estructura de Datos · UEES  
MSc. Lessette Carolina Zambrano Zurita – Docente UEES

## 1. Objetivo y alcance

Comparen los cinco métodos del sistema de un congreso utilizando cuatro estructuras: ArrayList y otras tres elegidas entre LinkedList, HashSet, TreeSet, HashMap y TreeMap.

La base entrega ArrayList funcional. Implementen los cinco métodos para sus tres alternativas y completen los TODO de medición y almacenamiento estático de tiempos. Cada alternativa debe usar la misma estructura para las tres colecciones: países, personas e inscripciones. Por ejemplo, si prueban HashSet, las tres deben ser HashSet; no combinen una lista de personas con un mapa de inscripciones en esa prueba.

Los cinco métodos son:

1. Buscar persona.
2. Corregir país.
3. Registrar inscripción.
4. Cancelar inscripción.
5. Contar personas por país.

La interfaz incluye un cuadro de cinco filas por cuatro estructuras y un gráfico de barras con las mediciones reales. Cada método registra un solo tiempo por estructura.

## 2. Contenido del proyecto

| Archivo | Responsabilidad |
|---|---|
| `datos/congreso_datos.csv` | CSV original común a todas las pruebas. |
| `src/Modelo.java` | Entidades Pais, Persona e Inscripcion, relacionadas por códigos. |
| `src/Datos.java` | Lectura del CSV y eliminación de repeticiones de entidades durante la carga. |
| `src/Estado.java` | Condicional de instanciación y tres colecciones persistentes del mismo tipo. |
| `src/Operaciones.java` | Cinco operaciones funcionales con ArrayList y TODO para las alternativas elegidas. |
| `src/Mediciones.java` | TODO de toma de tiempos y registro estático por método/estructura. |
| `src/Flujo.java` | Escenarios comunes, orden, comprobación de resultados y protección del estado ante errores. |
| `src/App.java` | Interfaz, selección de alternativas, cuadro comparativo, gráfico y exportación. |
| `compilar.sh` / `compilar.bat` | Compilan y generan el ejecutable con el CSV incorporado. |
| `ejecutar.sh` / `ejecutar.bat` | Compilan, generan el JAR y abren la aplicación en un solo paso. |
| `dist/BaseColecciones.jar` | Ejecutable de la base. Deben reconstruirlo después de cambiar el código. |

Completen `Operaciones.java` y `Mediciones.java`. Mantengan las firmas, datos, secuencia y validadores proporcionados. No modifiquen los escenarios para que una implementación incorrecta apruebe la comprobación.

## 3. Compilar y abrir en un solo paso

Requieren JDK 11 o superior y las herramientas `java`, `javac` y `jar` en PATH. Descompriman el ZIP y entren a `Base_Colecciones` desde el terminal.

### macOS · Terminal

```bash
cd "/ruta/real/Base_Colecciones"
bash ejecutar.sh
```

Ese único `.sh` compila los archivos Java, incorpora el CSV, genera `dist/BaseColecciones.jar` y abre la interfaz. Cada vez que vuelvan a ejecutarlo, recompila los fuentes actuales. No necesitan ejecutar otro comando de compilación.

### Windows · PowerShell

```powershell
cd "C:\ruta\real\Base_Colecciones"
.\ejecutar.bat
```

En CMD, dentro de la carpeta, escriban `ejecutar.bat`. El mismo archivo compila, genera el JAR y abre la interfaz. Si falla la compilación se detiene y no abre un ejecutable anterior.

`compilar.sh` y `compilar.bat` siguen disponibles si solo necesitan generar el ejecutable. Una vez generado, también pueden abrirlo con `java -jar dist/BaseColecciones.jar`.

Para comprobar la carga y los cinco escenarios de ArrayList sin abrir la ventana:

```bash
bash ejecutar.sh --verificar-base
```

En Windows: `.\ejecutar.bat --verificar-base`.

### Gestión interactiva

La aplicación abre en la pestaña `Gestión interactiva`. Pueden operar con los campos del formulario sin seguir un orden obligatorio:

| Acción | Uso |
|---|---|
| Buscar persona | Escriban el ID y pulsen el botón. La tabla muestra la persona encontrada y sus inscripciones. |
| Cambiar país | Usen el ID de persona, elijan otro país y pulsen `Aplicar cambio de país`. |
| Registrar inscripción | Indiquen el ID de persona, código nuevo y actividad; pulsen `Registrar inscripción`. |
| Cancelar inscripción | Escriban el código exacto que desean cancelar o selecciónenlo en la tabla de inscripciones. |
| Contar personas por país | Pulsen el botón y revisen las doce cantidades en la pestaña `Conteo por país`. |
| Ver todas las personas | Elimina el filtro de búsqueda para volver a explorar los datos. |
| Restaurar datos de uso libre | Restaura el CSV únicamente para la estructura activa en esta pestaña. |

Ejemplo: busquen P06000, que inicialmente pertenece a PAN y no tiene inscripciones. Cambien su país a CRI. Registren `I06000-N1` para `Taller de Java` y luego cancelen ese mismo código. La persona se conserva. También pueden seleccionar filas para copiar los códigos al formulario. La zona izquierda tiene desplazamiento vertical y los paneles se pueden redimensionar con sus separadores.

ArrayList funciona desde la base. Las alternativas muestran el TODO correspondiente hasta que implementen sus operaciones. Sus instancias y cambios de uso libre se conservan al cambiar de estructura y regresar.

### Comparación de tiempos

La pestaña `Comparación de tiempos` contiene los cinco escenarios estandarizados, la tabla, el gráfico y la exportación CSV. Esta comparación utiliza un estado independiente del uso libre, cargado desde el mismo CSV original. Así pueden experimentar con el formulario sin cambiar las condiciones del flujo de evaluación.

Dentro de la comparación, los pasos 1 a 5 sí conservan sus cambios y deben ejecutarse en el orden definido más adelante. El uso libre no agrega tiempos al cuadro comparativo. Para medir completen los TODO de `Mediciones.java` y ejecuten los botones de escenarios.

## 4. Carga común y relaciones

El archivo original contiene 7500 filas CSV. Una fila puede repetir una persona por tener varias inscripciones. Al cargarlo deben existir exactamente:

| Entidad | Cantidad inicial | Identidad |
|---|---:|---|
| Países | 12 | `codigo_pais` |
| Personas | 6000 | `id_persona` |
| Inscripciones | 6900 | `codigo_inscripcion` |

Columnas originales: `id_persona`, `nombre_persona`, `codigo_pais`, `nombre_pais`, `codigo_inscripcion`, `actividad`.

Una persona pertenece a un país y puede tener cero, una o varias inscripciones. Las 600 filas sin código de inscripción representan personas que deben conservarse. No creen una inscripción vacía para ellas. No creen otra Persona por cada inscripción de la misma persona.

Actividades permitidas: `Conferencias`, `Taller de Java` y `Taller de datos`.

Los modelos son inmutables. Al corregir el país creen la Persona actualizada con el mismo ID y nombre, y reemplacen la anterior en su colección. `equals`, `hashCode` y `compareTo` usan el código de identidad; en conjuntos deben retirar la entidad anterior antes de agregar la versión actualizada. Agregar otra con el mismo ID puede ser ignorado por el conjunto.

En HashMap y TreeMap las claves son el código del país, el ID de la persona y el código de la inscripción, respectivamente.

## 5. Selección e instancia activa

Usen `Elegir las otras 3 estructuras` para seleccionar tres alternativas distintas. ArrayList permanece en la comparación. Las alternativas visibles al iniciar son editables y no constituyen una selección obligatoria. Aplicar otra selección reinicia la comparación.

El condicional de `Estado.Almacen` ya instancia las colecciones para cada modo. El estado de cada estructura se crea al seleccionarla por primera vez y se conserva al cambiar a otra y regresar. Los cinco botones de cada modo reciben su mismo estado. Gestión interactiva y comparación tienen estados independientes por estructura.

| Estructura activa | Acceso a cada colección |
|---|---|
| ArrayList / LinkedList | `e.personas.lista`, `e.paises.lista`, `e.inscripciones.lista` |
| HashSet / TreeSet | `e.personas.conjunto`, `e.paises.conjunto`, `e.inscripciones.conjunto` |
| HashMap / TreeMap | `e.personas.mapa`, `e.paises.mapa`, `e.inscripciones.mapa` |

`valores()` permite recorrer la colección activa. No sustituyan las colecciones por otras nuevas dentro de una operación. Tampoco conviertan permanentemente las listas o conjuntos a mapas para resolver sus búsquedas: implementen el trabajo con la estructura que están comparando.

Las colecciones auxiliares usadas por la carga, los validadores y la vista están fuera de la medición. El mapa devuelto por contar personas por país es una salida auxiliar válida y no sustituye las tres colecciones del estado.

## 6. Regla de ejecución común

En la pestaña Comparación de tiempos, cada estructura comienza con el CSV original y recorre el flujo 1 → 2 → 3 → 4 → 5. Los cambios de un paso se conservan para el siguiente. No recarguen el CSV entre pasos de la misma estructura.

Cada botón ejecuta todas las llamadas de su tabla, en el orden indicado, y obtiene UN tiempo conjunto para ese método. Los resultados se validan después, fuera del intervalo medido.

Si intentan un paso futuro, la interfaz indica cuál corresponde. Si una operación falla o devuelve un resultado incorrecto, se restaura el estado previo a ese intento conservando las mismas instancias. Corrijan su implementación y vuelvan a iniciar una serie limpia después de compilar.

Un botón ya completado no vuelve a ejecutar sus cambios ni agrega tiempos. Cambiar de estructura permite continuar el flujo pendiente de esa estructura. `Reiniciar comparación` restaura todas las colecciones iniciales, conserva sus instancias y borra todos los registros de la serie.

## 7. Escenario 1 · Buscar persona

Firma: `buscarPersona(Estado e, String id)`.

Devuelvan la Persona completa o null si no existe. La búsqueda no modifica ninguna de las tres colecciones.

| Paso | Entrada | Resultado esperado |
|---|---|---|
| 1 | `P00001` | `P00001`, `Participante 00001`, país `ECU`. Tiene dos inscripciones en el CSV. |
| 2 | `P06000` | `P06000`, `Participante 06000`, país `PAN`. Inicialmente no tiene inscripción. |
| 3 | `P99999` | `null`; persona inexistente. |
| 4 | `null` | `null`; sin excepción. |

Final: 12 países, 6000 personas y 6900 inscripciones. Se mide el conjunto de cuatro búsquedas una sola vez.

## 8. Escenario 2 · Corregir país

Firma: `corregirPais(Estado e, String idPersona, String codigoPais)`.

Devuelvan true solo si una persona existente cambia a otro país existente. Devuelvan false si falta la persona, el país no existe o ya tiene ese mismo país. Conserven el nombre y el ID de la persona, los países y todas sus inscripciones.

| Paso | Persona | País solicitado | Resultado esperado |
|---|---|---|---|
| 1 | `P00002` | `CRI` | `true`; pasa de Ecuador (`ECU`) a Costa Rica (`CRI`). |
| 2 | `P99999` | `ECU` | `false`; persona inexistente. |
| 3 | `P00003` | `ZZZ` | `false`; país inexistente. P00003 sigue en `COL`. |
| 4 | `P00002` | `CRI` | `false`; ya está en ese país. |

Final: 12 países, 6000 personas, 6900 inscripciones. Solo cambia el país de P00002. La inscripción `I00002-1` sigue vinculada a esa misma persona. Se mide la secuencia de cuatro llamadas una sola vez.

## 9. Escenario 3 · Registrar inscripción

Firma: `registrarInscripcion(Estado e, String codigo, String idPersona, String actividad)`.

Una inscripción válida necesita código no vacío y único, persona existente y una actividad permitida. Tampoco se permite duplicar la misma combinación persona/actividad, aunque el nuevo código sea diferente. Devuelvan true solo si agregan la inscripción. No creen nuevas personas ni países.

| Paso | Código | Persona | Actividad | Resultado esperado |
|---|---|---|---|---|
| 1 | `I06000-N1` | `P06000` | `Taller de Java` | `true`; agrega la primera inscripción de esta persona. |
| 2 | `I06000-N1` | `P00003` | `Conferencias` | `false`; código ya utilizado en el paso 1. |
| 3 | `I06000-N2` | `P06000` | `Taller de Java` | `false`; persona/actividad ya inscrita. |
| 4 | `I99999-N1` | `P99999` | `Conferencias` | `false`; persona inexistente. |
| 5 | `I06000-N3` | `P06000` | `Actividad inexistente` | `false`; actividad no permitida. |

Final: 12 países, 6000 personas y 6901 inscripciones. Solo se agregó `I06000-N1`. P00002 conserva el país CRI del paso anterior. Se mide la secuencia de cinco llamadas una sola vez.

## 10. Escenario 4 · Cancelar inscripción

Firma: `cancelarInscripcion(Estado e, String codigo)`.

Devuelvan true si eliminan la inscripción indicada y false si no existe. Eliminar una inscripción no elimina a la persona ni al país.

| Paso | Código | Resultado esperado |
|---|---|---|
| 1 | `I00002-1` | `true`; P00002 queda sin inscripción, pero se conserva como persona de CRI. |
| 2 | `I00001-1` | `true`; P00001 conserva su otra inscripción, `I00001-2`. |
| 3 | `I99999-1` | `false`; código inexistente. |
| 4 | `I00002-1` | `false`; ya fue cancelada en el paso 1. |

Final: 12 países, 6000 personas y 6899 inscripciones. La inscripción nueva de P06000 sigue existiendo. Se mide la secuencia de cuatro llamadas una sola vez.

## 11. Escenario 5 · Contar personas por país

Firma: `contarPersonasPorPais(Estado e)`.

Devuelvan `Map<String, Integer>`, con código de país como clave y cantidad de PERSONAS como valor. Incluyan personas sin inscripción; una persona con dos inscripciones se cuenta una sola vez. Incluyan todos los países, aunque alguno tuviera cero personas.

Se realiza UNA llamada sobre el estado resultante de los cuatro escenarios anteriores.

| Código | País | Personas al terminar el flujo |
|---|---|---:|
| ARG | Argentina | 500 |
| BOL | Bolivia | 500 |
| BRA | Brasil | 500 |
| CHL | Chile | 500 |
| COL | Colombia | 500 |
| CRI | Costa Rica | 500 |
| ECU | Ecuador | 500 |
| MEX | México | 500 |
| PAN | Panamá | 500 |
| PER | Perú | 500 |
| PRY | Paraguay | 500 |
| URY | Uruguay | 500 |

La suma debe ser 6000. El CSV inicial tiene ECU=501 y CRI=499; el cambio de P00002 a CRI en el escenario 2 deja 500 en cada país. Las cancelaciones de inscripción no reducen el conteo de personas. La inscripción nueva de P06000 tampoco crea otra persona.

El orden de claves del resultado puede variar. Se validan las doce cantidades exactas. Final: 12 países, 6000 personas y 6899 inscripciones.

## 12. TODO de toma de tiempos

Completen `Mediciones.medir(Trabajo trabajo)`:

1. Tomen el instante inicial con `System.nanoTime()`.
2. Ejecuten `trabajo.ejecutar()` exactamente una vez y conserven su resultado.
3. Tomen el instante final.
4. Devuelvan `Medicion` con ese resultado y la diferencia en nanosegundos.

La base actual ejecuta el trabajo y devuelve tiempo null. Esto permite usar ArrayList sin presentar una medición falsa. Sustituyan ese comportamiento por la medición real. No ejecuten dos veces el trabajo para obtener su resultado.

| Dentro del tiempo | Fuera del tiempo, resuelto por la base |
|---|---|
| Todas las llamadas del escenario seleccionado, con sus validaciones de negocio. | Carga y lectura del CSV. |
| Búsquedas auxiliares necesarias dentro de ese método. | Preparación de expectativas y copia de respaldo del estado. |
| Creación de la salida del propio método y captura común de sus resultados. | Comprobación de resultados e integridad de las tres colecciones. |
| | Refresco de tablas, mensajes, dibujo del gráfico y exportación CSV. |

La función de contar por país puede construir su mapa de salida dentro del intervalo: es parte del trabajo solicitado. No introduzcan diálogos ni impresión por pantalla dentro de las operaciones. Si ocurre una excepción, propáguenla sin inventar una medición válida.

Si registrar inscripción utiliza buscar persona como auxiliar, ese trabajo pertenece al tiempo de registrar. No añadan otro registro de buscar: cada botón produce el tiempo del método seleccionado.

## 13. TODO de almacenamiento estático

Completen `Mediciones.registrar(tipo, metodo, nanosegundos)` usando `Mediciones.TIEMPOS`:

- Rechacen un tiempo negativo.
- Guarden solo el primer tiempo de ese método en esa estructura.
- Si ya existe una medición, consérvenla sin sumar ni reemplazar.
- No modifiquen registros de otros métodos ni de otras estructuras.

El registro se llama después de comprobar el resultado y las colecciones. El almacenamiento es estático durante la sesión: no se crea otro mapa en cada clic ni en cada operación. Cierren y abran el programa para una sesión nueva; el CSV exportado conserva la evidencia de la serie anterior.

Un método funcional con los TODO de medición o registro pendientes se muestra como `Probado · sin tiempo`. Esto no equivale a un tiempo de cero. Un método todavía no ejecutado aparece como `Pendiente`.

Ejemplo: buscar, corregir y registrar con tiempos válidos en ArrayList producen tres métodos medidos y un total parcial igual a la suma de ESOS tres tiempos. Repetir un botón no aumenta el contador ni el total. Otra estructura conserva sus propios registros.

Para comparar totales, las cuatro estructuras deben completar los mismos cinco métodos con tiempos válidos. El total completo suma cinco tiempos por estructura, no la cantidad de clics ni el número de llamadas internas de los escenarios.

## 14. Procedimiento de comparación

1. Seleccionen las tres alternativas que implementará su grupo.
2. Completen los cinco métodos para cada alternativa y ambos TODO de Mediciones.
3. Compilen y abran con `bash ejecutar.sh` en Mac o `ejecutar.bat` en Windows. Entren en `Comparación de tiempos`.
4. Definan las mismas tres alternativas en la interfaz y pulsen `Reiniciar comparación`.
5. Seleccionen ArrayList y ejecuten los pasos 1 a 5 en orden.
6. Cambien a la primera alternativa y repitan el mismo flujo. Continúen con las otras dos, usando el mismo equipo, JVM, datos y orden de alternativas en su serie.
7. Comprueben cinco tiempos por estructura: veinte mediciones en el cuadro 5 × 4.
8. Repitan un botón completado y cambien de colección y regresen: el estado y el primer tiempo deben conservarse.
9. Exporten `tiempos_congreso.csv` y revisen las veinte filas con estado `MEDIDO`.

La tabla y el gráfico muestran milisegundos; el CSV conserva nanosegundos. Las tablas de personas e inscripciones muestran hasta 500 filas por comodidad visual. Todos los métodos trabajan sobre las colecciones completas.

Las duraciones pueden variar por la estructura y la ejecución de la JVM. Este ejercicio fija los mismos datos y trabajo para cada alternativa; una medición por método no demuestra que una estructura sea siempre la más rápida para cualquier aplicación.

## 15. Comprobaciones finales

- Tres colecciones del mismo tipo por cada estructura probada.
- Una persona y un país por identidad, aunque aparezcan en varias filas del CSV.
- Personas sin inscripción conservadas.
- Cambio de país aplicado a la persona, sin duplicarla ni perder inscripciones.
- Códigos de inscripción únicos y combinación persona/actividad sin duplicados nuevos.
- Cancelación limitada a la inscripción solicitada.
- Conteo por personas, independiente de cuántas inscripciones tengan.
- Mismos códigos, actividades, países y secuencia de escenarios para las cuatro estructuras.
- Tiempos reales registrados una sola vez por método/estructura.
- JAR reconstruido desde los fuentes actuales y abierto desde el terminal.
