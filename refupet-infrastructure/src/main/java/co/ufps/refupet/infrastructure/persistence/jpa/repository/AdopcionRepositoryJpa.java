package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.AdopcionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.AdopcionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA de {@link IAdopcionRepository}. La adopción se carga siempre con sus seguimientos en
 * una sola consulta ({@code LEFT JOIN FETCH}).
 */
@ApplicationScoped
public class AdopcionRepositoryJpa implements IAdopcionRepository {

    @Inject
    EntityManager entityManager;

    /** Inserta o actualiza la adopción; los seguimientos nuevos se insertan por cascada (MERGE). */
    @Override
    @Transactional
    public Adopcion save(Adopcion adopcion) {
        entityManager.merge(AdopcionMapper.toJpa(adopcion));
        return adopcion;
    }

    @Override
    public Optional<Adopcion> findById(AdopcionId adopcionId) {
        return entityManager.createNamedQuery(AdopcionJpa.POR_ID, AdopcionJpa.class)
                .setParameter("id", adopcionId.value())
                .getResultStream()
                .findFirst()
                .map(AdopcionMapper::toDomain);
    }

    @Override
    public Optional<Adopcion> findBySolicitudId(SolicitudAdopcionId solicitudId) {
        return entityManager.createNamedQuery(AdopcionJpa.POR_SOLICITUD, AdopcionJpa.class)
                .setParameter("solicitudId", solicitudId.value())
                .getResultStream()
                .findFirst()
                .map(AdopcionMapper::toDomain);
    }

    @Override
    public List<Adopcion> findByAnimalId(AnimalId animalId) {
        return entityManager.createNamedQuery(AdopcionJpa.POR_ANIMAL, AdopcionJpa.class)
                .setParameter("animalId", animalId.value())
                .getResultList()
                .stream()
                .map(AdopcionMapper::toDomain)
                .toList();
    }
}
