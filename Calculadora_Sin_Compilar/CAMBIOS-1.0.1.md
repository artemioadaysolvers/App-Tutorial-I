# Versión 1.0.1

Se ha corregido un fallo al iniciar la actividad: se consultaba el controlador de la ventana antes de crear su contenido. En Android, esa consulta puede fallar si la vista de la ventana aún no existe. Ahora la apariencia de las barras del sistema se configura después de crear la pantalla y solo si hay un controlador disponible. Se vuelve a aplicar cuando la ventana obtiene el foco.

También se han protegido las llamadas de pausa y limpieza cuando la vista web todavía no existe o ya se ha separado de su contenedor.

La versión de instalación sube de 1 a 2 (`1.0.1`), para actualizar el APK anterior sin cambiar el identificador de la app. Compila utilizando la misma instalación de Android Studio y su misma clave de desarrollo para que Android acepte la actualización.

## Actualizar en tu teléfono

1. Extrae este ZIP en una carpeta nueva y ábrela en Android Studio.
2. Espera a que termine la sincronización de Gradle.
3. Genera el APK de la variante `debug`.
4. Copia al teléfono `app/build/outputs/apk/debug/app-debug.apk` y acepta actualizar la calculadora.

Utiliza el APK llamado **app-debug.apk**. El archivo que contiene `androidTest` en su nombre es el paquete de pruebas y no es la calculadora.

No se ha compilado ningún APK en esta revisión. La confirmación del funcionamiento en el Honor necesita instalar y abrir esta versión en el teléfono.

## Verificación realizada

- Prueba de inicio del código Java real con sustitutos locales de las API Android: el código anterior falla cuando la ventana aún no tiene contenido; la revisión pasa con controlador disponible y no disponible, usando los niveles de API 23, 29, 30, 35 (Android 15) y 36.
- Lectura de los cinco archivos web incluidos y bloqueo de solicitudes externas en esa prueba local.
- Compilación aislada del archivo Java contra las API reales de Android SDK 36, sin generar un APK.
- Pruebas de las operaciones de la calculadora.

Estas comprobaciones no sustituyen una ejecución en un emulador o en el teléfono.

Referencia de la API: https://developer.android.com/reference/android/view/Window#getInsetsController()
