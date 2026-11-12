package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

/**
 * Regla 22: el administrador aprueba una solicitud de adopción.
 */
public record ApproveAdoptionRequestCommand(String actorId, String solicitudId) {
    public ApproveAdoptionRequestCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitudId, "solicitudId");
    }

    public SolicitudAdopcionId toSolicitudId() { return SolicitudAdopcionId.of(solicitudId); }
}
