package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.UsuarioJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.mapper.UsuarioMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA de {@link IUsuarioRepository}. Guarda y consulta cualquier subclase de Usuario
 * en la tabla {@code usuario}; la conversión la hace {@link UsuarioMapper}.
 *
 * No contiene reglas de negocio (la unicidad del email la comprueban los casos de uso; la base
 * de datos la garantiza además con uq_usuario_email).
 */
@ApplicationScoped
public class UsuarioRepositoryJpa implements IUsuarioRepository {

    @Inject
    EntityManager entityManager;

    /**
     * Inserta o actualiza (merge por id asignado). REQUIRED: se une a la transacción del caso de uso
     * si existe; si no, abre una propia.
     */
    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        entityManager.merge(UsuarioMapper.toJpa(usuario));
        return usuario;
    }

    @Override
    public Optional<Usuario> findById(UsuarioId usuarioId) {
        return Optional.ofNullable(entityManager.find(UsuarioJpa.class, usuarioId.value()))
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(Email email) {
        return entityManager.createNamedQuery(UsuarioJpa.POR_EMAIL, UsuarioJpa.class)
                .setParameter("email", email.value())
                .getResultStream()
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        Long total = entityManager.createNamedQuery(UsuarioJpa.CONTAR_POR_EMAIL, Long.class)
                .setParameter("email", email.value())
                .getSingleResult();
        return total > 0;
    }

    /** Orden estable por fecha de registro (equivale al orden de inserción del repositorio en memoria). */
    @Override
    public List<Usuario> findByEstadoCuenta(EstadoCuenta estadoCuenta) {
        return entityManager.createNamedQuery(UsuarioJpa.POR_ESTADO_CUENTA, UsuarioJpa.class)
                .setParameter("estado", estadoCuenta)
                .getResultList()
                .stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }
}
