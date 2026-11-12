package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de un animal.
 */
public final class AnimalId extends Identificador {

    private AnimalId(String value) {
        super(value, "animalId");
    }

    public static AnimalId generate() {
        return new AnimalId(nuevoValor());
    }

    public static AnimalId of(String value) {
        return new AnimalId(value);
    }
}
