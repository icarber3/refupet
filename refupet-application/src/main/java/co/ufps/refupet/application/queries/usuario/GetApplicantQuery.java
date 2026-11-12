package co.ufps.refupet.application.queries.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;

/**
 * Regla 20: consultar la información de un solicitante.
 */
public record GetApplicantQuery(String actorId, String solicitanteId) {
    public GetApplicantQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(solicitanteId, "solicitanteId");
    }

    public UsuarioId toSolicitanteId() { return UsuarioId.of(solicitanteId); }
}
