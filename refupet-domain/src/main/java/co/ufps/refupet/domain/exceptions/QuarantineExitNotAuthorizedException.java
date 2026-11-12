package co.ufps.refupet.domain.exceptions;

/**
 * La salida de cuarentena no ha sido autorizada por un veterinario.
 */
public class QuarantineExitNotAuthorizedException extends DomainException {
    public QuarantineExitNotAuthorizedException(String message) {
        super(message);
    }
}
