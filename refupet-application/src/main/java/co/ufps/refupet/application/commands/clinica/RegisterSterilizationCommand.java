package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Reglas 9 y 34: el veterinario registra una esterilización.
 */
public record RegisterSterilizationCommand(
        String actorId,
        String animalId,
        LocalDate fecha,
        String observaciones
) {
    public RegisterSterilizationCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
