package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.ids.UsuarioId;

/**
 * No existe Usuario con el identificador indicado.
 */
public class UsuarioNotFoundException extends ResourceNotFoundException {
    public UsuarioNotFoundException(UsuarioId id) {
        super("Usuario", id.value());
    }
}
