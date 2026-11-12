package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.EvaluacionCompatibilidad;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.SolicitudAdopcionJpa;

import java.util.Objects;

/** Data Mapper del agregado SolicitudAdopcion. Reconstruye con {@code SolicitudAdopcion.of}. */
public final class SolicitudAdopcionMapper {

    private SolicitudAdopcionMapper() {
    }

    public static SolicitudAdopcionJpa toJpa(SolicitudAdopcion solicitud) {
        Objects.requireNonNull(solicitud, "solicitud");
        SolicitudAdopcionJpa jpa = new SolicitudAdopcionJpa();
        jpa.setId(solicitud.getSolicitudId().value());
        jpa.setAnimalId(solicitud.getAnimalId().value());
        jpa.setSolicitanteId(solicitud.getSolicitanteId().value());
        jpa.setComentarios(solicitud.getComentarios());
        jpa.setFechaSolicitud(solicitud.getFechaSolicitud());
        jpa.setEstado(solicitud.getEstado());
        solicitud.getEvaluacionCompatibilidad().ifPresent(evaluacion -> {
            jpa.setCompatibilidadCompatible(evaluacion.compatible());
            jpa.setCompatibilidadObservaciones(evaluacion.observaciones());
            jpa.setCompatibilidadEvaluadoPor(evaluacion.evaluadoPor().value());
            jpa.setCompatibilidadFecha(evaluacion.fechaEvaluacion());
        });
        jpa.setDecididaPor(solicitud.getDecididaPor() == null ? null : solicitud.getDecididaPor().value());
        jpa.setFechaDecision(solicitud.getFechaDecision());
        jpa.setMotivoRechazo(solicitud.getMotivoRechazo());
        return jpa;
    }

    public static SolicitudAdopcion toDomain(SolicitudAdopcionJpa jpa) {
        Objects.requireNonNull(jpa, "solicitudAdopcionJpa");
        // ck_solicitud_compatibilidad: compatible, evaluado_por y fecha son todos nulos o todos no nulos
        EvaluacionCompatibilidad evaluacion = jpa.getCompatibilidadCompatible() == null ? null
                : new EvaluacionCompatibilidad(
                        jpa.getCompatibilidadCompatible(),
                        jpa.getCompatibilidadObservaciones(),
                        UsuarioId.of(jpa.getCompatibilidadEvaluadoPor()),
                        jpa.getCompatibilidadFecha());
        return SolicitudAdopcion.of(
                SolicitudAdopcionId.of(jpa.getId()),
                AnimalId.of(jpa.getAnimalId()),
                UsuarioId.of(jpa.getSolicitanteId()),
                jpa.getComentarios(),
                jpa.getFechaSolicitud(),
                jpa.getEstado(),
                evaluacion,
                jpa.getDecididaPor() == null ? null : UsuarioId.of(jpa.getDecididaPor()),
                jpa.getFechaDecision(),
                jpa.getMotivoRechazo());
    }
}
