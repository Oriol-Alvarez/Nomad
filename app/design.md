# Diseño de la Base de Datos - Nomad App (SQLite / Room)

## T4.3 Esquema de la Base de Datos

### 1. Tabla `users` (T4.1)
Almacena la información de perfil persistente de cada usuario localmente.
- `id` (String, PK): UID proveniente de Firebase.
- `email` (String): Correo electrónico del usuario.
- `username` (String): Nombre de usuario único.
- `birthdate` (String): Fecha de nacimiento.
- `address` (String): Dirección física.
- `country` (String): País de residencia.
- `phoneNumber` (String): Teléfono de contacto.
- `acceptEmails` (Boolean): Consentimiento para comunicaciones.

### 2. Tabla `trips` (T4.2)
Almacena los viajes, ahora vinculados a un usuario específico.
- `id` (String, PK): ID único del viaje.
- `userId` (String, FK): Relación con la tabla `users.id`. Permite el soporte multiusuario.
- `title`, `country`, `description`, `dataInici`, `dataFinal`, `imageUri`, `isFeatured`, `budget`.

### 3. Tabla `itinerary_items` (Relacionada con Trips)
- `id` (String, PK).
- `tripId` (String, FK): Relación con `trips.id`.
- `nombre`, `dia`, `hora`, `precio`, `tipo`, `descripcion`.

### 4. Tabla `access_logs` (T4.4)
Registra cada evento de inicio y cierre de sesión.
- `id` (Int, PK Auto): ID secuencial.
- `userId` (String): Quién realizó la acción.
- `dateTime` (Long): Timestamp del evento.
- `type` (String): Tipo de evento ("LOGIN" o "LOGOUT").

## Flujo de Datos
1. Al iniciar la app, `SplashScreen` verifica el login en Firebase.
2. `AuthViewModel` registra el evento en `access_logs`.
3. `TripListViewModel` utiliza el UID del usuario activo para filtrar los viajes mediante `TripDao.getTripsForUser(uid)`.
