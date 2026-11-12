package co.ufps.refupet.application.queries.animal;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Regla 5: consultar la trazabilidad del ingreso de un animal.
 */
public record GetAnimalIngressTraceQuery(String actorId, String animalId) {
    public GetAnimalIngressTraceQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() {
        return AnimalId.of(animalId);
    }
}
