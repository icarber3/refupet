package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.usuario.ListPendingUsersQuery;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListPendingUsersHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public ListPendingUsersHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    public List<UsuarioDTO> execute(ListPendingUsersQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.GESTIONAR_USUARIOS));
        return usuarioRepository.findByEstadoCuenta(EstadoCuenta.PENDIENTE_APROBACION).stream()
                .map(UsuarioDTO::from)
                .toList();
    }
}
