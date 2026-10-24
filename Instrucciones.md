INSTRUCCIONES DE NOCTUNE

¿Qué es Noctune?

Noctune es una aplicación móvil Android de orientación preventiva en seguridad digital. Permite revisar hábitos básicos de seguridad y consultar recomendaciones sencillas. Está pensada como un MVP académico y utiliza información ficticia.

Funciones principales

- Crear una cuenta y un perfil ficticio.
- Registrar dispositivos de prueba Android o iOS, seleccionar cuál se está evaluando y conservar sus estadísticas por separado.
- Responder una evaluación de ocho preguntas sobre actualizaciones, respaldos, autenticación, recuperación de cuentas, permisos y conexiones.
- Consultar el resultado, las recomendaciones y el historial de evaluaciones.
- Marcar recomendaciones como realizadas y revisar el porcentaje de avance.
- Leer consejos educativos sobre seguridad digital, incluida la regla de respaldos 3-2-1.
- Enviar solicitudes de asistencia simuladas, elegir una categoría y consultar su estado.
- Usar cámara o galería para agregar una foto al perfil y activar recordatorios preventivos.

La información de usuarios, dispositivos, evaluaciones y solicitudes de prueba se conserva localmente con Room/SQLite. La aplicación no registra incidentes reales ni está conectada a un servicio productivo de MCmovil.

Requisitos para ejecutar

- Android Studio.
- JDK 17.
- Android SDK 34 instalado.
- Emulador o teléfono con Android 8.0 (API 26) o superior.
- Conexión a Internet para que Android Studio descargue las dependencias durante la primera sincronización de Gradle.

Cómo abrir y ejecutar

1. Descarga o clona el repositorio en el computador.
2. En Android Studio, selecciona **File > Open**.
3. Elige la carpeta `NoctuneAPP`, donde se encuentra `settings.gradle.kts`.
4. Espera a que finalice **Gradle Sync**. Si Android Studio solicita instalar el SDK 34, acepta la instalación.
5. Selecciona un emulador Android 8.0 o superior, o conecta un teléfono con la depuración USB habilitada.
6. Pulsa **Run ▶** para instalar e iniciar la aplicación.

Recorrido de prueba recomendado

1. Registra un usuario de prueba e inicia sesión.
2. En **Inicio**, abre la gestión de dispositivos y agrega un dispositivo ficticio. El primero queda seleccionado.
3. Abre **Análisis** y responde las ocho preguntas.
4. Consulta **Historial** para revisar el resultado, las recomendaciones y su progreso.
5. Registra otro dispositivo y usa **Usar** para comprobar que sus evaluaciones se muestran por separado.
6. Desde el menú del avatar, prueba **Editar perfil**, la cámara o galería y **Ayuda y soporte**.

Estructura del proyecto

- `app/src/main/java/com/noctume/security/ui/screens`: pantallas de la aplicación.
- `app/src/main/java/com/noctume/security/ui/components`: componentes visuales reutilizables.
- `app/src/main/java/com/noctume/security/viewmodel`: estado y lógica de las pantallas (MVVM).
- `app/src/main/java/com/noctume/security/model`: preguntas y reglas de evaluación.
- `app/src/main/java/com/noctume/security/data/BaseDatos.kt`: entidades, consultas y persistencia Room.
- `app/src/main/java/com/noctume/security/navigation/Navegacion.kt`: rutas y navegación.
- `app/src/main/java/com/noctume/security/notifications`: recordatorios preventivos.

DISFRUTA DE LA APP
