package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.ids.AnimalId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia del agregado Animal (incluye sus observaciones de comportamiento).
 */
public interface IAnimalRepository {
    Animal save(Animal animal);

    Optional<Animal> findById(AnimalId animalId);

    boolean existsById(AnimalId animalId);

    List<Animal> findAll();

    List<Animal> findByUbicacion(EstadoUbicacionAnimal ubicacion);
}
