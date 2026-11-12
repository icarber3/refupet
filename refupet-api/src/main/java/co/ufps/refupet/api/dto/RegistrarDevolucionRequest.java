package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionReturnCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/adopciones/{id}/devolucion}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarDevolucionRequest(LocalDate fecha, String motivo) {

    public RegisterAdoptionReturnCommand toCommand(String actorId, String adopcionId) {
        return new RegisterAdoptionReturnCommand(actorId, adopcionId, fecha, motivo);
    }
}
