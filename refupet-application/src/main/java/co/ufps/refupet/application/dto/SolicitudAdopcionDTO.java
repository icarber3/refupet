package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.valueobjects.EvaluacionCompatibilidad;

import java.time.LocalDateTime;

/**
 * Datos de una solicitud de adopción. compatible es null mientras no se haya evaluado.
 */
public record SolicitudAdopcionDTO(
        String solicitudId,
        String animalId,
        String solicitanteId,
        LocalDateTime fechaSolicitud,
        String comentarios,
        String estado,
        Boolean compatible,
        String observacionesCompatibilidad,
        String decididaPorId,
        LocalDateTime fechaDecision,
        String motivoRechazo
) {
    public static SolicitudAdopcionDTO from(SolicitudAdopcion solicitud) {
        EvaluacionCompatibilidad evaluacion = solicitud.getEvaluacionCompatibilidad().orElse(null);
        return new SolicitudAdopcionDTO(
                solicitud.getSolicitudId().value(),
                solicitud.getAnimalId().value(),
                solicitud.getSolicitanteId().value(),
                solicitud.getFechaSolicitud(),
                solicitud.getComentarios(),
                solicitud.getEstado().name(),
                evaluacion == null ? null : evaluacion.compatible(),
                evaluacion == null ? null : evaluacion.observaciones(),
                solicitud.getDecididaPor() == null ? null : solicitud.getDecididaPor().value(),
                solicitud.getFechaDecision(),
                solicitud.getMotivoRechazo());
    }
}
