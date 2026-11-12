package co.ufps.refupet.application.commands.cuarentena;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.ids.CuarentenaId;

/**
 * Regla 16: registrar el paso del animal a poblacion general.
 */
public record MoveAnimalToGeneralPopulationCommand(String actorId, String cuarentenaId) {
    public MoveAnimalToGeneralPopulationCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(cuarentenaId, "cuarentenaId");
    }

    public CuarentenaId toCuarentenaId() {
        return CuarentenaId.of(cuarentenaId);
    }
}
