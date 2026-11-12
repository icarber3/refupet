package co.ufps.refupet.application.commands.animal;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.enums.Especie;

import java.time.LocalDate;

/**
 * Regla 1 y 35: el voluntario registra el ingreso de un animal.
 * fechaIngreso es opcional (por defecto, hoy); raza, descripcion, procedencia y
 * condicionInicial son opcionales.
 */
public record RegisterAnimalCommand(
        String actorId,
        String nombre,
        String especie,
        String raza,
        String descripcion,
        LocalDate fechaIngreso,
        String procedencia,
        String condicionInicial
) {
    public RegisterAnimalCommand {
        Entrada.requerido(actorId, "actorId");
        Entrada.requerido(nombre, "nombre");
        Entrada.requerido(especie, "especie");
    }

    public Especie toEspecie() { return Especie.fromString(especie); }
}
