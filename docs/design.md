# 📐 Diseño Arquitectónico de Nomad

## 🏛️ Arquitectura General
Nomad sigue una arquitectura **MVVM (Model-View-ViewModel)** para una mejor separación de responsabilidades y escalabilidad.


## 📊 Modelo de Datos: Creado completo para futuros Sprints

![alt text](domain_model.png)

El dominio de la aplicación se divide en módulos lógicos interconectados que representan las entidades principales del ecosistema de viajes.

### Entidades Principales
* **User & Authentication**: El `User` centraliza la identidad (nombre, email, foto) y se vincula 1:1 con `Authentication` para gestionar tokens de sesión y seguridad.
* **Trip (Viaje)**: La entidad núcleo que contiene detalles como `title`, `country`, `description` y `budget`. Posee métodos de lógica de negocio como `optimizeBudgetDistribution()`.
* **ItineraryItem (Paradas)**: Cada viaje contiene una lista de ítems (vuelos, hoteles, actividades) vinculados a un `ItineraryType` para su categorización.
* **Preferences**: Almacena la configuración del usuario, incluyendo el estado de `termsAccepted` y el idioma preferido.
* **AIRecommendations**: Sistema que consume las `Preferences` del usuario para generar sugerencias personalizadas de destinos y paradas.

---

## 🎨 UI & Screens (Capa de Presentación)

### 1. Formulario de Viaje (`FormularioViaje`)
Esta pantalla gestiona la creación de un `Trip` mediante un proceso de pasos dinámicos.

* **Paso 1 (Detalles)**: Captura la información general utilizando `OutlinedTextField` y selectores de fecha (`DatePicker`) para ida y vuelta.
* **Paso 2 (Actividades)**: Permite visualizar y añadir `ItineraryItem` a la lista actual del viaje.
* **Navegación**: Utiliza `AnimatedContent` para transiciones fluidas entre las etapas del formulario.

### 2. Gestión de Itinerarios (`DialogoNuevaActividad`)
Componente modal que permite la entrada de datos para nuevas paradas.
* **Input de Tiempo**: Implementa `TimePicker` de Material 3 para definir el `schedule` de la actividad.
* **Categorización**: Un `DropdownMenu` permite seleccionar el tipo de actividad (Vuelo, Restaurante, Hotel, etc.), mapeándolo con `ItineraryType`.

---

## 🛠️ Tecnologías y Estándares

| Componente | Tecnología |
| :--- | :--- |
| **Framework UI** | Jetpack Compose (Declarativo) |
| **Diseño** | Material Design 3 (M3) |
| **Navegación** | Compose Navigation Recomended |
| **Persistencia de Configuración** | SharedPreferences (para `terms_accepted`) |
| **Gestión de Estado** | `mutableStateOf` con `rememberSaveable` |




---