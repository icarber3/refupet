package co.ufps.refupet.domain.exceptions;

/**
 * Transicion no permitida en el ciclo de vida de una cuarentena.
 */
public class InvalidQuarantineStateException extends DomainException {
    public InvalidQuarantineStateException(String message) {
        super(message);
    }
}
