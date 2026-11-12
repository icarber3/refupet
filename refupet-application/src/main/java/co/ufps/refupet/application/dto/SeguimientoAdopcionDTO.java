package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.SeguimientoAdopcion;

import java.time.LocalDate;

public record SeguimientoAdopcionDTO(
        String seguimientoId,
        LocalDate fecha,
        String observaciones,
        String registradoPorId
) {
    public static SeguimientoAdopcionDTO from(SeguimientoAdopcion seguimiento) {
        return new SeguimientoAdopcionDTO(
                seguimiento.getSeguimientoId().value(),
                seguimiento.getFecha(),
                seguimiento.getObservaciones(),
                seguimiento.getRegistradoPor().value());
    }
}
