# control-gasto-ingreso-core

Librería en Kotlin puro con el modelo de datos y las reglas del negocio de la app de ingresos y gastos. No conoce Android ni ninguna base de datos: por eso se puede probar sin teléfono y la puede usar cualquier interfaz.

## Qué contiene

```
src/main/kotlin/umg/edu/gt/progra2/
├── modelo/
│   ├── Tipo.kt                   INGRESO o GASTO
│   ├── Categoria.kt              Categorías, cada una sabe a qué tipo pertenece
│   ├── Movimiento.kt             Un ingreso o un gasto (descripción, monto, categoría, fecha)
│   ├── Usuario.kt                El único usuario de la app (nombre)
│   └── ResumenPeriodo.kt         Ingresos, gastos y balance de un día o de un mes
├── datos/
│   ├── MovimientoRepository.kt   Interface: qué se necesita para guardar y leer movimientos
│   └── UsuarioRepository.kt      Interface: qué se necesita para guardar y leer el usuario
└── servicio/
    ├── ControlMovimiento.kt      Reglas de los movimientos, totales, balance y límite
    ├── ControlUsuario.kt         Reglas del login de un solo usuario
    └── (formatearQuetzales)      Convierte un número en texto, por ejemplo "Q 45.50"
```

## Qué reglas aplica

- La descripción no puede estar vacía.
- El monto debe ser mayor que 0.
- La fecha debe tener el formato `yyyy-MM-dd`.
- El nombre de usuario no puede estar vacío ni pasar de 30 caracteres.
- Solo puede existir un usuario.
- El límite diario es opcional: si no se pone, no afecta en nada. Si se pone, debe ser mayor que 0.
- El balance es el total de ingresos menos el total de gastos. El monto siempre se guarda positivo y es el tipo de la categoría quien decide si suma o resta.

## Por qué hay interfaces

Los servicios necesitan pedir "guarda esto" o "dame la lista", pero el core no sabe dónde se guardan los datos. Por eso declara lo que necesita en las interfaces `MovimientoRepository` y `UsuarioRepository`, y la aplicación las cumple con su propia base de datos.

```
ControlMovimiento  →  MovimientoRepository (interface, está aquí)  ←  implementación en la app (Room)
```

Así los servicios se pueden usar con cualquier forma de guardar datos sin cambiarlos.

## Cómo se instala

```bash
mvn clean install
```

Esto compila el código, genera el `.jar` y lo copia a `~/.m2`. Las coordenadas son las del `pom.xml`:

```
groupId:    umg.edu.gt.progra2
artifactId: control-gasto-ingreso-core
version:    la que indique el pom.xml
```

En la app se declara así, con la misma versión del `pom.xml`:

```kotlin
implementation("umg.edu.gt.progra2:control-gasto-ingreso-core:<version>")
```

## Requisitos

- JDK 11
- Maven
- Kotlin 2.2 (debe ser igual o menor a la versión de Kotlin de la app)

## Cuidado al cambiar el core

Después de cualquier cambio hay que reinstalarlo con `mvn clean install` y sincronizar Gradle en la app. Si se sube la versión en el `pom.xml`, también se cambia en la app.
