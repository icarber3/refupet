package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Regla 8: el veterinario registra un tratamiento médico.
 */
public record RegisterTreatmentCommand(
        String actorId,
        String animalId,
        LocalDate fechaInicio,
        String descripcion,
        String indicaciones,
        String observaciones
) {
    public RegisterTreatmentCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(descripcion, "descripcion");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
