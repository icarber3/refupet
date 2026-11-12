package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.clinica.RegisterSterilizationCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales/{id}/esterilizacion}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarEsterilizacionRequest(LocalDate fecha, String observaciones) {

    /** El cuerpo es opcional: sin él, la fecha es la actual y no hay observaciones. */
    public static RegisterSterilizationCommand toCommand(RegistrarEsterilizacionRequest cuerpo, String actorId, String animalId) {
        return cuerpo == null
                ? new RegisterSterilizationCommand(actorId, animalId, null, null)
                : new RegisterSterilizationCommand(actorId, animalId, cuerpo.fecha(), cuerpo.observaciones());
    }
}
