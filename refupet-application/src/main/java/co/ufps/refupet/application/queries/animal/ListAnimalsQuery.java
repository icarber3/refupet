package co.ufps.refupet.application.queries.animal;

import co.ufps.refupet.application.Entrada;

/**
 * Regla 2: consultar los animales registrados.
 */
public record ListAnimalsQuery(String actorId) {
    public ListAnimalsQuery {
        Entrada.requerido(actorId, "actorId");
    }
}
