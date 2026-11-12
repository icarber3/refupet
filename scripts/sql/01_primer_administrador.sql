-- =====================================================================
-- RefuPet - Inicialización del primer administrador
-- =====================================================================
-- Script para REVISAR y EJECUTAR MANUALMENTE. La aplicación no lo ejecuta:
-- está fuera de src/main/resources y Quarkus tiene sql-load-script=no-file.
--
-- Por qué hace falta: toda cuenta nueva nace PENDIENTE_APROBACION y solo un
-- administrador ACTIVO puede aprobarla (reglas 26, 30 y 32). Es el equivalente
-- en base de datos de RefuPetAplicacion.inicializarAdministrador(nombre, email),
-- que crea un Administrador con estado ACTIVA y fecha de registro actual.
--
-- Contraseñas: RefuPet NO almacena contraseñas. La tabla `usuario` no tiene
-- columna de contraseña y el dominio no la modela (sin autenticación avanzada).
-- Por eso este script no genera ningún hash.
--
-- Restricciones del esquema que respeta (verificadas en information_schema):
--   id CHAR(36) (UUID en texto, como UUID.randomUUID().toString() del dominio)
--   rol = 'ADMINISTRADOR' -> telefono y direccion NULL (ck_usuario_contacto_solicitante)
--   nombre no vacío, VARCHAR(150) (ck_usuario_nombre)
--   email único, VARCHAR(254), formato '%_@_%._%' (uq_usuario_email, ck_usuario_email)
--   El dominio guarda el email en minúsculas y sin espacios (Email.of): escríbelo así.
--
-- Uso: edita los dos valores siguientes y ejecuta el script completo.
-- Es idempotente: si el email ya existe, no inserta nada.
-- =====================================================================

SET @nombre_admin = 'Nombre del administrador';
SET @email_admin  = 'admin@ejemplo.org';

INSERT INTO usuario (id, rol, nombre, email, estado_cuenta, fecha_registro, telefono, direccion)
SELECT UUID(), 'ADMINISTRADOR', TRIM(@nombre_admin), LOWER(TRIM(@email_admin)), 'ACTIVA', NOW(), NULL, NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = LOWER(TRIM(@email_admin)));

-- Id del administrador: úsalo como valor de la cabecera X-Actor-Id
SELECT id, rol, nombre, email, estado_cuenta, fecha_registro
FROM usuario
WHERE email = LOWER(TRIM(@email_admin));
