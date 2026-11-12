package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;

/**
 * Columnas comunes de las cinco tablas clínicas (evaluacion_clinica, vacunacion, desparasitacion,
 * tratamiento, esterilizacion), que en el esquema son tablas independientes sin tabla base.
 *
 * Decisión: {@code @MappedSuperclass} y no {@code @Inheritance}: reutiliza el mapeo común sin crear
 * una jerarquía polimórfica en JPA (el polimorfismo de RegistroClinico vive en el dominio y cada
 * tipo tiene su propio repositorio). Los registros clínicos son inmutables: ninguna columna se actualiza.
 * {@code animal_id} y {@code veterinario_id} referencian a otros agregados por identificador.
 */
@MappedSuperclass
public abstract class RegistroClinicoJpa {

    /**
     * Sufijo de la consulta con nombre "registros de un animal" que declara cada entidad clínica
     * ({@code <NombreEntidad>.porAnimal}); Hibernate las valida contra el modelo al arrancar.
     */
    public static final String POR_ANIMAL = ".porAnimal";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String animalId;

    @Column(name = "veterinario_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String veterinarioId;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDate fecha;

    @Column(name = "observaciones", columnDefinition = "TEXT", updatable = false)
    private String observaciones;

    protected RegistroClinicoJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAnimalId() { return animalId; }
    public void setAnimalId(String animalId) { this.animalId = animalId; }

    public String getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(String veterinarioId) { this.veterinarioId = veterinarioId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
