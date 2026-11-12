package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.commands.usuario.UpdateUserCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.EmailAlreadyRegisteredException;
import co.ufps.refupet.domain.exceptions.UsuarioNotFoundException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 27: el administrador modifica la información de un usuario.
 * El email debe seguir siendo único entre todos los usuarios.
 */
@ApplicationScoped
public class UpdateUserHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public UpdateUserHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    @Transactional
    public UsuarioDTO execute(UpdateUserCommand command) {
        permisos.execute(new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_USUARIOS));

        UsuarioId usuarioId = command.toUsuarioId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        Email nuevoEmail = command.toEmail();
        usuarioRepository.findByEmail(nuevoEmail)
                .filter(otro -> !otro.getUsuarioId().equals(usuarioId))
                .ifPresent(otro -> {
                    throw new EmailAlreadyRegisteredException(
                            "El email " + nuevoEmail.value() + " ya pertenece a otro usuario");
                });

        usuario.actualizarDatos(command.nombre(), nuevoEmail);
        if (usuario instanceof Solicitante solicitante
                && (command.telefono() != null || command.direccion() != null)) {
            solicitante.actualizarContacto(command.telefono(), command.direccion());
        }
        return UsuarioDTO.from(usuarioRepository.save(usuario));
    }
}
