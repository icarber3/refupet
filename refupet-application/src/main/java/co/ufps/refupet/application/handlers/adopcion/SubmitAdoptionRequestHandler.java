package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.SubmitAdoptionRequestCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.application.services.DisponibilidadAdopcionService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Reglas 19 y 38: el solicitante presenta una solicitud para un animal disponible.
 * No se admite una segunda solicitud pendiente del mismo solicitante para el mismo animal.
 */
@ApplicationScoped
public class SubmitAdoptionRequestHandler {

    private final ISolicitudAdopcionRepository solicitudRepository;
    private final AdopcionService adopciones;
    private final DisponibilidadAdopcionService disponibilidad;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public SubmitAdoptionRequestHandler(ISolicitudAdopcionRepository solicitudRepository, AdopcionService adopciones,
                                        DisponibilidadAdopcionService disponibilidad,
                                        ValidateRolePermissionHandler permisos) {
        this.solicitudRepository = solicitudRepository;
        this.adopciones = adopciones;
        this.disponibilidad = disponibilidad;
        this.permisos = permisos;
    }

    @Transactional
    public SolicitudAdopcionDTO execute(SubmitAdoptionRequestCommand command) {
        Usuario solicitante = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.PRESENTAR_SOLICITUD_ADOPCION));

        Animal animal = adopciones.obtenerAnimal(command.toAnimalId());
        disponibilidad.exigirDisponible(animal);

        boolean yaTienePendiente = solicitudRepository.findBySolicitanteId(solicitante.getUsuarioId()).stream()
                .anyMatch(s -> s.getAnimalId().equals(animal.getAnimalId()) && s.estaPendiente());
        if (yaTienePendiente) {
            throw new InvalidAdoptionRequestStateException(
                    "Ya existe una solicitud pendiente de este solicitante para el animal " + animal.getNombre());
        }

        SolicitudAdopcion solicitud = SolicitudAdopcion.create(
                animal.getAnimalId(), solicitante.getUsuarioId(), command.comentarios());
        return SolicitudAdopcionDTO.from(solicitudRepository.save(solicitud));
    }
}
