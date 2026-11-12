package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.clinica.UpdateAnimalHealthCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.services.AtencionVeterinariaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 11: el veterinario actualiza el estado de salud del animal.
 */
@ApplicationScoped
public class UpdateAnimalHealthHandler {

    private final IAnimalRepository animalRepository;
    private final AtencionVeterinariaService atencion;

    @Inject
    public UpdateAnimalHealthHandler(IAnimalRepository animalRepository, AtencionVeterinariaService atencion) {
        this.animalRepository = animalRepository;
        this.atencion = atencion;
    }

    @Transactional
    public AnimalDTO execute(UpdateAnimalHealthCommand command) {
        Usuario veterinario = atencion.verificarVeterinario(command.actorId());
        Animal animal = atencion.obtenerAnimal(command.toAnimalId());

        animal.actualizarEstadoSalud(command.estadoSalud(), veterinario.getUsuarioId());
        return AnimalDTO.from(animalRepository.save(animal));
    }
}
