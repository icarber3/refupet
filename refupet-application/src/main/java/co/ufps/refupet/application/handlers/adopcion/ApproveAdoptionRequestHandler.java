package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.ApproveAdoptionRequestCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.application.services.DisponibilidadAdopcionService;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Reglas 22 y 32: el administrador aprueba una solicitud pendiente;
 * el animal debe seguir disponible en el momento de la aprobación.
 */
@ApplicationScoped
public class ApproveAdoptionRequestHandler {

    private final ISolicitudAdopcionRepository solicitudRepository;
    private final AdopcionService adopciones;
    private final DisponibilidadAdopcionService disponibilidad;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public ApproveAdoptionRequestHandler(ISolicitudAdopcionRepository solicitudRepository, AdopcionService adopciones,
                                         DisponibilidadAdopcionService disponibilidad,
                                         ValidateRolePermissionHandler permisos) {
        this.solicitudRepository = solicitudRepository;
        this.adopciones = adopciones;
        this.disponibilidad = disponibilidad;
        this.permisos = permisos;
    }

    @Transactional
    public SolicitudAdopcionDTO execute(ApproveAdoptionRequestCommand command) {
        Usuario administrador = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_SOLICITUDES_ADOPCION));

        SolicitudAdopcion solicitud = adopciones.obtenerSolicitud(command.toSolicitudId());
        disponibilidad.exigirDisponible(adopciones.obtenerAnimal(solicitud.getAnimalId()));
        solicitud.aprobar(administrador);
        return SolicitudAdopcionDTO.from(solicitudRepository.save(solicitud));
    }
}
