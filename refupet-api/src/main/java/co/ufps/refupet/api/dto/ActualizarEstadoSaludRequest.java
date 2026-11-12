package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.UpdateAnimalHealthCommand;

/** Cuerpo JSON de {@code PUT /api/animales/{id}/estado-salud}. La validación de los campos la hacen el Command y el dominio. */
public record ActualizarEstadoSaludRequest(String estadoSalud) {

    public UpdateAnimalHealthCommand toCommand(String actorId, String animalId) {
        return new UpdateAnimalHealthCommand(actorId, animalId, estadoSalud);
    }
}
