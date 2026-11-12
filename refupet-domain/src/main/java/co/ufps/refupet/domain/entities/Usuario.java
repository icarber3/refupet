package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.AccountApprovalNotAllowedException;
import co.ufps.refupet.domain.exceptions.InvalidUserStateException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Abstracción de cualquier persona que usa el sistema.
 *
 * Herencia: Administrador, Veterinario, Voluntario y Solicitante SON usuarios
 * (relación "es-un" real) y comparten identidad, datos y ciclo de vida de la cuenta.
 * Polimorfismo: cada subclase responde de forma distinta a getRol() y a
 * operacionesPermitidas(), y el control de permisos (verificarPermiso) es el mismo para todos.
 *
 * Reglas encapsuladas: 26-31 (ciclo de vida de la cuenta) y 39 (permisos por perfil).
 */
public abstract class Usuario {

    private final UsuarioId usuarioId;
    private String nombre;
    private Email email;
    private EstadoCuenta estadoCuenta;
    private LocalDateTime fechaRegistro;

    protected Usuario(UsuarioId usuarioId, String nombre, Email email) {
        this.usuarioId = Validaciones.obligatorio(usuarioId, "usuarioId");
        this.nombre = Validaciones.textoObligatorio(nombre, "nombre");
        this.email = Validaciones.obligatorio(email, "email");
        // Toda cuenta nueva queda pendiente hasta que un administrador la apruebe (regla 30)
        this.estadoCuenta = EstadoCuenta.PENDIENTE_APROBACION;
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Factory polimórfica: crea el tipo de usuario correspondiente al rol.
     * Los datos de contacto adicionales del solicitante se completan con Solicitante.create.
     */
    public static Usuario create(RolUsuario rol, String nombre, Email email) {
        return switch (Validaciones.obligatorio(rol, "rol")) {
            case ADMINISTRADOR -> Administrador.create(nombre, email);
            case VETERINARIO -> Veterinario.create(nombre, email);
            case VOLUNTARIO -> Voluntario.create(nombre, email);
            case SOLICITANTE -> Solicitante.create(nombre, email, null, null);
        };
    }

    /** Usado por las factories of(...) de las subclases al reconstruir desde persistencia. */
    protected final void restaurar(EstadoCuenta estadoCuenta, LocalDateTime fechaRegistro) {
        this.estadoCuenta = Validaciones.obligatorio(estadoCuenta, "estadoCuenta");
        this.fechaRegistro = Validaciones.obligatorio(fechaRegistro, "fechaRegistro");
    }

    // ── Comportamiento polimórfico ──

    public abstract RolUsuario getRol();

    protected abstract Set<Operacion> operacionesPermitidas();

    // ── Comandos de negocio ──

    /** Indica si el usuario puede ejecutar la operación en este momento. */
    public boolean puedeRealizar(Operacion operacion) {
        return estaActiva() && operacionesPermitidas().contains(operacion);
    }

    /**
     * Regla 39: solo cuentas activas, y solo con las operaciones de su perfil.
     */
    public void verificarPermiso(Operacion operacion) {
        Validaciones.obligatorio(operacion, "operacion");
        if (!estaActiva()) {
            throw new InvalidUserStateException(
                    "La cuenta del usuario " + usuarioId.value() + " no está activa (estado: " + estadoCuenta + ")");
        }
        if (!operacionesPermitidas().contains(operacion)) {
            throw new UnauthorizedRoleOperationException(getRol(), operacion);
        }
    }

    /** Regla 27: modificar información del usuario. */
    public void actualizarDatos(String nuevoNombre, Email nuevoEmail) {
        this.nombre = Validaciones.textoObligatorio(nuevoNombre, "nombre");
        this.email = Validaciones.obligatorio(nuevoEmail, "email");
    }

    /** Regla 30: solo una cuenta pendiente puede aprobarse. */
    public void aprobar() {
        if (estadoCuenta != EstadoCuenta.PENDIENTE_APROBACION) {
            throw new AccountApprovalNotAllowedException(
                    "Solo se puede aprobar una cuenta pendiente; estado actual: " + estadoCuenta);
        }
        this.estadoCuenta = EstadoCuenta.ACTIVA;
    }

    /** Regla 28: solo una cuenta activa puede suspenderse. */
    public void suspender() {
        if (estadoCuenta != EstadoCuenta.ACTIVA) {
            throw new InvalidUserStateException(
                    "Solo se puede suspender una cuenta activa; estado actual: " + estadoCuenta);
        }
        this.estadoCuenta = EstadoCuenta.SUSPENDIDA;
    }

    /** Regla 29: activar (reactivar) una cuenta suspendida. Las pendientes se aprueban, no se activan. */
    public void activar() {
        if (estadoCuenta != EstadoCuenta.SUSPENDIDA) {
            throw new InvalidUserStateException(
                    "Solo se puede activar una cuenta suspendida; estado actual: " + estadoCuenta);
        }
        this.estadoCuenta = EstadoCuenta.ACTIVA;
    }

    public boolean estaActiva() {
        return estadoCuenta == EstadoCuenta.ACTIVA;
    }

    public boolean estaPendienteDeAprobacion() {
        return estadoCuenta == EstadoCuenta.PENDIENTE_APROBACION;
    }

    // ── Getters ──

    public UsuarioId getUsuarioId() { return usuarioId; }
    public String getNombre() { return nombre; }
    public Email getEmail() { return email; }
    public EstadoCuenta getEstadoCuenta() { return estadoCuenta; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
