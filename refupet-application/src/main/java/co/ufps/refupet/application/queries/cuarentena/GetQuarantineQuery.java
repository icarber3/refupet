package co.ufps.refupet.application.queries.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.CuarentenaId;

/**
 * Consultar una cuarentena.
 */
public record GetQuarantineQuery(String actorId, String cuarentenaId) {
    public GetQuarantineQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(cuarentenaId, "cuarentenaId");
    }

    public CuarentenaId toCuarentenaId() {
        return CuarentenaId.of(cuarentenaId);
    }
}
