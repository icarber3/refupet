package co.ufps.refupet.domain.enums;

import co.ufps.refupet.domain.Validaciones;

/**
 * Especie del animal.
 * Decisión: enum y no entidad, porque el enunciado no pide administrar especies
 * (alta/baja/edición); solo clasificar a cada animal.
 */
public enum Especie {
    PERRO,
    GATO,
    OTRA;

    public static Especie fromString(String dato) {
        return Validaciones.enumDesdeTexto(Especie.class, dato, "especie");
    }
}
