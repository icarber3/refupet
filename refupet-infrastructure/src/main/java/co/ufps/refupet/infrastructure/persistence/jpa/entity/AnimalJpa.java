package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de persistencia de la tabla {@code animal} (agregado raíz Animal).
 *
 * Los Value Objects del agregado se aplanan en columnas propias:
 * {@code RegistroIngreso} → fecha_ingreso, ingreso_registrado_por, procedencia, condicion_inicial;
 * {@code EstadoSalud} → estado_salud_* (todo o nada: ck_animal_estado_salud).
 *
 * Única relación JPA: la composición con sus observaciones de comportamiento (entidad interna del
 * agregado). Cascada limitada a PERSIST/MERGE: el dominio nunca elimina observaciones.
 * Las referencias a usuarios son identificadores (CHAR(36)), no relaciones.
 */
@NamedQuery(name = AnimalJpa.POR_ID,
        query = "SELECT a FROM AnimalJpa a LEFT JOIN FETCH a.observaciones WHERE a.id = :id")
@NamedQuery(name = AnimalJpa.CONTAR_POR_ID, query = "SELECT COUNT(a) FROM AnimalJpa a WHERE a.id = :id")
@NamedQuery(name = AnimalJpa.TODOS,
        query = "SELECT a FROM AnimalJpa a LEFT JOIN FETCH a.observaciones ORDER BY a.fechaIngreso, a.id")
@NamedQuery(name = AnimalJpa.POR_UBICACION,
        query = "SELECT a FROM AnimalJpa a LEFT JOIN FETCH a.observaciones WHERE a.ubicacion = :ubicacion ORDER BY a.fechaIngreso, a.id")
@Entity
@Table(name = "animal")
public class AnimalJpa {

    // Consultas con nombre: Hibernate las valida contra el modelo al arrancar (sin consultar las tablas).
    // LEFT JOIN FETCH carga las observaciones en la misma consulta; Hibernate 6 elimina raíces duplicadas.
    public static final String POR_ID = "AnimalJpa.porId";
    public static final String CONTAR_POR_ID = "AnimalJpa.contarPorId";
    public static final String TODOS = "AnimalJpa.todos";
    public static final String POR_UBICACION = "AnimalJpa.porUbicacion";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "nombre", length = 100, nullable = false, updatable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false, updatable = false, columnDefinition = "ENUM('PERRO','GATO','OTRA')")
    private Especie especie;

    @Column(name = "raza", length = 100, updatable = false)
    private String raza;

    @Column(name = "descripcion", columnDefinition = "TEXT", updatable = false)
    private String descripcion;

    // ── RegistroIngreso (inmutable) ──
    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDate fechaIngreso;

    @Column(name = "ingreso_registrado_por", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String ingresoRegistradoPor;

    @Column(name = "procedencia", length = 255, updatable = false)
    private String procedencia;

    @Column(name = "condicion_inicial", columnDefinition = "TEXT", updatable = false)
    private String condicionInicial;

    // ── Estado mutable ──
    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion", columnDefinition = "ENUM('CUARENTENA','POBLACION_GENERAL')")
    private EstadoUbicacionAnimal ubicacion;

    @Column(name = "estado_salud_descripcion", columnDefinition = "TEXT")
    private String estadoSaludDescripcion;

    @Column(name = "estado_salud_fecha")
    private LocalDateTime estadoSaludFecha;

    @Column(name = "estado_salud_actualizado_por", columnDefinition = "CHAR(36)", length = 36)
    private String estadoSaludActualizadoPor;

    @Column(name = "esterilizado", nullable = false)
    private boolean esterilizado;

    @OneToMany(mappedBy = "animal", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("fechaRegistro ASC, id ASC")
    private List<ObservacionComportamientoJpa> observaciones = new ArrayList<>();

    /** Requerido por JPA. */
    public AnimalJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getIngresoRegistradoPor() { return ingresoRegistradoPor; }
    public void setIngresoRegistradoPor(String ingresoRegistradoPor) { this.ingresoRegistradoPor = ingresoRegistradoPor; }

    public String getProcedencia() { return procedencia; }
    public void setProcedencia(String procedencia) { this.procedencia = procedencia; }

    public String getCondicionInicial() { return condicionInicial; }
    public void setCondicionInicial(String condicionInicial) { this.condicionInicial = condicionInicial; }

    public EstadoUbicacionAnimal getUbicacion() { return ubicacion; }
    public void setUbicacion(EstadoUbicacionAnimal ubicacion) { this.ubicacion = ubicacion; }

    public String getEstadoSaludDescripcion() { return estadoSaludDescripcion; }
    public void setEstadoSaludDescripcion(String estadoSaludDescripcion) { this.estadoSaludDescripcion = estadoSaludDescripcion; }

    public LocalDateTime getEstadoSaludFecha() { return estadoSaludFecha; }
    public void setEstadoSaludFecha(LocalDateTime estadoSaludFecha) { this.estadoSaludFecha = estadoSaludFecha; }

    public String getEstadoSaludActualizadoPor() { return estadoSaludActualizadoPor; }
    public void setEstadoSaludActualizadoPor(String estadoSaludActualizadoPor) { this.estadoSaludActualizadoPor = estadoSaludActualizadoPor; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public List<ObservacionComportamientoJpa> getObservaciones() { return observaciones; }
    public void setObservaciones(List<ObservacionComportamientoJpa> observaciones) { this.observaciones = observaciones; }
}
