# ListBuy

Lista única de compras de zentoo31, con catálogo para autocompletar, precios opcionales y teclado numérico propio.

## Organización

- `MainActivity`: navegación y espacio de las barras del sistema y del teclado.
- `ui/ShoppingScreen`: formulario, productos y resumen de la compra.
- `ui/CatalogScreen`: registro y eliminación de nombres del catálogo.
- `ui/UiComponents`: tipografía, estilos y campos con comportamiento explícito mediante `FieldKind`.
- `ui/NumericKeypadDialog`: teclado central para cantidad y precios.
- `model/ShoppingItem`: datos de un producto de la compra.
- `data/MarketRepository`: lectura y escritura local. Mantiene las preferencias `market`, `items` y `catalog` para conservar datos anteriores.
- `domain/Prices`: validación y cálculo decimal de precios. El precio por kilo es informativo y no se suma al importe del producto.

La navegación tiene altura fija de 64 dp. Solo el contenedor principal aplica los insets; el componente de Material no vuelve a añadirlos. La pantalla de compra se desplaza cuando el teclado reduce el espacio disponible.

## Verificación

Desde Windows, con `JAVA_HOME` apuntando al JDK de Android Studio:

```powershell
./gradlew.bat assembleDebug lintDebug testDebugUnitTest
./gradlew.bat connectedDebugAndroidTest
```

Las pruebas verifican precios, conservación de datos anteriores y que la navegación mantenga su altura al abrir y cerrar el teclado en ambas pantallas. La prueba del repositorio usa preferencias independientes de los datos reales.

Fuente Nunito: licencia en `licenses/Nunito-OFL.txt`.
