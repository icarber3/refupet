package co.ufps.refupet.application.commands.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.AdopcionId;

import java.time.LocalDate;

/**
 * Regla 24: registrar un seguimiento posterior a la adopción.
 */
public record RegisterAdoptionFollowUpCommand(String actorId, String adopcionId, LocalDate fecha, String observaciones) {
    public RegisterAdoptionFollowUpCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(adopcionId, "adopcionId");
        Entrada.requerido(observaciones, "observaciones");
    }

    public AdopcionId toAdopcionId() { return AdopcionId.of(adopcionId); }
}
