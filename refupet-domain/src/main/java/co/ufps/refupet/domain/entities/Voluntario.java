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
 * Voluntario: registra ingresos de animales y observaciones de comportamiento (reglas 35 y 36).
 */
public final class Voluntario extends Usuario {

    private static final Set<Operacion> OPERACIONES = Collections.unmodifiableSet(EnumSet.of(
            Operacion.REGISTRAR_INGRESO_ANIMAL,
            Operacion.REGISTRAR_OBSERVACION_COMPORTAMIENTO,
            Operacion.CONSULTAR_ANIMALES,
            Operacion.CONSULTAR_ANIMALES_DISPONIBLES));

    private Voluntario(UsuarioId usuarioId, String nombre, Email email) {
        super(usuarioId, nombre, email);
    }

    public static Voluntario create(String nombre, Email email) {
        return new Voluntario(UsuarioId.generate(), nombre, email);
    }

    public static Voluntario of(UsuarioId usuarioId, String nombre, Email email,
                                EstadoCuenta estadoCuenta, LocalDateTime fechaRegistro) {
        Voluntario voluntario = new Voluntario(usuarioId, nombre, email);
        voluntario.restaurar(estadoCuenta, fechaRegistro);
        return voluntario;
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.VOLUNTARIO;
    }

    @Override
    protected Set<Operacion> operacionesPermitidas() {
        return OPERACIONES;
    }
}
