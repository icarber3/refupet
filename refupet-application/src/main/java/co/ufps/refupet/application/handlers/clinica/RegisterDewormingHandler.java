package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.clinica.RegisterDewormingCommand;
import co.ufps.refupet.application.dto.RegistroClinicoDTO;
import co.ufps.refupet.application.services.AtencionVeterinariaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Desparasitacion;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.repositories.IDesparasitacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 7: registrar una desparasitación.
 */
@ApplicationScoped
public class RegisterDewormingHandler {

    private final IDesparasitacionRepository desparasitacionRepository;
    private final AtencionVeterinariaService atencion;

    @Inject
    public RegisterDewormingHandler(IDesparasitacionRepository desparasitacionRepository,
                                    AtencionVeterinariaService atencion) {
        this.desparasitacionRepository = desparasitacionRepository;
        this.atencion = atencion;
    }

    @Transactional
    public RegistroClinicoDTO execute(RegisterDewormingCommand command) {
        Usuario veterinario = atencion.verificarVeterinario(command.actorId());
        Animal animal = atencion.obtenerAnimal(command.toAnimalId());

        Desparasitacion desparasitacion = Desparasitacion.create(animal.getAnimalId(), veterinario.getUsuarioId(),
                atencion.fechaDelRegistro(command.fecha()), command.producto(), command.observaciones());
        return RegistroClinicoDTO.from(desparasitacionRepository.save(desparasitacion));
    }
}
