package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.animal.RegisterBehaviorObservationCommand;

/** Cuerpo JSON de {@code POST /api/animales/{id}/observaciones}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarObservacionRequest(String descripcion) {

    public RegisterBehaviorObservationCommand toCommand(String actorId, String animalId) {
        return new RegisterBehaviorObservationCommand(actorId, animalId, descripcion);
    }
}
