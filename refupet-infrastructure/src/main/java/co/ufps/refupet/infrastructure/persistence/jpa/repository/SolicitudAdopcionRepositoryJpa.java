package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.SolicitudAdopcionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.SolicitudAdopcionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/** Adaptador JPA de {@link ISolicitudAdopcionRepository}. Listados en orden de presentación. */
@ApplicationScoped
public class SolicitudAdopcionRepositoryJpa implements ISolicitudAdopcionRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public SolicitudAdopcion save(SolicitudAdopcion solicitud) {
        entityManager.merge(SolicitudAdopcionMapper.toJpa(solicitud));
        return solicitud;
    }

    @Override
    public Optional<SolicitudAdopcion> findById(SolicitudAdopcionId solicitudId) {
        return Optional.ofNullable(entityManager.find(SolicitudAdopcionJpa.class, solicitudId.value()))
                .map(SolicitudAdopcionMapper::toDomain);
    }

    @Override
    public List<SolicitudAdopcion> findAll() {
        return aDominio(entityManager.createNamedQuery(SolicitudAdopcionJpa.TODAS, SolicitudAdopcionJpa.class));
    }

    @Override
    public List<SolicitudAdopcion> findByEstado(EstadoSolicitudAdopcion estado) {
        return aDominio(entityManager.createNamedQuery(SolicitudAdopcionJpa.POR_ESTADO, SolicitudAdopcionJpa.class)
                .setParameter("estado", estado));
    }

    @Override
    public List<SolicitudAdopcion> findBySolicitanteId(UsuarioId solicitanteId) {
        return aDominio(entityManager.createNamedQuery(SolicitudAdopcionJpa.POR_SOLICITANTE, SolicitudAdopcionJpa.class)
                .setParameter("solicitanteId", solicitanteId.value()));
    }

    private static List<SolicitudAdopcion> aDominio(TypedQuery<SolicitudAdopcionJpa> query) {
        return query.getResultList().stream().map(SolicitudAdopcionMapper::toDomain).toList();
    }
}
