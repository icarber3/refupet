package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.RegistroClinico;

import java.time.LocalDate;

/**
 * Entrada genérica del historial clínico. Se construye de forma polimórfica a partir
 * de cualquier subclase de RegistroClinico (vacunación, desparasitación, esterilización, etc.).
 */
public record RegistroClinicoDTO(
        String registroId,
        String tipo,
        String animalId,
        String veterinarioId,
        LocalDate fecha,
        String resumen,
        String observaciones
) {
    public static RegistroClinicoDTO from(RegistroClinico registro) {
        return new RegistroClinicoDTO(
                registro.getId().value(),
                registro.getTipo().name(),
                registro.getAnimalId().value(),
                registro.getVeterinarioId().value(),
                registro.getFecha(),
                registro.resumen(),
                registro.getObservaciones());
    }
}
