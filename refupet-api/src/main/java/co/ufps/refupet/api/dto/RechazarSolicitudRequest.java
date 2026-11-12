package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.RejectAdoptionRequestCommand;

/** Cuerpo JSON de {@code POST /api/solicitudes-adopcion/{id}/rechazo}. La validación de los campos la hacen el Command y el dominio. */
public record RechazarSolicitudRequest(String motivo) {

    /** El cuerpo es opcional: el motivo de rechazo no es obligatorio en el dominio. */
    public static RejectAdoptionRequestCommand toCommand(RechazarSolicitudRequest cuerpo, String actorId, String solicitudId) {
        return new RejectAdoptionRequestCommand(actorId, solicitudId, cuerpo == null ? null : cuerpo.motivo());
    }
}
