package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Veterinario: gestiona información clínica, tratamientos, vacunaciones,
 * esterilizaciones y autorizaciones sanitarias (reglas 33 y 34), incluida la
 * autorización de salida de cuarentena (regla 15).
 */
public final class Veterinario extends Usuario {

    private static final Set<Operacion> OPERACIONES = Collections.unmodifiableSet(EnumSet.of(
            Operacion.GESTIONAR_INFORMACION_CLINICA,
            Operacion.CONSULTAR_HISTORIAL_CLINICO,
            Operacion.GESTIONAR_CUARENTENA,
            Operacion.AUTORIZAR_SALIDA_CUARENTENA,
            Operacion.CONSULTAR_CUARENTENAS,
            Operacion.CONSULTAR_ANIMALES,
            Operacion.CONSULTAR_ANIMALES_DISPONIBLES));

    private Veterinario(UsuarioId usuarioId, String nombre, Email email) {
        super(usuarioId, nombre, email);
    }

    public static Veterinario create(String nombre, Email email) {
        return new Veterinario(UsuarioId.generate(), nombre, email);
    }

    public static Veterinario of(UsuarioId usuarioId, String nombre, Email email,
                                 EstadoCuenta estadoCuenta, LocalDateTime fechaRegistro) {
        Veterinario veterinario = new Veterinario(usuarioId, nombre, email);
        veterinario.restaurar(estadoCuenta, fechaRegistro);
        return veterinario;
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.VETERINARIO;
    }

    @Override
    protected Set<Operacion> operacionesPermitidas() {
        return OPERACIONES;
    }
}
