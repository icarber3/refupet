package co.ufps.refupet.domain.ids;

/**
 * Identificador inmutable de una evaluacion clinica.
 */
public final class EvaluacionClinicaId extends Identificador {

    private EvaluacionClinicaId(String value) {
        super(value, "evaluacionClinicaId");
    }

    public static EvaluacionClinicaId generate() {
        return new EvaluacionClinicaId(nuevoValor());
    }

    public static EvaluacionClinicaId of(String value) {
        return new EvaluacionClinicaId(value);
    }
}
