package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.valueobjects.Devolucion;

import java.time.LocalDate;
import java.util.List;

/**
 * Adopción formalizada con sus seguimientos y, si existe, la devolución.
 */
public record AdopcionDTO(
        String adopcionId,
        String solicitudId,
        String animalId,
        String solicitanteId,
        LocalDate fechaFormalizacion,
        String estado,
        List<SeguimientoAdopcionDTO> seguimientos,
        LocalDate fechaDevolucion,
        String motivoDevolucion
) {
    public static AdopcionDTO from(Adopcion adopcion) {
        Devolucion devolucion = adopcion.getDevolucion().orElse(null);
        return new AdopcionDTO(
                adopcion.getAdopcionId().value(),
                adopcion.getSolicitudId().value(),
                adopcion.getAnimalId().value(),
                adopcion.getSolicitanteId().value(),
                adopcion.getFechaFormalizacion(),
                adopcion.getEstado().name(),
                adopcion.getSeguimientos().stream().map(SeguimientoAdopcionDTO::from).toList(),
                devolucion == null ? null : devolucion.fecha(),
                devolucion == null ? null : devolucion.motivo());
    }
}
