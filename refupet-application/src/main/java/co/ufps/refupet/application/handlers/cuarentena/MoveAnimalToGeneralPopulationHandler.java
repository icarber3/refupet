package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.cuarentena.MoveAnimalToGeneralPopulationCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 16: registrar el paso del animal a población general.
 * Solo es posible con la salida de cuarentena autorizada (lo exige Cuarentena.finalizar).
 */
@ApplicationScoped
public class MoveAnimalToGeneralPopulationHandler {

    private final ICuarentenaRepository cuarentenaRepository;
    private final IAnimalRepository animalRepository;
    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public MoveAnimalToGeneralPopulationHandler(ICuarentenaRepository cuarentenaRepository,
                                                IAnimalRepository animalRepository, CuarentenaService cuarentenas,
                                                ValidateRolePermissionHandler permisos) {
        this.cuarentenaRepository = cuarentenaRepository;
        this.animalRepository = animalRepository;
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
    }

    @Transactional
    public AnimalDTO execute(MoveAnimalToGeneralPopulationCommand command) {
        permisos.execute(new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_CUARENTENA));

        Cuarentena cuarentena = cuarentenas.obtener(command.toCuarentenaId());
        Animal animal = animalRepository.findById(cuarentena.getAnimalId())
                .orElseThrow(() -> new AnimalNotFoundException(cuarentena.getAnimalId()));

        cuarentena.finalizar(cuarentenas.hoy());
        animal.trasladarAPoblacionGeneral();

        cuarentenaRepository.save(cuarentena);
        return AnimalDTO.from(animalRepository.save(animal));
    }
}
