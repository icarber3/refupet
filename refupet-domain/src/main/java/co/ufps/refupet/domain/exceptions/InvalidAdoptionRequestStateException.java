package co.ufps.refupet.domain.exceptions;

/**
 * Transicion no permitida en una solicitud de adopcion.
 */
public class InvalidAdoptionRequestStateException extends DomainException {
    public InvalidAdoptionRequestStateException(String message) {
        super(message);
    }
}
