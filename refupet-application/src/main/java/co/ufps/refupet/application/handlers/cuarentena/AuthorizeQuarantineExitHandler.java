package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.cuarentena.AuthorizeQuarantineExitCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Reglas 14 y 15: el veterinario autoriza la salida de cuarentena.
 * La entidad Cuarentena exige rol veterinario y periodo mínimo cumplido.
 */
@ApplicationScoped
public class AuthorizeQuarantineExitHandler {

    private final ICuarentenaRepository cuarentenaRepository;
    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public AuthorizeQuarantineExitHandler(ICuarentenaRepository cuarentenaRepository, CuarentenaService cuarentenas,
                                          ValidateRolePermissionHandler permisos) {
        this.cuarentenaRepository = cuarentenaRepository;
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
    }

    @Transactional
    public CuarentenaDTO execute(AuthorizeQuarantineExitCommand command) {
        Usuario veterinario = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.AUTORIZAR_SALIDA_CUARENTENA));

        Cuarentena cuarentena = cuarentenas.obtener(command.toCuarentenaId());
        cuarentena.autorizarSalida(veterinario, cuarentenas.hoy());
        cuarentenaRepository.save(cuarentena);
        return cuarentenas.toDTO(cuarentena);
    }
}
