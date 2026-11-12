package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Regla 11: el veterinario actualiza el estado de salud del animal.
 */
public record UpdateAnimalHealthCommand(String actorId, String animalId, String estadoSalud) {
    public UpdateAnimalHealthCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(estadoSalud, "estadoSalud");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
