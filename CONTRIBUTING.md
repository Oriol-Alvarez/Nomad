# 🤝 Guía de Contribución — Nomad

Gracias por tu interés en contribuir a **Nomad**.  
Este documento describe el flujo de trabajo oficial para mantener el proyecto estable, organizado y fácil de escalar.

---

## ✅ Introducción

Nomad es una aplicación Android desarrollada con **Kotlin + Jetpack Compose** cuyo objetivo es facilitar la planificación de viajes de forma intuitiva.

Las contribuciones son bienvenidas siempre que sigan las normas descritas en este documento. Estas reglas garantizan:

- Estabilidad en producción
- Historial de cambios limpio
- Integración segura entre colaboradores
- Código mantenible

Antes de contribuir, por favor lee completamente esta guía.

---

## 🌳 Flujo de Trabajo y Estrategia de Ramas

El repositorio utiliza un flujo sencillo adaptado a un equipo de dos desarrolladores.

### 🔒 Ramas Principales

| Rama | Propósito |
|---|---|
| `main` | Versión estable y libre de errores. Solo recibe cambios desde `test`. |
| `test` | Rama de integración y validación. |
| `guillem` | Desarrollo individual de Guillem. |
| `oriol` | Desarrollo individual de oriol. |

---

### 📌 Propósito Detallado de Cada Rama

**`main` — Producción 🔐**

Contiene únicamente código estable y debe compilar siempre sin errores. Están **prohibidos los commits directos** a esta rama. Solo recibe cambios mediante merge desde `test`, una vez que el código ha sido validado.

**`test` — Integración 🧪**

Rama intermedia donde se combinan los avances de `guillem` y `oriol`. Aquí se realizan las pruebas de integración antes de fusionar con `main`. Es obligatorio que el proyecto compile correctamente en esta rama antes de cualquier merge a producción.

**`guillem` / `oriol` — Desarrollo Individual 👨‍💻**

Ramas de trabajo personal para implementar nuevas features y corregir bugs. Se mergean a `test` una vez que el trabajo está listo y probado localmente.

---

## 🔀 Cómo Contribuir — Pull Request Workflow

### 1️⃣ Preparar el Entorno

Clona el repositorio y sitúate en tu rama personal:

```bash
git clone https://github.com/Oriol-Alvarez/Nomad.git
cd Nomad

# Sitúate en tu rama personal
git checkout guillem   # o oriol
git pull               # Asegúrate de tener los últimos cambios
```

---

### 2️⃣ Desarrollar en tu Rama Personal

Trabaja siempre desde tu rama personal. Haz commits frecuentes y descriptivos siguiendo las [convenciones de commits](#-convenciones-de-commits).

```bash
# Ver el estado de tus cambios
git status

# Añadir cambios al staging
git add .

# Hacer commit con mensaje descriptivo
git commit -m "feat(itinerary): add drag-and-drop for travel stops"

# Subir cambios a tu rama remota
git push origin guillem   # o oriol
```

---

### 3️⃣ Sincronizar con `test` antes de Mergear

Antes de abrir un Pull Request a `test`, sincroniza tu rama con los últimos cambios para evitar conflictos:

```bash
# Asegúrate de estar en tu rama personal
git checkout guillem   # o oriol

# Descarga los cambios remotos de test sin fusionarlos
git fetch origin

# Fusiona los cambios de test en tu rama local
git merge origin/test

# Resuelve conflictos si los hay, luego sube los cambios
git push origin guillem   # o oriol
```

---

### 4️⃣ Abrir un Pull Request a `test`

Una vez que tu funcionalidad está terminada y probada localmente:

1. Ve al repositorio en GitHub.
2. Abre un **Pull Request** desde tu rama (`guillem` o `oriol`) hacia `test`.
3. Rellena la descripción del PR con:
    - **¿Qué cambia?** Descripción breve de los cambios realizados.
    - **¿Por qué?** Motivación o issue relacionado (ej. `Closes #12`).
    - **¿Cómo probarlo?** Pasos para verificar que funciona correctamente.
4. Asigna al otro miembro del equipo como **Reviewer**.
5. Espera la revisión antes de mergear.

> ⚠️ **No hagas merge sin revisión previa**, aunque sea un cambio pequeño.

---

### 5️⃣ Merge de `test` a `main`

Solo se puede mergear `test` a `main` cuando:

- ✅ El proyecto compila sin errores.
- ✅ Las funcionalidades han sido probadas en `test`.
- ✅ Ambos desarrolladores están de acuerdo.
- ✅ No hay conflictos pendientes.

```bash
git checkout main
git merge test
git push origin main
```

---

## 🛠️ Configuración del Entorno de Desarrollo

### Requisitos Previos

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 17** o superior
- **SDK de Android** con API 26 (Android Oreo) o superior instalada
- **Git** instalado en tu sistema

### Primeros Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/Oriol-Alvarez/Nomad.git
cd Nomad

# 2. Abre el proyecto en Android Studio
# File > Open > selecciona la carpeta del proyecto

# 3. Gradle sincronizará las dependencias automáticamente
# Si no, ve a: File > Sync Project with Gradle Files
```

### Ejecutar la App

Conecta un dispositivo físico (API 26+) o inicia un emulador y pulsa **Run ▶️**, o usa:

```bash
./gradlew assembleDebug
```

### Ejecutar Tests

```bash
# Tests unitarios
./gradlew test

# Tests instrumentados (requiere dispositivo o emulador)
./gradlew connectedAndroidTest
```

---

## 📐 Estándares de Código

### Kotlin

- Sigue las [convenciones oficiales de Kotlin](https://kotlinlang.org/docs/coding-conventions.html).
- Usa nombres descriptivos para variables, funciones y clases.
- Prefiere la inmutabilidad (`val` sobre `var`) siempre que sea posible.
- Evita el uso de `!!`. Usa manejo seguro de nulos con `?.` o `?:`.
- Documenta con KDoc las funciones públicas relevantes.

### Jetpack Compose

- Nombra los Composables en **PascalCase** (ej. `TravelCard`, `ItineraryScreen`).
- Cada Composable debe tener una única responsabilidad.
- Extrae la lógica hacia **ViewModels**, nunca dentro de los Composables.
- Añade `@Preview` a los Composables de UI siempre que sea posible.
- Usa `remember` y `derivedStateOf` para optimizar recomposiciones.

### Arquitectura

El proyecto sigue el patrón **MVVM**. Respeta siempre esta separación:

```
UI (Composables) → ViewModel → Repository → Fuente de datos
```

### Formateo

```bash
./gradlew ktlintCheck    # Verifica el estilo del código
./gradlew ktlintFormat   # Formatea automáticamente
```

---

## 📝 Convenciones de Commits

Seguimos el estándar **Conventional Commits** para mantener un historial claro y legible.

### Formato

```
tipo(ámbito): descripción breve en presente
```

### Tipos Permitidos

| Tipo | Descripción |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de un bug |
| `docs` | Cambios en documentación |
| `style` | Cambios de formato (sin afectar lógica) |
| `refactor` | Refactorización de código |
| `test` | Añadir o modificar tests |
| `chore` | Mantenimiento (dependencias, config) |
| `perf` | Mejoras de rendimiento |

### Ejemplos

```bash
feat(map): add nearby places search on map screen
fix(auth): resolve crash on login with empty email field
docs(contributing): add branch sync instructions
refactor(itinerary): extract ItineraryCard into reusable component
```

---

## ⚠️ Buenas Prácticas y Errores Comunes

- **No hagas commits directos a `main`** bajo ninguna circunstancia.
- **Sincroniza tu rama con `test` antes de abrir un PR** para evitar conflictos.
- **No subas claves API ni credenciales** al repositorio. Usa `local.properties` o variables de entorno.
- **Haz commits pequeños y frecuentes** en lugar de un solo commit grande al final.
- **Prueba siempre en un dispositivo real o emulador** antes de abrir un PR.

---

## 👥 Equipo

| Nombre | Rama      | Rol |
|---|-----------|---|
| **Oriol Alvarez Arisa** | `oriol`   | Co-fundador & Desarrollador Android |
| **Guillem Talayero Carrasco** | `guillem` | Co-fundador & Desarrollador Android |

---