package co.ufps.refupet.integracion;

import co.ufps.refupet.domain.entities.Administrador;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de la API REST de extremo a extremo por HTTP: recursos → handlers de Application → dominio →
 * repositorios EN MEMORIA (perfil {@link PerfilRepositoriosEnMemoria}). No se ejecuta ninguna operación
 * sobre las tablas de la base de datos; Quarkus solo valida el esquema al arrancar.
 */
@QuarkusTest
@TestProfile(PerfilRepositoriosEnMemoria.class)
class ApiRestTest {

    private static final String ACTOR = "X-Actor-Id";

    @Inject IUsuarioRepository usuarios;
    @Inject RegistroTransacciones registro;

    private String adminId;

    // ── Utilidades ──

    private static String email(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8) + "@refupet.co";
    }

    private static RequestSpecification como(String actorId) {
        return given().header(ACTOR, actorId).contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    private static Map<String, Object> json(Object... claveValor) {
        Map<String, Object> mapa = new HashMap<>();
        for (int i = 0; i < claveValor.length; i += 2) {
            mapa.put((String) claveValor[i], claveValor[i + 1]);
        }
        return mapa;
    }

    private static void assertError(ValidatableResponse respuesta, int status, String categoria) {
        respuesta.statusCode(status)
                .contentType(ContentType.JSON)
                .body("status", equalTo(status))
                .body("error", equalTo(categoria))
                .body("mensaje", not(emptyOrNullString()));
    }

    /** Crea una cuenta con el rol indicado y la aprueba; devuelve su id. */
    private String usuarioActivo(String rol) {
        String id = como(adminId).body(json("nombre", "Persona " + rol, "email", email(rol.toLowerCase()), "rol", rol))
                .post("/api/usuarios").then().statusCode(201).extract().path("usuarioId");
        como(adminId).post("/api/usuarios/" + id + "/aprobacion").then().statusCode(200).body("estadoCuenta", equalTo("ACTIVA"));
        return id;
    }

    private String registrarAnimal(String voluntarioId) {
        return como(voluntarioId).body(json("nombre", "Luna", "especie", "gato"))
                .post("/api/animales").then().statusCode(201).extract().path("animalId");
    }

    @BeforeEach
    void administradorInicial() {
        registro.reiniciar();
        Administrador admin = Administrador.of(UsuarioId.generate(), "Admin", Email.of(email("admin")),
                EstadoCuenta.ACTIVA, LocalDateTime.now());
        usuarios.save(admin);
        adminId = admin.getUsuarioId().value();
    }

    // ── Flujo completo ──

    @Test
    void flujoCompletoDesdeElIngresoHastaLaDevolucion() {
        String veterinario = usuarioActivo("VETERINARIO");
        String voluntario = usuarioActivo("VOLUNTARIO");

        // Ingreso y observaciones (voluntario)
        String animal = como(voluntario)
                .body(json("nombre", "Firulais", "especie", "PERRO", "raza", "Criollo",
                        "fechaIngreso", LocalDate.now().minusDays(10).toString(), "procedencia", "Rescate", "condicionInicial", "Desnutrido"))
                .post("/api/animales")
                .then().statusCode(201)
                .header("Location", containsString("/api/animales/"))
                .body("nombre", equalTo("Firulais"))
                .body("especie", equalTo("PERRO"))
                .body("ubicacion", nullValue())
                .extract().path("animalId");
        como(voluntario).get("/api/animales/" + animal).then().statusCode(200).body("animalId", equalTo(animal));
        como(voluntario).get("/api/animales").then().statusCode(200).body("animalId", hasItem(animal));
        como(voluntario).get("/api/animales/" + animal + "/ingreso").then().statusCode(200)
                .body("procedencia", equalTo("Rescate")).body("registradoPorId", equalTo(voluntario));
        como(voluntario).body(json("descripcion", "Tímido al principio"))
                .post("/api/animales/" + animal + "/observaciones").then().statusCode(201)
                .body("descripcion", equalTo("Tímido al principio")).body("registradoPorId", equalTo(voluntario));
        como(voluntario).get("/api/animales/" + animal + "/observaciones").then().statusCode(200).body("$", hasSize(1));
        como(veterinario).get("/api/animales/" + animal + "/cuarentena").then().statusCode(204);

        // Atención veterinaria
        como(veterinario).body(json("estadoSalud", "Estable")).put("/api/animales/" + animal + "/estado-salud")
                .then().statusCode(200).body("estadoSalud", equalTo("Estable"));
        como(veterinario).body(json("hallazgos", "Mucosas pálidas", "diagnostico", "Anemia leve"))
                .post("/api/animales/" + animal + "/evaluaciones-clinicas").then().statusCode(201).body("hallazgos", equalTo("Mucosas pálidas"));
        como(veterinario).body(json("vacuna", "Rabia")).post("/api/animales/" + animal + "/vacunaciones")
                .then().statusCode(201).body("tipo", equalTo("VACUNACION"));
        como(veterinario).body(json("producto", "Ivermectina")).post("/api/animales/" + animal + "/desparasitaciones")
                .then().statusCode(201).body("tipo", equalTo("DESPARASITACION"));
        como(veterinario).body(json("descripcion", "Antibiótico", "indicaciones", "Cada 12 h"))
                .post("/api/animales/" + animal + "/tratamientos").then().statusCode(201).body("descripcion", equalTo("Antibiótico"));
        como(veterinario).post("/api/animales/" + animal + "/esterilizacion").then().statusCode(201).body("tipo", equalTo("ESTERILIZACION"));
        como(veterinario).get("/api/animales/" + animal + "/historial-clinico").then().statusCode(200)
                .body("registros", hasSize(5)).body("esterilizado", equalTo(true)).body("estadoSalud", equalTo("Estable"));

        // Cuarentena
        String cuarentena = como(veterinario)
                .body(json("fechaInicio", LocalDate.now().minusDays(3).toString(), "diasMinimos", 2))
                .post("/api/animales/" + animal + "/cuarentenas")
                .then().statusCode(201).header("Location", containsString("/api/cuarentenas/"))
                .body("estado", equalTo("EN_CURSO")).body("periodoMinimoCumplido", equalTo(true))
                .extract().path("cuarentenaId");
        como(veterinario).get("/api/animales/" + animal + "/cuarentena").then().statusCode(200).body("cuarentenaId", equalTo(cuarentena));
        como(veterinario).get("/api/animales/en-cuarentena").then().statusCode(200).body("cuarentenaId", hasItem(cuarentena));
        como(veterinario).get("/api/cuarentenas/" + cuarentena).then().statusCode(200).body("animalId", equalTo(animal));
        como(veterinario).get("/api/cuarentenas/" + cuarentena + "/periodo-minimo").then().statusCode(200)
                .body("fechaMinimaSalida", equalTo(LocalDate.now().minusDays(1).toString())).body("diasRestantes", equalTo(0));
        como(veterinario).post("/api/cuarentenas/" + cuarentena + "/autorizacion-salida").then().statusCode(200)
                .body("estado", equalTo("SALIDA_AUTORIZADA")).body("salidaAutorizadaPorId", equalTo(veterinario));
        como(veterinario).post("/api/cuarentenas/" + cuarentena + "/traslado-poblacion-general").then().statusCode(200)
                .body("ubicacion", equalTo("POBLACION_GENERAL"));

        // Solicitante: autorregistro (sin actor) y aprobación
        String solicitante = given().contentType(ContentType.JSON)
                .body(json("nombre", "Sara", "email", email("sara"), "telefono", "3001234567", "direccion", "Calle 1"))
                .post("/api/solicitantes")
                .then().statusCode(201).header("Location", containsString("/api/solicitantes/"))
                .body("estadoCuenta", equalTo("PENDIENTE_APROBACION")).body("telefono", equalTo("3001234567"))
                .extract().path("usuarioId");
        como(adminId).get("/api/usuarios/pendientes").then().statusCode(200).body("usuarioId", hasItem(solicitante));
        como(adminId).post("/api/usuarios/" + solicitante + "/aprobacion").then().statusCode(200);
        como(adminId).get("/api/solicitantes/" + solicitante).then().statusCode(200).body("direccion", equalTo("Calle 1"));

        // Solicitud de adopción
        como(solicitante).get("/api/animales/disponibles").then().statusCode(200).body("animalId", hasItem(animal));
        String solicitud = como(solicitante).body(json("animalId", animal, "comentarios", "Tengo patio"))
                .post("/api/solicitudes-adopcion")
                .then().statusCode(201).header("Location", containsString("/api/solicitudes-adopcion/"))
                .body("estado", equalTo("PENDIENTE")).extract().path("solicitudId");
        como(adminId).queryParam("estado", "PENDIENTE").get("/api/solicitudes-adopcion")
                .then().statusCode(200).body("solicitudId", hasItem(solicitud));
        como(adminId).get("/api/solicitudes-adopcion/" + solicitud).then().statusCode(200)
                .body("solicitud.solicitudId", equalTo(solicitud))
                .body("solicitante.usuarioId", equalTo(solicitante))
                .body("animal.animalId", equalTo(animal));
        como(adminId).body(json("compatible", true, "observaciones", "Hogar adecuado"))
                .post("/api/solicitudes-adopcion/" + solicitud + "/evaluacion-compatibilidad")
                .then().statusCode(200).body("compatible", equalTo(true));
        como(adminId).post("/api/solicitudes-adopcion/" + solicitud + "/aprobacion").then().statusCode(200)
                .body("estado", equalTo("APROBADA")).body("decididaPorId", equalTo(adminId));

        // Adopción, seguimiento y devolución
        String adopcion = como(adminId).body(json("solicitudId", solicitud))
                .post("/api/adopciones")
                .then().statusCode(201).header("Location", containsString("/api/adopciones/"))
                .body("estado", equalTo("FORMALIZADA")).extract().path("adopcionId");
        como(adminId).get("/api/animales/disponibles").then().statusCode(200).body("animalId", not(hasItem(animal)));
        como(adminId).body(json("observaciones", "Se adapta bien"))
                .post("/api/adopciones/" + adopcion + "/seguimientos").then().statusCode(201).body("observaciones", equalTo("Se adapta bien"));
        como(adminId).body(json("motivo", "Alergias en la familia"))
                .post("/api/adopciones/" + adopcion + "/devolucion").then().statusCode(200)
                .body("estado", equalTo("CON_DEVOLUCION")).body("motivoDevolucion", equalTo("Alergias en la familia"));
        como(adminId).get("/api/adopciones/" + adopcion).then().statusCode(200).body("seguimientos", hasSize(1));
        como(adminId).get("/api/animales/disponibles").then().statusCode(200).body("animalId", hasItem(animal));
    }

    @Test
    void gestionDeCuentas() {
        String id = como(adminId).body(json("nombre", "Valeria", "email", email("valeria"), "rol", "VOLUNTARIO"))
                .post("/api/usuarios")
                .then().statusCode(201).header("Location", containsString("/api/usuarios/"))
                .body("rol", equalTo("VOLUNTARIO")).body("estadoCuenta", equalTo("PENDIENTE_APROBACION"))
                .extract().path("usuarioId");
        String nuevoEmail = email("valeria.r");
        como(adminId).body(json("nombre", "Valeria Ruiz", "email", nuevoEmail)).put("/api/usuarios/" + id)
                .then().statusCode(200).body("nombre", equalTo("Valeria Ruiz")).body("email", equalTo(nuevoEmail));
        como(adminId).get("/api/usuarios/" + id).then().statusCode(200).body("usuarioId", equalTo(id)).body("fechaRegistro", notNullValue());
        como(adminId).post("/api/usuarios/" + id + "/aprobacion").then().statusCode(200).body("estadoCuenta", equalTo("ACTIVA"));
        como(adminId).post("/api/usuarios/" + id + "/suspension").then().statusCode(200).body("estadoCuenta", equalTo("SUSPENDIDA"));
        como(adminId).post("/api/usuarios/" + id + "/activacion").then().statusCode(200).body("estadoCuenta", equalTo("ACTIVA"));
    }

    @Test
    void rechazoDeUnaSolicitud() {
        String veterinario = usuarioActivo("VETERINARIO");
        String voluntario = usuarioActivo("VOLUNTARIO");
        String solicitante = usuarioActivo("SOLICITANTE");
        String animal = registrarAnimal(voluntario);
        String cuarentena = como(veterinario).body(json("fechaInicio", LocalDate.now().minusDays(2).toString(), "diasMinimos", 1))
                .post("/api/animales/" + animal + "/cuarentenas").then().statusCode(201).extract().path("cuarentenaId");
        como(veterinario).post("/api/cuarentenas/" + cuarentena + "/autorizacion-salida").then().statusCode(200);
        como(veterinario).post("/api/cuarentenas/" + cuarentena + "/traslado-poblacion-general").then().statusCode(200);
        String solicitud = como(solicitante).body(json("animalId", animal)).post("/api/solicitudes-adopcion")
                .then().statusCode(201).extract().path("solicitudId");
        como(adminId).body(json("motivo", "No cumple requisitos"))
                .post("/api/solicitudes-adopcion/" + solicitud + "/rechazo").then().statusCode(200)
                .body("estado", equalTo("RECHAZADA")).body("motivoRechazo", equalTo("No cumple requisitos"));
        como(adminId).queryParam("estado", "RECHAZADA").get("/api/solicitudes-adopcion")
                .then().statusCode(200).body("solicitudId", hasItem(solicitud));
    }

    // ── Errores ──

    @Nested
    class Errores {

        @Test
        void sinCabeceraDeActorEs400() {
            assertError(given().accept(ContentType.JSON).get("/api/animales").then(), 400, "ENTRADA_INVALIDA");
            given().accept(ContentType.JSON).get("/api/animales").then().body("mensaje", containsString("X-Actor-Id"));
        }

        @Test
        void sinCuerpoEs400() {
            assertError(como(adminId).post("/api/usuarios").then(), 400, "ENTRADA_INVALIDA");
        }

        @Test
        void jsonMalFormadoEs400() {
            assertError(como(adminId).body("{\"nombre\": ").post("/api/usuarios").then(), 400, "ENTRADA_INVALIDA");
        }

        @Test
        void fechaConFormatoIncorrectoEs400() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            assertError(como(voluntario).body(json("nombre", "X", "especie", "PERRO", "fechaIngreso", "10/01/2026"))
                    .post("/api/animales").then(), 400, "ENTRADA_INVALIDA");
        }

        @Test
        void campoObligatorioAusenteOValorInvalidoEs400() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            assertError(como(voluntario).body(json("especie", "PERRO")).post("/api/animales").then(), 400, "ENTRADA_INVALIDA");
            assertError(como(voluntario).body(json("nombre", "X", "especie", "DRAGON")).post("/api/animales").then(), 400, "ENTRADA_INVALIDA");
            assertError(como(adminId).body(json("nombre", "X", "email", "sin-arroba", "rol", "VOLUNTARIO"))
                    .post("/api/usuarios").then(), 400, "ENTRADA_INVALIDA");
            String veterinario = usuarioActivo("VETERINARIO");
            String animal = registrarAnimal(voluntario);
            assertError(como(veterinario).body(json("fechaInicio", LocalDate.now().toString()))
                    .post("/api/animales/" + animal + "/cuarentenas").then(), 400, "ENTRADA_INVALIDA");
            assertError(como(veterinario).body(json("vacuna", "Rabia", "fecha", LocalDate.now().plusDays(1).toString()))
                    .post("/api/animales/" + animal + "/vacunaciones").then(), 400, "ENTRADA_INVALIDA");
        }

        @Test
        void rolSinPermisoEs403() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            assertError(como(voluntario).body(json("nombre", "X", "email", email("x"), "rol", "VOLUNTARIO"))
                    .post("/api/usuarios").then(), 403, "PERMISO_DENEGADO");
            String animal = registrarAnimal(voluntario);
            assertError(como(voluntario).body(json("vacuna", "Rabia"))
                    .post("/api/animales/" + animal + "/vacunaciones").then(), 403, "PERMISO_DENEGADO");
        }

        @Test
        void recursoInexistenteEs404() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            assertError(como(voluntario).get("/api/animales/" + UUID.randomUUID()).then(), 404, "NO_ENCONTRADO");
            assertError(como(adminId).get("/api/cuarentenas/" + UUID.randomUUID()).then(), 404, "NO_ENCONTRADO");
            assertError(como(adminId).get("/api/adopciones/" + UUID.randomUUID()).then(), 404, "NO_ENCONTRADO");
            assertError(como(adminId).get("/api/solicitudes-adopcion/" + UUID.randomUUID()).then(), 404, "NO_ENCONTRADO");
        }

        @Test
        void actorInexistenteEs404() {
            assertError(como(UUID.randomUUID().toString()).get("/api/animales").then(), 404, "NO_ENCONTRADO");
        }

        @Test
        void rutaInexistenteEs404YMetodoNoSoportadoEs405() {
            como(adminId).get("/api/no-existe").then().statusCode(404);
            // No hay operaciones de eliminación
            como(adminId).delete("/api/usuarios/" + adminId).then().statusCode(405);
        }

        @Test
        void conflictosDeEstadoSon409() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            String veterinario = usuarioActivo("VETERINARIO");
            String animal = registrarAnimal(voluntario);
            // Cuenta ya activa: no se puede aprobar otra vez
            assertError(como(adminId).post("/api/usuarios/" + voluntario + "/aprobacion").then(), 409, "CONFLICTO");
            // Esterilizar dos veces
            como(veterinario).post("/api/animales/" + animal + "/esterilizacion").then().statusCode(201);
            assertError(como(veterinario).post("/api/animales/" + animal + "/esterilizacion").then(), 409, "CONFLICTO");
            // Periodo mínimo de cuarentena no cumplido
            String cuarentena = como(veterinario).body(json("diasMinimos", 30))
                    .post("/api/animales/" + animal + "/cuarentenas").then().statusCode(201).extract().path("cuarentenaId");
            assertError(como(veterinario).post("/api/cuarentenas/" + cuarentena + "/autorizacion-salida").then(), 409, "CONFLICTO");
            // Pasar a población general sin autorización
            assertError(como(veterinario).post("/api/cuarentenas/" + cuarentena + "/traslado-poblacion-general").then(), 409, "CONFLICTO");
            // Animal no disponible para adopción (sigue en cuarentena)
            String solicitante = usuarioActivo("SOLICITANTE");
            assertError(como(solicitante).body(json("animalId", animal)).post("/api/solicitudes-adopcion").then(), 409, "CONFLICTO");
        }

        @Test
        void emailRepetidoEs409() {
            String repetido = email("repetido");
            como(adminId).body(json("nombre", "A", "email", repetido, "rol", "VOLUNTARIO")).post("/api/usuarios").then().statusCode(201);
            assertError(como(adminId).body(json("nombre", "B", "email", repetido, "rol", "VETERINARIO"))
                    .post("/api/usuarios").then(), 409, "CONFLICTO");
            assertError(given().contentType(ContentType.JSON).body(json("nombre", "C", "email", repetido))
                    .post("/api/solicitantes").then(), 409, "CONFLICTO");
        }

        /**
         * Si la base de datos rechaza una escritura por una restricción única (aquí simulada con la misma
         * excepción que produce JPA), la API responde 409 sin exponer el nombre de la restricción ni el SQL,
         * y la transacción del caso de uso se revierte.
         */
        @Test
        void violacionDeRestriccionUnicaEs409SinDetallesInternos() {
            String voluntario = usuarioActivo("VOLUNTARIO");
            String veterinario = usuarioActivo("VETERINARIO");
            String animal = registrarAnimal(voluntario);
            registro.reiniciar();
            registro.violarIntegridadEn("cuarentena.save");
            ValidatableResponse respuesta = como(veterinario).body(json("diasMinimos", 5))
                    .post("/api/animales/" + animal + "/cuarentenas").then();
            assertError(respuesta, 409, "CONFLICTO");
            respuesta.body("mensaje", not(containsString("uq_")))
                    .body("mensaje", not(containsString("Duplicate")))
                    .body(not(containsString("SQL")));
            Object transaccion = registro.operaciones().stream()
                    .filter(o -> o.nombre().equals("animal.save")).findFirst().orElseThrow().transaccion();
            org.junit.jupiter.api.Assertions.assertEquals(jakarta.transaction.Status.STATUS_ROLLEDBACK, registro.estadoFinal(transaccion));
        }
    }
}
