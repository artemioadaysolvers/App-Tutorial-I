# Verificación de la entrega

- Compilación Android: `:app:assembleDebug`, completada correctamente.
- Pruebas JVM: 45 pruebas, cero fallos, cero errores. Cubren aritmética secuencial, corrección C/AC, funciones en una expresión pendiente, unidades DEG/RAD/GRA, trigonometría e inversas, hiperbólicas e inversas, dominios inválidos, logaritmos, potencias, raíces, factorial, exponentes, signos, retroceso, fracciones propias y mixtas, conversión sexagesimal y redondeo, memoria, SD, borrado, formato de ocho cifras, límites y encendido.
- Análisis Android: `:app:lintDebug`, completado, cero errores. Quedan cuatro avisos: tres sobre versiones/SDK más recientes y uno que informa de que el atributo de navegación predictiva se usa desde API 33. El código registra ese mecanismo solo desde API 33 y conserva el manejo anterior para API 26–32. Se mantienen las versiones del proyecto base.
- Sintaxis de app.js y scene.js verificada.
- Revisión visual y de límites en navegador: 320×568, 360×640, 390×760, 768×1024, 1280×800 y 740×360. Historia, modelo y teclado se ajustan al área disponible sin desplazamiento. El manual tiene seis capítulos sin scroll, revisados en vertical y horizontal.
- Modelo GLB cargado con texturas en el visor real. Orientación inicial y encuadre ajustados para ver el instrumento completo, incluida su funda. El cálculo 2D usa una vista independiente; no carga el modelo como fondo.
- Prueba de integración en navegador con el motor Kotlin de producción mediante un adaptador JVM local exclusivo de pruebas: selección DEG y pulsación 30 sin, resultado 0.5 mostrado en la interfaz. El adaptador no se distribuye en el APK.
- SHA-256 del modelo dentro del proyecto y dentro del APK idéntico al archivo original aportado.
- Recursos HTML, CSS y JavaScript del APK cotejados con los archivos finales del proyecto.
- No hay archivos fuente .java. La superficie visual sigue siendo AppView/WebView + HTML/CSS/JavaScript y la lógica de cálculo está en Kotlin.

## Límite de la verificación

No se ejecutó el APK en un teléfono o emulador Android durante esta entrega. La navegación interna, el ajuste visual, la carga GLB y el flujo de mensajes se revisaron en navegador; el motor se probó en JVM. Compilar y analizar el APK no sustituye una prueba de instalación en el dispositivo final. Sonido, vibración, compartir, portapapeles y barras del sistema requieren esa comprobación física.

Los límites de fidelidad histórica se detallan en FUENTES.md. La aplicación es una recreación funcional, no un emulador de ROM.

- Firma del APK comprobada con apksigner: válida, esquema v2.

## Revisión 2.0.1 · botones retirados

- Eliminados los botones Examinar instrumento, Giro, Abrir calculadora y Pantalla completa, y sus referencias de eventos JavaScript.
- Verificada en navegador la navegación Historia → Modelo 3D → Cálculo y la apertura de Ajustes; ausencia de los cuatro botones y consola sin errores. El modelo sigue cargando con sus texturas.
- Los archivos del motor Kotlin, todas las demás fuentes de app/src, CSS, fuentes, sonido y GLB coinciden byte por byte con la entrega anterior. No se repitieron las 45 pruebas numéricas porque el motor no cambió.
- APK 2.0.1 (versionCode 4) empaquetado desde el APK anterior validado, sustituyendo únicamente index.html, app.js y la versión del manifiesto. Alineación y firma APK comprobadas. No se completó una nueva compilación Gradle en esta revisión; el proyecto contiene los cambios para una compilación normal en Android Studio.
- Comparación de las entradas del APK: fuera de la firma, solo difieren AndroidManifest.xml, assets/index.html y assets/app.js. El código nativo compilado, recursos y modelo se conservan íntegros.
- Continúa pendiente la prueba de instalación en un dispositivo Android.

## Revisión 2.0.2 · documentación y modelo corregido

- Retirados de Ajustes los selectores RAD, DEG, GRA y SD. La tecla MODE y el motor Kotlin permanecen sin cambios.
- El manual se abre con un botón visible solo en Cálculo. Cinco capítulos: Operaciones, Funciones científicas, Fracción, Memoria y SD. Se actualiza el contador a cinco páginas.
- Historia abre una ventana independiente con una sola pestaña, Fuentes, sin capítulos de operación. El botón también es accesible en orientación horizontal.
- Sustituido el GLB por Casio_fx2200_mesh_0_6_copia1_corregido.glb. Verificada su carga con texturas y ajustada la cámara a la orientación vertical del nuevo archivo.
- Revisión de límites de contenido en 320×568, 390×760 y 740×360: Historia, Cálculo, los cinco capítulos del manual y Fuentes sin desplazamiento ni elementos fuera del área. Ajustes abre y muestra únicamente tacto, sonido, compartir y borrado de memoria/datos.
- Consola del navegador sin errores. Sintaxis JavaScript verificada.
- Código nativo sin cambios: no se repiten las 45 pruebas numéricas ya aprobadas. APK actualizado por empaquetado de recursos web y GLB sobre el código nativo validado; versión 2.0.2 (5). Firma y contenido del paquete verificados. No se afirma una nueva compilación Gradle.
- El GLB del proyecto y del APK coincide por SHA-256 con el nuevo archivo suministrado. El motor, sus pruebas, fuentes, sonido y bibliotecas conservan los bytes de la revisión anterior.
- Continúa pendiente la ejecución del APK en un dispositivo Android.
