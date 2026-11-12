package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.FormalizeAdoptionCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.application.services.DisponibilidadAdopcionService;
import co.ufps.refupet.application.services.FechaOperacion;
import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * Regla 23: registrar la adopción formalizada.
 * La solicitud debe estar aprobada (lo exige Adopcion.formalizar), no haberse formalizado antes
 * y el animal debe seguir disponible.
 */
@ApplicationScoped
public class FormalizeAdoptionHandler {

    private final IAdopcionRepository adopcionRepository;
    private final AdopcionService adopciones;
    private final DisponibilidadAdopcionService disponibilidad;
    private final ValidateRolePermissionHandler permisos;
    private final Clock clock;

    @Inject
    public FormalizeAdoptionHandler(IAdopcionRepository adopcionRepository, AdopcionService adopciones,
                                    DisponibilidadAdopcionService disponibilidad,
                                    ValidateRolePermissionHandler permisos, Clock clock) {
        this.adopcionRepository = adopcionRepository;
        this.adopciones = adopciones;
        this.disponibilidad = disponibilidad;
        this.permisos = permisos;
        this.clock = clock;
    }

    @Transactional
    public AdopcionDTO execute(FormalizeAdoptionCommand command) {
        Usuario administrador = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_ADOPCIONES));

        SolicitudAdopcion solicitud = adopciones.obtenerSolicitud(command.toSolicitudId());
        if (adopcionRepository.findBySolicitudId(solicitud.getSolicitudId()).isPresent()) {
            throw new InvalidAdoptionRequestStateException("La solicitud ya fue formalizada como adopción");
        }
        disponibilidad.exigirDisponible(adopciones.obtenerAnimal(solicitud.getAnimalId()));

        Adopcion adopcion = Adopcion.formalizar(solicitud,
                FechaOperacion.resolver(command.fecha(), clock, "fecha de formalización"),
                administrador.getUsuarioId());
        return AdopcionDTO.from(adopcionRepository.save(adopcion));
    }
}
