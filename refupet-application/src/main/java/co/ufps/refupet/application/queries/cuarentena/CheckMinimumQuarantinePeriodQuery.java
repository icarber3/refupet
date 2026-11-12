package co.ufps.refupet.application.queries.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.CuarentenaId;

/**
 * Regla 14: controlar si se cumplio el periodo minimo de una cuarentena.
 */
public record CheckMinimumQuarantinePeriodQuery(String actorId, String cuarentenaId) {
    public CheckMinimumQuarantinePeriodQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(cuarentenaId, "cuarentenaId");
    }

    public CuarentenaId toCuarentenaId() {
        return CuarentenaId.of(cuarentenaId);
    }
}
