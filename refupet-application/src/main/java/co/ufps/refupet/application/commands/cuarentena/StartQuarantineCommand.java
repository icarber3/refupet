package co.ufps.refupet.application.commands.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AnimalId;

import java.time.LocalDate;

/**
 * Regla 12: registrar el inicio de la cuarentena de un animal.
 * diasMinimos es el periodo mínimo establecido para esta cuarentena (el enunciado no fija un valor).
 * fechaInicio es opcional (por defecto, hoy).
 */
public record StartQuarantineCommand(String actorId, String animalId, LocalDate fechaInicio, int diasMinimos) {
    public StartQuarantineCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(animalId, "animalId");
    }

    public AnimalId toAnimalId() { return AnimalId.of(animalId); }
}
