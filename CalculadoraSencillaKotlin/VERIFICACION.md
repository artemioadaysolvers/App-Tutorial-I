# Verificación de la entrega 1.1.1

- Se compiló aisladamente `MainActivity.kt` con Kotlin 2.3.20, destino JVM 17 y las API reales de Android SDK 36. Esto comprueba el archivo Kotlin sin generar un APK.
- Se ejecutó el código Kotlin de la actividad contra sustitutos locales de las API de Android: inicio, foco, pausa, reanudación y destrucción; controlador de barras disponible y ausente; niveles de API 23, 29, 30, 35 y 36.
- Esa prueba comprueba la lectura de los cinco archivos web locales y el rechazo de solicitudes y navegación externas.
- Pasaron las 12 pruebas automatizadas de las operaciones JavaScript.
- Se comprobó la sintaxis XML y que los archivos HTML, CSS y JavaScript coinciden con los de la calculadora anterior.
- Se verificó el archivo ZIP y la presencia de la actividad Kotlin, la configuración y el Gradle Wrapper completo. No contiene archivos `.java`, APK, claves de firma, carpetas de compilación ni rutas de configuración de otro equipo.

No se ha ejecutado una compilación completa con Gradle, no se ha generado un APK y no se ha ejecutado esta versión en un emulador o en el Honor. La comprobación final del funcionamiento en el teléfono requiere generar e instalar el APK desde Android Studio.

- Se revisó la ausencia de imports `java.*` en el código Kotlin y el tratamiento de respuestas sin cuerpo.
