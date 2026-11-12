package co.ufps.refupet.application.queries.cuarentena;

import co.ufps.refupet.application.Entrada;

/**
 * Regla 13: consultar qué animales se encuentran en cuarentena.
 */
public record GetAnimalsInQuarantineQuery(String actorId) {
    public GetAnimalsInQuarantineQuery {
        Entrada.requerido(actorId, "actorId");
    }
}
