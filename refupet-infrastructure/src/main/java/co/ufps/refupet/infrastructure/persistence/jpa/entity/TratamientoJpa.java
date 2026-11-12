package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Modelo de persistencia de la tabla {@code tratamiento}: tratamiento indicado (regla 8). La columna heredada {@code fecha} es la fecha de inicio.
 * Hereda las columnas comunes de {@link RegistroClinicoJpa}.
 */
@NamedQuery(name = "TratamientoJpa" + RegistroClinicoJpa.POR_ANIMAL,
        query = "SELECT r FROM TratamientoJpa r WHERE r.animalId = :animalId ORDER BY r.fecha, r.id")
@Entity
@Table(name = "tratamiento")
public class TratamientoJpa extends RegistroClinicoJpa {


    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false, updatable = false)
    private String descripcion;

    @Column(name = "indicaciones", columnDefinition = "TEXT", updatable = false)
    private String indicaciones;

    /** Requerido por JPA. */
    public TratamientoJpa() {
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
}
