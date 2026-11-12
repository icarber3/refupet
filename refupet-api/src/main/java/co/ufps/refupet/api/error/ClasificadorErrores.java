package co.ufps.refupet.api.error;

import co.ufps.refupet.domain.exceptions.DomainException;
import co.ufps.refupet.domain.exceptions.InvalidClinicalDataException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.ResourceNotFoundException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

/**
 * Traduce cualquier excepción a una respuesta HTTP con {@link ErrorResponse}. Es la única política de
 * errores de la API; los {@code ExceptionMapper} solo delegan aquí.
 *
 * Recorre la cadena de causas porque las excepciones pueden llegar envueltas (por ejemplo, una violación
 * de restricción detectada al confirmar la transacción llega dentro de excepciones de JTA/JPA).
 *
 * <ul>
 *   <li>400 — entrada inválida: validación de Commands ({@link IllegalArgumentException}),
 *       {@link InvalidDomainDataException}, {@link InvalidClinicalDataException}, JSON mal formado y datos
 *       rechazados por la base de datos (SQLState 22, p. ej. texto más largo que su columna).</li>
 *   <li>403 — {@link UnauthorizedRoleOperationException}: el rol del actor no permite la operación.</li>
 *   <li>404 — {@link ResourceNotFoundException} (animal, usuario, cuarentena, solicitud, adopción).</li>
 *   <li>409 — resto de {@link DomainException} (estado inválido, email repetido, periodo no cumplido...)
 *       y violaciones de integridad de la base de datos (únicas, FK, CHECK), sin exponer sus detalles.</li>
 *   <li>Errores HTTP propios de Jakarta REST (ruta inexistente, método o tipo no soportado) conservan su código.</li>
 *   <li>500 — cualquier otro error; el detalle solo se registra en el log del servidor.</li>
 * </ul>
 */
public final class ClasificadorErrores {

    private static final Logger LOG = Logger.getLogger(ClasificadorErrores.class);

    static final String MENSAJE_CONFLICTO_INTEGRIDAD =
            "La operación no se pudo completar porque entra en conflicto con datos ya registrados";
    static final String MENSAJE_DATO_RECHAZADO =
            "Algún dato supera el tamaño o no tiene el formato que admite el sistema";
    static final String MENSAJE_JSON_INVALIDO =
            "El cuerpo de la petición no es un JSON válido o algún campo tiene un formato incorrecto";
    static final String MENSAJE_ERROR_INTERNO = "Error interno del servidor";

    private ClasificadorErrores() {
    }

    public static Response respuesta(Throwable excepcion) {
        ErrorResponse error = clasificar(excepcion);
        return Response.status(error.status())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }

    static ErrorResponse clasificar(Throwable excepcion) {
        Set<Throwable> vistas = new HashSet<>();
        for (Throwable actual = excepcion; actual != null && vistas.add(actual); actual = actual.getCause()) {
            if (actual instanceof DomainException dominio) {
                return deDominio(dominio);
            }
            if (actual instanceof WebApplicationException web) {
                int status = web.getResponse() == null ? 500 : web.getResponse().getStatus();
                if (status >= 500) {
                    return interno(excepcion);
                }
                return new ErrorResponse(status, categoriaHttp(status), mensajeHttp(status));
            }
            if (actual instanceof JsonProcessingException) {
                return new ErrorResponse(400, "ENTRADA_INVALIDA", MENSAJE_JSON_INVALIDO);
            }
            if (actual instanceof IllegalArgumentException) {
                return new ErrorResponse(400, "ENTRADA_INVALIDA", actual.getMessage());
            }
            if (esDatoRechazadoPorLaBaseDeDatos(actual)) {
                LOG.warnf("Dato rechazado por la base de datos: %s", actual.getMessage());
                return new ErrorResponse(400, "ENTRADA_INVALIDA", MENSAJE_DATO_RECHAZADO);
            }
            if (esViolacionDeIntegridad(actual)) {
                LOG.warnf("Conflicto de integridad en la base de datos: %s", actual.getMessage());
                return new ErrorResponse(409, "CONFLICTO", MENSAJE_CONFLICTO_INTEGRIDAD);
            }
        }
        return interno(excepcion);
    }

    private static ErrorResponse deDominio(DomainException excepcion) {
        if (excepcion instanceof ResourceNotFoundException) {
            return new ErrorResponse(404, "NO_ENCONTRADO", excepcion.getMessage());
        }
        if (excepcion instanceof UnauthorizedRoleOperationException) {
            return new ErrorResponse(403, "PERMISO_DENEGADO", excepcion.getMessage());
        }
        if (excepcion instanceof InvalidDomainDataException || excepcion instanceof InvalidClinicalDataException) {
            return new ErrorResponse(400, "ENTRADA_INVALIDA", excepcion.getMessage());
        }
        return new ErrorResponse(409, "CONFLICTO", excepcion.getMessage());
    }

    /**
     * SQLState de clase 22 = excepción de datos (p. ej. 22001 "Data too long" cuando un texto supera el
     * VARCHAR del esquema, que el dominio no limita). Es un problema de la entrada, no del servidor.
     */
    private static boolean esDatoRechazadoPorLaBaseDeDatos(Throwable excepcion) {
        return excepcion instanceof SQLException sql && sql.getSQLState() != null && sql.getSQLState().startsWith("22");
    }

    /** SQLState de clase 23 = violación de restricción de integridad (única, FK, CHECK). */
    private static boolean esViolacionDeIntegridad(Throwable excepcion) {
        return excepcion instanceof SQLIntegrityConstraintViolationException
                || (excepcion instanceof SQLException sql && sql.getSQLState() != null && sql.getSQLState().startsWith("23"));
    }

    private static ErrorResponse interno(Throwable excepcion) {
        LOG.error("Error no controlado al procesar la petición", excepcion);
        return new ErrorResponse(500, "ERROR_INTERNO", MENSAJE_ERROR_INTERNO);
    }

    private static String categoriaHttp(int status) {
        return switch (status) {
            case 400 -> "ENTRADA_INVALIDA";
            case 403 -> "PERMISO_DENEGADO";
            case 404 -> "NO_ENCONTRADO";
            case 409 -> "CONFLICTO";
            default -> "HTTP_" + status;
        };
    }

    private static String mensajeHttp(int status) {
        Response.Status conocido = Response.Status.fromStatusCode(status);
        return conocido != null ? conocido.getReasonPhrase() : "Error HTTP " + status;
    }
}
