package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.CuarentenaJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.CuarentenaMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/** Adaptador JPA de {@link ICuarentenaRepository}. */
@ApplicationScoped
public class CuarentenaRepositoryJpa implements ICuarentenaRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Cuarentena save(Cuarentena cuarentena) {
        entityManager.merge(CuarentenaMapper.toJpa(cuarentena));
        return cuarentena;
    }

    @Override
    public Optional<Cuarentena> findById(CuarentenaId cuarentenaId) {
        return Optional.ofNullable(entityManager.find(CuarentenaJpa.class, cuarentenaId.value()))
                .map(CuarentenaMapper::toDomain);
    }

    /** Historial de cuarentenas del animal, de la más antigua a la más reciente. */
    @Override
    public List<Cuarentena> findByAnimalId(AnimalId animalId) {
        return entityManager.createNamedQuery(CuarentenaJpa.POR_ANIMAL, CuarentenaJpa.class)
                .setParameter("animalId", animalId.value())
                .getResultList()
                .stream()
                .map(CuarentenaMapper::toDomain)
                .toList();
    }
}
