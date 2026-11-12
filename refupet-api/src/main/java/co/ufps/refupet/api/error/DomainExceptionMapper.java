package co.ufps.refupet.api.error;

import co.ufps.refupet.domain.exceptions.DomainException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Excepciones del dominio: 400, 403, 404 o 409 según su tipo. Delega en {@link ClasificadorErrores}. */
@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException exception) {
        return ClasificadorErrores.respuesta(exception);
    }
}
