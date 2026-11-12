package co.ufps.refupet.application.queries.adopcion;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;

import java.util.Optional;

/**
 * Lista las solicitudes de adopción, opcionalmente filtradas por estado.
 */
public record ListAdoptionRequestsQuery(String actorId, String estado) {
    public ListAdoptionRequestsQuery {
        Entrada.requerido(actorId, "actorId");
    }

    public Optional<EstadoSolicitudAdopcion> toEstado() {
        return (estado == null || estado.isBlank())
                ? Optional.empty()
                : Optional.of(EstadoSolicitudAdopcion.fromString(estado));
    }
}
