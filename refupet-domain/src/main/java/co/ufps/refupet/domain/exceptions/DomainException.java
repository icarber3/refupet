package co.ufps.refupet.domain.exceptions;

/**
 * Excepción base del dominio. Todas las violaciones de reglas de negocio de RefuPet
 * heredan de ella, de modo que las capas externas pueden tratarlas de forma uniforme.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
