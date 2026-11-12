package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.ObservacionComportamiento;
import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.ObservacionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.EstadoSalud;
import co.ufps.refupet.domain.valueobjects.RegistroIngreso;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.AnimalJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.ObservacionComportamientoJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias (sin base de datos) del Data Mapper del agregado Animal. */
class AnimalMapperTest {

    private static final AnimalId ID = AnimalId.of("a1b2c3d4-0000-4000-8000-000000000001");
    private static final UsuarioId VOLUNTARIO = UsuarioId.of("u0000000-0000-4000-8000-000000000001");
    private static final UsuarioId VETERINARIO = UsuarioId.of("u0000000-0000-4000-8000-000000000002");
    private static final LocalDate INGRESO = LocalDate.of(2026, 1, 10);
    private static final LocalDateTime INSTANTE = LocalDateTime.of(2026, 1, 12, 9, 15, 30);

    private static Animal animalCompleto() {
        return Animal.of(ID, "Firulais", Especie.PERRO, "Criollo", "Café con manchas",
                new RegistroIngreso(INGRESO, VOLUNTARIO, "Rescate en vía pública", "Desnutrido"),
                EstadoUbicacionAnimal.CUARENTENA,
                new EstadoSalud("Estable", INSTANTE, VETERINARIO),
                true,
                List.of(
                        ObservacionComportamiento.of(ObservacionId.of("o0000000-0000-4000-8000-000000000001"),
                                "Tímido", VOLUNTARIO, INSTANTE),
                        ObservacionComportamiento.of(ObservacionId.of("o0000000-0000-4000-8000-000000000002"),
                                "Juega con otros perros", VOLUNTARIO, INSTANTE.plusDays(1))));
    }

    private static Animal animalMinimo() {
        return Animal.of(ID, "Michi", Especie.GATO, null, null,
                new RegistroIngreso(INGRESO, VOLUNTARIO, null, null),
                null, null, false, List.of());
    }

    private static void assertMismoAnimal(Animal esperado, Animal obtenido) {
        assertEquals(esperado.getAnimalId(), obtenido.getAnimalId());
        assertEquals(esperado.getNombre(), obtenido.getNombre());
        assertEquals(esperado.getEspecie(), obtenido.getEspecie());
        assertEquals(esperado.getRaza(), obtenido.getRaza());
        assertEquals(esperado.getDescripcion(), obtenido.getDescripcion());
        assertEquals(esperado.getRegistroIngreso(), obtenido.getRegistroIngreso());
        assertEquals(esperado.getUbicacion(), obtenido.getUbicacion());
        assertEquals(esperado.getEstadoSalud(), obtenido.getEstadoSalud());
        assertEquals(esperado.isEsterilizado(), obtenido.isEsterilizado());
        assertEquals(esperado.getObservaciones().size(), obtenido.getObservaciones().size());
        for (int i = 0; i < esperado.getObservaciones().size(); i++) {
            ObservacionComportamiento e = esperado.getObservaciones().get(i);
            ObservacionComportamiento o = obtenido.getObservaciones().get(i);
            assertEquals(e.getObservacionId(), o.getObservacionId());
            assertEquals(e.getDescripcion(), o.getDescripcion());
            assertEquals(e.getRegistradoPor(), o.getRegistradoPor());
            assertEquals(e.getFechaRegistro(), o.getFechaRegistro());
        }
    }

    @Test
    void idaYVueltaConTodosLosDatos() {
        Animal original = animalCompleto();
        assertMismoAnimal(original, AnimalMapper.toDomain(AnimalMapper.toJpa(original)));
    }

    @Test
    void idaYVueltaConCamposOpcionalesVacios() {
        Animal original = animalMinimo();
        Animal reconstruido = AnimalMapper.toDomain(AnimalMapper.toJpa(original));
        assertMismoAnimal(original, reconstruido);
        assertTrue(reconstruido.getUbicacion().isEmpty());
        assertTrue(reconstruido.getEstadoSalud().isEmpty());
        assertTrue(reconstruido.getObservaciones().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Especie.class)
    void conservaCualquierEspecie(Especie especie) {
        Animal original = Animal.of(ID, "X", especie, null, null,
                new RegistroIngreso(INGRESO, VOLUNTARIO, null, null), null, null, false, List.of());
        assertEquals(especie, AnimalMapper.toDomain(AnimalMapper.toJpa(original)).getEspecie());
    }

    @ParameterizedTest
    @EnumSource(EstadoUbicacionAnimal.class)
    void conservaCualquierUbicacion(EstadoUbicacionAnimal ubicacion) {
        Animal original = Animal.of(ID, "X", Especie.OTRA, null, null,
                new RegistroIngreso(INGRESO, VOLUNTARIO, null, null), ubicacion, null, false, List.of());
        assertEquals(ubicacion, AnimalMapper.toDomain(AnimalMapper.toJpa(original)).getUbicacion().orElseThrow());
    }

    @Test
    void aplanaLosValueObjectsEnColumnas() {
        AnimalJpa jpa = AnimalMapper.toJpa(animalCompleto());
        assertEquals(INGRESO, jpa.getFechaIngreso());
        assertEquals(VOLUNTARIO.value(), jpa.getIngresoRegistradoPor());
        assertEquals("Rescate en vía pública", jpa.getProcedencia());
        assertEquals("Desnutrido", jpa.getCondicionInicial());
        assertEquals("Estable", jpa.getEstadoSaludDescripcion());
        assertEquals(INSTANTE, jpa.getEstadoSaludFecha());
        assertEquals(VETERINARIO.value(), jpa.getEstadoSaludActualizadoPor());
        assertTrue(jpa.isEsterilizado());
    }

    /** ck_animal_estado_salud: sin estado de salud, las tres columnas quedan nulas. */
    @Test
    void sinEstadoDeSaludLasTresColumnasSonNulas() {
        AnimalJpa jpa = AnimalMapper.toJpa(animalMinimo());
        assertNull(jpa.getEstadoSaludDescripcion());
        assertNull(jpa.getEstadoSaludFecha());
        assertNull(jpa.getEstadoSaludActualizadoPor());
        assertNull(jpa.getUbicacion());
    }

    @Test
    void cadaObservacionApuntaAlAnimalDueno() {
        AnimalJpa jpa = AnimalMapper.toJpa(animalCompleto());
        assertEquals(2, jpa.getObservaciones().size());
        for (ObservacionComportamientoJpa observacion : jpa.getObservaciones()) {
            assertSame(jpa, observacion.getAnimal());
        }
        assertEquals("o0000000-0000-4000-8000-000000000001", jpa.getObservaciones().get(0).getId());
        assertEquals(VOLUNTARIO.value(), jpa.getObservaciones().get(0).getRegistradoPor());
    }

    @Test
    void conservaElOrdenDeLasObservaciones() {
        Animal reconstruido = AnimalMapper.toDomain(AnimalMapper.toJpa(animalCompleto()));
        assertEquals("Tímido", reconstruido.getObservaciones().get(0).getDescripcion());
        assertEquals("Juega con otros perros", reconstruido.getObservaciones().get(1).getDescripcion());
    }

    /** El animal reconstruido sigue aplicando sus reglas (regla 9: no esterilizar dos veces). */
    @Test
    void elAnimalReconstruidoConservaSusInvariantes() {
        Animal reconstruido = AnimalMapper.toDomain(AnimalMapper.toJpa(animalCompleto()));
        assertThrows(co.ufps.refupet.domain.exceptions.InvalidAnimalStateException.class,
                reconstruido::marcarEsterilizado);
        assertThrows(co.ufps.refupet.domain.exceptions.InvalidAnimalStateException.class,
                reconstruido::ingresarACuarentena);
    }

    @Test
    void observacionNuevaDelDominioSeMapea() {
        Animal animal = animalMinimo();
        ObservacionComportamiento nueva = animal.registrarObservacion("Come bien", VOLUNTARIO);
        AnimalJpa jpa = AnimalMapper.toJpa(animal);
        assertEquals(1, jpa.getObservaciones().size());
        assertEquals(nueva.getObservacionId().value(), jpa.getObservaciones().get(0).getId());
        assertEquals(nueva.getFechaRegistro(), jpa.getObservaciones().get(0).getFechaRegistro());
    }
}
