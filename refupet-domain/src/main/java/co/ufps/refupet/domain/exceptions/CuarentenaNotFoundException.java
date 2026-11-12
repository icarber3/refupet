package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.ids.CuarentenaId;

/**
 * No existe Cuarentena con el identificador indicado.
 */
public class CuarentenaNotFoundException extends ResourceNotFoundException {
    public CuarentenaNotFoundException(CuarentenaId id) {
        super("Cuarentena", id.value());
    }
}
