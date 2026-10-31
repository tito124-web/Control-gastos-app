# control-gasto-ingreso-ui

Aplicación Android con las pantallas de la app de ingresos y gastos. Usa la librería `control-gasto-ingreso-core` para todas las reglas y guarda los datos en SQLite con Room.

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Login | Pide el nombre. La primera vez crea la cuenta, y después compara el nombre escrito con el guardado |
| Principal | Muestra el balance, los totales de ingresos y gastos, el límite diario y la lista de movimientos |
| Formulario | Registra un movimiento nuevo o edita uno existente |
| Estado financiero | Gráfica de ingresos contra gastos por día y por mes, con un resumen y el desglose de gastos por categoría |

```
LoginActivity  →  MainActivity  →  FormularioActivity
                       └────────→  EstadoFinancieroActivity
```

## Estructura

```
app/src/main/
├── java/umg/edu/gt/progra2/
│   ├── ProyectoApp.kt               Se crea una sola vez al abrir la app y prepara todo
│   ├── Dependencias.kt              Guarda los servicios que comparten todas las pantallas
│   ├── LoginActivity.kt             Pantalla de inicio
│   ├── MainActivity.kt              Pantalla principal
│   ├── FormularioActivity.kt        Nuevo movimiento y edición
│   ├── EstadoFinancieroActivity.kt  Gráfica y resumen
│   ├── MovimientoAdapter.kt         Dibuja cada fila de la lista y la expande al tocarla
│   ├── GraficaBarrasView.kt         Gráfica de barras dibujada a mano, sin librerías
│   └── datos/                       Room: Entities, DAOs, AppDatabase y repositorios
├── res/
│   ├── layout/                      Un archivo XML por pantalla y por fila
│   └── drawable/caja.xml            Fondo de las cajas de ingresos y gastos
└── AndroidManifest.xml              Registra las pantallas, el login es la primera
```

Cada pantalla son dos archivos: el XML (cómo se ve) y la clase en Kotlin (qué hace), conectados con ViewBinding.

## Cómo se guardan los datos

El core define dos interfaces, `MovimientoRepository` y `UsuarioRepository`. En la carpeta `datos/` la app las cumple con Room:

```
ControlMovimiento (core) → MovimientoRepository (core) ← MovimientoRepositoryRoom (app) → MovimientoDao → SQLite
```

- `MovimientoEntity` y `UsuarioEntity` son las tablas. Son clases aparte de las del core porque el core no conoce Room.
- Las categorías se guardan por su nombre (`name`), que es el valor más estable de un enum.
- La base se abre una sola vez en `ProyectoApp` y las pantallas usan `Dependencias`.

El límite diario es solo un número opcional, y se guarda aparte en `SharedPreferences`.

## Cómo ejecutarla

1. Instalar el core con `mvn clean install` desde su carpeta.
2. Abrir esta carpeta en Android Studio y presionar **Sync Now**.
3. Elegir un emulador o un teléfono y ejecutar.

## Configuración principal

| Dato | Valor |
|---|---|
| Lenguaje | Kotlin |
| minSdk | 24 |
| Gradle | Kotlin DSL (`build.gradle.kts`) |
| Room | 2.8.5, con el plugin KSP |
| ViewBinding | Activado |
| Dependencia del core | `umg.edu.gt.progra2:control-gasto-ingreso-core:<version>` |

La dependencia declarada en `app/build.gradle.kts` debe tener la misma versión que el `pom.xml` del core, y `settings.gradle.kts` debe incluir `mavenLocal()` en `dependencyResolutionManagement`.

## Problemas frecuentes

| Síntoma | Causa |
|---|---|
| `Could not find ...control-gasto-ingreso-core` | Falta correr `mvn clean install` en el core, o falta `mavenLocal()` |
| Error por una versión incompatible de Kotlin | El core usa una versión de Kotlin más nueva que la app. Se iguala en el `pom.xml` |
| La app se cierra al abrir | Falta registrar `ProyectoApp` en el Manifest o alguna pantalla no está registrada |
| `Unresolved reference` a una función del core | Se cambió el core y no se reinstaló |
