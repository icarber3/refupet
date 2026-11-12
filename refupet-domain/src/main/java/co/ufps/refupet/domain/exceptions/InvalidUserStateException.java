package co.ufps.refupet.domain.exceptions;

/**
 * Operacion no permitida por el estado de la cuenta del usuario.
 */
public class InvalidUserStateException extends DomainException {
    public InvalidUserStateException(String message) {
        super(message);
    }
}
