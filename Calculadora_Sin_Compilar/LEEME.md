# Calculadora sencilla · HTML, CSS y JavaScript para Android

Proyecto completo para abrir en Android Studio y generar tú mismo el APK. **Este paquete contiene el código fuente y la configuración; no contiene un APK compilado.**

Versión **1.0.1**: corrige la configuración de la ventana durante el inicio. Consulta `CAMBIOS-1.0.1.md` para actualizar la versión anterior.

## 1. Abrir el proyecto

1. Extrae **todo** el ZIP a una carpeta local, por ejemplo `C:\Proyectos\CalculadoraSencilla`.
2. Abre Android Studio y elige **Open / Abrir**.
3. Selecciona la carpeta **CalculadoraSencilla**, la que contiene `settings.gradle`, `build.gradle` y `gradlew.bat`. No selecciones únicamente `app` ni `www`.
4. Permite que termine la sincronización de Gradle. La primera sincronización necesita Internet para descargar Gradle y el complemento de Android.
5. Si Android Studio pide instalar componentes del SDK, acepta la instalación de **Android SDK Platform 36** y **Android SDK Build-Tools 35.0.0**.

No necesitas crear otro proyecto, copiar archivos a mano, instalar Node.js ni instalar Capacitor.

## 2. Requisitos

- Android Studio compatible con **Android Gradle Plugin 8.12.2**.
- **JDK 17** para ejecutar Gradle. En Android Studio se selecciona en `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`. JDK 21 también es compatible con la versión de Gradle incluida en la configuración.
- **Android SDK Platform 36**, con las licencias del SDK aceptadas en tu instalación.
- Conexión a Internet para la primera descarga de herramientas. La app instalada funciona sin conexión.
- Un móvil o emulador con **Android 6.0 (API 23) o posterior**, con Android System WebView disponible y actualizado.

El ZIP incluye los lanzadores de Gradle para Windows, macOS y Linux, el archivo `gradle-wrapper.jar` y la configuración que descarga **Gradle 8.13** con comprobación SHA-256. Android Studio, el JDK, el SDK y la distribución de Gradle son herramientas externas que se instalan o descargan en tu equipo.

## 3. Generar un APK de prueba

1. Con el proyecto sincronizado, comprueba que la variante seleccionada sea **debug**.
2. Abre el menú **Build** y elige la opción de generar APK. Según la versión del IDE, aparece como **Generate App Bundles or APKs > Generate APKs**, o **Build Bundle(s) / APK(s) > Build APK(s)**.
3. Cuando termine, pulsa **Locate** en la notificación o busca este archivo dentro del proyecto:

   `app/build/outputs/apk/debug/app-debug.apk`

Este APK se firma automáticamente con una clave de desarrollo y sirve para pruebas e instalación manual.

Para una versión de distribución, utiliza **Build > Generate Signed App Bundle or APK > APK** y crea o selecciona tu propia clave de firma. Conserva esa clave para poder publicar futuras actualizaciones. El proyecto no incluye claves privadas ni contraseñas.

Opcionalmente, desde una terminal abierta en la carpeta del proyecto:

```powershell
# Windows: tú ejecutas este comando cuando quieras compilar.
.\gradlew.bat assembleDebug
```

```sh
# macOS / Linux
chmod +x gradlew
./gradlew assembleDebug
```

## 4. Qué hace la calculadora

- Suma, resta, multiplicación y división.
- Decimales con coma en pantalla; admite coma o punto desde el teclado.
- Cambio de signo `±`, borrado de un dígito y limpieza completa `AC`.
- Porcentaje simple: `%` divide el número mostrado entre 100. Ejemplo: `200 × 10 % = 20`.
- Operaciones encadenadas de izquierda a derecha, como una calculadora de bolsillo. Ejemplo: `2 + 3 × 4 = 20`.
- Repetición de la última operación al pulsar `=` otra vez.
- Aviso al dividir entre cero y recuperación al introducir un nuevo número.
- Hasta 12 dígitos de entrada y 12 cifras significativas en los cálculos. Los resultados muy largos se muestran con notación científica.
- Uso táctil y mediante teclado: números, `+`, `-`, `*`, `/`, `%`, `Enter` o `=`, `Backspace`, `Escape` o `Delete`.

Es una calculadora básica con números de coma flotante; no ofrece precisión decimal arbitraria. No guarda historial ni requiere cuenta. La operación actual se reinicia al volver a crear la pantalla, por ejemplo al girar el dispositivo.

## 5. Dónde modificarla

```text
CalculadoraSencilla/
├── app/
│   ├── build.gradle                  Identificador, versiones y SDK
│   └── src/main/
│       ├── AndroidManifest.xml       Configuración Android
│       ├── assets/www/
│       │   ├── index.html            Estructura y botones
│       │   ├── styles.css            Diseño, colores y tamaños
│       │   ├── engine.js             Operaciones y estado
│       │   ├── app.js                Interacción y teclado
│       │   └── favicon.svg           Icono para navegador
│       ├── java/com/ejemplo/calculadorasencilla/
│       │   └── MainActivity.java     Contenedor Android de la interfaz web
│       └── res/                      Nombre, tema e iconos Android
├── gradle/wrapper/                   Gradle Wrapper completo
├── tests/calculator.test.cjs         Pruebas de las operaciones
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
└── gradlew.bat
```

Edita directamente los archivos de `app/src/main/assets/www/` y vuelve a generar el APK. No hay un paso de empaquetado web ni una copia adicional que sincronizar.

- Para cambiar el nombre visible, modifica `app/src/main/res/values/strings.xml`.
- Para cambiar el identificador de instalación, modifica `applicationId` en `app/build.gradle`.
- Para cambiar el diseño, modifica `styles.css`.
- Los iconos del lanzador están en `app/src/main/res/mipmap-anydpi/` y `mipmap-anydpi-v26/`.

La actividad nativa utiliza un WebView y sirve únicamente los archivos incluidos en la app bajo un origen HTTPS local. No necesita un servidor funcionando en el teléfono, no solicita permiso de Internet y no expone un puente JavaScript a funciones del teléfono.

## 6. Verificación y pruebas

Se verificaron la sintaxis JavaScript, los recursos XML y la compatibilidad del código Java con las API del SDK 36. Las pruebas automáticas de cálculo se pueden ejecutar con Node.js 18 o posterior, de forma opcional:

```sh
node --test tests/calculator.test.cjs
```

**No se ha generado un APK, no se ha ejecutado una compilación completa de Gradle y no se ha probado en un emulador o teléfono Android.** La compilación y la prueba en tu dispositivo quedan a tu cargo, conforme a lo solicitado.

Para probar la interfaz web en un ordenador sin compilar, si tienes Python:

```sh
python -m http.server 8080 --bind 127.0.0.1 --directory app/src/main/assets/www
```

Después abre `http://127.0.0.1:8080` en tu navegador. Este servidor es solo una herramienta de vista previa en el ordenador y no es necesario en Android.

## 7. Si Android Studio muestra un error

- **SDK location not found**: elige tu Android SDK en la configuración del proyecto. Android Studio puede generar `local.properties` con la ruta de tu equipo; ese archivo no se distribuye en el ZIP.
- **Unsupported class file / Java version**: selecciona JDK 17 o JDK 21 en la configuración de Gradle.
- **SDK Platform 36 no instalado**: abre **SDK Manager**, instala la plataforma API 36 y sincroniza otra vez.
- **No se puede descargar Gradle o el plugin**: revisa la conexión y desactiva el modo de trabajo sin conexión de Gradle durante la primera sincronización.
- **Versión del plugin no compatible**: utiliza una versión de Android Studio que admita AGP 8.12.2. No cambies Gradle y el complemento de Android de forma independiente.

## Referencias oficiales

- [Compatibilidad de Android Gradle Plugin 8.12](https://developer.android.com/build/releases/agp-8-12-0-release-notes)
- [Compilar y ejecutar una app](https://developer.android.com/studio/run)
- [Firmar una app](https://developer.android.com/studio/publish/app-signing)
- [Comprobaciones SHA-256 oficiales de Gradle](https://gradle.org/release-checksums/)

Los componentes de Gradle incluidos conservan sus avisos originales. Consulta `TERCEROS.md` y `licenses/GRADLE-APACHE-2.0.txt`.
