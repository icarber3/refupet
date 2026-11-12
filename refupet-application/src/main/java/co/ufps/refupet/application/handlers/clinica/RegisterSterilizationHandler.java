package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.clinica.RegisterSterilizationCommand;
import co.ufps.refupet.application.dto.RegistroClinicoDTO;
import co.ufps.refupet.application.services.AtencionVeterinariaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.IEsterilizacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Reglas 9 y 34: registrar una esterilización. Coordina dos agregados:
 * marca al animal como esterilizado (el dominio impide hacerlo dos veces)
 * y guarda el registro en el historial clínico.
 */
@ApplicationScoped
public class RegisterSterilizationHandler {

    private final IEsterilizacionRepository esterilizacionRepository;
    private final IAnimalRepository animalRepository;
    private final AtencionVeterinariaService atencion;

    @Inject
    public RegisterSterilizationHandler(IEsterilizacionRepository esterilizacionRepository,
                                        IAnimalRepository animalRepository, AtencionVeterinariaService atencion) {
        this.esterilizacionRepository = esterilizacionRepository;
        this.animalRepository = animalRepository;
        this.atencion = atencion;
    }

    @Transactional
    public RegistroClinicoDTO execute(RegisterSterilizationCommand command) {
        Usuario veterinario = atencion.verificarVeterinario(command.actorId());
        Animal animal = atencion.obtenerAnimal(command.toAnimalId());

        Esterilizacion esterilizacion = Esterilizacion.create(animal.getAnimalId(), veterinario.getUsuarioId(),
                atencion.fechaDelRegistro(command.fecha()), command.observaciones());
        animal.marcarEsterilizado();

        animalRepository.save(animal);
        return RegistroClinicoDTO.from(esterilizacionRepository.save(esterilizacion));
    }
}
