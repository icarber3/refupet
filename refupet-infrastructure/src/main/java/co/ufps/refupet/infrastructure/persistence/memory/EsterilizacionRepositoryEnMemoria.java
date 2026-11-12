package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.EsterilizacionId;
import co.ufps.refupet.domain.repositories.IEsterilizacionRepository;

import java.util.List;

/**
 * Adaptador en memoria de IEsterilizacionRepository.
 */
public class EsterilizacionRepositoryEnMemoria extends RepositorioEnMemoria<EsterilizacionId, Esterilizacion>
        implements IEsterilizacionRepository {

    public EsterilizacionRepositoryEnMemoria() {
        super(Esterilizacion::getId);
    }

    @Override
    public Esterilizacion save(Esterilizacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Esterilizacion> findByAnimalId(AnimalId animalId) {
        return filtrar(r -> r.getAnimalId().equals(animalId));
    }
}
