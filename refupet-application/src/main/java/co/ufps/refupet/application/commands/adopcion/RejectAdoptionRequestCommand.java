package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

/**
 * Regla 22: el administrador rechaza una solicitud de adopción (motivo opcional).
 */
public record RejectAdoptionRequestCommand(String actorId, String solicitudId, String motivo) {
    public RejectAdoptionRequestCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitudId, "solicitudId");
    }

    public SolicitudAdopcionId toSolicitudId() { return SolicitudAdopcionId.of(solicitudId); }
}
