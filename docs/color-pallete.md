# 🎨 Guía Completa de Estilo y Color - Nomad

Esta guía detalla la paleta de colores de Nomad, su implementación técnica a través de Material 3 y cómo se aplican específicamente en los componentes de la interfaz de usuario.

---

## 🟦 Colores de Marca (Brand Colors)
Definen la identidad visual de la aplicación.

| Nombre | Color | Valor HEX | Uso Principal |
| :--- | :--- | :--- | :--- |
| **Nomad Blue** | ![#2196F3](https://img.shields.io/static/v1?label=&message=%20&color=2196F3) | `#2196F3` | Color primario en Modo Claro, botones principales. |
| **Nomad Blue Dark** | ![#0D47A1](https://img.shields.io/static/v1?label=&message=%20&color=0D47A1) | `#0D47A1` | Contenedores secundarios y énfasis en Modo Claro. |
| **Sky Blue Accent** | ![#4FC3F7](https://img.shields.io/static/v1?label=&message=%20&color=4FC3F7) | `#4FC3F7` | Color primario en Modo Oscuro (mejor contraste sobre negro). |

---

## 🌙 Modo Oscuro (Dark Theme)
Implementado para una experiencia inmersiva y ahorro de energía.

| Token M3 | Valor / Color | Aplicación en la UI |
| :--- | :--- | :--- |
| **primary** | ![#4FC3F7](https://img.shields.io/static/v1?label=&message=%20&color=4FC3F7) | Textos destacados, iconos de acción y enlaces. |
| **background** | ![#121212](https://img.shields.io/static/v1?label=&message=%20&color=121212) | Fondo de pantallas y del diálogo de términos. |
| **surface** | ![#1E1E1E](https://img.shields.io/static/v1?label=&message=%20&color=1E1E1E) | Tarjetas (`RecomendadoCard`, `DestacadoCard`, `OfertaTipCard`). |
| **onSurface** | ![#FFFFFF](https://img.shields.io/static/v1?label=&message=%20&color=FFFFFF) | Texto principal dentro de tarjetas y diálogos. |
| **onSurfaceVariant** | ![#B0BEC5](https://img.shields.io/static/v1?label=&message=%20&color=B0BEC5) | Etiquetas secundarias y textos de menor jerarquía. |
| **surfaceContainer** | ![#4FC3F7](https://img.shields.io/static/v1?label=&message=%20&color=4FC3F7) | Fondo de botones de confirmación (vibrante sobre oscuro). |
| **onPrimary** | ![#FFFFFF](https://img.shields.io/static/v1?label=&message=%20&color=FFFFFF) | Texto sobre el color primario. |

---

## ☀️ Modo Claro (Light Theme)
Una interfaz limpia, aireada y profesional.

| Token M3 | Valor / Color | Aplicación en la UI |
| :--- | :--- | :--- |
| **primary** | ![#2196F3](https://img.shields.io/static/v1?label=&message=%20&color=2196F3) | Acciones principales, botones y enlaces. |
| **background** | ![#F7FAFC](https://img.shields.io/static/v1?label=&message=%20&color=F7FAFC) | Fondo de pantalla general. |
| **surface** | ![#FFFFFF](https://img.shields.io/static/v1?label=&message=%20&color=FFFFFF) | Cuerpo de las tarjetas (cards) para resaltar sobre el fondo grisáceo. |
| **surfaceVariant** | ![#0D47A1](https://img.shields.io/static/v1?label=&message=%20&color=0D47A1) | Elementos de superficie con mayor énfasis. |
| **onBackground** | ![#0F172A](https://img.shields.io/static/v1?label=&message=%20&color=0F172A) | Títulos de secciones y texto general del cuerpo. |
| **onSurfaceVariant** | ![#0D47A1](https://img.shields.io/static/v1?label=&message=%20&color=0D47A1) | Subtítulos y etiquetas de marca. |
| **surfaceContainer** | ![#0D47A1](https://img.shields.io/static/v1?label=&message=%20&color=0D47A1) | Botones de acción principal (color sólido). |

---

## 🛠️ Aplicación por Componente

### 1. Tarjetas (Cards)
Utilizan `MaterialTheme.colorScheme.surface` como color de fondo.
- **En Modo Claro:** Fondo blanco puro (`#FFFFFF`) para despegarse del fondo de pantalla.
- **En Modo Oscuro:** Gris oscuro suave (`#1E1E1E`) para legibilidad.

### 2. Botones de Acción
Definidos mediante `surfaceContainer`.
- **Modo Claro:** Utiliza `NomadBlueDark` para un look corporativo.
- **Modo Oscuro:** Utiliza un azul más claro (`#4FC3F7`) para garantizar que el botón sea visible y actúe como un faro en la interfaz oscura.

### 3. Diálogos (Ej. Términos y Condiciones)
- El fondo del diálogo utiliza `MaterialTheme.colorScheme.background`.
- Los enlaces dentro del texto utilizan `MaterialTheme.colorScheme.primary` con subrayado.

### 4. Textos y Tipografía
- **Títulos:** Usan `onBackground` para máximo contraste.
- **Cuerpo de Tarjetas:** Usan `onSurface` para asegurar que el texto sea legible sobre el fondo de la tarjeta.
- **Descripciones Secundarias:** Usan `onSurfaceVariant` o gris estándar para reducir la carga visual.

### 5. Barra de Navegación Inferior
- Sigue el esquema de colores del sistema, utilizando los colores de superficie y los tintes de `primary` para los estados seleccionados.

---

## 🎨 Resumen de la Paleta Técnica (`Color.kt`)

| Variable | Valor HEX | Descripción |
| :--- | :--- | :--- |
| `NomadBlue` | `#2196F3` | Azul base de Nomad. |
| `NomadBlueDark` | `#0D47A1` | Azul profundo para estados de énfasis. |
| `DarkBg` | `#1E1E1E` | Fondo base en modo oscuro. |
| `DarkSurf` | `#121212` | Superficie base en modo oscuro. |
| `LightBg` | `#F7FAFC` | Fondo "Ice White" para modo claro. |
| `LightSurf` | `#FFFFFF` | Blanco puro para superficies. |
