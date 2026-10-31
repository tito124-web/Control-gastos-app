# Control de ingresos y gastos

Aplicación Android para llevar el control del dinero personal: registrar ingresos y gastos, ver cuánto dinero queda disponible, ponerse un límite diario y revisar un estado financiero por día y por mes.

El proyecto está dividido en dos partes que trabajan juntas: una librería con las reglas del negocio (`core`) y la aplicación con las pantallas (`ui`).

## Qué puede hacer la app

- Entrar con un nombre de usuario. La app es para una sola persona.
- Registrar ingresos y gastos con descripción, monto, categoría y fecha.
- Ver el balance disponible, el total de ingresos y el total de gastos.
- Ver la lista de movimientos, abrir el detalle de cada uno, editarlo o eliminarlo deslizando hacia la izquierda.
- Poner un límite diario de gastos y recibir un aviso cuando se acerca o se supera. El límite es opcional.
- Consultar un estado financiero con una gráfica de ingresos contra gastos por día y por mes.
- Guardar todo en el teléfono, así los datos siguen ahí al cerrar la app.

## Estructura del repositorio

```
control-gasto-ingreso/
├── README.md                          Este archivo
├── .gitignore
├── docs/
│   └── EXPLICACION_UI.md              Qué se hizo en la interfaz y por qué
├── control-gasto-ingreso-core/        Librería Kotlin (Maven): modelo y reglas
│   └── README.md
└── control-gasto-ingreso-ui/          App Android (Gradle): pantallas y base de datos
    └── README.md
```

## Cómo se conectan las dos partes

La app no copia el código del core: lo usa como una librería.

```
control-gasto-ingreso-core          Repositorio local de Maven           control-gasto-ingreso-ui
   (IntelliJ + Maven)                     (~/.m2)                          (Android Studio + Gradle)

 mvn clean install  ───────────►  control-gasto-ingreso-core-X.jar  ◄───────  mavenLocal()
```

1. `mvn clean install` compila el core y copia el `.jar` a la carpeta `.m2` de la computadora.
2. La app lo declara como dependencia y Gradle lo busca ahí con `mavenLocal()`.

Como `mavenLocal()` solo existe en la computadora donde se instaló, quien clone el repositorio debe hacer el paso 1 antes de abrir la app.

## Cómo ejecutarlo la primera vez

Requisitos: JDK 11, Maven, Android Studio y un emulador o un teléfono con depuración USB.

1. Instalar el core, desde la carpeta `control-gasto-ingreso-core`:

   ```bash
   mvn clean install
   ```

   Debe terminar con `BUILD SUCCESS`.

2. Abrir la carpeta `control-gasto-ingreso-ui` en Android Studio y presionar **Sync Now**.
3. Ejecutar la app con el botón verde.

Cada vez que se cambie algo en el core hay que repetir el paso 1 y volver a sincronizar la app.

## Tecnologías

| Parte | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Core | Maven, JDK 11, Kotlin puro (sin dependencias de Android) |
| UI | Android con Views y ViewBinding, Gradle (Kotlin DSL), minSdk 24 |
| Base de datos | SQLite a través de Room 2.8.5 con KSP |
| Control de versiones | Git y GitHub, un solo repositorio con dos carpetas |

## Autor

Josué David Martínez Ortíz  0905-25-21930
