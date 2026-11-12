package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;

/**
 * Regla 28: suspender una cuenta activa.
 */
public record SuspendUserCommand(String actorId, String usuarioId) {
    public SuspendUserCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(usuarioId, "usuarioId");
    }

    public UsuarioId toUsuarioId() {
        return UsuarioId.of(usuarioId);
    }
}
