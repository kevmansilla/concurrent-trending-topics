# Grupo 11		
## Corrección		
	Tag o commit corregido:	lab-3-2
		
### Entrega y git		96.00%
	Commits frecuentes y con nombres significativos	90.00%
	Tag y entrega a tiempo de la primera parte	100.00%
	Tag y entrega a tiempo de la segunda parte	100.00%
### Informe		79.80%
	Informe escrito en Markdown, correctamente formateado, aprovechando las facilidades del lenguaje (numeración, itemización, código, títulos).	100.00%
	Diagrama de jerarquía básico completo (jpeg, ascii, etc.),	100.00%
	Justificación de la jerarquía utilizada (redacción coherente).	100.00%
	Protocolo de mensajes detallado correctamente, y denotando qué mensajes ocurren entre qué actores.	50.00%
	Descripción de responsabilidades de los actores.	50.00%
	Justificación del patrón de interacción escogido.	90.00%
	Pregunta: ¿Por qué es necesario el uso de este patrón de interacción (request/response)?	100.00%
	Pregunta: ¿Qué parte de la arquitectura deberían modificar para soportar el conteo de entidades nombradas?	100.00%
	Pregunta: ¿Qué modificaciones a la arquitectura deberían hacer para agrupar datos por sitio?	0.00%
	Pregunta: ¿Qué problema trae implementar esto de manera síncrona?	100.00%
	Pregunta: ¿Qué les asegura el sistema de pasaje de mensajes y como se diferencia de un semáforo/mutex?	100.00%
### Funcionalidad		82.50%
	Se implementó correctamente `readSubscriptions` y se hace uso para devolver la lista de subscripciones.	100.00%
	El sistema carga todas las subscripciones mediante el pasaje de un mensaje con la lista de subscripciones que devuelve `readSubscriptions`	100.00%
	Los feeds requests se hacen de manera concurrente utilizando un actor por subscripción.	100.00%
	Se hace uso de `Future` para resolver el request http. El future se resuelve mediante `pipteToSelf`.	100.00%
	Se resuelve el request/response con un método que garantice respuesta (ask pattern o similar).	100.00%
	Se hace sincronización a nivel del supervisor a la hora de recibir los mensajes previo a comenzar el proceso de guardado.	100.00%
	El guardado a disco es 1 archivo por feed y se lo hace de manera concurrente.	25.00%
	Se utiliza la mónada `Try` para resolver cualquier problema que pueda tener el guardado a disco.	0.00%
	[Opcional] Hacen sincronización y matan a los actores una vez que se termine el proceso de guardado.	0.00%
### Modularización y diseño		98.50%
	Los actores se trabajan en archivos separados.	100.00%
	Los protocolos se escriben en el Object Companion de cada actor. Están trabajado utilizando `case class` o `case object` para facilitar el *pattern matching*.	100.00%
	El protocolo de mensajes se hace con herencia entre mensajes de la misma índole (i.e. mensajes del tipo `Request` heredan de un mismo `trait` y lo mismo con mensajes del tipo `Response`).	100.00%
	Hacen *pattern matching* para lidiar con pasaje de mensajes entre actores.	100.00%
	Respetan la jerarquía y el protocolo dado en el informe, no comunican cosas que no tiene sentido que se comuniquen.	100.00%
	Los `var` son usados sólo en el caso de que no hay otra opción (i.e. en caso de colecciones mutable, utilizan la estructura de datos adecuada pero sobre un `val`, no están constatemente sobreescribiendo).	90.00%
	Hacen uso de construcciones de lenguaje funcional (`map`, `filter` y eventualmente `fold`/`reduce` y `flatMap`). Evitan el uso de `flatten`.	100.00%
	No se utiliza la sentencia `try { ... } catch { ... }` y se resuelven las excepciones mediante el uso de la mónada `scala.util.Try` y *pattern matching*.	100.00%
	Cualquier librería "extra" fue correctamente agregada a `build.sbt`/ `project/build.properties` y los plugins están en `project/plugins.sbt`.	100.00%
### Calidad de código		100.00%
	Ninguna línea sobrepasa los 100 caracteres, y sólo unas pocas los 80.	100.00%
	El código es legible, está correctamente indentado (con espacios, no tabs), y con espacios uniformes (i.e. no hay más de dos saltos de línea en ningún lado).	100.00%
	Hacen uso comprensivo de comentarios y documentan partes que sean complejas.	100.00%
	No hay código spaghetti, no utilizan variables *flags* (i.e. variables que guarden valores temporales para ser utilizadas más adelante como un booleano).	100.00%
	Evitan el uso de `return` en las funciones.	100.00%
### Opcionales		
	Punto estrella: Subscripción a Reddit/Json	0.00%
	Punto estrella: Conteo de entidades nombradas	0.00%
	Punto estrella: Rest API	0.00%
		
# Nota Final		8.25
		
		
# Comentarios		
		
- Sean más específicos con las descripciones de los commits: `Parte 2`, `Parte 2 arreglada`, etc. no me dice que es lo que está pasando.		
- El detalle del protocolo de mensajes, así como la descripción de cada actor es bastante escueta y está mezclada, se esperaba más detalle y puesto de manera más clara, en una lista itemizada o similar.		
- El patrón es `request-response` no `request-responde` o similar.		
- No explicaron las modificaciones para agrupar datos por sitio.		
- No están creando un actor por cada storage, luego no se realiza de manera concurrente		
- Faltó usar la mónada `Try` para evaluar cualquier posible problema en el guardado a disco.		
- En general es mejor utilizar `ListBuffer` o alguna otra colección mutable en lugar de `var`.		
- Dejaron los archivos de los buffers en el repositorio, no debían agregarlos.		