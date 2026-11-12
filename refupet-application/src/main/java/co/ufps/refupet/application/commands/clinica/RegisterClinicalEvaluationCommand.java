package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Regla 6: el veterinario registra una evaluación clínica.
 * Solo los hallazgos son obligatorios; fecha (por defecto hoy), diagnóstico y observaciones son opcionales.
 */
public record RegisterClinicalEvaluationCommand(
        String actorId,
        String animalId,
        LocalDate fecha,
        String hallazgos,
        String diagnostico,
        String observaciones
) {
    public RegisterClinicalEvaluationCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(hallazgos, "hallazgos");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
