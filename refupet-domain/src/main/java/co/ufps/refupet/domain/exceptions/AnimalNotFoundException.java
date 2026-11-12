package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.ids.AnimalId;

/**
 * No existe Animal con el identificador indicado.
 */
public class AnimalNotFoundException extends ResourceNotFoundException {
    public AnimalNotFoundException(AnimalId id) {
        super("Animal", id.value());
    }
}
