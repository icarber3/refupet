package co.ufps.refupet.application.queries.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Consultar la cuarentena activa (o la más reciente) de un animal.
 */
public record GetAnimalQuarantineStatusQuery(String actorId, String animalId) {
    public GetAnimalQuarantineStatusQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
