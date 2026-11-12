package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Reglas 19 y 38: el solicitante presenta una solicitud de adopción para un animal disponible.
 */
public record SubmitAdoptionRequestCommand(String actorId, String animalId, String comentarios) {
    public SubmitAdoptionRequestCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
