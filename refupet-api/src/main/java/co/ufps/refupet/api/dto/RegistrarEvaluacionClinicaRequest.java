package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.RegisterClinicalEvaluationCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/evaluaciones-clinicas}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarEvaluacionClinicaRequest(LocalDate fecha, String hallazgos, String diagnostico, String observaciones) {

    public RegisterClinicalEvaluationCommand toCommand(String actorId, String animalId) {
        return new RegisterClinicalEvaluationCommand(actorId, animalId, fecha, hallazgos, diagnostico, observaciones);
    }
}
