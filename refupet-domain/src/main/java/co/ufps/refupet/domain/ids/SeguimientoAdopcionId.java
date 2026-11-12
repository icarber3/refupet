package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de un seguimiento posterior a la adopcion.
 */
public final class SeguimientoAdopcionId extends Identificador {

    private SeguimientoAdopcionId(String value) {
        super(value, "seguimientoAdopcionId");
    }

    public static SeguimientoAdopcionId generate() {
        return new SeguimientoAdopcionId(nuevoValor());
    }

    public static SeguimientoAdopcionId of(String value) {
        return new SeguimientoAdopcionId(value);
    }
}
