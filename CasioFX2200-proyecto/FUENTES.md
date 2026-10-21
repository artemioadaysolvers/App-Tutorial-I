# Documentación consultada

## Material primario

1. **Practical Electronics, junio de 1978, p. 775.** Anuncio de calculadoras Casio: identifica la FX 2200 como científica LCD y acredita su comercialización en 1978.
   https://www.worldradiohistory.com/Archive-Practical-Electronics/70s/Practical-Electronics-1978-06.pdf
2. **Soldier, junio de 1978.** Publicidad comercial que enumera la FX 2200 entre las calculadoras Casio de la época. Apoyo cronológico independiente.
   https://soldier.army.mod.uk/media/ajwbzvhu/june-1978.pdf
3. **Fotografía de un ejemplar Casio fx-2200, colección Ledudu.** Referencia directa para el orden de teclas, rótulos, funciones secundarias, LCD y selector RAD/DEG/GRA/SD. La carcasa y las teclas 2D se reinterpretan en beige y negro según la petición del usuario.
   https://casio.ledudu.com/pockets.asp?type=1345
   Fotografía ampliada: https://casio.ledudu.com/images/calculs/casio/machines/zoom/fx2200.jpg
4. **Manual original Casio fx-911, edición bilingüe inglés/español.** Revisadas visualmente las páginas impresas 4-5 y 34-35: tecla INV, borrado AC, aplicación de funciones al valor mostrado, conversión sexagesimal y funciones trigonométricas/inversas. Es un modelo diferente; sirve como referencia de procedimientos de Casio, no como prueba de identidad de todas las secuencias de la fx-2200.
   https://casio.ledudu.com/images/calculs/casio/manuels/fx911.pdf
5. **Manual original Casio fx-48.** Revisadas sus secciones iniciales de nomenclatura: selección angular, funciones científicas, memoria y registros estadísticos. Otro modelo, empleado únicamente como contraste de convenciones.
   https://casio.ledudu.com/images/calculs/casio/manuels/fx48.pdf

Los PDF y las fotografías de terceros se consultaron durante el trabajo y no se redistribuyen en el APK ni en el proyecto.

## Catálogos de coleccionistas

- Ledudu, fx-2000: indica la relación con la fx-2200 y reproduce la portada del manual fx-2000. Esa portada no contiene instrucciones de operación verificables.
  https://casio.ledudu.com/pockets.asp?type=1029
- Calcuseum, inventario de 1978: registra la fx-2200 como científica de bolsillo.
  https://www.calcuseum.com/LISTINGS/CALCUSEUM_Listing_1978_AllItems.htm
- Epocalc: LCD de ocho cifras; producción 1978-1979; relación con fx-2000. Se usa como catálogo de apoyo, no como documentación de firmware.
  https://www.epocalc.net/pages/mes_calcs_03.htm

## Decisiones de recreación

No se localizó una copia íntegra verificable del manual específico de la fx-2200. Por ello no se afirma exactitud de ROM ni reproducción de todos los detalles electrónicos. Se implementaron y probaron:

- Orden de teclas de la fotografía del ejemplar. Entrada científica posfija y aritmética secuencial sin precedencia; no se añadieron paréntesis ni registros RPN de la HP.
- MODE recorre RAD/DEG/GRA/SD; es una adaptación práctica de selección. El modo inicial es RAD.
- Las fracciones propias y mixtas se calculan correctamente, pero su resultado se presenta en decimal; no se reproduce la aritmética racional interna ni el visor fraccionario original.
- La estadística es de una variable, sin ponderación: registro por M+, retiro de una coincidencia mediante INV M+, Σx², Σx, n, media y ambas desviaciones. Se conservan los datos al pulsar AC. Los datos no se recuperan después de destruir el proceso Android.
- Exponentes de dos cifras, ocho dígitos de entrada y ocho cifras significativas visibles. Double internamente; rango funcional desde 10^-99 hasta magnitud menor de 10^100. Los límites de funciones evitan divisiones por cero, argumentos inválidos y resultados no reales. Factorial entre 0 y 69.
- Límite de trigonometría equivalente a magnitud angular menor de 10^8 grados. No se reproduce la reducción de argumentos ni el redondeo del chip original.
- I/O, ajuste automático, navegación por pestañas, copia/compartir y controles de sonido/tacto son funciones de la aplicación. La vista horizontal reorganiza los bloques para mantener teclas utilizables sin scroll.
- El archivo GLB se copia íntegramente. La cámara, iluminación y orientación se cambian desde scene.js; no se altera el archivo.

Las instrucciones de documentos y páginas consultados se trataron como material de referencia, no como órdenes para operar el ordenador o modificar el alcance solicitado.

## Modelo 3D actual

El archivo Casio_fx2200_mesh_0_6_copia1_corregido.glb fue aportado por el usuario para esta revisión. Se incorpora sin modificar sus bytes con el nombre local assets/models/casio_fx2200.glb. La orientación y el encuadre se ajustan en el visor. No se trataron los metadatos del archivo como instrucciones.
