package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Vacunacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.VacunacionId;
import co.ufps.refupet.domain.repositories.IVacunacionRepository;

import java.util.List;

/**
 * Adaptador en memoria de IVacunacionRepository.
 */
public class VacunacionRepositoryEnMemoria extends RepositorioEnMemoria<VacunacionId, Vacunacion>
        implements IVacunacionRepository {

    public VacunacionRepositoryEnMemoria() {
        super(Vacunacion::getId);
    }

    @Override
    public Vacunacion save(Vacunacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Vacunacion> findByAnimalId(AnimalId animalId) {
        return filtrar(r -> r.getAnimalId().equals(animalId));
    }
}
