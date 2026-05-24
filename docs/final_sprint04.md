# Sprint 04 – Execution & Review

## 1. Resultados obtenidos

El objetivo principal de este sprint era integrar la conexión a una API REST externa mediante Retrofit para buscar y gestionar reservas de hoteles, así como la persistencia local en Room para controlar estas reservas y gestionar las imágenes locales. Podemos confirmar que el objetivo se ha cumplido de manera muy satisfactoria, dotando a la aplicación de una conectividad y un dinamismo de datos que complementan a la perfección la persistencia lograda en el sprint anterior.

Hemos implementado un flujo completo de búsqueda de hoteles para Barcelona, Londres y París con calendarios obligatorios y validación de rango de fechas. Además de mostrar la información de las habitaciones y sus precios, el usuario puede ahora reservar directamente, guardando el registro localmente en Room y consumiendo la API de forma concurrente. La solución de guardado implementada es híbrida y robusta, permitiendo asociar la reserva a un viaje activo (con validaciones de fechas) o crear uno nuevo autocompletando sus datos a partir de la reserva.

Por otro lado, hemos desarrollado la pantalla de listado de reservas del usuario, en la cual se permite la cancelación en local y remoto de forma segura. En la misma UI, se añadió un carrete de fotos modal en formato carrusel interactivo para visualizar a gran tamaño la foto del hotel y de la habitación seleccionada. Además, para evitar que queden viajes huérfanos sin actividades tras cancelar una reserva, se ha implementado un sistema inteligente de limpieza automática que elimina el viaje de Room si este se queda completamente vacío.

El desarrollo ha respetado estrictamente el patrón Repository, la arquitectura estándar MVVM y la inyección de dependencias con Hilt. La versión final del sprint se ha publicado como Release v4.0.0, cumpliendo con toda la documentación y evidencias requeridas.

---

## 2. Tareas completadas

| ID | Completada | Comentarios |
|---|---|---|
| T1.1 | Sí | Cliente HTTP y Retrofit configurados e inyectados correctamente mediante Hilt. |
| T1.2 | Sí | Creación de modelos de datos e interfaces de la API de hoteles finalizados. |
| T1.3 | Sí | Capa repository de Hoteles configurada para abstraer el uso de la API externa. |
| T1.4 | Sí | Tests unitarios implementados mockeando las llamadas remotas. |
| T2.1 | Sí | Pantalla de búsqueda creada para BCN, Londres y París mediante Date Pickers obligatorios. |
| T2.2 | Sí | Despliegue de los hoteles y las habitaciones devueltos por la API. |
| T2.3 | Sí | Implementación de la reserva y guardado local en Room con opción híbrida de viaje. |
| T2.4 | Sí | Visualización de imágenes de hoteles y habitaciones en el flujo de búsqueda y detalle. |
| T3.1 | Sí | Funcionalidad de galería local y entidad TripImage creadas en Room. |
| T3.2 | Sí | Almacenamiento local de referencias a imágenes de galería en la base de datos finalizado. |
| T3.3 | Sí | Se removió la visualización de la galería en la UI de detalles finales por requerimiento del cliente. |
| T4.1 | Sí | Pantalla de listado de reservas locales creada vinculando cada reserva a su viaje. |
| T4.2 | Sí | Eliminación remota en API y local en Room de reservas implementada. |
| T4.3 | Sí | Integración de carrete de fotos modal interactivo en el listado de reservas. |
| T4.4 | Sí | Actualización de la pantalla "My Trips" indicando si tiene reserva y mostrando detalles. |
| T5.1 | Sí | Verificación de paquetes y dependencias en Hilt, Room y base de datos. |
| T5.2 | Sí | Grabación del vídeo demostrativo subido a la carpeta /docs. |

---

## 3. Desviaciones

* **Solución híbrida para la reserva (T2.3):** Aunque inicialmente se planificó que cada reserva de hotel creara un "nuevo viaje" de forma obligatoria, optamos por expandir esto a una solución híbrida. El usuario puede ahora elegir entre asociar la estancia a un viaje activo (validando que las fechas queden dentro del rango del viaje y mostrando un banner informativo en caso contrario) o crear un viaje nuevo automáticamente con los datos autocompletados.
* **Simplificación y eliminación de la galería de fotos en UI (T3.3):** A pesar de que la estructura de la base de datos (`TripImage` y DAOs) está totalmente implementada para la persistencia local de la galería de imágenes, el cliente solicitó remover la UI de carga y visualización de la galería en `DetalleViajeScreen2` para limpiar y estilizar la interfaz. Esto supuso descartar la implementación visual de esta característica.
* **Modelo de datos extendido y migración destructiva (T4.3):** Para mostrar de forma simultánea la imagen del hotel y la de la habitación en la pantalla de reservas, la API remota no proveía la imagen de la habitación en la consulta de reservas. Se tuvo que extender la entidad `Trip` agregando el campo `roomImageUrl` en Room y actualizar la base de datos a la versión 5 (`fallbackToDestructiveMigration`), lo que requirió borrar la caché durante las pruebas locales.
* **Limpieza reactiva de viajes vacíos:** Implementamos una lógica de negocio adicional no planificada en el backlog original: al cancelar una reserva, si el viaje asociado fue creado automáticamente y no contiene ninguna actividad manual en su itinerario, el viaje se elimina de manera automática para evitar la proliferación de viajes huérfanos sin contenido en la base de datos local.

---

## 4. Retrospectiva

### Qué funcionó bien
* **Consumo reactivo con Room y Retrofit:** La combinación de corrutinas y la exposición de los flujos de datos (`Flow`) desde Room permitieron que la interfaz responda al instante al realizar o cancelar reservas sin bloqueos de UI.
* **Normalización de URLs de imágenes:** Convertir a absolutas las rutas relativas de las imágenes obtenidas desde la API REST antes de almacenarlas en Room simplificó enormemente el renderizado con Coil en las distintas pantallas de la app.
* **Inyección modular con Hilt:** La configuración de módulos (`NetworkModule`, `DatabaseModule`, `RepositoryModule`) facilitó la inyección limpia del cliente HTTP, la base de datos y los repositorios correspondientes.

### Qué no funcionó
* **Esquema de datos cambiante a mitad de sprint:** No prever la necesidad de almacenar la imagen de la habitación para el carrete de fotos modal de la lista de reservas nos obligó a realizar una migración destructiva de la base de datos Room a la versión 5. Esto provocó pérdida de datos de prueba locales y ralentizó las pruebas integradas manuales.
* **Dependencia extrema de la API para las imágenes:** Si la API externa no está disponible, no solo fallan las reservas, sino que la visualización del carrete en las reservas previas se ve afectada al depender de URLs externas activas.

### Qué mejoraremos en el próximo sprint
* **Diseño previo de esquemas y migraciones:** Dedicaremos una fase inicial de diseño de la base de datos para planificar todas las columnas y relaciones de tablas necesarias, evitando migraciones destructivas a mitad del desarrollo.
* **Manejo avanzado de estados offline:** Implementaremos estrategias de acache local más robustas y placeholders visuales de error para que la UI no se rompa o se vea vacía en caso de fallos de red.

---

## 5. Autoevaluación del equipo (0-10)
**Nota:** 9.5

**Justificación:** El equipo ha superado con éxito las dificultades del consumo de APIs REST mediante Retrofit, ha mantenido una arquitectura limpia e inyectada con Hilt, y ha aportado soluciones de alto nivel a la experiencia de usuario final (validación inteligente de fechas de viajes, opción híbrida de reservas, carrete interactivo de fotografías y limpieza automática de viajes huérfanos). Se resta medio punto debido a la falta de previsión en el diseño de las entidades de base de datos que obligó a realizar la migración destructiva a la versión 5 durante el sprint.
