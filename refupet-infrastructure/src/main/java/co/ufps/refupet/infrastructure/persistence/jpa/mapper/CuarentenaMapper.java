package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.CuarentenaJpa;

import java.util.Objects;

/** Data Mapper del agregado Cuarentena. Reconstruye con {@code Cuarentena.of} y {@code PeriodoCuarentena}. */
public final class CuarentenaMapper {

    private CuarentenaMapper() {
    }

    public static CuarentenaJpa toJpa(Cuarentena cuarentena) {
        Objects.requireNonNull(cuarentena, "cuarentena");
        CuarentenaJpa jpa = new CuarentenaJpa();
        jpa.setId(cuarentena.getCuarentenaId().value());
        jpa.setAnimalId(cuarentena.getAnimalId().value());
        jpa.setFechaInicio(cuarentena.getPeriodo().fechaInicio());
        jpa.setDiasMinimos(cuarentena.getPeriodo().diasMinimos());
        jpa.setIniciadaPor(cuarentena.getIniciadaPor().value());
        jpa.setEstado(cuarentena.getEstado());
        jpa.setSalidaAutorizadaPor(cuarentena.getSalidaAutorizadaPor() == null
                ? null : cuarentena.getSalidaAutorizadaPor().value());
        jpa.setFechaAutorizacionSalida(cuarentena.getFechaAutorizacionSalida());
        jpa.setFechaFinalizacion(cuarentena.getFechaFinalizacion());
        return jpa;
    }

    public static Cuarentena toDomain(CuarentenaJpa jpa) {
        Objects.requireNonNull(jpa, "cuarentenaJpa");
        return Cuarentena.of(
                CuarentenaId.of(jpa.getId()),
                AnimalId.of(jpa.getAnimalId()),
                new PeriodoCuarentena(jpa.getFechaInicio(), jpa.getDiasMinimos()),
                UsuarioId.of(jpa.getIniciadaPor()),
                jpa.getEstado(),
                jpa.getSalidaAutorizadaPor() == null ? null : UsuarioId.of(jpa.getSalidaAutorizadaPor()),
                jpa.getFechaAutorizacionSalida(),
                jpa.getFechaFinalizacion());
    }
}
