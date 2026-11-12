package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.RegisterVaccinationCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/vacunaciones}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarVacunacionRequest(LocalDate fecha, String vacuna, String observaciones) {

    public RegisterVaccinationCommand toCommand(String actorId, String animalId) {
        return new RegisterVaccinationCommand(actorId, animalId, fecha, vacuna, observaciones);
    }
}
