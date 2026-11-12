package co.ufps.refupet.application.queries.adopcion;

import co.ufps.refupet.application.Entrada;

/**
 * Reglas 17 y 37: consultar animales disponibles para adopción.
 */
public record GetAvailableAnimalsQuery(String actorId) {
    public GetAvailableAnimalsQuery {
        Entrada.requerido(actorId, "actorId");
    }
}
