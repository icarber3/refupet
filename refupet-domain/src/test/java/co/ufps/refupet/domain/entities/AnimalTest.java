package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.exceptions.InvalidAnimalStateException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.RegistroIngreso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    private final UsuarioId voluntario = UsuarioId.generate();

    private Animal nuevoAnimal() {
        return Animal.create("Firulais", Especie.PERRO, "Criollo", "Pelaje café",
                new RegistroIngreso(LocalDate.of(2026, 11, 1), voluntario, "Rescate en vía pública", "Desnutrido"));
    }

    @Test
    @DisplayName("Un animal se registra con trazabilidad de ingreso y sin ubicación ni estado de salud asignados")
    void registroDeAnimal() {
        Animal animal = nuevoAnimal();
        assertNotNull(animal.getAnimalId());
        assertEquals(voluntario, animal.getRegistroIngreso().registradoPor());
        assertEquals("Rescate en vía pública", animal.getRegistroIngreso().procedencia());
        assertTrue(animal.getUbicacion().isEmpty());
        assertTrue(animal.getEstadoSalud().isEmpty());
        assertFalse(animal.isEsterilizado());
    }

    @Test
    @DisplayName("Nombre y especie son obligatorios")
    void datosObligatorios() {
        RegistroIngreso ingreso = new RegistroIngreso(LocalDate.now(), voluntario, null, null);
        assertThrows(InvalidDomainDataException.class, () -> Animal.create(" ", Especie.GATO, null, null, ingreso));
        assertThrows(InvalidDomainDataException.class, () -> Animal.create("Michi", null, null, null, ingreso));
    }

    @Test
    @DisplayName("Registra observaciones de comportamiento con su autor")
    void observaciones() {
        Animal animal = nuevoAnimal();
        ObservacionComportamiento obs = animal.registrarObservacion("Se muestra tímido", voluntario);
        assertEquals(1, animal.getObservaciones().size());
        assertEquals(voluntario, obs.getRegistradoPor());
        assertThrows(UnsupportedOperationException.class,
                () -> animal.getObservaciones().add(obs), "La lista expuesta debe ser inmutable");
    }

    @Test
    @DisplayName("Actualiza el estado de salud")
    void estadoSalud() {
        Animal animal = nuevoAnimal();
        UsuarioId veterinario = UsuarioId.generate();
        animal.actualizarEstadoSalud("Estable", veterinario);
        assertEquals("Estable", animal.getEstadoSalud().orElseThrow().descripcion());
        assertEquals(veterinario, animal.getEstadoSalud().orElseThrow().actualizadoPor());
    }

    @Test
    @DisplayName("No se puede esterilizar dos veces")
    void esterilizacionUnica() {
        Animal animal = nuevoAnimal();
        animal.marcarEsterilizado();
        assertTrue(animal.isEsterilizado());
        assertThrows(InvalidAnimalStateException.class, animal::marcarEsterilizado);
    }

    @Test
    @DisplayName("Solo pasa a población general desde cuarentena")
    void ubicacion() {
        Animal animal = nuevoAnimal();
        assertThrows(InvalidAnimalStateException.class, animal::trasladarAPoblacionGeneral);
        animal.ingresarACuarentena();
        assertThrows(InvalidAnimalStateException.class, animal::ingresarACuarentena);
        animal.trasladarAPoblacionGeneral();
        assertEquals(EstadoUbicacionAnimal.POBLACION_GENERAL, animal.getUbicacion().orElseThrow());
    }

    @Test
    @DisplayName("Los Ids son Value Objects: iguales por valor y distintos por tipo")
    void identificadores() {
        assertEquals(AnimalId.of("abc"), AnimalId.of(" abc "));
        assertNotEquals(AnimalId.of("abc"), UsuarioId.of("abc"));
        assertThrows(InvalidDomainDataException.class, () -> AnimalId.of(""));
    }
}
