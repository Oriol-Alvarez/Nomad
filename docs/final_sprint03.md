# Sprint 03 – Execution & Review

## 1. Resultados obtenidos

El objetivo principal de este sprint era dar el salto de una arquitectura de datos volátil (en memoria) a un sistema de persistencia real y robusto, integrando SQLite (mediante la librería Room) y añadiendo un sistema de autenticación completo con Firebase. Podemos confirmar que el objetivo se ha cumplido satisfactoriamente y la arquitectura de la aplicación ha dado un salto de calidad importante.

Se ha logrado implementar el patrón Repository y la inyección de dependencias con HILT, lo cual ha requerido un esfuerzo inicial de configuración pero ha dejado el código mucho más modular y preparado para el futuro. La transición de los ViewModels para consumir datos de la base de datos en lugar de la memoria se ha realizado gestionando correctamente las corrutinas, evitando así los bloqueos del hilo principal (uno de los riesgos previstos).

La integración con Firebase para el registro, inicio de sesión y recuperación de contraseñas es totalmente funcional. Además, se ha adaptado la lógica de negocio para que la aplicación sea multiusuario, filtrando los viajes según el usuario logueado y manteniendo un registro de auditoría local de los accesos.

El sprint concluye con la documentación correctamente actualizada (evitando el error de sprints pasados), la grabación del vídeo demostrativo y la publicación de la Release v3.0.0.

---

## 2. Tareas completadas

| ID   | Completada | Comentarios |
|------|------------|-------------|
| T1.1 | Sí         | Configuración base de Room completada. Se inyecta correctamente mediante HILT. |
| T1.2 | Sí         | Entidades creadas con conversores de tipo (TypeConverters) para las fechas. |
| T1.3 | Sí         | Consultas SQL verificadas. Uso de flujos (Flow) para observar cambios en tiempo real. |
| T1.4 | Sí         | Operaciones CRUD implementadas delegando el trabajo a hilos secundarios (Dispatchers.IO). |
| T1.5 | Sí         | ViewModels refactorizados usando el patrón Repository para abstraer el origen de datos. |
| T2.1 | Sí         | Proyecto de Firebase configurado y archivo `google-services.json` integrado sin problemas. |
| T2.2 | Sí         | Pantalla de login diseñada manteniendo la coherencia visual con Material Design. |
| T2.3 | Sí         | Autenticación implementada manejando correctamente los estados asíncronos. |
| T2.4 | Sí         | Acción de logout borra la sesión de Firebase y limpia el estado local del usuario. |
| T3.1 | Sí         | Formulario de registro con validaciones de contraseña segura y campos obligatorios. |
| T3.2 | Sí         | Registro funcional. El envío del correo de verificación se realiza correctamente. |
| T3.3 | Sí         | Flujo de recuperación de contraseña operativo mediante Firebase Auth. |
| T4.1 | Sí         | Entidad `User` creada en Room para cachear los datos del usuario logueado. |
| T4.2 | Sí         | Se añadió el `userId` a la tabla Trip. Los DAOs ahora filtran siempre por este campo. |
| T4.3 | Sí         | Tabla `AccessLog` implementada. Registra el timestamp de cada login/logout. |
| T5.1 | Sí         | Tests de Room implementados en paralelo al desarrollo, tal como nos propusimos en el sprint 2. |
| T5.2 | Sí         | Validaciones aplicadas tanto a nivel de UI como de restricciones en la base de datos (Unique). |
| T5.3 | Sí         | Esquema de base de datos añadido a `design.md`. Entregado en la carpeta correcta. |
| T5.4 | Sí         | Vídeo demostrativo grabado mostrando el flujo multiusuario y persistencia, subido a `/docs/evidence/v3.x.x`. |

---

## 3. Desviaciones

* **Curva de aprendizaje de HILT (T1.1 - T1.5):** Como preveíamos en el análisis de riesgos, la configuración inicial de HILT requirió más tiempo del estimado. Entender cómo proveer las instancias de la base de datos de Room y los repositorios a los ViewModels nos obligó a reescribir algunos módulos, consumiendo algo de margen de tiempo.
* **Gestión asíncrona de Firebase (T3.2):** La verificación del correo electrónico y el feedback visual al usuario tuvieron pequeñas complicaciones. A veces la UI no reflejaba inmediatamente que el correo se había enviado, por lo que tuvimos que refinar los estados de carga (Loading states) en la interfaz.
* **Refactorización Multiusuario (T4.2):** Modificar la tabla de viajes para depender de un usuario implicó tener que borrar y recrear la base de datos local durante las pruebas varias veces (FallbackToDestructiveMigration), lo que ralentizó un poco el testeo manual.
* **Cuidado especial en la entrega documental (T5.3):** Dada la advertencia en los riesgos sobre las entregas en la carpeta de documentación equivocada, el equipo hizo una revisión cruzada antes del push final, asegurando que `design.md` estaba perfecto.

---

## 4. Retrospectiva

### Qué funcionó bien
* **Testing en paralelo:** Aplicar la mejora propuesta en el Sprint 02 (escribir los tests unitarios de los DAOs mientras se desarrollaban y no al final) evitó cuellos de botella en los últimos días del sprint.
* **Gestión de hilos (Corrutinas):** El equipo estuvo muy atento al riesgo de bloquear el hilo principal. El uso de Kotlin Coroutines con Room y Firebase ha resultado en una aplicación muy fluida.
* **Patrón Repository:** La separación de responsabilidades ahora es excelente. La UI no sabe si los datos vienen de Room o de Firebase, lo que facilitará futuras integraciones (ej. una API REST).

### Qué no funcionó
* **Manejo de errores de red/Firebase:** Si bien la base de datos local (Room) es robusta, cuando Firebase falla (por ejemplo, sin conexión a internet), los mensajes de error en la UI a veces resultan ser muy genéricos o difíciles de interpretar para el usuario final, quedándose a veces registrados solo en el Logcat.
* **Sobrecarga de trabajo de inyección:** La configuración de los módulos de HILT centralizó mucho trabajo en una sola parte del código, generando algunos conflictos de merge menores durante el desarrollo paralelo.

### Qué mejoraremos en el próximo sprint
* **Feedback de red y Loading States globales:** Necesitamos implementar un sistema más robusto para mostrar spinners de carga y capturar excepciones específicas de Firebase (como "Usuario no encontrado" o "Contraseña débil") para traducirlas a mensajes amigables en el idioma del usuario.
* **Estandarizar las inyecciones:** Crear documentación interna rápida sobre cómo añadir nuevos módulos a HILT para que cualquier miembro del equipo pueda hacerlo sin fricciones.

---

## 5. Autoevaluación del equipo (0-10)
**Nota:** 9.0

**Justificación:** Ha sido un sprint técnicamente complejo. Pasar de datos en memoria a Room, añadir Firebase, e integrar HILT suponía un cambio profundo en el core de la aplicación. El equipo ha sabido gestionar la complejidad, ha solucionado de forma proactiva el riesgo de los bloqueos de interfaz mediante corrutinas y ha mejorado sus procesos internos cumpliendo la promesa de testear el código más tempranamente.