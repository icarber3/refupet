package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Tratamiento;
import co.ufps.refupet.domain.ids.AnimalId;

import java.util.List;

/**
 * Puerto de persistencia de tratamientos medicos.
 * Solo expone lo que necesitan los casos de uso: registrar y consultar el historial de un animal.
 */
public interface ITratamientoRepository {
    Tratamiento save(Tratamiento registro);

    List<Tratamiento> findByAnimalId(AnimalId animalId);
}
