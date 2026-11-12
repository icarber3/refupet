package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.RegisterDewormingCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/desparasitaciones}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarDesparasitacionRequest(LocalDate fecha, String producto, String observaciones) {

    public RegisterDewormingCommand toCommand(String actorId, String animalId) {
        return new RegisterDewormingCommand(actorId, animalId, fecha, producto, observaciones);
    }
}
