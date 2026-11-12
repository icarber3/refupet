package co.ufps.refupet.application.handlers.animal;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.RegistroIngresoDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.animal.GetAnimalIngressTraceQuery;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Regla 5: consultar la trazabilidad básica del ingreso de un animal.
 */
@ApplicationScoped
public class GetAnimalIngressTraceHandler {

    private final IAnimalRepository animalRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAnimalIngressTraceHandler(IAnimalRepository animalRepository, ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.permisos = permisos;
    }

    public RegistroIngresoDTO execute(GetAnimalIngressTraceQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_ANIMALES));
        AnimalId animalId = query.toAnimalId();
        return animalRepository.findById(animalId)
                .map(RegistroIngresoDTO::from)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
    }
}
