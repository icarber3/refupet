package co.ufps.refupet.domain.exceptions;

/**
 * Datos que violan una invariante basica: campo obligatorio vacio, formato invalido o valor fuera de rango.
 */
public class InvalidDomainDataException extends DomainException {
    public InvalidDomainDataException(String message) {
        super(message);
    }
}
