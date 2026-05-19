# Sprint 04 – Planning Document

## 1. Sprint Goal
El objetivo principal de este sprint es integrar la librería Retrofit para conectar la aplicación con una API REST externa que permita buscar y gestionar reservas de hoteles. Además, se implementará un sistema de gestión de imágenes para crear galerías locales asociadas a cada viaje, manteniendo la persistencia mediante SQLite (utilizando la librería Room). Todo el desarrollo debe aplicar el patrón Repository, seguir la arquitectura estándar MVVM y utilizar HILT como librería de inyección de dependencias de forma estricta, asegurando la correcta estructura de carpetas (`view`, `viewmodel`, `repo`, `di`, `data`). La versión final del sprint será publicada como Release v4.x.x.

## 2. Sprint Backlog

| ID    | Tarea                                                                 | Responsable        | Estimación (h) | Prioridad |
|-------|------------------------------------------------------------------------|--------------------|----------------|-----------|
| T1.1  | Añadir dependencia de Retrofit y configurar el cliente HTTP             | Oriol              | 0.5            | Alta      |
| T1.2  | Crear modelos de datos e interfaces de la API (MVVM)                    | Guillem            | 0.5            | Alta      |
| T1.3  | Crear capa repository para abstraer el uso de la API                    | Oriol              | 1.0            | Alta      |
| T1.4  | Crear tests unitarios mockeando la conexión remota                      | Guillem            | 1.0            | Alta      |
| T2.1  | Diseñar pantalla de búsqueda de hoteles (Londres, París, BCN) con date pickers | Oriol              | 1.0            | Alta      |
| T2.2  | Mostrar datos de hoteles y habitaciones devueltos por la API            | Guillem            | 1.0            | Alta      |
| T2.3  | Implementar reserva de habitación y guardado local en Room (nuevo viaje)| Oriol              | 1.5            | Alta      |
| T2.4  | Mostrar imágenes del hotel y habitaciones en la pantalla de reserva     | Guillem            | 0.5            | Media     |
| T3.1  | Implementar funcionalidad para adjuntar múltiples imágenes a un viaje   | Guillem            | 1.0            | Alta      |
| T3.2  | Guardar imágenes de la galería localmente en la base de datos o storage | Oriol              | 1.0            | Alta      |
| T3.3  | Mostrar galerías específicas en la pantalla de detalles de cada viaje   | Guillem            | 0.5            | Media     |
| T4.1  | Crear pantalla para listar todas las reservas locales y sus viajes      | Oriol              | 1.0            | Alta      |
| T4.2  | Implementar eliminación de reservas localmente y a través de la API     | Guillem            | 1.0            | Alta      |
| T4.3  | Mostrar imágenes asociadas al hotel/habitación en la lista de reservas  | Oriol              | 0.5            | Media     |
| T4.4  | Actualizar pantalla "My Trips" para mostrar detalles de reservas        | Guillem            | 1.0            | Alta      |
| T5.1  | Verificar estructura de carpetas y dependencias (HILT, ROOM, DB)        | Oriol y Guillem    | 0.5            | Alta      |
| T5.2  | Grabar vídeo demostrativo y guardarlo en /docs/evidence/v4.x.x          | Oriol y Guillem    | 0.5            | Baja      |

## 3. Definition of Done (DoD)
- [ ] Retrofit configurado y consumiendo correctamente los endpoints de la API de hoteles.
- [ ] Pantalla de búsqueda implementada utilizando selectores de fecha (date pickers) obligatorios.
- [ ] Reservas de habitaciones funcionales, persistiendo la información localmente mediante Room como un nuevo viaje.
- [ ] Galería de imágenes operativa: permite adjuntar, guardar localmente y visualizar fotos por viaje en su vista de detalles.
- [ ] Listado de reservas visible y con funcionalidad de cancelación operativa tanto en local como en la API.
- [ ] Pantalla "My Trips" actualizada indicando si el viaje tiene reserva y mostrando sus detalles correspondientes.
- [ ] Tests unitarios implementados mockeando las llamadas remotas.
- [ ] Arquitectura MVVM estricta respetando la estructura de carpetas (`view`, `viewmodel`, `repo`, `di`, `data`).
- [ ] Inyección de dependencias implementada estrictamente con HILT.
- [ ] Vídeo demostrativo grabado mostrando todas las tareas implementadas y guardado en la ruta correcta.
- [ ] Repositorio en GitHub enviado con el documento Sprint.md reflejando las asignaciones y la release etiquetada como v4.x.x.

## 4. Riesgos identificados
• Posibles errores de red, latencia o indisponibilidad temporal de la API externa que bloqueen el desarrollo o las pruebas de integración.
• Inconsistencias de estado si no se maneja correctamente la sincronización al cancelar una reserva (desfase entre el borrado en Room y la respuesta de la API).
• Desbordamiento de memoria o bloqueos en la interfaz (UI Thread) al procesar, cargar y almacenar múltiples imágenes en las galerías locales.
• Posibles descuidos en la implementación estricta de la estructura de paquetes requerida o en la configuración pura de HILT, lo cual podría desorganizar la inyección de dependencias.
• Errores en la validación de búsquedas si no se configuran e implementan adecuadamente los date pickers, causando envío de formatos de fecha incorrectos a la API.

⚠ Este documento no puede modificarse después del 30% del sprint. Fecha límite de entrega y modificación: 10/05/2026