package co.ufps.refupet.application.dto;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.valueobjects.RegistroIngreso;

import java.time.LocalDate;

/**
 * Trazabilidad básica del ingreso de un animal (regla 5).
 */
public record RegistroIngresoDTO(
        String animalId,
        String nombreAnimal,
        LocalDate fechaIngreso,
        String registradoPorId,
        String procedencia,
        String condicionInicial
) {
    public static RegistroIngresoDTO from(Animal animal) {
        RegistroIngreso ingreso = animal.getRegistroIngreso();
        return new RegistroIngresoDTO(
                animal.getAnimalId().value(),
                animal.getNombre(),
                ingreso.fechaIngreso(),
                ingreso.registradoPor().value(),
                ingreso.procedencia(),
                ingreso.condicionInicial());
    }
}
