# Módulo Equipos

Permite agregar equipos con nombre obligatorio e imagen opcional. La pantalla se
abre desde la tarjeta Equipos del inicio. Cada fila muestra el icono a la izquierda
del nombre; cuando no hay imagen se usa un escudo predeterminado.

## Referencias al estilo existente

Se conserva la estructura de Jugadores: clases Kotlin, XML, `findViewById`,
`AlertDialog`, `RecyclerView.Adapter`, `LiveData`, repositorio, ViewModel y fábrica.
No se agregan librerías. Esta primera versión implementa agregar y listar equipos.

Los archivos Kotlin están bajo
`app/src/main/java/com/example/gestion_de_campeonatos01/`:

| Referencia de Jugadores | Archivo de Equipos | Responsabilidad |
| --- | --- | --- |
| `data/Jugador.kt` | `data/Equipo.kt` | Entidad Room: id, nombre e imagen opcional |
| `data/JugadorDao.kt` | `data/EquipoDao.kt` | Listar por id descendente e insertar |
| `data/JugadorRepository.kt` | `data/EquipoRepository.kt` | Delegar operaciones al DAO |
| `viewmodel/JugadorViewModel.kt` | `viewmodel/EquipoViewModel.kt` | Exponer equipos y ejecutar operaciones con corrutinas |
| `viewmodel/JugadorViewModelFactory.kt` | `viewmodel/EquipoViewModelFactory.kt` | Crear el ViewModel con su repositorio |
| `JugadorAdapter.kt` | `adapter/EquipoAdapter.kt` | Cargar el diseño de fila y mostrar los datos |
| `GestionJugadoresActivity.kt` | `GestionEquiposActivity.kt` | Conectar vistas, formulario y ViewModel |

Recursos bajo `app/src/main/res/`:

- `layout/activity_gestion_equipos.xml`: misma distribución y colores de Jugadores.
- `layout/dialog_agregar_equipo.xml`: nombre, vista previa, seleccionar y quitar imagen.
- `layout/item_equipo.xml`: icono y nombre en una fila horizontal.
- `drawable/ic_equipo.xml`: escudo utilizado cuando no se selecciona imagen.

## Flujo de guardado

`GestionEquiposActivity` → `EquipoViewModel` → `EquipoRepository` → `EquipoDao` → Room.

La consulta observable devuelve la lista actualizada al ViewModel. La Activity la
observa y llama a `adapter.actualizarLista(equipos)`, igual que en Jugadores.

El formulario permanece abierto cuando falta el nombre. Mientras se procesa una
imagen o se guarda, los botones quedan desactivados para evitar operaciones
duplicadas. El mensaje de éxito se muestra después de completar la inserción.

## Imagen opcional

`ActivityResultContracts.GetContent` abre el selector del sistema para `image/*`.
Cancelar la selección conserva la imagen anterior. No requiere permisos generales
de almacenamiento.

`util/ImagenEquipo.kt` lee la imagen fuera del hilo de interfaz y la reduce a un
máximo de 192 píxeles por lado. Room guarda esa copia PNG como `BLOB`; no guarda
una ruta temporal ni la fotografía original. El icono sigue disponible aunque
se borre el archivo original. Una imagen ilegible muestra un mensaje y conserva
la selección anterior.

El ViewModel conserva la imagen durante una rotación. La Activity también guarda
el nombre, el icono reducido y si el formulario estaba abierto en su estado.

## Base de datos y navegación

`data/AppDatabase.kt` pasa de versión 1 a 2 y registra `Equipo` y `equipoDao()`.
`MIGRATION_1_2` crea exclusivamente la tabla `equipos`; conserva la tabla
`jugadores` y sus registros. Se mantiene el nombre `jugadores_database` para abrir
la base existente. No se usa migración destructiva.

`MainActivity.kt` conecta `cardEquipos` con la nueva Activity, registrada en
`app/src/main/AndroidManifest.xml`.

## Verificación

`app/src/androidTest/java/com/example/gestion_de_campeonatos01/EquipoDatabaseTest.kt`
comprueba la migración desde versión 1, la conservación de jugadores, el guardado
de equipos con/sin imagen, su persistencia al reabrir y la reducción de imágenes.
Estas pruebas requieren un dispositivo o emulador Android.

Revisión manual: abrir Equipos, intentar guardar un nombre vacío, agregar sin
imagen, agregar con imagen, cancelar el selector, quitar la imagen del formulario,
girar la pantalla con el formulario abierto y reabrir la aplicación.
