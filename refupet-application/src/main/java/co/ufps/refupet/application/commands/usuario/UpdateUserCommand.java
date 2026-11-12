package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;

/**
 * Regla 27: modificar la información de un usuario.
 * telefono/direccion se aplican solo si el usuario es solicitante y se envía alguno.
 */
public record UpdateUserCommand(
        String actorId,
        String usuarioId,
        String nombre,
        String email,
        String telefono,
        String direccion
) {
    public UpdateUserCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(usuarioId, "usuarioId");
        Entrada.requerido(nombre, "nombre");
        Entrada.requerido(email, "email");
    }

    public UsuarioId toUsuarioId() { return UsuarioId.of(usuarioId); }
    public Email toEmail() { return Email.of(email); }
}
