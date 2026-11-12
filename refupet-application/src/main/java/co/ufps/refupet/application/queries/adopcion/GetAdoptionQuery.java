package co.ufps.refupet.application.queries.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AdopcionId;

/**
 * Consultar una adopción formalizada con sus seguimientos y devolución.
 */
public record GetAdoptionQuery(String actorId, String adopcionId) {
    public GetAdoptionQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(adopcionId, "adopcionId");
    }

    public AdopcionId toAdopcionId() { return AdopcionId.of(adopcionId); }
}
