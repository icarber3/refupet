package co.ufps.refupet.api.error;

import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.exceptions.EmailAlreadyRegisteredException;
import co.ufps.refupet.domain.exceptions.InvalidAnimalStateException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.QuarantinePeriodNotCompletedException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.domain.ids.AnimalId;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Política de errores HTTP (sin contenedor): código, categoría y que no se filtren detalles internos. */
class ClasificadorErroresTest {

    private static void assertError(int status, String categoria, ErrorResponse error) {
        assertEquals(status, error.status());
        assertEquals(categoria, error.error());
        assertNotNull(error.mensaje());
    }

    @Test
    void recursoNoEncontradoEs404() {
        ErrorResponse error = ClasificadorErrores.clasificar(new AnimalNotFoundException(AnimalId.of("x")));
        assertError(404, "NO_ENCONTRADO", error);
        assertTrue(error.mensaje().contains("x"));
    }

    @Test
    void rolSinPermisoEs403() {
        assertError(403, "PERMISO_DENEGADO", ClasificadorErrores.clasificar(
                new UnauthorizedRoleOperationException(RolUsuario.VOLUNTARIO, Operacion.GESTIONAR_USUARIOS)));
    }

    @Test
    void datosInvalidosEs400() {
        assertError(400, "ENTRADA_INVALIDA", ClasificadorErrores.clasificar(new InvalidDomainDataException("mal")));
        assertError(400, "ENTRADA_INVALIDA", ClasificadorErrores.clasificar(new IllegalArgumentException("falta")));
        ErrorResponse json = ClasificadorErrores.clasificar(new JsonParseException(null, "Unexpected character"));
        assertError(400, "ENTRADA_INVALIDA", json);
        assertFalse(json.mensaje().contains("Unexpected"), "no debe exponer el detalle del parser");
    }

    @Test
    void conflictosDeEstadoDelDominioSon409() {
        assertError(409, "CONFLICTO", ClasificadorErrores.clasificar(new InvalidAnimalStateException("ya esterilizado")));
        assertError(409, "CONFLICTO", ClasificadorErrores.clasificar(new EmailAlreadyRegisteredException("repetido")));
        assertError(409, "CONFLICTO", ClasificadorErrores.clasificar(
                new QuarantinePeriodNotCompletedException(LocalDate.of(2026, 1, 1))));
    }

    /** Violación de restricción única envuelta como llega desde JPA y JTA al confirmar la transacción. */
    @Test
    void violacionDeIntegridadEnvueltaEs409SinDetallesInternos() {
        SQLIntegrityConstraintViolationException sql = new SQLIntegrityConstraintViolationException(
                "Duplicate entry 'abc' for key 'uq_usuario_email'", "23000", 1062);
        RollbackException commit = new RollbackException("ARJUNA016053: Could not commit transaction.");
        commit.initCause(new PersistenceException("could not execute statement", sql));
        ErrorResponse error = ClasificadorErrores.clasificar(new RuntimeException(commit));
        assertError(409, "CONFLICTO", error);
        assertEquals(ClasificadorErrores.MENSAJE_CONFLICTO_INTEGRIDAD, error.mensaje());
        assertFalse(error.mensaje().contains("uq_usuario_email"));
        assertFalse(error.mensaje().contains("Duplicate"));
    }

    @Test
    void cualquierSqlStateDeClase23EsConflictoDeIntegridad() {
        ErrorResponse error = ClasificadorErrores.clasificar(new PersistenceException(
                new SQLException("CONSTRAINT `ck_cuarentena_estado` failed", "23000", 4025)));
        assertError(409, "CONFLICTO", error);
        assertFalse(error.mensaje().contains("ck_cuarentena_estado"));
    }

    /** "Data too long" (SQLState 22001): el dominio no limita longitudes, el esquema sí → 400, no 500. */
    @Test
    void textoMasLargoQueSuColumnaEs400SinDetallesInternos() {
        ErrorResponse error = ClasificadorErrores.clasificar(new RuntimeException(new PersistenceException(
                new SQLException("Data too long for column 'nombre' at row 1", "22001", 1406))));
        assertError(400, "ENTRADA_INVALIDA", error);
        assertEquals(ClasificadorErrores.MENSAJE_DATO_RECHAZADO, error.mensaje());
        assertFalse(error.mensaje().contains("nombre"));
    }

    @Test
    void unaExcepcionDeDominioEnvueltaConservaSuClasificacion() {
        assertError(404, "NO_ENCONTRADO", ClasificadorErrores.clasificar(
                new RuntimeException(new AnimalNotFoundException(AnimalId.of("y")))));
    }

    @Test
    void erroresHttpDeJakartaRestConservanSuCodigo() {
        assertError(404, "NO_ENCONTRADO", ClasificadorErrores.clasificar(new NotFoundException()));
        ErrorResponse metodo = ClasificadorErrores.clasificar(new NotAllowedException("GET"));
        assertEquals(405, metodo.status());
    }

    @Test
    void errorInesperadoEs500SinDetalles() {
        ErrorResponse error = ClasificadorErrores.clasificar(new IllegalStateException("NullPointer en línea 42 del servidor"));
        assertError(500, "ERROR_INTERNO", error);
        assertEquals(ClasificadorErrores.MENSAJE_ERROR_INTERNO, error.mensaje());
        assertError(500, "ERROR_INTERNO", ClasificadorErrores.clasificar(new SQLException("Communications link failure", "08S01")));
    }

    @Test
    void laRespuestaEsJsonConElCodigoCorrecto() {
        Response respuesta = ClasificadorErrores.respuesta(new AnimalNotFoundException(AnimalId.of("z")));
        assertEquals(404, respuesta.getStatus());
        assertEquals("application/json", respuesta.getMediaType().toString());
        assertInstanceOf(ErrorResponse.class, respuesta.getEntity());
    }
}
