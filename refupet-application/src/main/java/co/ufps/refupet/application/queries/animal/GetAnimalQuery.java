package co.ufps.refupet.application.queries.animal;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Regla 3: visualizar la informacion general de un animal.
 */
public record GetAnimalQuery(String actorId, String animalId) {
    public GetAnimalQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() {
        return AnimalId.of(animalId);
    }
}
