package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.cuarentena.StartQuarantineCommand;

import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/cuarentenas}. La validación de los campos la hacen el Command y el dominio. */
public record IniciarCuarentenaRequest(LocalDate fechaInicio, Integer diasMinimos) {

    /**
     * {@code diasMinimos} es obligatorio: el enunciado no fija un periodo por defecto. Se recibe como
     * {@code Integer} para distinguir "ausente" (400) de un valor, que el dominio valida (> 0).
     */
    public StartQuarantineCommand toCommand(String actorId, String animalId) {
        if (diasMinimos == null) {
            throw new IllegalArgumentException("El campo 'diasMinimos' es obligatorio");
        }
        return new StartQuarantineCommand(actorId, animalId, fechaInicio, diasMinimos);
    }
}
