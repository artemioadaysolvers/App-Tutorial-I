# CASIO fx-2200 · 1978

<<<<<<< HEAD
Aplicación Android adaptada a partir de la aplicación HP9100A aportada. Interfaz HTML/CSS/JavaScript en la clase AppView basada en WebView y motor científico Kotlin. No contiene archivos fuente Java. Las bibliotecas y APIs de Android se invocan desde Kotlin.
=======
Tutorial Nivel 1 para desarrollar una calculadora como aplicación móvil para android con Kotlin, JavaScript, HTML y CSS utilizando WebView.
>>>>>>> 9deec97851ef4eccc6dd579a58785d34eec31a2f

## Abrir el proyecto

1. Descomprimir CasioFX2200-proyecto.zip y abrir la carpeta CasioFX2200 en Android Studio.
2. Usar JDK 17, Android SDK Platform 36 y Build Tools 35.0.0. Seleccionar un dispositivo con Android 8.0 o posterior.
3. Sincronizar y pulsar Run. También puede instalarse directamente el APK debug entregado.
4. Compilar en Windows: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.
   En macOS/Linux: `chmod +x gradlew` y `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.
5. APK generado: `app/build/outputs/apk/debug/app-debug.apk`.

La primera sincronización necesita internet. La app ya instalada calcula y carga el modelo 3D sin conexión. Las fuentes históricas solo usan internet cuando el usuario abre una referencia en su navegador externo.

## Organización

<<<<<<< HEAD
- Hoja 01: nombre, historia breve, características y acceso a fuentes.
- Hoja 02: el archivo Casio_fx2200_mesh_0_6_copia1_corregido.glb aportado, con giro manual al arrastrar, acercamiento y restablecimiento de cámara.
- Hoja 03: calculadora 2D con todas las teclas visibles. Ajuste automático a la altura disponible, sin desplazamiento. En horizontal, los bloques se distribuyen a ambos lados del visor.
- Manual de operación en Cálculo: cinco capítulos paginados, sin scroll. Fuentes en Historia: una única pestaña de referencias, independiente del manual.

Todas las palabras de la interfaz 2D son negras. Papel beige y Special Elite local, con aspecto de máquina de escribir. El GLB se conserva sin alterar sus materiales ni sus texturas originales.

## Operación

- Aritmética secuencial: `2 + 3 × 4 =` produce 20.
- MODE: RAD, DEG, GRA, SD. RAD es el modo inicial; el modo seleccionado se guarda.
- Funciones después del valor: en DEG, `30 sin` produce 0.5. `0.5 INV sin` produce 30.
- `2 INV log` produce 100; `27 INV xʸ 3 =` produce 3.
- `2 EXP +/− 3 =` produce 0.002. INV EXP introduce π.
- `1 a b/c 2 =` produce 0.5. `2 a b/c 1 a b/c 3 =` produce 2.3333333.
- `12 °′″ 30 °′″ 0 °′″` produce 12.5. INV °′″ muestra grados, minutos y segundos.
- Memoria: M in guarda, MR recupera, M+ acumula. Para restar, cambiar el signo antes de M+.
- SD: introducir cada dato seguido de M+. Las teclas a b/c, 1/x, x!, √, M in y MR pasan a Σx², Σx, n, media, desviación muestral y desviación poblacional. INV M+ retira una coincidencia.
- C borra la entrada y permite corregirla conservando la operación pendiente. AC borra el cálculo. La memoria M y los datos SD se borran por separado en Ajustes.
- I/O apaga o enciende el instrumento. Atrás vuelve al modelo y luego a la historia.
- Teclado físico: números, operadores, coma o punto; Enter = cuando no está activando un botón, Escape AC, Suprimir C, Retroceso, E para exponente y F9 para signo.

Copiar, compartir, sonido y tacto se ejecutan desde Kotlin. El sonido original se conserva y puede desactivarse. Solo modo angular, sonido y tacto persisten después de recrear el proceso Android; los registros de cálculo y SD permanecen durante la navegación interna.

## Arquitectura y archivos

- `app/src/main/kotlin/com/retrolab/casiofx2200/AppView.kt`: contenido local, puente de mensajes y acciones Android.
- `CalculatorEngine.kt`: aritmética, funciones, fracciones, DMS, memoria y SD.
- `MainActivity.kt`, `ButtonSound.kt`: ciclo de vida, navegación y sonido.
- `app/src/main/assets/index.html`, `styles.css`, `app.js`: interfaz, navegación y transporte de mensajes.
- `scene.js`: three.js local, GLTFLoader y cámara ajustada al tamaño del modelo.
- `assets/models/casio_fx2200.glb`: copia íntegra del archivo proporcionado.
- `app/src/test/kotlin/.../CalculatorEngineTest.kt`: pruebas numéricas.
- `PreviewHarness.kt`: adaptador de verificación JVM, solo en las clases de pruebas; no forma parte del APK.

El puente acepta mensajes solo del marco principal y de https://appassets.androidplatform.net. La navegación web está bloqueada salvo las referencias históricas de una lista cerrada, que Kotlin abre en el navegador externo. No se usan eval, interfaces JavaScript públicas ni recursos remotos durante el cálculo.

## Documentación y fidelidad

Consultar FUENTES.md y VERIFICACION.md. Es una recreación funcional, no un emulador de ROM. El cálculo interno usa Double; los resultados muestran ocho cifras significativas. Los procedimientos no confirmados para la fx-2200 están identificados en FUENTES.md.

Versiones mantenidas: AGP 8.12.2, Gradle 8.13, Kotlin 2.1.20, AndroidX WebKit 1.14.0, three.js 0.170.0. minSdk 26, compile/targetSdk 36. La configuración in-process evita depender de un proceso auxiliar Kotlin al compilar.

## Revisión 2.0.1

Se retiraron exclusivamente los botones Examinar instrumento, Giro, Abrir calculadora y Pantalla completa, junto con las vinculaciones JavaScript de los controles retirados. La navegación permanece en las pestañas superiores.

## Revisión 2.0.2

- Manual de operación accesible mediante un botón en Cálculo. Capítulos: Operaciones, Funciones científicas, Fracción, Memoria y SD.
- Consultar fuentes en Historia abre exclusivamente la pestaña Fuentes. No muestra instrucciones de operación.
- RAD, DEG, GRA y SD se seleccionan desde MODE en el propio instrumento; ya no hay selectores duplicados en Ajustes.
- Sustituido el modelo por el archivo corregido aportado en esta revisión. Se copia íntegramente a assets/models/casio_fx2200.glb y la cámara se adapta a su nueva orientación.
=======

>
<img width="400" height="900" alt="image" src="https://github.com/user-attachments/assets/d38c580f-70ba-4631-b989-f6ee15856f8b" />
>>>>>>> 9deec97851ef4eccc6dd579a58785d34eec31a2f
