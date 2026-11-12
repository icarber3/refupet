package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Vacunacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IVacunacionRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.VacunacionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.RegistroClinicoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/** Adaptador JPA de {@link IVacunacionRepository}. */
@ApplicationScoped
public class VacunacionRepositoryJpa extends RegistroClinicoRepositoryJpa<Vacunacion, VacunacionJpa>
        implements IVacunacionRepository {

    public VacunacionRepositoryJpa() {
        super(VacunacionJpa.class, RegistroClinicoMapper::toJpa, RegistroClinicoMapper::toDomain);
    }

    @Override
    @Transactional
    public Vacunacion save(Vacunacion registro) {
        return guardar(registro);
    }

    @Override
    public List<Vacunacion> findByAnimalId(AnimalId animalId) {
        return buscarPorAnimal(animalId);
    }
}
