package co.ufps.refupet.domain.enums;

import co.ufps.refupet.domain.Validaciones;

/**
 * Ubicación del animal dentro del refugio, según el módulo de cuarentena:
 * el animal está en cuarentena o ha pasado a población general.
 * Un animal recién registrado aún no tiene ubicación asignada (se modela como ausencia de valor,
 * sin inventar un estado adicional).
 */
public enum EstadoUbicacionAnimal {
    CUARENTENA,
    POBLACION_GENERAL;

    public static EstadoUbicacionAnimal fromString(String dato) {
        return Validaciones.enumDesdeTexto(EstadoUbicacionAnimal.class, dato, "ubicacion");
    }
}
