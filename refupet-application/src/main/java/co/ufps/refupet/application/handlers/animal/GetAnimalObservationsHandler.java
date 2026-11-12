package co.ufps.refupet.application.handlers.animal;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.ObservacionComportamientoDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.animal.GetAnimalObservationsQuery;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetAnimalObservationsHandler {

    private final IAnimalRepository animalRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAnimalObservationsHandler(IAnimalRepository animalRepository, ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.permisos = permisos;
    }

    public List<ObservacionComportamientoDTO> execute(GetAnimalObservationsQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_ANIMALES));
        AnimalId animalId = query.toAnimalId();
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
        return animal.getObservaciones().stream().map(ObservacionComportamientoDTO::from).toList();
    }
}
