package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.usuario.CreateUserCommand;

/** Cuerpo JSON de {@code POST /api/usuarios}. La validación de los campos la hacen el Command y el dominio. */
public record CrearUsuarioRequest(String nombre, String email, String rol, String telefono, String direccion) {

    public CreateUserCommand toCommand(String actorId) {
        return new CreateUserCommand(actorId, nombre, email, rol, telefono, direccion);
    }
}
