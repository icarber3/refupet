package co.ufps.refupet.application.handlers.animal;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.animal.GetAnimalQuery;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Regla 3: visualizar la información general de un animal.
 */
@ApplicationScoped
public class GetAnimalHandler {

    private final IAnimalRepository animalRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAnimalHandler(IAnimalRepository animalRepository, ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.permisos = permisos;
    }

    public AnimalDTO execute(GetAnimalQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_ANIMALES));
        AnimalId animalId = query.toAnimalId();
        return animalRepository.findById(animalId)
                .map(AnimalDTO::from)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
    }
}
