package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Modelo de persistencia de la tabla {@code seguimiento_adopcion}: entidad interna del agregado
 * Adopcion (sin repositorio propio), inmutable una vez registrada.
 */
@Entity
@Table(name = "seguimiento_adopcion")
public class SeguimientoAdopcionJpa {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    /** Lado dueño de la composición Adopcion 1—N Seguimiento (fk_seguimiento_adopcion). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adopcion_id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private AdopcionJpa adopcion;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDate fecha;

    @Column(name = "observaciones", columnDefinition = "TEXT", nullable = false, updatable = false)
    private String observaciones;

    @Column(name = "registrado_por", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String registradoPor;

    /** Requerido por JPA. */
    public SeguimientoAdopcionJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public AdopcionJpa getAdopcion() { return adopcion; }
    public void setAdopcion(AdopcionJpa adopcion) { this.adopcion = adopcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }
}
