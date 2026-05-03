# Sprint 03 – Planning Document

## 1. Sprint Goal
El objetivo principal de este sprint es integrar la persistencia de datos mediante SQLite (utilizando la librería Room) para almacenar los detalles de viajes e itinerarios, reemplazando el almacenamiento en memoria del sprint anterior. Además, se implementará un sistema de autenticación de usuarios utilizando Firebase, gestionando el registro, inicio de sesión y recuperación de contraseñas. También se garantizará la persistencia local de la información del usuario y los registros de acceso a la aplicación. Todo el desarrollo debe aplicar el patrón Repository y utilizar HILT como librería de inyección de dependencias de forma estricta. La versión final del sprint será publicada como Release v3.x.x.

---

## 2. Sprint Backlog

| ID    | Tarea                                                                 | Responsable        | Estimación (h) | Prioridad |
|-------|------------------------------------------------------------------------|--------------------|----------------|-----------|
| T1.1  | Crear clase principal de Room Database                      | Oriol              | 0.5            | Alta      |
| T1.2  | Definir Entities (Trip e ItineraryItem con campos datetime, text e int) | Guillem            | 1            | Alta      |
| T1.3  | Crear Data Access Objects (DAOs) para la base de datos      | Guillem            | 1.5            | Alta      |
| T1.4  | Implementar operaciones CRUD usando DAOs para viajes e itinerarios | Oriol              | 2            | Alta      |
| T1.5  | Modificar ViewModels para usar Room en lugar de In-Memory   | Oriol y Guillem    | 2.5            | Alta      |
| T2.1  | Conectar la aplicación a Firebase                           | Oriol              | 1.0            | Alta      |
| T2.2  | Diseñar la pantalla de login en Android                     | Guillem            | 2            | Media     |
| T2.3  | Implementar acciones de login con Firebase (Auth & Password)| Oriol              | 1.5            | Alta      |
| T2.4  | Crear la acción para permitir al usuario cerrar sesión (logout)| Guillem         | 0.5            | Media     |
| T3.1  | Diseñar la pantalla de registro (Register form)             | Guillem            | 1.5            | Media     |
| T3.2  | Implementar registro con Firebase, incluyendo verificación de email | Oriol | 2.5            | Alta      |
| T3.3  | Implementar pantalla y acción para recuperar contraseña     | Guillem            | 1.5            | Media     |
| T4.1  | Crear tabla para persistir datos locales del usuario (mínimo: login, username, fecha nac, etc.) | Oriol | 2.0 | Alta |
| T4.2  | Modificar tabla Trip para multiusuario y mostrar solo viajes del usuario logueado | Guillem | 2.5 | Alta |
| T4.3  | Crear tabla para registrar de forma persistente cada inicio y cierre de sesión (acceso app) | Oriol | 2 | Media |
| T5.1  | Escribir tests unitarios para DAOs e interacciones de la BD | Guillem            | 2.5            | Alta      |
| T5.2  | Validar datos (nombres de viaje duplicados, comprobación de fechas) | Oriol              | 1.5            | Media     |
| T5.3  | Actualizar documentación (`design.md`) con el esquema de base de datos | Guillem            | 1.0            | Baja      |
| T5.4  | Grabar vídeo demostrativo y guardarlo en `/docs/evidence/v3.x.x` | Oriol y Guillem | 0.5        | Baja      |

---

## 3. Definition of Done (DoD)
- [ ] Base de datos SQLite (Room) integrada y sustituyendo completamente el almacenamiento en memoria.
- [ ] Operaciones CRUD de viajes e itinerarios funcionales y probadas mediante DAOs.
- [ ] UI se actualiza correctamente cuando hay cambios en la base de datos.
- [ ] Autenticación con Firebase configurada y operativa (Login, Logout, Registro, Recuperación de contraseña y verificación de email).
- [ ] Persistencia local de datos de usuario e historial de accesos registrados correctamente.
- [ ] Base de datos de viajes adaptada para soportar múltiples usuarios y mostrar solo el contenido propio.
- [ ] Tests unitarios implementados para DAOs y validación de datos aplicada.
- [ ] Documentación `design.md` actualizada con el esquema de base de datos y estrategia de migración.
- [ ] Vídeo demostrativo grabado mostrando todas las tareas implementadas y guardado en la ruta correcta.
- [ ] Repositorio en GitHub enviado con el documento `final_sprint03.md` y release etiquetada como `v3.x.x`.

---

## 4. Riesgos identificados
- Bloqueos del hilo principal si las operaciones con la base de datos Room no se gestionan en hilos secundarios o corrutinas.
- Curva de aprendizaje al configurar e implementar correctamente la librería de inyección de dependencias HILT.
- Olvidos en la gestión de errores mediante Logcat durante las consultas complejas a la base de datos.
- Errores en la validación asíncrona de usuarios o en el flujo de verificación de correo electrónico mediante Firebase.
- Posible pérdida de datos o inconsistencias al adaptar la tabla de viajes de una estructura simple a una que soporte usuarios múltiples.
- Posibles olvidos la documentación de la carpeta docs y posteriormente hacer la entrega con la documentación equivoca como en anteriores sprints.

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite de entrega y modificación: 12/04/2026
