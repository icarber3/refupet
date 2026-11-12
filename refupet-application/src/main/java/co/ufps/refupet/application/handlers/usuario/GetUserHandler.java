package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.usuario.GetUserQuery;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.UsuarioNotFoundException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetUserHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetUserHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    public UsuarioDTO execute(GetUserQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.GESTIONAR_USUARIOS));
        UsuarioId usuarioId = query.toUsuarioId();
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioDTO::from)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
    }
}
