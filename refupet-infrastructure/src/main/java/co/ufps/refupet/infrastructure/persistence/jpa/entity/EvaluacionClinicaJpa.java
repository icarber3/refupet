package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Modelo de persistencia de la tabla {@code evaluacion_clinica}: evaluación clínica (regla 6).
 * Hereda las columnas comunes de {@link RegistroClinicoJpa}.
 */
@NamedQuery(name = "EvaluacionClinicaJpa" + RegistroClinicoJpa.POR_ANIMAL,
        query = "SELECT r FROM EvaluacionClinicaJpa r WHERE r.animalId = :animalId ORDER BY r.fecha, r.id")
@Entity
@Table(name = "evaluacion_clinica")
public class EvaluacionClinicaJpa extends RegistroClinicoJpa {


    @Column(name = "hallazgos", columnDefinition = "TEXT", nullable = false, updatable = false)
    private String hallazgos;

    @Column(name = "diagnostico", columnDefinition = "TEXT", updatable = false)
    private String diagnostico;

    /** Requerido por JPA. */
    public EvaluacionClinicaJpa() {
    }

    public String getHallazgos() { return hallazgos; }
    public void setHallazgos(String hallazgos) { this.hallazgos = hallazgos; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
}
