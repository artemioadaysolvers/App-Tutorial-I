# Versión 1.1.0: Kotlin para Android

La actividad Android se ha convertido de Java a Kotlin y se encuentra en `app/src/main/kotlin/com/ejemplo/calculadorasencilla/MainActivity.kt`. El proyecto de la app ya no contiene archivos `.java`.

La configuración de Gradle también está escrita en Kotlin: `settings.gradle.kts`, `build.gradle.kts` y `app/build.gradle.kts`.

## Función de cada lenguaje

| Lenguaje | Función en esta calculadora |
|---|---|
| Kotlin | Crea la actividad Android y el WebView, ajusta la pantalla a las barras del sistema, carga los archivos locales y gestiona las pausas y el cierre. Es la parte que utiliza las API nativas de Android. |
| JavaScript | Realiza los cálculos y responde a los botones y al teclado. |
| HTML | Define la estructura de la pantalla, los botones y el resultado. |
| CSS | Define tamaños, distribución, colores y adaptación a la pantalla. |

No se han añadido funciones de cámara, ubicación ni sensores, porque la calculadora no las necesita. El acceso nativo actual lo realiza Kotlin. No se expone un puente desde JavaScript hacia funciones del teléfono.

Se conserva la corrección del inicio de la versión 1.0.1: el controlador de las barras se consulta después de crear la pantalla y se comprueba que exista.

## Herramientas configuradas

- Kotlin y su plugin Android: 2.3.20.
- Android Gradle Plugin: 8.13.2.
- Gradle: 8.13.
- JDK y destino JVM: 17.
- SDK de compilación y destino: 36; mínimo: 23.
- Versión de la app: 1.1.0; código de versión: 3.

La selección de herramientas se basa en las tablas oficiales:

- https://kotlinlang.org/docs/gradle-configure-project.html
- https://developer.android.com/build/releases/agp-8-13-0-release-notes
- https://developer.android.com/build/kotlin-support

## Abrir y generar el APK

Extrae el ZIP en una carpeta nueva, abre `CalculadoraSencillaKotlin` en Android Studio, sincroniza y genera el APK de la variante `debug`. El resultado será `app/build/outputs/apk/debug/app-debug.apk`.

Para actualizar la app anterior, utiliza la misma clave de firma. No se ha compilado un APK ni se ha probado esta revisión en el Honor. El JDK sigue siendo una herramienta necesaria para compilar Kotlin; eso no significa que el código de esta app siga escrito en Java.
