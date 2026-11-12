package co.ufps.refupet.domain.ids;

import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;

import java.util.Objects;
import java.util.UUID;

/**
 * Base abstracta de todos los identificadores (Value Objects) de RefuPet.
 *
 * Decisión de diseño: en Streamflix cada Id repite value/equals/hashCode.
 * Aquí ese comportamiento común se centraliza mediante herencia; cada subclase
 * conserva su constructor privado y sus factories generate()/of(). Como equals
 * compara también la clase, un AnimalId nunca es igual a un UsuarioId aunque
 * tengan el mismo texto.
 */
public abstract class Identificador {

    private final String value;

    protected Identificador(String value, String nombreCampo) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainDataException("El " + nombreCampo + " no puede estar vacío");
        }
        this.value = value.strip();
    }

    protected static String nuevoValor() {
        return UUID.randomUUID().toString();
    }

    public String value() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((Identificador) o).value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), value);
    }

    @Override
    public String toString() {
        return value;
    }
}
