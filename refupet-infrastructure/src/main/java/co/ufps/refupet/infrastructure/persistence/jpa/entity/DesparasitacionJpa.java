package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Modelo de persistencia de la tabla {@code desparasitacion}: desparasitación aplicada (regla 7).
 * Hereda las columnas comunes de {@link RegistroClinicoJpa}.
 */
@NamedQuery(name = "DesparasitacionJpa" + RegistroClinicoJpa.POR_ANIMAL,
        query = "SELECT r FROM DesparasitacionJpa r WHERE r.animalId = :animalId ORDER BY r.fecha, r.id")
@Entity
@Table(name = "desparasitacion")
public class DesparasitacionJpa extends RegistroClinicoJpa {


    @Column(name = "producto", length = 150, nullable = false, updatable = false)
    private String producto;

    /** Requerido por JPA. */
    public DesparasitacionJpa() {
    }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
}
