package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.EvaluacionClinicaId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Evaluación clínica de un animal (regla 6). Corresponde al historial del animal atendido.
 *
 * Solo los hallazgos son obligatorios (es lo mínimo que define una evaluación);
 * el diagnóstico es opcional para no inventar datos clínicos obligatorios.
 */
public class EvaluacionClinica extends RegistroClinico {

    private final EvaluacionClinicaId evaluacionClinicaId;
    private final String hallazgos;
    private final String diagnostico;

    private EvaluacionClinica(EvaluacionClinicaId id, AnimalId animalId, UsuarioId veterinarioId,
                              LocalDate fecha, String hallazgos, String diagnostico, String observaciones) {
        super(animalId, veterinarioId, fecha, observaciones);
        this.evaluacionClinicaId = Validaciones.obligatorio(id, "evaluacionClinicaId");
        this.hallazgos = Validaciones.textoObligatorio(hallazgos, "hallazgos");
        this.diagnostico = Validaciones.textoOpcional(diagnostico);
    }

    public static EvaluacionClinica create(AnimalId animalId, UsuarioId veterinarioId, LocalDate fecha,
                                           String hallazgos, String diagnostico, String observaciones) {
        return new EvaluacionClinica(EvaluacionClinicaId.generate(), animalId, veterinarioId,
                fecha, hallazgos, diagnostico, observaciones);
    }

    public static EvaluacionClinica of(EvaluacionClinicaId id, AnimalId animalId, UsuarioId veterinarioId,
                                       LocalDate fecha, String hallazgos, String diagnostico, String observaciones) {
        return new EvaluacionClinica(id, animalId, veterinarioId, fecha, hallazgos, diagnostico, observaciones);
    }

    @Override
    public EvaluacionClinicaId getId() {
        return evaluacionClinicaId;
    }

    @Override
    public TipoRegistroClinico getTipo() {
        return TipoRegistroClinico.EVALUACION_CLINICA;
    }

    @Override
    public String resumen() {
        return "Evaluación clínica: " + hallazgos + (diagnostico != null ? " | Diagnóstico: " + diagnostico : "");
    }

    public String getHallazgos() { return hallazgos; }
    public String getDiagnostico() { return diagnostico; }
}
