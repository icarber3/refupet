package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionFollowUpCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/adopciones/{id}/seguimientos}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarSeguimientoRequest(LocalDate fecha, String observaciones) {

    public RegisterAdoptionFollowUpCommand toCommand(String actorId, String adopcionId) {
        return new RegisterAdoptionFollowUpCommand(actorId, adopcionId, fecha, observaciones);
    }
}
