package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.FormalizeAdoptionCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/adopciones}. La validación de los campos la hacen el Command y el dominio. */
public record FormalizarAdopcionRequest(String solicitudId, LocalDate fecha) {

    public FormalizeAdoptionCommand toCommand(String actorId) {
        return new FormalizeAdoptionCommand(actorId, solicitudId, fecha);
    }
}
