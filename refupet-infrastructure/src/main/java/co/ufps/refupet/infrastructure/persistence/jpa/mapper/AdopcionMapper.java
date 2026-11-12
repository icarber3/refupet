package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.SeguimientoAdopcion;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SeguimientoAdopcionId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Devolucion;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.AdopcionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.SeguimientoAdopcionJpa;

import java.util.List;
import java.util.Objects;

/**
 * Data Mapper del agregado Adopcion (con sus seguimientos y la eventual devolución).
 * Reconstruye con {@code Adopcion.of}, {@code SeguimientoAdopcion.of} y el record {@code Devolucion}.
 */
public final class AdopcionMapper {

    private AdopcionMapper() {
    }

    public static AdopcionJpa toJpa(Adopcion adopcion) {
        Objects.requireNonNull(adopcion, "adopcion");
        AdopcionJpa jpa = new AdopcionJpa();
        jpa.setId(adopcion.getAdopcionId().value());
        jpa.setSolicitudId(adopcion.getSolicitudId().value());
        jpa.setAnimalId(adopcion.getAnimalId().value());
        jpa.setSolicitanteId(adopcion.getSolicitanteId().value());
        jpa.setFechaFormalizacion(adopcion.getFechaFormalizacion());
        jpa.setFormalizadaPor(adopcion.getFormalizadaPor().value());
        jpa.setEstado(adopcion.getEstado());
        adopcion.getDevolucion().ifPresent(devolucion -> {
            jpa.setDevolucionFecha(devolucion.fecha());
            jpa.setDevolucionMotivo(devolucion.motivo());
            jpa.setDevolucionRegistradaPor(devolucion.registradoPor().value());
        });
        for (SeguimientoAdopcion seguimiento : adopcion.getSeguimientos()) {
            jpa.getSeguimientos().add(toJpa(seguimiento, jpa));
        }
        return jpa;
    }

    public static Adopcion toDomain(AdopcionJpa jpa) {
        Objects.requireNonNull(jpa, "adopcionJpa");
        // ck_adopcion_devolucion: las tres columnas son todas nulas o todas no nulas
        Devolucion devolucion = jpa.getDevolucionFecha() == null ? null : new Devolucion(
                jpa.getDevolucionFecha(),
                jpa.getDevolucionMotivo(),
                UsuarioId.of(jpa.getDevolucionRegistradaPor()));
        List<SeguimientoAdopcion> seguimientos = jpa.getSeguimientos().stream()
                .map(AdopcionMapper::toDomain)
                .toList();
        return Adopcion.of(
                AdopcionId.of(jpa.getId()),
                SolicitudAdopcionId.of(jpa.getSolicitudId()),
                AnimalId.of(jpa.getAnimalId()),
                UsuarioId.of(jpa.getSolicitanteId()),
                jpa.getFechaFormalizacion(),
                UsuarioId.of(jpa.getFormalizadaPor()),
                jpa.getEstado(),
                seguimientos,
                devolucion);
    }

    static SeguimientoAdopcionJpa toJpa(SeguimientoAdopcion seguimiento, AdopcionJpa adopcion) {
        SeguimientoAdopcionJpa jpa = new SeguimientoAdopcionJpa();
        jpa.setId(seguimiento.getSeguimientoId().value());
        jpa.setAdopcion(adopcion);
        jpa.setFecha(seguimiento.getFecha());
        jpa.setObservaciones(seguimiento.getObservaciones());
        jpa.setRegistradoPor(seguimiento.getRegistradoPor().value());
        return jpa;
    }

    static SeguimientoAdopcion toDomain(SeguimientoAdopcionJpa jpa) {
        return SeguimientoAdopcion.of(
                SeguimientoAdopcionId.of(jpa.getId()),
                jpa.getFecha(),
                jpa.getObservaciones(),
                UsuarioId.of(jpa.getRegistradoPor()));
    }
}
