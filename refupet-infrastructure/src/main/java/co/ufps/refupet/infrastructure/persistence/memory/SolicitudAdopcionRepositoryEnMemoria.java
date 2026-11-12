package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador en memoria de ISolicitudAdopcionRepository.
 */
public class SolicitudAdopcionRepositoryEnMemoria extends RepositorioEnMemoria<SolicitudAdopcionId, SolicitudAdopcion>
        implements ISolicitudAdopcionRepository {

    public SolicitudAdopcionRepositoryEnMemoria() {
        super(SolicitudAdopcion::getSolicitudId);
    }

    @Override
    public SolicitudAdopcion save(SolicitudAdopcion solicitud) {
        return guardar(solicitud);
    }

    @Override
    public Optional<SolicitudAdopcion> findById(SolicitudAdopcionId solicitudId) {
        return buscarPorId(solicitudId);
    }

    @Override
    public List<SolicitudAdopcion> findAll() {
        return todos();
    }

    @Override
    public List<SolicitudAdopcion> findByEstado(EstadoSolicitudAdopcion estado) {
        return filtrar(s -> s.getEstado() == estado);
    }

    @Override
    public List<SolicitudAdopcion> findBySolicitanteId(UsuarioId solicitanteId) {
        return filtrar(s -> s.getSolicitanteId().equals(solicitanteId));
    }
}
