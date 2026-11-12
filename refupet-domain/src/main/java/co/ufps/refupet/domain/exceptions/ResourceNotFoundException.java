package co.ufps.refupet.domain.exceptions;

/**
 * Base abstracta para las excepciones de "recurso no encontrado".
 * Cada agregado tiene su subclase específica (AnimalNotFoundException, etc.).
 */
public abstract class ResourceNotFoundException extends DomainException {
    protected ResourceNotFoundException(String recurso, String id) {
        super(recurso + " no encontrado con id: " + id);
    }
}
