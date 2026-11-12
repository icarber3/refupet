package co.ufps.refupet.application.commands.animal;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Reglas 4 y 36: el voluntario registra una observación de comportamiento.
 */
public record RegisterBehaviorObservationCommand(String actorId, String animalId, String descripcion) {
    public RegisterBehaviorObservationCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(descripcion, "descripcion");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
