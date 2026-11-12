package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Usuario;

import java.time.LocalDateTime;

/**
 * Datos de un usuario para las capas externas. Sin reglas de negocio.
 */
public record UsuarioDTO(
        String usuarioId,
        String nombre,
        String email,
        String rol,
        String estadoCuenta,
        LocalDateTime fechaRegistro
) {
    public static UsuarioDTO from(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getUsuarioId().value(),
                usuario.getNombre(),
                usuario.getEmail().value(),
                usuario.getRol().name(),
                usuario.getEstadoCuenta().name(),
                usuario.getFechaRegistro());
    }
}
