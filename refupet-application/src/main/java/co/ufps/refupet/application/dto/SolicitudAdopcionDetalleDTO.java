package co.ufps.refupet.application.dto;

/**
 * Solicitud junto con la información del solicitante y del animal correspondiente (regla 20).
 */
public record SolicitudAdopcionDetalleDTO(
        SolicitudAdopcionDTO solicitud,
        SolicitanteDTO solicitante,
        AnimalDTO animal
) {
}
