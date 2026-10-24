# INSTRUCCIONES DE NOCTUNE

## Sobre la aplicación

Noctune es una aplicación Android académica de prevención y educación en seguridad digital. Trabaja con perfiles y dispositivos ficticios; no registra incidentes reales ni se conecta con sistemas productivos de MCmovil.

## Integrantes

- Andy Flores
- Sebastian Yajure

## Funciones

- Crear un perfil ficticio y agregar una foto desde la cámara o galería.
- Registrar dispositivos de prueba Android o iOS y seleccionar el dispositivo que se evaluará.
- Responder ocho preguntas sobre actualizaciones, respaldos, autenticación, recuperación de cuentas, permisos y redes.
- Consultar el resultado, recomendaciones, checklist de avance e historial por dispositivo.
- Revisar consejos educativos, incluida la regla de copias de seguridad 3-2-1.
- Enviar solicitudes de asistencia simuladas con categoría y estado.
- Activar recordatorios preventivos.

La información de prueba se almacena localmente con Room/SQLite.

## Requisitos

- Android Studio y JDK 17.
- Android SDK 34.
- Emulador o dispositivo Android 8.0 (API 26) o superior.
- Conexión a Internet para descargar dependencias durante la primera sincronización de Gradle.

## Cómo ejecutar

1. Descarga o clona este repositorio.
2. En Android Studio, selecciona **File > Open** y abre la carpeta que contiene `settings.gradle.kts`.
3. Espera a que termine **Gradle Sync** e instala Android SDK 34 si Android Studio lo solicita.
4. Selecciona un emulador Android 8.0 o superior, o conecta un teléfono con depuración USB habilitada.
5. Pulsa **Run ▶**.

## Recorrido de prueba

1. Registra un usuario ficticio e inicia sesión.
2. Agrega un dispositivo en **Gestionar dispositivos**; el primero queda seleccionado.
3. Responde las ocho preguntas en **Análisis**.
4. Abre **Historial** para ver puntaje, recomendaciones y avance.
5. Agrega otro dispositivo y selecciónalo para comprobar que el historial se mantiene separado.
6. Abre el menú del avatar para editar el perfil, probar cámara/galería y acceder a **Ayuda y soporte**.

## Tecnologías y organización

Kotlin, Jetpack Compose, Material Design 3, arquitectura MVVM, Room/SQLite, Navigation Compose y WorkManager.

- `app/src/main/java/com/noctume/security/ui/screens`: pantallas.
- `app/src/main/java/com/noctume/security/ui/components`: componentes reutilizables.
- `app/src/main/java/com/noctume/security/viewmodel`: estado y lógica de presentación.
- `app/src/main/java/com/noctume/security/model`: preguntas y reglas de evaluación.
- `app/src/main/java/com/noctume/security/data/BaseDatos.kt`: persistencia Room.
- `app/src/main/java/com/noctume/security/navigation/Navegacion.kt`: navegación.
- `app/src/main/java/com/noctume/security/notifications`: recordatorios.

Para más instrucciones, consulta también [`Instrucciones.md`](Instrucciones.md).
