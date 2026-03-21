# Sprint 02 – Planning Document

## 1. Sprint Goal
El objetivo de este sprint es centrarse en la implementación de la lógica principal de la aplicación Travel Planner. Esto incluye gestionar los itinerarios de viaje, manejar las operaciones CRUD para los elementos del viaje y asegurar las interacciones entre las diferentes partes del sistema. La meta es construir funcionalidades que permitan a los usuarios planificar y modificar sus viajes. En este sprint, la aplicación almacenará los datos de forma InMemory (en memoria temporal) para centrar el esfuerzo en la arquitectura y la lógica de negocio sin la complejidad de la base de datos. La versión final será publicada en el repositorio asegurando que la release sea la v2.X.X.

---

## 2. Sprint Backlog

| ID    | Tarea                                                                                   | Responsable     | Estimación (h) | Prioridad |
|-------|-----------------------------------------------------------------------------------------|-----------------|----------------|-----------|
| T1.1  | Implementar operaciones CRUD en memoria para viajes (addTrip, editTrip, deleteTrip)     | Oriol           | 5              | Alta      |
| T1.2  | Implementar operaciones CRUD en memoria para actividades (addActivity, updateActivity)  | Guillem         | 5              | Alta      |
| T1.3  | Implementar DatePickers y bloquear texto libre en todos los campos de fecha             | Oriol           | 2            | Alta      |
| T1.4  | Validar fechas (inicio antes de fin y actividades dentro del rango del viaje)           | Guillem         | 3              | Alta      |
| T2.1  | Persistir ajustes de usuario con SharedPreferences (username, DOB, dark mode, idioma)   | Guillem         | 2              | Media     |
| T2.2  | Implementar soporte multi-idioma (mínimo en, ca, es)                                    | Oriol           | 2              | Media     |
| T2.3  | Desarrollar flujo UI básico para añadir/modificar detalles de viajes e itinerarios      | Oriol y Guillem | 7              | Alta      |
| T2.4  | Asegurar que la UI se actualice dinámicamente al modificar las listas                   | Guillem         | 4              | Alta      |
| T3.1  | Mostrar mensajes de error en UI para campos vacíos o fechas incorrectas                 | Oriol           | 2              | Alta      |
| T3.2  | Escribir tests unitarios para las operaciones CRUD de viajes e itinerarios              | Guillem         | 2.5            | Media     |
| T3.3  | Añadir mensajes de log (Logcat) aplicando buenas prácticas                              | Oriol           | 1              | Baja      |
| T4.1  | Grabar vídeo demostrativo de las tareas implementadas                                   | Guillem         | 1              | Alta      |
| T4.2  | Guardar el vídeo en el directorio /doc/evidence/v2.X.X                                  | Guillem         | 0            | Alta      |


---

## 3. Definition of Done (DoD)
- [ ] Operaciones CRUD de viajes implementadas en memoria siguiendo el patrón MVVM.
- [ ] Operaciones CRUD de itinerario/actividades implementadas en memoria siguiendo el patrón MVVM.
- [ ] Fechas seleccionadas exclusivamente mediante componentes DatePicker.
- [ ] Bloqueo de inserción de texto libre en campos de fecha implementado.
- [ ] Validación aplicada: fecha de inicio anterior a fin de viaje y actividades dentro del rango.
- [ ] Interfaz gráfica muestra errores claros cuando los campos obligatorios están vacíos o las fechas son incorrectas.
- [ ] Ajustes de usuario (username, fecha nacimiento, dark mode, idioma) persistidos en SharedPreferences.
- [ ] La aplicación carga automáticamente las preferencias guardadas al iniciar.
- [ ] Soporte para un mínimo de 3 idiomas implementado (en, ca, es).
- [ ] Actualizaciones reflejadas dinámicamente en la lista principal y lista de itinerarios.
- [ ] Tests unitarios para las operaciones CRUD escritos.
- [ ] Logs añadidos para ser visualizados en Logcat.
- [ ] Vídeo demostrativo grabado y guardado en /doc/evidence/v2.X.X.
- [ ] Última versión del código entregada con la release/tag v2.X.X en GitHub.

---

## 4. Riesgos identificados

- Pérdida de estado de la interfaz (ViewModel State) al rotar la pantalla o navegar entre pantallas.
- Falta de feedback al usuario si los errores de validación ocurren internamente pero fallan silenciosamente en la interfaz.
- Uso incorrecto de la arquitectura, como implementar lógica de negocio directamente en la capa UI.
- Navegación confusa que dificulte entender dónde se encuentra el usuario o cómo regresar a la pantalla anterior.

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite modificación: 15/03/2025