package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de cuarentenas.
 * Decidir cual cuarentena esta activa es regla de dominio (Cuarentena.estaActiva), no del repositorio.
 */
public interface ICuarentenaRepository {
    Cuarentena save(Cuarentena cuarentena);

    Optional<Cuarentena> findById(CuarentenaId cuarentenaId);

    List<Cuarentena> findByAnimalId(AnimalId animalId);
}
