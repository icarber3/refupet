package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Modelo de persistencia de la tabla {@code observacion_comportamiento}: entidad interna del
 * agregado Animal (no tiene repositorio propio). Es inmutable una vez registrada.
 * {@code registrado_por} referencia a un usuario por identificador.
 */
@Entity
@Table(name = "observacion_comportamiento")
public class ObservacionComportamientoJpa {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    /** Lado dueño de la composición Animal 1—N Observación (fk_observacion_animal). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private AnimalJpa animal;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false, updatable = false)
    private String descripcion;

    @Column(name = "registrado_por", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String registradoPor;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** Requerido por JPA. */
    public ObservacionComportamientoJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public AnimalJpa getAnimal() { return animal; }
    public void setAnimal(AnimalJpa animal) { this.animal = animal; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
