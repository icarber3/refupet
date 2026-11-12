package co.ufps.refupet.application.commands.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.CuarentenaId;

/**
 * Regla 15: el veterinario autoriza la salida de cuarentena.
 */
public record AuthorizeQuarantineExitCommand(String actorId, String cuarentenaId) {
    public AuthorizeQuarantineExitCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(cuarentenaId, "cuarentenaId");
    }

    public CuarentenaId toCuarentenaId() {
        return CuarentenaId.of(cuarentenaId);
    }
}
