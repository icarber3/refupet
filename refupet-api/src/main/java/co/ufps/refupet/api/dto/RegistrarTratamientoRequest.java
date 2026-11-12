package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.RegisterTreatmentCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/tratamientos}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarTratamientoRequest(LocalDate fechaInicio, String descripcion, String indicaciones, String observaciones) {

    public RegisterTreatmentCommand toCommand(String actorId, String animalId) {
        return new RegisterTreatmentCommand(actorId, animalId, fechaInicio, descripcion, indicaciones, observaciones);
    }
}
