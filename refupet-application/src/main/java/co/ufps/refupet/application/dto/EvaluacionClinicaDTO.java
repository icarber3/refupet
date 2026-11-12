package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.EvaluacionClinica;

import java.time.LocalDate;

public record EvaluacionClinicaDTO(
        String evaluacionClinicaId,
        String animalId,
        String veterinarioId,
        LocalDate fecha,
        String hallazgos,
        String diagnostico,
        String observaciones
) {
    public static EvaluacionClinicaDTO from(EvaluacionClinica evaluacion) {
        return new EvaluacionClinicaDTO(
                evaluacion.getId().value(),
                evaluacion.getAnimalId().value(),
                evaluacion.getVeterinarioId().value(),
                evaluacion.getFecha(),
                evaluacion.getHallazgos(),
                evaluacion.getDiagnostico(),
                evaluacion.getObservaciones());
    }
}
