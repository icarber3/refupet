package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.commands.usuario.CreateUserCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.EmailAlreadyRegisteredException;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 26 y 32: el administrador crea cuentas de usuario.
 * La cuenta nace PENDIENTE_APROBACION (regla de la entidad Usuario).
 */
@ApplicationScoped
public class CreateUserHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public CreateUserHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    @Transactional
    public UsuarioDTO execute(CreateUserCommand command) {
        permisos.execute(new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_USUARIOS));

        Email email = command.toEmail();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Ya existe un usuario registrado con el email " + email.value());
        }

        RolUsuario rol = command.toRol();
        Usuario usuario = rol == RolUsuario.SOLICITANTE
                ? Solicitante.create(command.nombre(), email, command.telefono(), command.direccion())
                : Usuario.create(rol, command.nombre(), email);

        return UsuarioDTO.from(usuarioRepository.save(usuario));
    }
}
