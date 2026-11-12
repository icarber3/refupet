package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;

/**
 * Regla 30: aprobar una cuenta pendiente de aprobacion.
 */
public record ApprovePendingUserCommand(String actorId, String usuarioId) {
    public ApprovePendingUserCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(usuarioId, "usuarioId");
    }

    public UsuarioId toUsuarioId() {
        return UsuarioId.of(usuarioId);
    }
}
