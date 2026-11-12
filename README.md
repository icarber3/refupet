# RefuPet — Sistema de Gestión de un Refugio de Animales

Proyecto académico de POO en Java 21 + Maven. Usa una arquitectura hexagonal/clean multimódulo,
tomando como referencia el proyecto Streamflix.

> **Migración en curso** a Quarkus 3.8.2 + Jakarta Persistence/Hibernate ORM + API REST (por etapas).
> Estado actual: Etapa 6 — persistencia JPA (validada contra el esquema), CDI, transacciones y API REST
> implementadas y probadas (185 pruebas). Las operaciones JPA aún no se han ejecutado contra las tablas reales.
> Estado detallado, pruebas y riesgos: [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md).

## Módulos

```
refupet/
├── refupet-domain          → entidades, Value Objects, IDs, enums, excepciones, puertos (repositorios)
├── refupet-application     → Commands, Queries, Handlers, DTOs y servicios de coordinación
├── refupet-infrastructure  → repositorios (en memoria; adaptadores JPA en construcción) y ensamblaje (RefuPetAplicacion)
└── refupet-api             → aplicación Quarkus ejecutable: paquete api (REST) y application.properties
```

Las dependencias apuntan hacia el dominio: `api → application/infrastructure → domain`.
El dominio no depende de ninguna librería externa (ni JPA, ni Hibernate, ni Quarkus).
Application solo usa las APIs estándar de Jakarta CDI y Transactions (anotaciones), nunca Quarkus ni HTTP.

Paquete raíz: `co.ufps.refupet`.

| Capa | Paquetes |
|---|---|
| domain | `entities`, `valueobjects`, `ids`, `enums`, `exceptions`, `repositories` |
| application | `commands.<módulo>`, `queries.<módulo>`, `handlers.<módulo>`, `dto`, `services` |
| infrastructure | `persistence.memory`, `config` |

Módulos funcionales: `usuario`, `animal`, `clinica`, `cuarentena`, `adopcion` y `seguridad` (control de permisos).

## Compilar y probar

Requiere **JDK 21** (Quarkus 3.8.2 no se ejecuta en este proyecto con JDK 23 o posterior).

```
set JAVA_HOME=<ruta del JDK 21>
mvn clean verify
```

Ejecutar la aplicación Quarkus empaquetada:

```
java -jar refupet-api/target/quarkus-app/quarkus-run.jar
```

## Migración a Quarkus y persistencia relacional

- **Conexión**: las propiedades `quarkus.datasource.*` y `quarkus.devservices.enabled` de
  `refupet-api/src/main/resources/application.properties` se copiaron literalmente de
  `streamflix-api/src/main/resources/application.properties` (mismo motor, driver, URL y credenciales). No se modifican.
- **Esquema**: la base de datos ya existe. Hibernate solo la **valida**
  (`quarkus.hibernate-orm.database.generation=validate`); nunca `update`, `create` ni `drop-and-create`.
  Ningún script SQL se carga al arrancar (`sql-load-script=no-file`).
- **`quarkus.datasource.db-version=5.5`**: el servidor es MariaDB 10.2.38, pero Connector/J 8.3.0 lo
  informa como `MySQL 5.5.5-10.2.38-MariaDB` (versión mayor 5, menor 5). Con el valor por defecto (8.0)
  Quarkus aborta el arranque al comprobar la versión.
- **Primer administrador**: `scripts/sql/01_primer_administrador.sql` se revisa y ejecuta **manualmente**
  (no hay endpoint ni tarea automática). RefuPet no almacena contraseñas.
- **Identificación del actor (provisional)**: la API identificará a quien ejecuta cada operación con la
  cabecera HTTP `X-Actor-Id` (id del usuario). Es una **solución temporal**: la cabecera por sí sola
  **no autentica** ni impide la suplantación de identidad; cualquiera que conozca un id puede usarlo.
  Debe sustituirse por un mecanismo de autenticación real antes de un uso fuera del entorno académico.

## API REST

Paquete `co.ufps.refupet.api` (módulo `refupet-api`): recursos en `api.recursos`, cuerpos de entrada en `api.dto`
(records propios que se convierten en Commands), errores en `api.error`. Las respuestas reutilizan los DTOs de Application.
Todas las rutas usan JSON y, salvo `POST /api/solicitantes`, requieren la cabecera `X-Actor-Id` (provisional, ver arriba).
No hay operaciones `DELETE`: el sistema no contempla eliminaciones.

| Recurso | Endpoints |
|---|---|
| `/api/usuarios` | `POST /` (201) · `GET /pendientes` · `GET /{id}` · `PUT /{id}` · `POST /{id}/aprobacion` · `POST /{id}/suspension` · `POST /{id}/activacion` |
| `/api/solicitantes` | `POST /` (registro público, 201) · `GET /{id}` |
| `/api/animales` | `POST /` (201) · `GET /` · `GET /disponibles` · `GET /en-cuarentena` · `GET /{id}` · `GET /{id}/ingreso` · `GET`/`POST /{id}/observaciones` · `PUT /{id}/estado-salud` · `POST /{id}/evaluaciones-clinicas` · `POST /{id}/vacunaciones` · `POST /{id}/desparasitaciones` · `POST /{id}/tratamientos` · `POST /{id}/esterilizacion` · `GET /{id}/historial-clinico` · `POST /{id}/cuarentenas` (201) · `GET /{id}/cuarentena` (204 si nunca estuvo en cuarentena) |
| `/api/cuarentenas` | `GET /{id}` · `GET /{id}/periodo-minimo` · `POST /{id}/autorizacion-salida` · `POST /{id}/traslado-poblacion-general` |
| `/api/solicitudes-adopcion` | `POST /` (201) · `GET /?estado=` · `GET /{id}` · `POST /{id}/evaluacion-compatibilidad` · `POST /{id}/aprobacion` · `POST /{id}/rechazo` |
| `/api/adopciones` | `POST /` (201) · `GET /{id}` · `POST /{id}/seguimientos` (201) · `POST /{id}/devolucion` |

Errores (`{"status", "error", "mensaje"}`): **400** entrada inválida (falta `X-Actor-Id` o el cuerpo, JSON o fecha
mal formados, datos inválidos) · **403** el rol no tiene permiso · **404** recurso o actor inexistente · **409** estado
inválido, email repetido o violación de una restricción de la base de datos (sin exponer su nombre ni el SQL) · **500**
error inesperado (el detalle solo queda en el log). Las fechas se intercambian en formato ISO (`2026-01-31`).

## Decisiones de diseño

### Herencia, abstracción y polimorfismo
- **`Usuario` (abstracta)** con las subclases `Administrador`, `Veterinario`, `Voluntario` y `Solicitante`.
  Cada perfil redefine `getRol()` y `operacionesPermitidas()`. Por eso `verificarPermiso(Operacion)`
  aplica la regla 39 igual para todos, sin `if` por rol.
- **`RegistroClinico` (abstracta)** con las subclases `EvaluacionClinica`, `Vacunacion`, `Desparasitacion`,
  `Tratamiento` y `Esterilizacion`. El historial clínico es una lista polimórfica ordenada por fecha.
- **`Identificador` (abstracta)** es la base de los 12 IDs. Centraliza `value()`, `equals()` y `hashCode()`.
  Cada ID conserva su constructor privado y sus métodos `generate()`/`of()`, y un `AnimalId` nunca es igual a un `UsuarioId`.

### Entidad, entidad interna o Value Object
| Concepto | Modelo | Motivo |
|---|---|---|
| ObservacionComportamiento | entidad interna de `Animal` | Tiene identidad y trazabilidad, pero no existe sin su animal (como `Profile` en `Account` de Streamflix). |
| RegistroIngreso | Value Object en `Animal` | Ocurre una vez y no cambia. |
| EstadoSalud | Value Object | El enunciado no define un catálogo cerrado de estados, así que no se inventa un enum. |
| PeriodoCuarentena | Value Object | Tiene reglas propias: fecha mínima de salida y si el periodo se cumplió. |
| EvaluacionCompatibilidad | Value Object en `SolicitudAdopcion` | El enunciado no define criterios, así que solo se registra el resultado y las observaciones. |
| SeguimientoAdopcion | entidad interna de `Adopcion` | Puede haber varios por adopción, cada uno con datos propios. |
| Devolucion | Value Object en `Adopcion` | Hay como máximo una por adopción. |
| Especie, RolUsuario | enum | Son conjuntos cerrados que no se administran. |
| Historial clínico | vista de lectura (`HistorialClinicoDTO`) | No es un agregado, por eso no tiene repositorio propio. |

### Reglas que no se inventaron
- **Periodo mínimo de cuarentena.** El enunciado no fija un número de días, así que se indica al iniciar cada cuarentena (`diasMinimos`).
- **Ubicación inicial.** Un animal recién registrado no tiene ubicación asignada. Pasa a `CUARENTENA` al iniciar la cuarentena
  y a `POBLACION_GENERAL` cuando el veterinario autoriza la salida y se registra el paso.
- **Disponibilidad para adopción.** Un animal está disponible si está en población general y no tiene una adopción vigente.
  Si se registra una devolución, vuelve a estar disponible.

### Reglas de consistencia añadidas
Estas reglas evitan estados inconsistentes:
- Las cuentas nuevas nacen en `PENDIENTE_APROBACION` y solo operan cuando están `ACTIVA`.
- Una cuenta suspendida se *activa*; una pendiente se *aprueba*.
- El email es único entre todos los usuarios.
- Un animal no puede tener dos cuarentenas activas ni registrarse como esterilizado dos veces.
- Una solicitud solo se aprueba o rechaza mientras está `PENDIENTE`.
- Un solicitante no puede tener dos solicitudes pendientes para el mismo animal.
- No se aceptan fechas futuras en los registros.

### Diferencias con Streamflix
> Las dos primeras diferencias describen la versión en memoria y se revisarán al terminar la migración.

- **Sin Quarkus/CDI.** El alcance excluye frameworks. Los handlers siguen recibiendo sus dependencias por constructor
  y `RefuPetAplicacion` hace explícito el ensamblaje que en Streamflix hace el contenedor.
- **Sin JPA ni mapeadores.** Los repositorios en memoria guardan directamente los objetos de dominio
  en un `LinkedHashMap` (base común `RepositorioEnMemoria`).
- **Excepciones "no encontrado" en el dominio.** Heredan de `ResourceNotFoundException`, que a su vez hereda de `DomainException`.
- **`java.time.Clock` inyectado.** Permite probar reglas que dependen del tiempo, como el periodo mínimo de cuarentena.

### Arranque
Toda cuenta nueva necesita la aprobación de un administrador activo, así que el primer
administrador se carga con `RefuPetAplicacion.inicializarAdministrador(nombre, email)`.

## Pruebas
- `refupet-domain/src/test`: reglas de las entidades, como transiciones de estado, permisos, periodo de cuarentena y adopción;
  invariantes de los Value Objects e identificadores; regla de arquitectura (sin dependencias externas).
- `refupet-application/src/test`: validación de Commands/Queries, fechas de operación y regla de arquitectura.
- `refupet-infrastructure/src/test`: casos de uso de extremo a extremo con los repositorios en memoria reales
  (todos los escenarios del apartado 16 del enunciado), Data Mappers JPA y correspondencia del mapeo con el esquema.
- `refupet-api/src/test`: CDI, transacciones (incluida la atomicidad de los casos de uso con dos agregados),
  política de errores HTTP y la API REST por HTTP, con los repositorios en memoria. Ninguna prueba accede a las
  tablas de la base de datos; Quarkus solo valida el esquema al arrancar.
