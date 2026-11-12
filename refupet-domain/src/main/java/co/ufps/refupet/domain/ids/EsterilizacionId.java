package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de una esterilizacion.
 */
public final class EsterilizacionId extends Identificador {

    private EsterilizacionId(String value) {
        super(value, "esterilizacionId");
    }

    public static EsterilizacionId generate() {
        return new EsterilizacionId(nuevoValor());
    }

    public static EsterilizacionId of(String value) {
        return new EsterilizacionId(value);
    }
}
