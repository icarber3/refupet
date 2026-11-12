package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.EvaluacionClinica;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.EvaluacionClinicaId;
import co.ufps.refupet.domain.repositories.IEvaluacionClinicaRepository;

import java.util.List;

/**
 * Adaptador en memoria de IEvaluacionClinicaRepository.
 */
public class EvaluacionClinicaRepositoryEnMemoria extends RepositorioEnMemoria<EvaluacionClinicaId, EvaluacionClinica>
        implements IEvaluacionClinicaRepository {

    public EvaluacionClinicaRepositoryEnMemoria() {
        super(EvaluacionClinica::getId);
    }

    @Override
    public EvaluacionClinica save(EvaluacionClinica registro) {
        return guardar(registro);
    }

    @Override
    public List<EvaluacionClinica> findByAnimalId(AnimalId animalId) {
        return filtrar(r -> r.getAnimalId().equals(animalId));
    }
}
