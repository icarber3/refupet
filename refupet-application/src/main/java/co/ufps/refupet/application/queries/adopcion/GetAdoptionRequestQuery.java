package co.ufps.refupet.application.queries.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

/**
 * Regla 20: consultar una solicitud con la información del solicitante y del animal.
 */
public record GetAdoptionRequestQuery(String actorId, String solicitudId) {
    public GetAdoptionRequestQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitudId, "solicitudId");
    }

    public SolicitudAdopcionId toSolicitudId() { return SolicitudAdopcionId.of(solicitudId); }
}
