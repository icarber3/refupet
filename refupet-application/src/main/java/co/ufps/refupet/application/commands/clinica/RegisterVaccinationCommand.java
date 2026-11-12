package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Regla 7: el veterinario registra una vacunación.
 */
public record RegisterVaccinationCommand(
        String actorId,
        String animalId,
        LocalDate fecha,
        String vacuna,
        String observaciones
) {
    public RegisterVaccinationCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(vacuna, "vacuna");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
