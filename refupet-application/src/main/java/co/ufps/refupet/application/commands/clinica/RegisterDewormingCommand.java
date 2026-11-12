package co.ufps.refupet.application.commands.clinica;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Regla 7: el veterinario registra una desparasitación.
 */
public record RegisterDewormingCommand(
        String actorId,
        String animalId,
        LocalDate fecha,
        String producto,
        String observaciones
) {
    public RegisterDewormingCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
        Entrada.requerido(producto, "producto");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
