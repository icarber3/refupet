package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Modelo de persistencia de la tabla {@code esterilizacion}: esterilización realizada (regla 9). {@code animal_id} es único en el esquema (uq_esterilizacion_animal): una por animal.
 * Hereda las columnas comunes de {@link RegistroClinicoJpa}.
 */
@NamedQuery(name = "EsterilizacionJpa" + RegistroClinicoJpa.POR_ANIMAL,
        query = "SELECT r FROM EsterilizacionJpa r WHERE r.animalId = :animalId ORDER BY r.fecha, r.id")
@Entity
@Table(name = "esterilizacion")
public class EsterilizacionJpa extends RegistroClinicoJpa {


    /** Requerido por JPA. */
    public EsterilizacionJpa() {
    }
}
