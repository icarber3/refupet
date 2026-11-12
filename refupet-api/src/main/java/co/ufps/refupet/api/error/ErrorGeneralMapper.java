package co.ufps.refupet.api.error;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Cualquier otra excepción: conflictos de integridad envueltos (409), errores HTTP de Jakarta REST (su código) o 500. Delega en {@link ClasificadorErrores}. */
@Provider
public class ErrorGeneralMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        return ClasificadorErrores.respuesta(exception);
    }
}
