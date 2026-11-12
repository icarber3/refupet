package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SolicitanteDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.usuario.GetApplicantQuery;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.UsuarioNotFoundException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Regla 20: el administrador consulta la información de un solicitante
 * (por ejemplo, al revisar una solicitud de adopción).
 */
@ApplicationScoped
public class GetApplicantHandler {

    private final IUsuarioRepository usuarioRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetApplicantHandler(IUsuarioRepository usuarioRepository, ValidateRolePermissionHandler permisos) {
        this.usuarioRepository = usuarioRepository;
        this.permisos = permisos;
    }

    public SolicitanteDTO execute(GetApplicantQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.GESTIONAR_SOLICITUDES_ADOPCION));
        UsuarioId solicitanteId = query.toSolicitanteId();
        Usuario usuario = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNotFoundException(solicitanteId));
        if (!(usuario instanceof Solicitante solicitante)) {
            throw new InvalidDomainDataException("El usuario " + solicitanteId.value() + " no es un solicitante");
        }
        return SolicitanteDTO.from(solicitante);
    }
}
