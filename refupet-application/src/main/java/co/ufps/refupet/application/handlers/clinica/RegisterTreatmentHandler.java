package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.clinica.RegisterTreatmentCommand;
import co.ufps.refupet.application.dto.TratamientoDTO;
import co.ufps.refupet.application.services.AtencionVeterinariaService;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Tratamiento;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.repositories.ITratamientoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 8: registrar un tratamiento médico.
 */
@ApplicationScoped
public class RegisterTreatmentHandler {

    private final ITratamientoRepository tratamientoRepository;
    private final AtencionVeterinariaService atencion;

    @Inject
    public RegisterTreatmentHandler(ITratamientoRepository tratamientoRepository, AtencionVeterinariaService atencion) {
        this.tratamientoRepository = tratamientoRepository;
        this.atencion = atencion;
    }

    @Transactional
    public TratamientoDTO execute(RegisterTreatmentCommand command) {
        Usuario veterinario = atencion.verificarVeterinario(command.actorId());
        Animal animal = atencion.obtenerAnimal(command.toAnimalId());

        Tratamiento tratamiento = Tratamiento.create(animal.getAnimalId(), veterinario.getUsuarioId(),
                atencion.fechaDelRegistro(command.fechaInicio()), command.descripcion(),
                command.indicaciones(), command.observaciones());
        return TratamientoDTO.from(tratamientoRepository.save(tratamiento));
    }
}
