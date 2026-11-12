package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Desparasitacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IDesparasitacionRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.DesparasitacionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.RegistroClinicoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/** Adaptador JPA de {@link IDesparasitacionRepository}. */
@ApplicationScoped
public class DesparasitacionRepositoryJpa extends RegistroClinicoRepositoryJpa<Desparasitacion, DesparasitacionJpa>
        implements IDesparasitacionRepository {

    public DesparasitacionRepositoryJpa() {
        super(DesparasitacionJpa.class, RegistroClinicoMapper::toJpa, RegistroClinicoMapper::toDomain);
    }

    @Override
    @Transactional
    public Desparasitacion save(Desparasitacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Desparasitacion> findByAnimalId(AnimalId animalId) {
        return buscarPorAnimal(animalId);
    }
}
