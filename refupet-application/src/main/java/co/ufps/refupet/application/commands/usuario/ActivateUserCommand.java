package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;

/**
 * Regla 29: activar (reactivar) una cuenta suspendida.
 */
public record ActivateUserCommand(String actorId, String usuarioId) {
    public ActivateUserCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(usuarioId, "usuarioId");
    }

    public UsuarioId toUsuarioId() {
        return UsuarioId.of(usuarioId);
    }
}
