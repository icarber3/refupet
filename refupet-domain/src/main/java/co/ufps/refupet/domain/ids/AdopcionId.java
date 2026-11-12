package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de una adopcion formalizada.
 */
public final class AdopcionId extends Identificador {

    private AdopcionId(String value) {
        super(value, "adopcionId");
    }

    public static AdopcionId generate() {
        return new AdopcionId(nuevoValor());
    }

    public static AdopcionId of(String value) {
        return new AdopcionId(value);
    }
}
