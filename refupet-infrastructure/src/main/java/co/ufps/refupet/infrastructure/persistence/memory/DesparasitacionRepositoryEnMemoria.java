package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Desparasitacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.DesparasitacionId;
import co.ufps.refupet.domain.repositories.IDesparasitacionRepository;

import java.util.List;

/**
 * Adaptador en memoria de IDesparasitacionRepository.
 */
public class DesparasitacionRepositoryEnMemoria extends RepositorioEnMemoria<DesparasitacionId, Desparasitacion>
        implements IDesparasitacionRepository {

    public DesparasitacionRepositoryEnMemoria() {
        super(Desparasitacion::getId);
    }

    @Override
    public Desparasitacion save(Desparasitacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Desparasitacion> findByAnimalId(AnimalId animalId) {
        return filtrar(r -> r.getAnimalId().equals(animalId));
    }
}
