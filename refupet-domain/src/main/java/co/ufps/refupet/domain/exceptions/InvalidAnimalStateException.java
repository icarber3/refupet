package co.ufps.refupet.domain.exceptions;

/**
 * Operacion no permitida por el estado actual del animal (ubicacion, esterilizacion, disponibilidad).
 */
public class InvalidAnimalStateException extends DomainException {
    public InvalidAnimalStateException(String message) {
        super(message);
    }
}
