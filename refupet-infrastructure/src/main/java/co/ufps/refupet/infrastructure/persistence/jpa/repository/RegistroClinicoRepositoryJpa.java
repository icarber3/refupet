package co.ufps.refupet.infrastructure.persistence.jpa.repository;

import co.ufps.refupet.domain.entities.RegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.RegistroClinicoJpa;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.function.Function;

/**
 * Base común de los cinco adaptadores clínicos: todos guardan un registro y consultan por animal
 * con la misma semántica. Cada subclase concreta implementa su contrato del dominio
 * (IVacunacionRepository, etc.), declara {@code @Transactional} en su {@code save} y delega aquí.
 * No es un bean (es abstracta) y no contiene reglas de negocio.
 *
 * @param <D> registro clínico del dominio
 * @param <J> entidad JPA correspondiente
 */
public abstract class RegistroClinicoRepositoryJpa<D extends RegistroClinico, J extends RegistroClinicoJpa> {

    @Inject
    EntityManager entityManager;

    private final Class<J> tipoJpa;
    private final Function<D, J> haciaJpa;
    private final Function<J, D> haciaDominio;

    protected RegistroClinicoRepositoryJpa(Class<J> tipoJpa, Function<D, J> haciaJpa, Function<J, D> haciaDominio) {
        this.tipoJpa = tipoJpa;
        this.haciaJpa = haciaJpa;
        this.haciaDominio = haciaDominio;
    }

    /** Inserta (o actualiza si ya existiera) el registro; debe invocarse dentro de una transacción. */
    protected D guardar(D registro) {
        entityManager.merge(haciaJpa.apply(registro));
        return registro;
    }

    /** Ordenado por fecha (como el historial clínico) y por id para un orden estable. */
    protected List<D> buscarPorAnimal(AnimalId animalId) {
        return entityManager.createNamedQuery(tipoJpa.getSimpleName() + RegistroClinicoJpa.POR_ANIMAL, tipoJpa)
                .setParameter("animalId", animalId.value())
                .getResultList()
                .stream()
                .map(haciaDominio)
                .toList();
    }
}
