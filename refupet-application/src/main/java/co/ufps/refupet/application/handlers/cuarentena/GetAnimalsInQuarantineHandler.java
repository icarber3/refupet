package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.cuarentena.GetAnimalsInQuarantineQuery;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

/**
 * Regla 13: consultar los animales que se encuentran en cuarentena, con los datos de su cuarentena activa.
 */
@ApplicationScoped
public class GetAnimalsInQuarantineHandler {

    private final IAnimalRepository animalRepository;
    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAnimalsInQuarantineHandler(IAnimalRepository animalRepository, CuarentenaService cuarentenas,
                                         ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
    }

    public List<CuarentenaDTO> execute(GetAnimalsInQuarantineQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_CUARENTENAS));
        return animalRepository.findByUbicacion(EstadoUbicacionAnimal.CUARENTENA).stream()
                .map(Animal::getAnimalId)
                .map(cuarentenas::activaDe)
                .flatMap(Optional::stream)
                .map(cuarentenas::toDTO)
                .toList();
    }
}
