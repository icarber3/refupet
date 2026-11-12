package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.adopcion.GetAvailableAnimalsQuery;
import co.ufps.refupet.application.services.DisponibilidadAdopcionService;
import co.ufps.refupet.domain.enums.Operacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Reglas 17 y 37: consultar los animales disponibles para adopción.
 */
@ApplicationScoped
public class GetAvailableAnimalsHandler {

    private final DisponibilidadAdopcionService disponibilidad;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAvailableAnimalsHandler(DisponibilidadAdopcionService disponibilidad, ValidateRolePermissionHandler permisos) {
        this.disponibilidad = disponibilidad;
        this.permisos = permisos;
    }

    public List<AnimalDTO> execute(GetAvailableAnimalsQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_ANIMALES_DISPONIBLES));
        return disponibilidad.animalesDisponibles().stream().map(AnimalDTO::from).toList();
    }
}
