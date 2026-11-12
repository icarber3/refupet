package co.ufps.refupet.application.queries.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;

public record GetUserQuery(String actorId, String usuarioId) {
    public GetUserQuery {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(usuarioId, "usuarioId");
    }

    public UsuarioId toUsuarioId() { return UsuarioId.of(usuarioId); }
}
