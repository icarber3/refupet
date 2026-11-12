package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import co.ufps.refupet.domain.enums.EstadoCuarentena;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Modelo de persistencia de la tabla {@code cuarentena} (agregado Cuarentena).
 * El Value Object {@code PeriodoCuarentena} se aplana en fecha_inicio y dias_minimos.
 *
 * La columna generada {@code animal_id_activa} (STORED GENERATED, base de uq_cuarentena_activa_por_animal)
 * NO se mapea: la calcula la base de datos y nunca debe escribirse.
 * Coherencia estado/columnas: ck_cuarentena_estado, ck_cuarentena_periodo_cumplido,
 * ck_cuarentena_fecha_finalizacion y ck_cuarentena_dias_minimos (las garantiza el dominio).
 */
@NamedQuery(name = CuarentenaJpa.POR_ANIMAL,
        query = "SELECT c FROM CuarentenaJpa c WHERE c.animalId = :animalId ORDER BY c.fechaInicio, c.id")
@Entity
@Table(name = "cuarentena")
public class CuarentenaJpa {

    public static final String POR_ANIMAL = "CuarentenaJpa.porAnimal";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String animalId;

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDate fechaInicio;

    @Column(name = "dias_minimos", nullable = false, updatable = false)
    private int diasMinimos;

    @Column(name = "iniciada_por", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String iniciadaPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('EN_CURSO','SALIDA_AUTORIZADA','FINALIZADA')")
    private EstadoCuarentena estado;

    @Column(name = "salida_autorizada_por", columnDefinition = "CHAR(36)", length = 36)
    private String salidaAutorizadaPor;

    @Column(name = "fecha_autorizacion_salida")
    private LocalDate fechaAutorizacionSalida;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    /** Requerido por JPA. */
    public CuarentenaJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAnimalId() { return animalId; }
    public void setAnimalId(String animalId) { this.animalId = animalId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public int getDiasMinimos() { return diasMinimos; }
    public void setDiasMinimos(int diasMinimos) { this.diasMinimos = diasMinimos; }

    public String getIniciadaPor() { return iniciadaPor; }
    public void setIniciadaPor(String iniciadaPor) { this.iniciadaPor = iniciadaPor; }

    public EstadoCuarentena getEstado() { return estado; }
    public void setEstado(EstadoCuarentena estado) { this.estado = estado; }

    public String getSalidaAutorizadaPor() { return salidaAutorizadaPor; }
    public void setSalidaAutorizadaPor(String salidaAutorizadaPor) { this.salidaAutorizadaPor = salidaAutorizadaPor; }

    public LocalDate getFechaAutorizacionSalida() { return fechaAutorizacionSalida; }
    public void setFechaAutorizacionSalida(LocalDate fechaAutorizacionSalida) { this.fechaAutorizacionSalida = fechaAutorizacionSalida; }

    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDate fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
}
