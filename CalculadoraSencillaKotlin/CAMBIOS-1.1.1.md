# Versión 1.1.1: sin imports directos de java.io

Se retiraron de `MainActivity.kt` los imports y los usos explícitos de `java.io.ByteArrayInputStream` y `java.io.IOException`.

- Los errores habituales al cargar recursos locales se manejan con `runCatching` y `getOrElse`, de la biblioteca estándar de Kotlin. Los errores graves se siguen propagando.
- Las respuestas 404 no contienen cuerpo: se entrega `null` al parámetro de datos de `WebResourceResponse`.
- Se conservan los archivos HTML, CSS y JavaScript y la corrección del inicio en Android 15.
- La app tiene `versionCode = 4` y `versionName = "1.1.1"`, para permitir actualizar la versión anterior con la misma clave de firma.

## Alcance exacto

El código fuente de la app no contiene archivos `.java` ni imports `java.*`. Esto no significa que Android, Kotlin o las dependencias de la plataforma estén libres de código procedente de Java. La lectura de los recursos sigue pasando por las API del SDK de Android; sustituir un import no elimina esas API ni su implementación.

No se ha generado un APK. Abre la carpeta `CalculadoraSencillaKotlin` en Android Studio, sincroniza y genera el APK de la variante `debug` cuando quieras instalar esta revisión.

Referencias:

- https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/run-catching.html
- https://developer.android.com/reference/android/webkit/WebResourceResponse
