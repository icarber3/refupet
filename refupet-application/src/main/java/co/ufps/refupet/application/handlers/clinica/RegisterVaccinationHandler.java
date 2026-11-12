package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.clinica.RegisterVaccinationCommand;
import co.ufps.refupet.application.dto.RegistroClinicoDTO;
import co.ufps.refupet.application.services.AtencionVeterinariaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.entities.Vacunacion;
import co.ufps.refupet.domain.repositories.IVacunacionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 7: registrar una vacunación.
 */
@ApplicationScoped
public class RegisterVaccinationHandler {

    private final IVacunacionRepository vacunacionRepository;
    private final AtencionVeterinariaService atencion;

    @Inject
    public RegisterVaccinationHandler(IVacunacionRepository vacunacionRepository, AtencionVeterinariaService atencion) {
        this.vacunacionRepository = vacunacionRepository;
        this.atencion = atencion;
    }

    @Transactional
    public RegistroClinicoDTO execute(RegisterVaccinationCommand command) {
        Usuario veterinario = atencion.verificarVeterinario(command.actorId());
        Animal animal = atencion.obtenerAnimal(command.toAnimalId());

        Vacunacion vacunacion = Vacunacion.create(animal.getAnimalId(), veterinario.getUsuarioId(),
                atencion.fechaDelRegistro(command.fecha()), command.vacuna(), command.observaciones());
        return RegistroClinicoDTO.from(vacunacionRepository.save(vacunacion));
    }
}
