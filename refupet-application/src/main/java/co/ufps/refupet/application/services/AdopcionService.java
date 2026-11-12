package co.ufps.refupet.application.services;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.exceptions.AdopcionNotFoundException;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.exceptions.SolicitudAdopcionNotFoundException;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Búsquedas comunes del módulo de adopción, con la excepción de dominio correspondiente
 * cuando el recurso no existe.
 */
@ApplicationScoped
public class AdopcionService {

    private final ISolicitudAdopcionRepository solicitudRepository;
    private final IAdopcionRepository adopcionRepository;
    private final IAnimalRepository animalRepository;

    @Inject
    public AdopcionService(ISolicitudAdopcionRepository solicitudRepository, IAdopcionRepository adopcionRepository,
                           IAnimalRepository animalRepository) {
        this.solicitudRepository = solicitudRepository;
        this.adopcionRepository = adopcionRepository;
        this.animalRepository = animalRepository;
    }

    public SolicitudAdopcion obtenerSolicitud(SolicitudAdopcionId solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId));
    }

    public Adopcion obtenerAdopcion(AdopcionId adopcionId) {
        return adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new AdopcionNotFoundException(adopcionId));
    }

    public Animal obtenerAnimal(AnimalId animalId) {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
    }
}
