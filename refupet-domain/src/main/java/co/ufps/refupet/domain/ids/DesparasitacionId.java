package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de una desparasitacion.
 */
public final class DesparasitacionId extends Identificador {

    private DesparasitacionId(String value) {
        super(value, "desparasitacionId");
    }

    public static DesparasitacionId generate() {
        return new DesparasitacionId(nuevoValor());
    }

    public static DesparasitacionId of(String value) {
        return new DesparasitacionId(value);
    }
}
