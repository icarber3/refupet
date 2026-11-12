package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.valueobjects.EstadoSalud;

import java.time.LocalDate;

/**
 * Información general de un animal (regla 3).
 * ubicacion y estadoSalud son null mientras no se hayan asignado.
 */
public record AnimalDTO(
        String animalId,
        String nombre,
        String especie,
        String raza,
        String descripcion,
        LocalDate fechaIngreso,
        String ubicacion,
        String estadoSalud,
        boolean esterilizado,
        int cantidadObservaciones
) {
    public static AnimalDTO from(Animal animal) {
        return new AnimalDTO(
                animal.getAnimalId().value(),
                animal.getNombre(),
                animal.getEspecie().name(),
                animal.getRaza(),
                animal.getDescripcion(),
                animal.getRegistroIngreso().fechaIngreso(),
                animal.getUbicacion().map(Enum::name).orElse(null),
                animal.getEstadoSalud().map(EstadoSalud::descripcion).orElse(null),
                animal.isEsterilizado(),
                animal.getObservaciones().size());
    }
}
