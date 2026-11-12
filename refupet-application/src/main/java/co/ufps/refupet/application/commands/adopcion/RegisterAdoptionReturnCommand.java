package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AdopcionId;

import java.time.LocalDate;

/**
 * Regla 25: registrar la devolución de un animal adoptado.
 */
public record RegisterAdoptionReturnCommand(String actorId, String adopcionId, LocalDate fecha, String motivo) {
    public RegisterAdoptionReturnCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(adopcionId, "adopcionId");
        Entrada.requerido(motivo, "motivo");
    }

    public AdopcionId toAdopcionId() { return AdopcionId.of(adopcionId); }
}
