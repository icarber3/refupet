package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

/**
 * Regla 21: evaluar aspectos básicos de compatibilidad de una solicitud.
 */
public record EvaluateBasicCompatibilityCommand(String actorId, String solicitudId,
                                                Boolean compatible, String observaciones) {
    public EvaluateBasicCompatibilityCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitudId, "solicitudId");
        Entrada.requerido(compatible, "compatible");
    }

    public SolicitudAdopcionId toSolicitudId() { return SolicitudAdopcionId.of(solicitudId); }
}
