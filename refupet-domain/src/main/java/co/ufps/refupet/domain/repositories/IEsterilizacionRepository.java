package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.ids.AnimalId;

import java.util.List;

/**
 * Puerto de persistencia de esterilizaciones.
 * Solo expone lo que necesitan los casos de uso: registrar y consultar el historial de un animal.
 */
public interface IEsterilizacionRepository {
    Esterilizacion save(Esterilizacion registro);

    List<Esterilizacion> findByAnimalId(AnimalId animalId);
}
