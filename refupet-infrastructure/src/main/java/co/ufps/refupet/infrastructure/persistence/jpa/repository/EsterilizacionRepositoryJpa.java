package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IEsterilizacionRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.EsterilizacionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.RegistroClinicoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/** Adaptador JPA de {@link IEsterilizacionRepository}. */
@ApplicationScoped
public class EsterilizacionRepositoryJpa extends RegistroClinicoRepositoryJpa<Esterilizacion, EsterilizacionJpa>
        implements IEsterilizacionRepository {

    public EsterilizacionRepositoryJpa() {
        super(EsterilizacionJpa.class, RegistroClinicoMapper::toJpa, RegistroClinicoMapper::toDomain);
    }

    @Override
    @Transactional
    public Esterilizacion save(Esterilizacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Esterilizacion> findByAnimalId(AnimalId animalId) {
        return buscarPorAnimal(animalId);
    }
}
