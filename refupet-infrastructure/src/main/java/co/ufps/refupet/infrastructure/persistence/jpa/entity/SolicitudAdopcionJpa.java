package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Modelo de persistencia de la tabla {@code solicitud_adopcion} (agregado SolicitudAdopcion).
 * El Value Object {@code EvaluacionCompatibilidad} se aplana en compatibilidad_* (ck_solicitud_compatibilidad).
 *
 * La columna generada {@code animal_id_pendiente} (STORED GENERATED, base de uq_solicitud_pendiente)
 * NO se mapea: la calcula la base de datos y nunca debe escribirse.
 * Coherencia: ck_solicitud_decision y ck_solicitud_motivo_rechazo (las garantiza el dominio).
 */
@NamedQuery(name = SolicitudAdopcionJpa.TODAS,
        query = "SELECT s FROM SolicitudAdopcionJpa s ORDER BY s.fechaSolicitud, s.id")
@NamedQuery(name = SolicitudAdopcionJpa.POR_ESTADO,
        query = "SELECT s FROM SolicitudAdopcionJpa s WHERE s.estado = :estado ORDER BY s.fechaSolicitud, s.id")
@NamedQuery(name = SolicitudAdopcionJpa.POR_SOLICITANTE,
        query = "SELECT s FROM SolicitudAdopcionJpa s WHERE s.solicitanteId = :solicitanteId ORDER BY s.fechaSolicitud, s.id")
@Entity
@Table(name = "solicitud_adopcion")
public class SolicitudAdopcionJpa {

    public static final String TODAS = "SolicitudAdopcionJpa.todas";
    public static final String POR_ESTADO = "SolicitudAdopcionJpa.porEstado";
    public static final String POR_SOLICITANTE = "SolicitudAdopcionJpa.porSolicitante";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String animalId;

    @Column(name = "solicitante_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String solicitanteId;

    @Column(name = "comentarios", columnDefinition = "TEXT", updatable = false)
    private String comentarios;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('PENDIENTE','APROBADA','RECHAZADA')")
    private EstadoSolicitudAdopcion estado;

    // ── EvaluacionCompatibilidad (todo o nada; observaciones opcional) ──
    @Column(name = "compatibilidad_compatible")
    private Boolean compatibilidadCompatible;

    @Column(name = "compatibilidad_observaciones", columnDefinition = "TEXT")
    private String compatibilidadObservaciones;

    @Column(name = "compatibilidad_evaluado_por", columnDefinition = "CHAR(36)", length = 36)
    private String compatibilidadEvaluadoPor;

    @Column(name = "compatibilidad_fecha")
    private LocalDateTime compatibilidadFecha;

    // ── Decisión del administrador ──
    @Column(name = "decidida_por", columnDefinition = "CHAR(36)", length = 36)
    private String decididaPor;

    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    /** Requerido por JPA. */
    public SolicitudAdopcionJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAnimalId() { return animalId; }
    public void setAnimalId(String animalId) { this.animalId = animalId; }

    public String getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(String solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public EstadoSolicitudAdopcion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudAdopcion estado) { this.estado = estado; }

    public Boolean getCompatibilidadCompatible() { return compatibilidadCompatible; }
    public void setCompatibilidadCompatible(Boolean compatibilidadCompatible) { this.compatibilidadCompatible = compatibilidadCompatible; }

    public String getCompatibilidadObservaciones() { return compatibilidadObservaciones; }
    public void setCompatibilidadObservaciones(String compatibilidadObservaciones) { this.compatibilidadObservaciones = compatibilidadObservaciones; }

    public String getCompatibilidadEvaluadoPor() { return compatibilidadEvaluadoPor; }
    public void setCompatibilidadEvaluadoPor(String compatibilidadEvaluadoPor) { this.compatibilidadEvaluadoPor = compatibilidadEvaluadoPor; }

    public LocalDateTime getCompatibilidadFecha() { return compatibilidadFecha; }
    public void setCompatibilidadFecha(LocalDateTime compatibilidadFecha) { this.compatibilidadFecha = compatibilidadFecha; }

    public String getDecididaPor() { return decididaPor; }
    public void setDecididaPor(String decididaPor) { this.decididaPor = decididaPor; }

    public LocalDateTime getFechaDecision() { return fechaDecision; }
    public void setFechaDecision(LocalDateTime fechaDecision) { this.fechaDecision = fechaDecision; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
