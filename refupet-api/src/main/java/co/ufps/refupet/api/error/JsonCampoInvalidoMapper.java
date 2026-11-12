package co.ufps.refupet.api.error;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Campo JSON con tipo o formato incorrecto (por ejemplo, una fecha no ISO): 400. Sustituye al mapeador integrado de Quarkus para no exponer detalles de deserialización. Delega en {@link ClasificadorErrores}. */
@Provider
public class JsonCampoInvalidoMapper implements ExceptionMapper<MismatchedInputException> {

    @Override
    public Response toResponse(MismatchedInputException exception) {
        return ClasificadorErrores.respuesta(exception);
    }
}
