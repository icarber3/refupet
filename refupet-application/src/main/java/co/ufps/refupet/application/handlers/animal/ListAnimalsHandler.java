package co.ufps.refupet.application.handlers.animal;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.animal.ListAnimalsQuery;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Regla 2: consultar los animales registrados.
 */
@ApplicationScoped
public class ListAnimalsHandler {

    private final IAnimalRepository animalRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public ListAnimalsHandler(IAnimalRepository animalRepository, ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.permisos = permisos;
    }

    public List<AnimalDTO> execute(ListAnimalsQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_ANIMALES));
        return animalRepository.findAll().stream().map(AnimalDTO::from).toList();
    }
}
