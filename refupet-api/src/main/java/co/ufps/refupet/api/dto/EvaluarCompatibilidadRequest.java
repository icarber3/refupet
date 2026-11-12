package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.adopcion.EvaluateBasicCompatibilityCommand;

/** Cuerpo JSON de {@code POST /api/solicitudes-adopcion/{id}/evaluacion-compatibilidad}. La validación de los campos la hacen el Command y el dominio. */
public record EvaluarCompatibilidadRequest(Boolean compatible, String observaciones) {

    public EvaluateBasicCompatibilityCommand toCommand(String actorId, String solicitudId) {
        return new EvaluateBasicCompatibilityCommand(actorId, solicitudId, compatible, observaciones);
    }
}
