package co.ufps.refupet.api.error;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Validación de forma de Commands, Queries y peticiones: 400. Delega en {@link ClasificadorErrores}. */
@Provider
public class EntradaInvalidaMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return ClasificadorErrores.respuesta(exception);
    }
}
