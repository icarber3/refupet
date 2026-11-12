package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.EvaluacionClinica;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IEvaluacionClinicaRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.EvaluacionClinicaJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.RegistroClinicoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/** Adaptador JPA de {@link IEvaluacionClinicaRepository}. */
@ApplicationScoped
public class EvaluacionClinicaRepositoryJpa extends RegistroClinicoRepositoryJpa<EvaluacionClinica, EvaluacionClinicaJpa>
        implements IEvaluacionClinicaRepository {

    public EvaluacionClinicaRepositoryJpa() {
        super(EvaluacionClinicaJpa.class, RegistroClinicoMapper::toJpa, RegistroClinicoMapper::toDomain);
    }

    @Override
    @Transactional
    public EvaluacionClinica save(EvaluacionClinica registro) {
        return guardar(registro);
    }

    @Override
    public List<EvaluacionClinica> findByAnimalId(AnimalId animalId) {
        return buscarPorAnimal(animalId);
    }
}
