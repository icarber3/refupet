# RefuPet — Estado de la implementación

Migración de RefuPet desde repositorios en memoria a Quarkus 3.8.2 + Jakarta Persistence/Hibernate ORM 6.4.4
sobre el esquema existente (MariaDB 10.2.38), con API REST. Última verificación: 2026-11-11, JDK 21.0.13.

> **Resumen:** la implementación está completa y verificada **hasta donde permiten las autorizaciones actuales**
> (validación del esquema y metadatos). Las operaciones JPA reales (SELECT/INSERT/UPDATE) **no se han ejecutado
> nunca contra las tablas**: ver [Pendiente de autorización](#pendiente-de-autorización).

## 1. Arquitectura

| Módulo | Contenido | Dependencias |
|---|---|---|
| `refupet-domain` | Entidades, VO, IDs, enums, excepciones, contratos de repositorio | Ninguna (solo JDK) — verificado con `dependency:tree` y `ArquitecturaDominioTest` |
| `refupet-application` | 41 handlers (24 de escritura con `@Transactional`), 4 servicios, Commands/Queries, DTOs | Domain + `jakarta.enterprise.cdi-api` + `jakarta.transaction-api` (solo anotaciones) — verificado con `dependency:tree` y `ArquitecturaApplicationTest` |
| `refupet-infrastructure` | 13 entidades JPA, 6 Data Mappers, 10 adaptadores JPA, productor de `Clock`; repositorios en memoria y `RefuPetAplicacion` (no son beans) | Domain, Application, Quarkus Hibernate ORM, JDBC MySQL, ArC |
| `refupet-api` | 6 recursos / 40 endpoints, 18 DTOs de entrada, política de errores; `application.properties` | Application, Infrastructure, RESTEasy Reactive Jackson |

## 2. Configuración

- Conexión: `quarkus.datasource.db-kind`, `username`, `password`, `jdbc.url` y `quarkus.devservices.enabled`
  copiadas literalmente de Streamflix (comprobado byte a byte). Motor/driver: MySQL Connector/J 8.3.0.
- `quarkus.datasource.db-version=5.5`: Connector/J informa el servidor como `MySQL 5.5.5-10.2.38-MariaDB`
  (major 5, minor 5; verificado por JDBC). Con el valor por defecto (8.0) Quarkus aborta el arranque.
- `quarkus.hibernate-orm.database.generation=validate` y `sql-load-script=no-file` (nada se crea ni se carga).
- `quarkus.hibernate-orm.log.sql=true` (útil para diagnóstico; conviene desactivarlo fuera de desarrollo).
- Primer administrador: `scripts/sql/01_primer_administrador.sql`, **manual y no ejecutado**. RefuPet no almacena contraseñas.

## 3. Pruebas (`mvn clean verify`, JDK 21) — 185/185 en verde

| Módulo | Pruebas | Qué cubren | ¿Usa la BD? |
|---|---|---|---|
| Domain | 47 | Reglas de las entidades, invariantes de los VO, IDs, validaciones, regla de arquitectura | No |
| Application | 8 | Validación de Commands/Queries, fechas de operación (no futuras, zona del reloj), regla de arquitectura | No |
| Infrastructure | 87 | 27 casos de uso E2E en memoria; 57 de mappers (ida y vuelta de los 6 agregados, VO, hijos, ciclo de vida); 3 de correspondencia con el esquema (todas las columnas mapeadas, generadas excluidas, CHAR(36)/TEXT/ENUM) | No |
| API | 43 | CDI (6), transacciones (11), política de errores (11), API REST por HTTP (15) | Solo el arranque (`validate`, metadatos) |

Además, verificaciones manuales sobre el jar de producción contra la base real (solo metadatos):
arranque en ~5 s con `validate`; los 40 endpoints registrados (responden 400 sin `X-Actor-Id` antes de acceder a datos);
`DELETE` → 405; ninguna sentencia SQL en el log.

Controles negativos realizados (y revertidos): sin `CHAR(36)` `validate` falla; una `@NamedQuery` rota impide el arranque.

## 4. Cobertura funcional verificada (con repositorios en memoria)

- Los 40 endpoints, por HTTP: flujo completo ingreso → clínica → cuarentena → solicitud → adopción → seguimiento → devolución;
  gestión de cuentas; rechazo de solicitudes; códigos 200/201 (+`Location`)/204/400/403/404/405/409.
- CDI: cada contrato se resuelve con su adaptador JPA; los repositorios en memoria y `RefuPetAplicacion` no son beans
  (solo se activan con el perfil de prueba `PerfilRepositoriosEnMemoria`); 45 beans de Application con proxy.
- Transacciones: los 24 handlers de escritura (y solo ellos) son transaccionales; las consultas no abren transacción.
  Los **3 casos de uso de dos agregados** (`StartQuarantine`, `MoveAnimalToGeneralPopulation`, `RegisterSterilization`)
  escriben en una sola transacción, y **los 3** se revierten completos si falla la segunda escritura.
- Errores: restricción única, FK o CHECK (SQLState 23) → 409 y texto demasiado largo (SQLState 22) → 400, sin exponer SQL ni nombres de restricciones.
- Las 19 consultas JPQL (`@NamedQuery`) se validan contra el modelo al arrancar y sus parámetros coinciden con los repositorios.

## Pendiente de autorización

Requieren acceso a las tablas de negocio de la base compartida (hoy no autorizado):

| Prueba | Operaciones | Qué verificaría |
|---|---|---|
| Lectura de los 10 repositorios JPA | SELECT | Ejecución real de las 19 consultas, `LEFT JOIN FETCH`, orden, conversión de ENUM/`tinyint(1)`/DATETIME |
| Escritura de los 10 repositorios (con rollback) | INSERT/UPDATE en transacción revertida | `merge`, cascada de observaciones/seguimientos, CHECK y únicas reales, columnas generadas |
| API contra la base real | SELECT (+ escrituras si se autorizan) | Flujo completo con persistencia real; requiere el primer administrador (script manual) |

## Riesgos y decisiones pendientes

| Riesgo | Situación | Recomendación |
|---|---|---|
| `merge()` | Cada `save` hace un SELECT previo (y carga la colección en Animal/Adopción). Correcto pero con un acceso extra por escritura. | Aceptable para el volumen académico. Optimizar solo si se mide un problema. |
| Sin bloqueo optimista | El esquema no tiene columna de versión: dos escrituras concurrentes del mismo agregado → gana la última. Las únicas de la BD sí protegen los invariantes críticos (una cuarentena activa, una solicitud pendiente, una adopción vigente, una esterilización por animal, email único). | **Decisión de esquema**: añadir `version` requiere modificar tablas. Recomiendo aceptarlo en el alcance actual. |
| `DATETIME` | Sin fracciones: los `LocalDateTime.now()` se truncan al guardar; empates del mismo segundo se ordenan por id. Zona: `LocalDateTime` no lleva zona; dominio y base usan la hora local del JVM (Connector/J `connectionTimeZone=LOCAL` por defecto). | Fijar la zona del JVM en despliegue (p. ej. `-Duser.timezone=America/Bogota`) para que todas las instancias coincidan. |
| Dialecto | `MySQLDialect` en modo 5.5 (HHH000511: versión no soportada) sobre MariaDB 10.2. Sin incidencias en `validate` ni en el análisis de las 19 consultas. | Mantener. No usar paginación compleja, funciones de ventana ni JSON sin probarlo. Cambiar a driver/dialecto MariaDB alteraría el driver de referencia. |
| JPQL/repositorios sin datos reales | Validados en estructura (arranque + parámetros), no en ejecución. | Autorizar al menos las pruebas de lectura. |
| Longitudes de texto | El dominio no limita longitudes y el esquema sí (`VARCHAR(100/150/254/255/30)`). Con modo estricto la BD rechaza el dato (ahora → 400); sin modo estricto MariaDB lo **truncaría** en silencio (no se verificó `sql_mode`). | **Decisión de requisitos**: validar longitudes en la API o en los Commands según el esquema. |
| Restricciones únicas, FK y CHECK | El dominio las respeta; si la BD las rechaza (concurrencia), la transacción se revierte y la API responde 409 genérico. Los 14 CHECK verificados son compatibles con el dominio. | Ninguna acción. |
| `X-Actor-Id` | Provisional: **no autentica y permite suplantar** a cualquier usuario cuyo id se conozca. | **Decisión de seguridad**: sustituir por autenticación real antes de cualquier uso fuera del entorno académico. |
| `DisponibilidadAdopcionService` | Consulta las adopciones de cada animal en población general (1 + N consultas). Lógica de Application existente. | Aceptable con pocos animales; optimizar con una consulta dedicada si crece. |
| Consultas fuera de petición HTTP | Las consultas usan el `EntityManager` sin transacción; funcionan dentro de una petición REST. | Si se añaden tareas programadas, activar contexto de petición o transacción. |

## Cómo ejecutar

```
set JAVA_HOME=<JDK 21>
<maven>/bin/mvn clean verify
java -jar refupet-api/target/quarkus-app/quarkus-run.jar
```
