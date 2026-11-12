package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.SubmitAdoptionRequestCommand;

/** Cuerpo JSON de {@code POST /api/solicitudes-adopcion}. La validación de los campos la hacen el Command y el dominio. */
public record PresentarSolicitudRequest(String animalId, String comentarios) {

    public SubmitAdoptionRequestCommand toCommand(String actorId) {
        return new SubmitAdoptionRequestCommand(actorId, animalId, comentarios);
    }
}
