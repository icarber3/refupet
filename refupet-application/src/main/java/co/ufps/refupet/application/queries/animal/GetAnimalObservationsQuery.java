package co.ufps.refupet.application.queries.animal;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Regla 4: consultar las observaciones de comportamiento de un animal.
 */
public record GetAnimalObservationsQuery(String actorId, String animalId) {
    public GetAnimalObservationsQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() {
        return AnimalId.of(animalId);
    }
}
