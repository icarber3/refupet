package co.ufps.refupet.application.queries.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

/**
 * Regla 10: consultar el historial clínico de un animal.
 */
public record GetClinicalHistoryQuery(String actorId, String animalId) {
    public GetClinicalHistoryQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
