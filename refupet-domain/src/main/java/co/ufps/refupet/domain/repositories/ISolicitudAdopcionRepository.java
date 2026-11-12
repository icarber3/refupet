package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de solicitudes de adopcion.
 */
public interface ISolicitudAdopcionRepository {
    SolicitudAdopcion save(SolicitudAdopcion solicitud);

    Optional<SolicitudAdopcion> findById(SolicitudAdopcionId solicitudId);

    List<SolicitudAdopcion> findAll();

    List<SolicitudAdopcion> findByEstado(EstadoSolicitudAdopcion estado);

    List<SolicitudAdopcion> findBySolicitanteId(UsuarioId solicitanteId);
}
