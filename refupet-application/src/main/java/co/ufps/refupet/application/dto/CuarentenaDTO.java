package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;

import java.time.LocalDate;

/**
 * Datos de una cuarentena, incluido el control del periodo mínimo a la fecha de consulta.
 */
public record CuarentenaDTO(
        String cuarentenaId,
        String animalId,
        String nombreAnimal,
        LocalDate fechaInicio,
        int diasMinimos,
        LocalDate fechaMinimaSalida,
        boolean periodoMinimoCumplido,
        long diasRestantes,
        String estado,
        String salidaAutorizadaPorId,
        LocalDate fechaAutorizacionSalida,
        LocalDate fechaFinalizacion
) {
    public static CuarentenaDTO from(Cuarentena cuarentena, String nombreAnimal, LocalDate fechaConsulta) {
        PeriodoCuarentena periodo = cuarentena.getPeriodo();
        return new CuarentenaDTO(
                cuarentena.getCuarentenaId().value(),
                cuarentena.getAnimalId().value(),
                nombreAnimal,
                periodo.fechaInicio(),
                periodo.diasMinimos(),
                periodo.fechaMinimaSalida(),
                cuarentena.periodoMinimoCumplido(fechaConsulta),
                periodo.diasRestantes(fechaConsulta),
                cuarentena.getEstado().name(),
                cuarentena.getSalidaAutorizadaPor() == null ? null : cuarentena.getSalidaAutorizadaPor().value(),
                cuarentena.getFechaAutorizacionSalida(),
                cuarentena.getFechaFinalizacion());
    }
}
