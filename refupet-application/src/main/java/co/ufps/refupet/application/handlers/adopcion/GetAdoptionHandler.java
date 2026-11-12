package co.ufps.refupet.application.handlers.adopcion;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AdopcionDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.adopcion.GetAdoptionQuery;
import co.ufps.refupet.application.services.AdopcionService;
import co.ufps.refupet.domain.enums.Operacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetAdoptionHandler {

    private final AdopcionService adopciones;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAdoptionHandler(AdopcionService adopciones, ValidateRolePermissionHandler permisos) {
        this.adopciones = adopciones;
        this.permisos = permisos;
    }

    public AdopcionDTO execute(GetAdoptionQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.GESTIONAR_ADOPCIONES));
        return AdopcionDTO.from(adopciones.obtenerAdopcion(query.toAdopcionId()));
    }
}
