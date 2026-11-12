package co.ufps.refupet.domain.exceptions;

/**
 * Datos clinicos incoherentes en un registro veterinario.
 */
public class InvalidClinicalDataException extends DomainException {
    public InvalidClinicalDataException(String message) {
        super(message);
    }
}
