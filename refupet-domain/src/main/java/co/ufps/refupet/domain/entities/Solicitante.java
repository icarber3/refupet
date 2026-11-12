package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
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
 * Solicitante de adopción: consulta animales disponibles y presenta solicitudes (reglas 37 y 38).
 *
 * Además de los datos comunes de Usuario guarda datos de contacto opcionales
 * (teléfono y dirección), que forman parte de la "información del solicitante" (regla 20).
 * No se declaran obligatorios porque el enunciado no lo exige.
 */
public final class Solicitante extends Usuario {

    private static final Set<Operacion> OPERACIONES = Collections.unmodifiableSet(EnumSet.of(
            Operacion.CONSULTAR_ANIMALES_DISPONIBLES,
            Operacion.PRESENTAR_SOLICITUD_ADOPCION));

    private String telefono;
    private String direccion;

    private Solicitante(UsuarioId usuarioId, String nombre, Email email, String telefono, String direccion) {
        super(usuarioId, nombre, email);
        this.telefono = Validaciones.textoOpcional(telefono);
        this.direccion = Validaciones.textoOpcional(direccion);
    }

    public static Solicitante create(String nombre, Email email, String telefono, String direccion) {
        return new Solicitante(UsuarioId.generate(), nombre, email, telefono, direccion);
    }

    public static Solicitante of(UsuarioId usuarioId, String nombre, Email email, String telefono,
                                 String direccion, EstadoCuenta estadoCuenta, LocalDateTime fechaRegistro) {
        Solicitante solicitante = new Solicitante(usuarioId, nombre, email, telefono, direccion);
        solicitante.restaurar(estadoCuenta, fechaRegistro);
        return solicitante;
    }

    /** Actualiza los datos de contacto del solicitante. */
    public void actualizarContacto(String nuevoTelefono, String nuevaDireccion) {
        this.telefono = Validaciones.textoOpcional(nuevoTelefono);
        this.direccion = Validaciones.textoOpcional(nuevaDireccion);
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.SOLICITANTE;
    }

    @Override
    protected Set<Operacion> operacionesPermitidas() {
        return OPERACIONES;
    }

    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
}
