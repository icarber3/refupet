package co.ufps.refupet.domain;

import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.ids.UsuarioId;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Identificadores (base Identificador) y validaciones reutilizables del dominio. */
class IdentificadoresYValidacionesTest {

    @Test
    void unIdSoloEsIgualAOtroDelMismoTipoYValor() {
        assertEquals(AnimalId.of("abc"), AnimalId.of(" abc "));
        assertEquals(AnimalId.of("abc").hashCode(), AnimalId.of("abc").hashCode());
        assertNotEquals(AnimalId.of("abc"), UsuarioId.of("abc"));
        assertNotEquals(AnimalId.of("abc"), CuarentenaId.of("abc"));
        assertEquals("abc", AnimalId.of("abc").toString());
    }

    /** Los ids generados son UUID en texto de 36 caracteres (columnas CHAR(36) del esquema). */
    @Test
    void losIdsGeneradosSonUuidsDe36CaracteresYUnicos() {
        Set<AnimalId> generados = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            AnimalId id = AnimalId.generate();
            assertEquals(36, id.value().length());
            assertEquals(id.value(), UUID.fromString(id.value()).toString());
            generados.add(id);
        }
        assertEquals(200, generados.size());
    }

    @Test
    void unIdNoPuedeEstarVacio() {
        assertThrows(InvalidDomainDataException.class, () -> AnimalId.of(null));
        assertThrows(InvalidDomainDataException.class, () -> UsuarioId.of("  "));
    }

    @Test
    void textoObligatorioYOpcional() {
        assertEquals("Luna", Validaciones.textoObligatorio("  Luna ", "nombre"));
        assertThrows(InvalidDomainDataException.class, () -> Validaciones.textoObligatorio(" ", "nombre"));
        assertNull(Validaciones.textoOpcional("   "));
        assertNull(Validaciones.textoOpcional(null));
        assertEquals("x", Validaciones.textoOpcional(" x "));
    }

    @Test
    void losEnumsSeLeenDesdeTextoTolerandoMayusculasEspaciosYGuiones() {
        assertEquals(Especie.GATO, Especie.fromString(" gato "));
        assertEquals(EstadoUbicacionAnimal.POBLACION_GENERAL, EstadoUbicacionAnimal.fromString("poblacion general"));
        assertEquals(EstadoUbicacionAnimal.POBLACION_GENERAL, EstadoUbicacionAnimal.fromString("Poblacion-General"));
        assertEquals(RolUsuario.VETERINARIO, RolUsuario.fromString("veterinario"));
        assertEquals(EstadoSolicitudAdopcion.RECHAZADA, EstadoSolicitudAdopcion.fromString("rechazada"));
        InvalidDomainDataException error = assertThrows(InvalidDomainDataException.class, () -> Especie.fromString("dragón"));
        assertTrue(error.getMessage().contains("especie"));
        assertThrows(InvalidDomainDataException.class, () -> RolUsuario.fromString(null));
    }
}
