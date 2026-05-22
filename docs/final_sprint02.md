# Sprint 02 – Execution & Review

## 1. Resultados obtenidos

El objetivo principal de este sprint era implementar la lógica core de la aplicación, incluyendo la gestión in-memory de viajes y actividades mediante operaciones CRUD, así como la persistencia de las preferencias del usuario. Este objetivo se ha cumplido con éxito.

A nivel general, el equipo ha logrado completar todas las tareas del Sprint Backlog. La transición hacia el patrón MVVM ha permitido separar adecuadamente la lógica de la capa visual, mitigando uno de los riesgos identificados. La implementación de los DatePickers, la validación de fechas cruzadas y el soporte multi-idioma (inglés, catalán y español) ya son completamente funcionales.

La principal dificultad de este sprint estuvo relacionada con uno de los riesgos previstos: la pérdida de estado de la interfaz al interactuar con la navegación o al reconfigurar la pantalla. Nos obligó a invertir un poco más de tiempo en asegurar el uso correcto de `rememberSaveable` y la retención de estados en el ViewModel para que la UI se mantuviera reactiva y estable. Por otro lado, la persistencia de datos mediante SharedPreferences resultó ser un proceso muy ágil y directo. 

El sprint concluye con la grabación del vídeo demostrativo guardado en la ruta correcta y la publicación de la Release v2.0.0. El balance es muy positivo, demostrando un mejor control sobre los tiempos de UI tras el aprendizaje del sprint anterior.

---

## 2. Tareas completadas

| ID    | Completada | Comentarios |
|-------|------------|-------------|
| T1.1  | Sí         | Implementado correctamente en memoria usando el ViewModel. |
| T1.2  | Sí         | Operaciones CRUD de actividades funcionales y vinculadas a la lista. |
| T1.3  | Sí         | DatePickers modulares creados. Texto libre bloqueado con éxito. |
| T1.4  | Sí         | Lógica implementada. Requirió algo de ajuste para mostrar errores en tiempo real. |
| T2.1  | Sí         | SharedPreferences configurado sin problemas. Los ajustes cargan al inicio. |
| T2.2  | Sí         | Archivos `strings.xml` configurados para `en`, `ca` y `es`. |
| T2.3  | Sí         | Las horas estimadas fueron mucho más realistas esta vez. |
| T2.4  | Sí         | Las listas reaccionan correctamente a los cambios de estado. |
| T3.1  | Sí         | Mensajes de error mapeados y visibles bajo los campos correspondientes en rojo. |
| T3.2  | Sí         | Pruebas básicas superadas. |
| T3.3  | Sí         | Logs implementados para seguimiento interno. |
| T4.1  | Sí         | Vídeo demostrativo grabado mostrando todas las validaciones y cambios de idioma. |
| T4.2  | Sí         | Vídeo subido correctamente a `/doc/evidence/v2.X.X`. |

---

## 3. Desviaciones

* **Gestión de Estados en la UI (T2.4):** Tal y como se predijo en los riesgos, lidiar con la recomposición de las pantallas al validar fechas o cambiar de idioma nos llevó a tener que refactorizar ligeramente la forma en la que guardábamos los datos temporales del formulario (usando `rememberSaveable`). Consumió algo más del tiempo estimado, pero se solucionó de raíz.
* **Validación de fechas (T1.4):** Asegurar que las validaciones no fallaran silenciosamente y dieran buen feedback visual tomó un poco más de tiempo de las 3 horas planificadas.
* **Tiempos de UI más ajustados (T2.3):** A diferencia del Sprint 01, esta vez la estimación para desarrollar los flujos de la interfaz fue mucho más precisa (7 horas), lo que demuestra una mejora en nuestra capacidad de planificación.

---

## 4. Retrospectiva

### Qué funcionó bien
* **Mejora en las estimaciones:** El reajuste de horas pactado en la retrospectiva anterior dio sus frutos. Dedicar más tiempo planificado a las tareas de interfaz nos permitió trabajar sin la presión del primer sprint.
* **Arquitectura limpia:** Respetar el patrón MVVM desde el inicio ha hecho que el código sea mucho más legible y que implementar el CRUD en memoria fuera un proceso metódico y ordenado, sin mezclar lógica en las vistas.
* **Implementación de SharedPreferences:** Tarea ágil que funcionó a la primera, mejorando mucho la experiencia de usuario (UX) al recordar el modo oscuro y el idioma.

### Qué no funcionó
* **Manejo de errores iniciales silenciosos:** En las primeras iteraciones de los formularios, las validaciones (como fechas incoherentes) bloqueaban el guardado pero no avisaban al usuario, cumpliéndose uno de los riesgos previstos.
* **Testing al final del ciclo:** Dejar la escritura de los tests unitarios (T3.2) para el tramo final del sprint generó un pequeño cuello de botella. 

### Qué mejoraremos en el próximo sprint
* **Test-Driven / Pruebas tempranas:** Intentaremos implementar los tests unitarios al mismo tiempo que desarrollamos la lógica (CRUD), en lugar de dejarlos como una tarea de cierre.
* **Refinar la modularización visual:** Seguir extrayendo componentes repetitivos (como diálogos o campos de texto genéricos) a funciones propias para mantener el código de las pantallas principales aún más limpio.

---

## 5. Autoevaluación del equipo (0-10)
**Nota:** 8.5 

**Justificación:** El equipo ha trabajado de forma constante y coordinada, corrigiendo el principal defecto del primer sprint: la mala medición de tiempos de desarrollo visual. Hemos superado el reto de mantener los estados de la aplicación vivos durante la navegación y hemos dejado una lógica sólida para cuando toque integrar una base de datos real en el futuro. Se resta un poco de nota porque dejamos la redacción de los tests unitarios para el final del ciclo, lo que generó un pequeño cuello de botella organizativo en los últimos días, aunque la capacidad del equipo para adaptarse, sacar el trabajo adelante y entregar la release a tiempo fue excelente.
