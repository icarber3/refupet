package co.ufps.refupet.api.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** JSON mal formado: 400 con el formato de error de la API. Delega en {@link ClasificadorErrores}. */
@Provider
public class JsonInvalidoMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {
        return ClasificadorErrores.respuesta(exception);
    }
}
