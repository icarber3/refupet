package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.ids.AdopcionId;

/**
 * No existe Adopcion con el identificador indicado.
 */
public class AdopcionNotFoundException extends ResourceNotFoundException {
    public AdopcionNotFoundException(AdopcionId id) {
        super("Adopcion", id.value());
    }
}
