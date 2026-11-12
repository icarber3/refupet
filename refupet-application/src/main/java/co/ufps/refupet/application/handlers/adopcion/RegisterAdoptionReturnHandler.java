package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionReturnCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.application.services.FechaOperacion;
import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * Regla 25: registrar la devolución de un animal adoptado.
 * Al dejar de estar vigente la adopción, el animal vuelve a estar disponible.
 */
@ApplicationScoped
public class RegisterAdoptionReturnHandler {

    private final IAdopcionRepository adopcionRepository;
    private final AdopcionService adopciones;
    private final ValidateRolePermissionHandler permisos;
    private final Clock clock;

    @Inject
    public RegisterAdoptionReturnHandler(IAdopcionRepository adopcionRepository, AdopcionService adopciones,
                                         ValidateRolePermissionHandler permisos, Clock clock) {
        this.adopcionRepository = adopcionRepository;
        this.adopciones = adopciones;
        this.permisos = permisos;
        this.clock = clock;
    }

    @Transactional
    public AdopcionDTO execute(RegisterAdoptionReturnCommand command) {
        Usuario responsable = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_ADOPCIONES));

        Adopcion adopcion = adopciones.obtenerAdopcion(command.toAdopcionId());
        adopcion.registrarDevolucion(
                FechaOperacion.resolver(command.fecha(), clock, "fecha de devolución"),
                command.motivo(), responsable.getUsuarioId());
        return AdopcionDTO.from(adopcionRepository.save(adopcion));
    }
}
