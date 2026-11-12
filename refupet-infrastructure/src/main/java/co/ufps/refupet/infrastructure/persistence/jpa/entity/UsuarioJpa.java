package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Modelo de persistencia de la tabla {@code usuario} (Data Mapper: no es la entidad de dominio).
 *
 * Decisión: una sola clase plana, sin {@code @Inheritance}. La tabla guarda los cuatro perfiles con la
 * columna {@code rol} como discriminador; el polimorfismo (Administrador, Veterinario, Voluntario,
 * Solicitante) vive en el dominio y {@code UsuarioMapper} elige la subclase según {@code rol}.
 *
 * Correspondencia con el esquema existente (validado con generation=validate):
 * <pre>
 * id             CHAR(36)      NOT NULL  PK
 * rol            ENUM(...)     NOT NULL
 * nombre         VARCHAR(150)  NOT NULL  (ck_usuario_nombre)
 * email          VARCHAR(254)  NOT NULL  UNIQUE uq_usuario_email (ck_usuario_email)
 * estado_cuenta  ENUM(...)     NOT NULL
 * fecha_registro DATETIME      NOT NULL
 * telefono       VARCHAR(30)   NULL      (solo SOLICITANTE: ck_usuario_contacto_solicitante)
 * direccion      VARCHAR(255)  NULL      (solo SOLICITANTE: ck_usuario_contacto_solicitante)
 * </pre>
 */
@NamedQuery(name = UsuarioJpa.POR_EMAIL, query = "SELECT u FROM UsuarioJpa u WHERE u.email = :email")
@NamedQuery(name = UsuarioJpa.CONTAR_POR_EMAIL, query = "SELECT COUNT(u) FROM UsuarioJpa u WHERE u.email = :email")
@NamedQuery(name = UsuarioJpa.POR_ESTADO_CUENTA,
        query = "SELECT u FROM UsuarioJpa u WHERE u.estadoCuenta = :estado ORDER BY u.fechaRegistro, u.id")
@Entity
@Table(name = "usuario",
        uniqueConstraints = @UniqueConstraint(name = "uq_usuario_email", columnNames = "email"))
public class UsuarioJpa {

    public static final String POR_EMAIL = "UsuarioJpa.porEmail";
    public static final String CONTAR_POR_EMAIL = "UsuarioJpa.contarPorEmail";
    public static final String POR_ESTADO_CUENTA = "UsuarioJpa.porEstadoCuenta";

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private String id;

    /** El rol no cambia durante la vida del usuario: no se actualiza. */
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, updatable = false,
            columnDefinition = "ENUM('ADMINISTRADOR','VETERINARIO','VOLUNTARIO','SOLICITANTE')")
    private RolUsuario rol;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false,
            columnDefinition = "ENUM('PENDIENTE_APROBACION','ACTIVA','SUSPENDIDA')")
    private EstadoCuenta estadoCuenta;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    /** Requerido por JPA. */
    public UsuarioJpa() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public EstadoCuenta getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(EstadoCuenta estadoCuenta) { this.estadoCuenta = estadoCuenta; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
