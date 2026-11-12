package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Modelo de persistencia de la tabla {@code vacunacion}: vacunación aplicada (regla 7).
 * Hereda las columnas comunes de {@link RegistroClinicoJpa}.
 */
@NamedQuery(name = "VacunacionJpa" + RegistroClinicoJpa.POR_ANIMAL,
        query = "SELECT r FROM VacunacionJpa r WHERE r.animalId = :animalId ORDER BY r.fecha, r.id")
@Entity
@Table(name = "vacunacion")
public class VacunacionJpa extends RegistroClinicoJpa {


    @Column(name = "vacuna", length = 150, nullable = false, updatable = false)
    private String vacuna;

    /** Requerido por JPA. */
    public VacunacionJpa() {
    }

    public String getVacuna() { return vacuna; }
    public void setVacuna(String vacuna) { this.vacuna = vacuna; }
}
