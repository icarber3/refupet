package co.ufps.refupet.application.queries.usuario;

import co.ufps.refupet.application.Entrada;

/**
 * Lista las cuentas pendientes de aprobación (apoya la regla 30).
 */
public record ListPendingUsersQuery(String actorId) {
    public ListPendingUsersQuery {
        Entrada.requerido(actorId, "actorId");
    }
}
