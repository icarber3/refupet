package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.usuario.UpdateUserCommand;

/** Cuerpo JSON de {@code PUT /api/usuarios/{id}}. La validación de los campos la hacen el Command y el dominio. */
public record ActualizarUsuarioRequest(String nombre, String email, String telefono, String direccion) {

    public UpdateUserCommand toCommand(String actorId, String usuarioId) {
        return new UpdateUserCommand(actorId, usuarioId, nombre, email, telefono, direccion);
    }
}
