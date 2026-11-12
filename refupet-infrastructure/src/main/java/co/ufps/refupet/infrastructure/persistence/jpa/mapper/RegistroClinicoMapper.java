package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Desparasitacion;
import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.entities.EvaluacionClinica;
import co.ufps.refupet.domain.entities.RegistroClinico;
import co.ufps.refupet.domain.entities.Tratamiento;
import co.ufps.refupet.domain.entities.Vacunacion;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.DesparasitacionId;
import co.ufps.refupet.domain.ids.EsterilizacionId;
import co.ufps.refupet.domain.ids.EvaluacionClinicaId;
import co.ufps.refupet.domain.ids.TratamientoId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.ids.VacunacionId;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.DesparasitacionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.EsterilizacionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.EvaluacionClinicaJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.RegistroClinicoJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.TratamientoJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.VacunacionJpa;

import java.util.Objects;

/**
 * Data Mapper de los cinco registros clínicos. Las columnas comunes se copian en un único método;
 * cada tipo añade sus columnas propias y se reconstruye con su fábrica {@code of(...)}.
 */
public final class RegistroClinicoMapper {

    private RegistroClinicoMapper() {
    }

    // ── Dominio → JPA ──

    public static EvaluacionClinicaJpa toJpa(EvaluacionClinica registro) {
        EvaluacionClinicaJpa jpa = comunes(registro, new EvaluacionClinicaJpa());
        jpa.setHallazgos(registro.getHallazgos());
        jpa.setDiagnostico(registro.getDiagnostico());
        return jpa;
    }

    public static VacunacionJpa toJpa(Vacunacion registro) {
        VacunacionJpa jpa = comunes(registro, new VacunacionJpa());
        jpa.setVacuna(registro.getVacuna());
        return jpa;
    }

    public static DesparasitacionJpa toJpa(Desparasitacion registro) {
        DesparasitacionJpa jpa = comunes(registro, new DesparasitacionJpa());
        jpa.setProducto(registro.getProducto());
        return jpa;
    }

    public static TratamientoJpa toJpa(Tratamiento registro) {
        TratamientoJpa jpa = comunes(registro, new TratamientoJpa());
        jpa.setDescripcion(registro.getDescripcion());
        jpa.setIndicaciones(registro.getIndicaciones());
        return jpa;
    }

    public static EsterilizacionJpa toJpa(Esterilizacion registro) {
        return comunes(registro, new EsterilizacionJpa());
    }

    private static <J extends RegistroClinicoJpa> J comunes(RegistroClinico registro, J jpa) {
        Objects.requireNonNull(registro, "registro");
        jpa.setId(registro.getId().value());
        jpa.setAnimalId(registro.getAnimalId().value());
        jpa.setVeterinarioId(registro.getVeterinarioId().value());
        jpa.setFecha(registro.getFecha());
        jpa.setObservaciones(registro.getObservaciones());
        return jpa;
    }

    // ── JPA → dominio ──

    public static EvaluacionClinica toDomain(EvaluacionClinicaJpa jpa) {
        return EvaluacionClinica.of(EvaluacionClinicaId.of(jpa.getId()), animal(jpa), veterinario(jpa),
                jpa.getFecha(), jpa.getHallazgos(), jpa.getDiagnostico(), jpa.getObservaciones());
    }

    public static Vacunacion toDomain(VacunacionJpa jpa) {
        return Vacunacion.of(VacunacionId.of(jpa.getId()), animal(jpa), veterinario(jpa),
                jpa.getFecha(), jpa.getVacuna(), jpa.getObservaciones());
    }

    public static Desparasitacion toDomain(DesparasitacionJpa jpa) {
        return Desparasitacion.of(DesparasitacionId.of(jpa.getId()), animal(jpa), veterinario(jpa),
                jpa.getFecha(), jpa.getProducto(), jpa.getObservaciones());
    }

    public static Tratamiento toDomain(TratamientoJpa jpa) {
        return Tratamiento.of(TratamientoId.of(jpa.getId()), animal(jpa), veterinario(jpa),
                jpa.getFecha(), jpa.getDescripcion(), jpa.getIndicaciones(), jpa.getObservaciones());
    }

    public static Esterilizacion toDomain(EsterilizacionJpa jpa) {
        return Esterilizacion.of(EsterilizacionId.of(jpa.getId()), animal(jpa), veterinario(jpa),
                jpa.getFecha(), jpa.getObservaciones());
    }

    private static AnimalId animal(RegistroClinicoJpa jpa) {
        return AnimalId.of(Objects.requireNonNull(jpa, "registroJpa").getAnimalId());
    }

    private static UsuarioId veterinario(RegistroClinicoJpa jpa) {
        return UsuarioId.of(jpa.getVeterinarioId());
    }
}
