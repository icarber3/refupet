package co.ufps.refupet.application.services;

import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.exceptions.CuarentenaNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Operaciones de lectura comunes a los casos de uso de cuarentena: buscar una cuarentena,
 * encontrar la cuarentena activa de un animal y armar su DTO con la fecha actual.
 */
@ApplicationScoped
public class CuarentenaService {

    private final ICuarentenaRepository cuarentenaRepository;
    private final IAnimalRepository animalRepository;
    private final Clock clock;

    @Inject
    public CuarentenaService(ICuarentenaRepository cuarentenaRepository, IAnimalRepository animalRepository, Clock clock) {
        this.cuarentenaRepository = cuarentenaRepository;
        this.animalRepository = animalRepository;
        this.clock = clock;
    }

    public Cuarentena obtener(CuarentenaId cuarentenaId) {
        return cuarentenaRepository.findById(cuarentenaId)
                .orElseThrow(() -> new CuarentenaNotFoundException(cuarentenaId));
    }

    public Optional<Cuarentena> activaDe(AnimalId animalId) {
        return cuarentenaRepository.findByAnimalId(animalId).stream()
                .filter(Cuarentena::estaActiva)
                .findFirst();
    }

    public LocalDate hoy() {
        return LocalDate.now(clock);
    }

    public CuarentenaDTO toDTO(Cuarentena cuarentena) {
        String nombreAnimal = animalRepository.findById(cuarentena.getAnimalId())
                .map(Animal::getNombre)
                .orElse(null);
        return CuarentenaDTO.from(cuarentena, nombreAnimal, hoy());
    }
}
