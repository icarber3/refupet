package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.ObservacionComportamiento;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.ObservacionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.EstadoSalud;
import co.ufps.refupet.domain.valueobjects.RegistroIngreso;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.AnimalJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.ObservacionComportamientoJpa;

import java.util.List;
import java.util.Objects;

/**
 * Data Mapper del agregado Animal (con sus observaciones de comportamiento).
 * Solo traduce datos; la reconstrucción usa {@code Animal.of} y {@code ObservacionComportamiento.of}.
 */
public final class AnimalMapper {

    private AnimalMapper() {
    }

    public static AnimalJpa toJpa(Animal animal) {
        Objects.requireNonNull(animal, "animal");
        AnimalJpa jpa = new AnimalJpa();
        jpa.setId(animal.getAnimalId().value());
        jpa.setNombre(animal.getNombre());
        jpa.setEspecie(animal.getEspecie());
        jpa.setRaza(animal.getRaza());
        jpa.setDescripcion(animal.getDescripcion());

        RegistroIngreso ingreso = animal.getRegistroIngreso();
        jpa.setFechaIngreso(ingreso.fechaIngreso());
        jpa.setIngresoRegistradoPor(ingreso.registradoPor().value());
        jpa.setProcedencia(ingreso.procedencia());
        jpa.setCondicionInicial(ingreso.condicionInicial());

        jpa.setUbicacion(animal.getUbicacion().orElse(null));
        animal.getEstadoSalud().ifPresent(salud -> {
            jpa.setEstadoSaludDescripcion(salud.descripcion());
            jpa.setEstadoSaludFecha(salud.fechaActualizacion());
            jpa.setEstadoSaludActualizadoPor(salud.actualizadoPor().value());
        });
        jpa.setEsterilizado(animal.isEsterilizado());

        for (ObservacionComportamiento observacion : animal.getObservaciones()) {
            jpa.getObservaciones().add(toJpa(observacion, jpa));
        }
        return jpa;
    }

    public static Animal toDomain(AnimalJpa jpa) {
        Objects.requireNonNull(jpa, "animalJpa");
        RegistroIngreso ingreso = new RegistroIngreso(
                jpa.getFechaIngreso(),
                UsuarioId.of(jpa.getIngresoRegistradoPor()),
                jpa.getProcedencia(),
                jpa.getCondicionInicial());
        // ck_animal_estado_salud garantiza que las tres columnas son todas nulas o todas no nulas
        EstadoSalud estadoSalud = jpa.getEstadoSaludDescripcion() == null ? null : new EstadoSalud(
                jpa.getEstadoSaludDescripcion(),
                jpa.getEstadoSaludFecha(),
                UsuarioId.of(jpa.getEstadoSaludActualizadoPor()));
        List<ObservacionComportamiento> observaciones = jpa.getObservaciones().stream()
                .map(AnimalMapper::toDomain)
                .toList();
        return Animal.of(
                AnimalId.of(jpa.getId()),
                jpa.getNombre(),
                jpa.getEspecie(),
                jpa.getRaza(),
                jpa.getDescripcion(),
                ingreso,
                jpa.getUbicacion(),
                estadoSalud,
                jpa.isEsterilizado(),
                observaciones);
    }

    static ObservacionComportamientoJpa toJpa(ObservacionComportamiento observacion, AnimalJpa animal) {
        ObservacionComportamientoJpa jpa = new ObservacionComportamientoJpa();
        jpa.setId(observacion.getObservacionId().value());
        jpa.setAnimal(animal);
        jpa.setDescripcion(observacion.getDescripcion());
        jpa.setRegistradoPor(observacion.getRegistradoPor().value());
        jpa.setFechaRegistro(observacion.getFechaRegistro());
        return jpa;
    }

    static ObservacionComportamiento toDomain(ObservacionComportamientoJpa jpa) {
        return ObservacionComportamiento.of(
                ObservacionId.of(jpa.getId()),
                jpa.getDescripcion(),
                UsuarioId.of(jpa.getRegistradoPor()),
                jpa.getFechaRegistro());
    }
}
