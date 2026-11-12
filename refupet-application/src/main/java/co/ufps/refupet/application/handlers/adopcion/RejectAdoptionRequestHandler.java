package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.RejectAdoptionRequestCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 22: el administrador rechaza una solicitud pendiente.
 */
@ApplicationScoped
public class RejectAdoptionRequestHandler {

    private final ISolicitudAdopcionRepository solicitudRepository;
    private final AdopcionService adopciones;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public RejectAdoptionRequestHandler(ISolicitudAdopcionRepository solicitudRepository, AdopcionService adopciones,
                                        ValidateRolePermissionHandler permisos) {
        this.solicitudRepository = solicitudRepository;
        this.adopciones = adopciones;
        this.permisos = permisos;
    }

    @Transactional
    public SolicitudAdopcionDTO execute(RejectAdoptionRequestCommand command) {
        Usuario administrador = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_SOLICITUDES_ADOPCION));

        SolicitudAdopcion solicitud = adopciones.obtenerSolicitud(command.toSolicitudId());
        solicitud.rechazar(administrador, command.motivo());
        return SolicitudAdopcionDTO.from(solicitudRepository.save(solicitud));
    }
}
