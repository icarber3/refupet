package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;

/**
 * Regla 26: el administrador crea una cuenta de usuario con un perfil.
 * telefono y direccion solo aplican al perfil SOLICITANTE (opcionales).
 */
public record CreateUserCommand(
        String actorId,
        String nombre,
        String email,
        String rol,
        String telefono,
        String direccion
) {
    public CreateUserCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(nombre, "nombre");
        Entrada.requerido(email, "email");
        Entrada.requerido(rol, "rol");
    }

    public UsuarioId toActorId() { return UsuarioId.of(actorId); }
    public Email toEmail() { return Email.of(email); }
    public RolUsuario toRol() { return RolUsuario.fromString(rol); }
}
