package co.ufps.refupet.domain.exceptions;

import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;

/**
 * El perfil del usuario no tiene permitida la operación solicitada.
 */
public class UnauthorizedRoleOperationException extends DomainException {

    public UnauthorizedRoleOperationException(RolUsuario rol, Operacion operacion) {
        super("El rol " + rol + " no puede realizar la operación: " + operacion.getDescripcion());
    }

    public UnauthorizedRoleOperationException(String message) {
        super(message);
    }
}
