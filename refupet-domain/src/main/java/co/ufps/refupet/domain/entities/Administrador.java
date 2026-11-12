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
 * Administrador: gestiona usuarios, supervisa el refugio y decide sobre las
 * solicitudes de adopción (reglas 22 y 32).
 */
public final class Administrador extends Usuario {

    private static final Set<Operacion> OPERACIONES = Collections.unmodifiableSet(EnumSet.of(
            Operacion.GESTIONAR_USUARIOS,
            Operacion.GESTIONAR_SOLICITUDES_ADOPCION,
            Operacion.GESTIONAR_ADOPCIONES,
            Operacion.CONSULTAR_ANIMALES,
            Operacion.CONSULTAR_ANIMALES_DISPONIBLES,
            Operacion.CONSULTAR_HISTORIAL_CLINICO,
            Operacion.CONSULTAR_CUARENTENAS));

    private Administrador(UsuarioId usuarioId, String nombre, Email email) {
        super(usuarioId, nombre, email);
    }

    public static Administrador create(String nombre, Email email) {
        return new Administrador(UsuarioId.generate(), nombre, email);
    }

    public static Administrador of(UsuarioId usuarioId, String nombre, Email email,
                                   EstadoCuenta estadoCuenta, LocalDateTime fechaRegistro) {
        Administrador administrador = new Administrador(usuarioId, nombre, email);
        administrador.restaurar(estadoCuenta, fechaRegistro);
        return administrador;
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.ADMINISTRADOR;
    }

    @Override
    protected Set<Operacion> operacionesPermitidas() {
        return OPERACIONES;
    }
}
