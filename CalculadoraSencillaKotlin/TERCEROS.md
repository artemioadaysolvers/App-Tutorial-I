# Componentes de terceros

## Gradle Wrapper

Los archivos `gradlew`, `gradlew.bat` y `gradle/wrapper/gradle-wrapper.jar` proceden del Gradle Wrapper 8.11.1 y conservan su licencia Apache 2.0. Este lanzador puede descargar y ejecutar la distribución Gradle 8.13 indicada en `gradle-wrapper.properties`.

- Proyecto: https://github.com/gradle/gradle
- Licencia: `licenses/GRADLE-APACHE-2.0.txt`
- SHA-256 del JAR incluido: `2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046`.
- SHA-256 oficial de la distribución Gradle 8.13 binaria: `20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78`.
- Referencia: https://gradle.org/release-checksums/

La app no incorpora librerías JavaScript, fuentes remotas, paquetes npm ni AndroidX. Android Studio descargará las herramientas de compilación y sus dependencias desde sus repositorios oficiales cuando sincronices el proyecto.

## Kotlin

El plugin de Kotlin 2.3.20 añade la biblioteca estándar de Kotlin como dependencia de la app. Gradle la descargará de los repositorios oficiales al sincronizar; no se incluyen esos binarios dentro del ZIP fuente. Kotlin se distribuye bajo licencia Apache 2.0. Referencias: https://kotlinlang.org/ y https://github.com/JetBrains/kotlin.
