package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

/**
 * No existe Solicitud de adopcion con el identificador indicado.
 */
public class SolicitudAdopcionNotFoundException extends ResourceNotFoundException {
    public SolicitudAdopcionNotFoundException(SolicitudAdopcionId id) {
        super("Solicitud de adopcion", id.value());
    }
}
