package co.ufps.refupet.domain.enums;

import co.ufps.refupet.domain.Validaciones;

/**
 * Perfiles de usuario del refugio.
 * Decisión: enum y no entidad, porque los perfiles son un conjunto cerrado
 * definido por el enunciado y no tienen datos propios que gestionar.
 */
public enum RolUsuario {
    ADMINISTRADOR,
    VETERINARIO,
    VOLUNTARIO,
    SOLICITANTE;

    public static RolUsuario fromString(String dato) {
        return Validaciones.enumDesdeTexto(RolUsuario.class, dato, "rol");
    }
}
