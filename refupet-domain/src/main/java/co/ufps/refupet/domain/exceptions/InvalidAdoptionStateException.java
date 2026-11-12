package co.ufps.refupet.domain.exceptions;

/**
 * Operacion no permitida por el estado de una adopcion formalizada.
 */
public class InvalidAdoptionStateException extends DomainException {
    public InvalidAdoptionStateException(String message) {
        super(message);
    }
}
