package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.cuarentena.StartQuarantineCommand;
import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.application.services.FechaOperacion;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.exceptions.InvalidQuarantineStateException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * Regla 12: registrar el inicio de la cuarentena de un animal.
 * Coordina dos agregados: crea la Cuarentena y ubica al Animal en cuarentena.
 * Un animal no puede tener dos cuarentenas activas a la vez.
 */
@ApplicationScoped
public class StartQuarantineHandler {

    private final ICuarentenaRepository cuarentenaRepository;
    private final IAnimalRepository animalRepository;
    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;
    private final Clock clock;

    @Inject
    public StartQuarantineHandler(ICuarentenaRepository cuarentenaRepository, IAnimalRepository animalRepository,
                                  CuarentenaService cuarentenas, ValidateRolePermissionHandler permisos, Clock clock) {
        this.cuarentenaRepository = cuarentenaRepository;
        this.animalRepository = animalRepository;
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
        this.clock = clock;
    }

    @Transactional
    public CuarentenaDTO execute(StartQuarantineCommand command) {
        Usuario responsable = permisos.execute(
                new ValidateRolePermissionCommand(command.actorId(), Operacion.GESTIONAR_CUARENTENA));

        AnimalId animalId = command.toAnimalId();
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
        if (cuarentenas.activaDe(animalId).isPresent()) {
            throw new InvalidQuarantineStateException("El animal " + animal.getNombre() + " ya tiene una cuarentena activa");
        }

        PeriodoCuarentena periodo = new PeriodoCuarentena(
                FechaOperacion.resolver(command.fechaInicio(), clock, "fecha de inicio de la cuarentena"),
                command.diasMinimos());
        Cuarentena cuarentena = Cuarentena.iniciar(animalId, periodo, responsable.getUsuarioId());
        animal.ingresarACuarentena();

        animalRepository.save(animal);
        cuarentenaRepository.save(cuarentena);
        return cuarentenas.toDTO(cuarentena);
    }
}
