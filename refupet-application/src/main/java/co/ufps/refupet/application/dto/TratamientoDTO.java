package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Tratamiento;

import java.time.LocalDate;

public record TratamientoDTO(
        String tratamientoId,
        String animalId,
        String veterinarioId,
        LocalDate fechaInicio,
        String descripcion,
        String indicaciones,
        String observaciones
) {
    public static TratamientoDTO from(Tratamiento tratamiento) {
        return new TratamientoDTO(
                tratamiento.getId().value(),
                tratamiento.getAnimalId().value(),
                tratamiento.getVeterinarioId().value(),
                tratamiento.getFechaInicio(),
                tratamiento.getDescripcion(),
                tratamiento.getIndicaciones(),
                tratamiento.getObservaciones());
    }
}
