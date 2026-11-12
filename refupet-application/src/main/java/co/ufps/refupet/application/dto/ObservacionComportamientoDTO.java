package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.ObservacionComportamiento;

import java.time.LocalDateTime;

public record ObservacionComportamientoDTO(
        String observacionId,
        String descripcion,
        String registradoPorId,
        LocalDateTime fechaRegistro
) {
    public static ObservacionComportamientoDTO from(ObservacionComportamiento observacion) {
        return new ObservacionComportamientoDTO(
                observacion.getObservacionId().value(),
                observacion.getDescripcion(),
                observacion.getRegistradoPor().value(),
                observacion.getFechaRegistro());
    }
}
