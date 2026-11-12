package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.animal.RegisterAnimalCommand;
import java.time.LocalDate;

/** Cuerpo JSON de {@code POST /api/animales}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarAnimalRequest(String nombre, String especie, String raza, String descripcion, LocalDate fechaIngreso, String procedencia, String condicionInicial) {

    public RegisterAnimalCommand toCommand(String actorId) {
        return new RegisterAnimalCommand(actorId, nombre, especie, raza, descripcion, fechaIngreso, procedencia, condicionInicial);
    }
}
