package co.ufps.refupet.application.handlers.seguridad;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.exceptions.UsuarioNotFoundException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Control de acceso por perfil (reglas 31 y 39).
 *
 * Carga al usuario que ejecuta la operación y delega en el dominio (Usuario.verificarPermiso)
 * la decisión: la regla vive en las entidades; este handler solo coordina.
 * Devuelve el usuario verificado para que el caso de uso pueda registrar quién actuó.
 * Sin autenticación avanzada (fuera de alcance): el actor se identifica por su id.
 */
@ApplicationScoped
public class ValidateRolePermissionHandler {

    private final IUsuarioRepository usuarioRepository;

    @Inject
    public ValidateRolePermissionHandler(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(ValidateRolePermissionCommand command) {
        UsuarioId actorId = command.toActorId();
        Usuario actor = usuarioRepository.findById(actorId)
                .orElseThrow(() -> new UsuarioNotFoundException(actorId));
        actor.verificarPermiso(command.operacion());
        return actor;
    }
}
