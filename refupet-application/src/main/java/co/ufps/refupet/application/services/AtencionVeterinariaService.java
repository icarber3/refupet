package co.ufps.refupet.application.services;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Pasos comunes a todos los registros clínicos (reglas 6 a 9, 33 y 34):
 * verificar que quien registra es un veterinario activo, que el animal existe
 * y resolver la fecha del acto clínico. Evita repetir la misma coordinación en cada handler.
 */
@ApplicationScoped
public class AtencionVeterinariaService {

    private final IAnimalRepository animalRepository;
    private final ValidateRolePermissionHandler permisos;
    private final Clock clock;

    @Inject
    public AtencionVeterinariaService(IAnimalRepository animalRepository,
                                      ValidateRolePermissionHandler permisos, Clock clock) {
        this.animalRepository = animalRepository;
        this.permisos = permisos;
        this.clock = clock;
    }

    public Usuario verificarVeterinario(String actorId) {
        return permisos.execute(new ValidateRolePermissionCommand(actorId, Operacion.GESTIONAR_INFORMACION_CLINICA));
    }

    public Animal obtenerAnimal(AnimalId animalId) {
        return animalRepository.findById(animalId).orElseThrow(() -> new AnimalNotFoundException(animalId));
    }

    public LocalDate fechaDelRegistro(LocalDate fechaIndicada) {
        return FechaOperacion.resolver(fechaIndicada, clock, "fecha del registro clínico");
    }
}
