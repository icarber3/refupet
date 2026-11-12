package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.adopcion.ListAdoptionRequestsQuery;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListAdoptionRequestsHandler {

    private final ISolicitudAdopcionRepository solicitudRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public ListAdoptionRequestsHandler(ISolicitudAdopcionRepository solicitudRepository,
                                       ValidateRolePermissionHandler permisos) {
        this.solicitudRepository = solicitudRepository;
        this.permisos = permisos;
    }

    public List<SolicitudAdopcionDTO> execute(ListAdoptionRequestsQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.GESTIONAR_SOLICITUDES_ADOPCION));
        List<SolicitudAdopcion> solicitudes = query.toEstado()
                .map(solicitudRepository::findByEstado)
                .orElseGet(solicitudRepository::findAll);
        return solicitudes.stream().map(SolicitudAdopcionDTO::from).toList();
    }
}
