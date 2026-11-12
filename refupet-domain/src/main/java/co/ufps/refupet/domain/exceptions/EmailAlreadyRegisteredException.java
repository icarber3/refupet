package co.ufps.refupet.domain.exceptions;

/**
 * Ya existe un usuario registrado con el email indicado.
 */
public class EmailAlreadyRegisteredException extends DomainException {
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
