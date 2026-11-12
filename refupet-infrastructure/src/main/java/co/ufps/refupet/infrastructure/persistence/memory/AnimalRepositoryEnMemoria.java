package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador en memoria de IAnimalRepository.
 */
public class AnimalRepositoryEnMemoria extends RepositorioEnMemoria<AnimalId, Animal> implements IAnimalRepository {

    public AnimalRepositoryEnMemoria() {
        super(Animal::getAnimalId);
    }

    @Override
    public Animal save(Animal animal) {
        return guardar(animal);
    }

    @Override
    public Optional<Animal> findById(AnimalId animalId) {
        return buscarPorId(animalId);
    }

    @Override
    public boolean existsById(AnimalId animalId) {
        return existe(animalId);
    }

    @Override
    public List<Animal> findAll() {
        return todos();
    }

    @Override
    public List<Animal> findByUbicacion(EstadoUbicacionAnimal ubicacion) {
        return filtrar(a -> a.getUbicacion().filter(ubicacion::equals).isPresent());
    }
}
