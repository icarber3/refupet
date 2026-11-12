package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Tratamiento;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.TratamientoId;
import co.ufps.refupet.domain.repositories.ITratamientoRepository;

import java.util.List;

/**
 * Adaptador en memoria de ITratamientoRepository.
 */
public class TratamientoRepositoryEnMemoria extends RepositorioEnMemoria<TratamientoId, Tratamiento>
        implements ITratamientoRepository {

    public TratamientoRepositoryEnMemoria() {
        super(Tratamiento::getId);
    }

    @Override
    public Tratamiento save(Tratamiento registro) {
        return guardar(registro);
    }

    @Override
    public List<Tratamiento> findByAnimalId(AnimalId animalId) {
        return filtrar(r -> r.getAnimalId().equals(animalId));
    }
}
