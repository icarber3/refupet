package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.cuarentena.GetQuarantineQuery;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.domain.enums.Operacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetQuarantineHandler {

    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetQuarantineHandler(CuarentenaService cuarentenas, ValidateRolePermissionHandler permisos) {
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
    }

    public CuarentenaDTO execute(GetQuarantineQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_CUARENTENAS));
        return cuarentenas.toDTO(cuarentenas.obtener(query.toCuarentenaId()));
    }
}
