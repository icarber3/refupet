package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import co.ufps.refupet.domain.enums.EstadoAdopcion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de persistencia de la tabla {@code adopcion} (agregado Adopcion).
 * El Value Object {@code Devolucion} se aplana en devolucion_* (ck_adopcion_devolucion: presente si y solo si
 * estado = CON_DEVOLUCION; ck_adopcion_fecha_devolucion).
 *
 * solicitud_id, animal_id y solicitante_id referencian a otros agregados por identificador; en el esquema
 * forman la FK compuesta fk_adopcion_solicitud hacia solicitud_adopcion(id, animal_id, solicitante_id).
 * La columna generada {@code animal_id_vigente} (base de uq_adopcion_vigente_por_animal) NO se mapea.
 *
 * Única relación JPA: la composición con sus seguimientos (cascada PERSIST/MERGE; nunca se eliminan).
 */
@NamedQuery(name = AdopcionJpa.POR_ID,
        query = "SELECT a FROM AdopcionJpa a LEFT JOIN FETCH a.seguimientos WHERE a.id = :id")
@NamedQuery(name = AdopcionJpa.POR_SOLICITUD,
        query = "SELECT a FROM AdopcionJpa a LEFT JOIN FETCH a.seguimientos WHERE a.solicitudId = :solicitudId")
@NamedQuery(name = AdopcionJpa.POR_ANIMAL,
        query = "SELECT a FROM AdopcionJpa a LEFT JOIN FETCH a.seguimientos WHERE a.animalId = :animalId ORDER BY a.fechaFormalizacion, a.id")
@Entity
@Table(name = "adopcion",
        uniqueConstraints = @UniqueConstraint(name = "uq_adopcion_solicitud", columnNames = "solicitud_id"))
public class AdopcionJpa {

    public static final String POR_ID = "AdopcionJpa.porId";
    public static final String POR_SOLICITUD = "AdopcionJpa.porSolicitud";
    public static final String POR_ANIMAL = "AdopcionJpa.porAnimal";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "solicitud_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String solicitudId;

    @Column(name = "animal_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String animalId;

    @Column(name = "solicitante_id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String solicitanteId;

    @Column(name = "fecha_formalizacion", nullable = false, updatable = false)
    private LocalDate fechaFormalizacion;

    @Column(name = "formalizada_por", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String formalizadaPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('FORMALIZADA','CON_SEGUIMIENTO','CON_DEVOLUCION')")
    private EstadoAdopcion estado;

    // ── Devolucion (todo o nada) ──
    @Column(name = "devolucion_fecha")
    private LocalDate devolucionFecha;

    @Column(name = "devolucion_motivo", columnDefinition = "TEXT")
    private String devolucionMotivo;

    @Column(name = "devolucion_registrada_por", columnDefinition = "CHAR(36)", length = 36)
    private String devolucionRegistradaPor;

    @OneToMany(mappedBy = "adopcion", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("fecha ASC, id ASC")
    private List<SeguimientoAdopcionJpa> seguimientos = new ArrayList<>();

    /** Requerido por JPA. */
    public AdopcionJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSolicitudId() { return solicitudId; }
    public void setSolicitudId(String solicitudId) { this.solicitudId = solicitudId; }

    public String getAnimalId() { return animalId; }
    public void setAnimalId(String animalId) { this.animalId = animalId; }

    public String getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(String solicitanteId) { this.solicitanteId = solicitanteId; }

    public LocalDate getFechaFormalizacion() { return fechaFormalizacion; }
    public void setFechaFormalizacion(LocalDate fechaFormalizacion) { this.fechaFormalizacion = fechaFormalizacion; }

    public String getFormalizadaPor() { return formalizadaPor; }
    public void setFormalizadaPor(String formalizadaPor) { this.formalizadaPor = formalizadaPor; }

    public EstadoAdopcion getEstado() { return estado; }
    public void setEstado(EstadoAdopcion estado) { this.estado = estado; }

    public LocalDate getDevolucionFecha() { return devolucionFecha; }
    public void setDevolucionFecha(LocalDate devolucionFecha) { this.devolucionFecha = devolucionFecha; }

    public String getDevolucionMotivo() { return devolucionMotivo; }
    public void setDevolucionMotivo(String devolucionMotivo) { this.devolucionMotivo = devolucionMotivo; }

    public String getDevolucionRegistradaPor() { return devolucionRegistradaPor; }
    public void setDevolucionRegistradaPor(String devolucionRegistradaPor) { this.devolucionRegistradaPor = devolucionRegistradaPor; }

    public List<SeguimientoAdopcionJpa> getSeguimientos() { return seguimientos; }
    public void setSeguimientos(List<SeguimientoAdopcionJpa> seguimientos) { this.seguimientos = seguimientos; }
}
