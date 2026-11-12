package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de adopciones formalizadas (incluye seguimientos y devolucion).
 */
public interface IAdopcionRepository {
    Adopcion save(Adopcion adopcion);

    Optional<Adopcion> findById(AdopcionId adopcionId);

    Optional<Adopcion> findBySolicitudId(SolicitudAdopcionId solicitudId);

    List<Adopcion> findByAnimalId(AnimalId animalId);
}
