package co.ufps.refupet.application.dto;

import java.util.List;

/**
 * Historial clínico de un animal (regla 10): estado de salud vigente y todos sus
 * registros clínicos ordenados por fecha.
 *
 * Decisión: el historial es una vista de lectura que se arma en application a partir de
 * los repositorios clínicos; no es un agregado, por lo que no hay IHistorialClinicoRepository.
 */
public record HistorialClinicoDTO(
        String animalId,
        String nombreAnimal,
        String estadoSalud,
        boolean esterilizado,
        List<RegistroClinicoDTO> registros
) {
}
