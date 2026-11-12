package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionFollowUpCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.SeguimientoAdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.application.services.FechaOperacion;
import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.SeguimientoAdopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * Regla 24: registrar un seguimiento posterior a la adopción.
 */
@ApplicationScoped
public class RegisterAdoptionFollowUpHandler {

    private final IAdopcionRepository adopcionRepository;
    private final AdopcionService adopciones;
    private final ValidateRolePermissionHandler permisos;
    private final Clock clock;

    @Inject
    public RegisterAdoptionFollowUpHandler(IAdopcionRepository adopcionRepository, AdopcionService adopciones,
                                           ValidateRolePermissionHandler permisos, Clock clock) {
        this.adopcionRepository = adopcionRepository;
        this.adopciones = adopciones;
        this.permisos = permisos;
        this.clock = clock;
    }

    @Transactional
    public SeguimientoAdopcionDTO execute(RegisterAdoptionFollowUpCommand command) {
        Usuario responsable = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_ADOPCIONES));

        Adopcion adopcion = adopciones.obtenerAdopcion(command.toAdopcionId());
        SeguimientoAdopcion seguimiento = adopcion.registrarSeguimiento(
                FechaOperacion.resolver(command.fecha(), clock, "fecha del seguimiento"),
                command.observaciones(), responsable.getUsuarioId());
        adopcionRepository.save(adopcion);
        return SeguimientoAdopcionDTO.from(seguimiento);
    }
}
