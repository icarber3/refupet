package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

import java.time.LocalDate;

/**
 * Regla 23: registrar la adopción formalizada de una solicitud aprobada (fecha opcional, por defecto hoy).
 */
public record FormalizeAdoptionCommand(String actorId, String solicitudId, LocalDate fecha) {
    public FormalizeAdoptionCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitudId, "solicitudId");
    }

    public SolicitudAdopcionId toSolicitudId() { return SolicitudAdopcionId.of(solicitudId); }
}
