package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.commands.usuario.ActivateUserCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.UsuarioNotFoundException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 29: el administrador activa una cuenta suspendida.
 * La transicion valida la decide la entidad Usuario.activar().
 */
@ApplicationScoped
public class ActivateUserHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public ActivateUserHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    @Transactional
    public UsuarioDTO execute(ActivateUserCommand command) {
        permisos.execute(new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_USUARIOS));

        UsuarioId usuarioId = command.toUsuarioId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        usuario.activar();
        return UsuarioDTO.from(usuarioRepository.save(usuario));
    }
}
