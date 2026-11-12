package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegistroClinicoTest {

    private final AnimalId animal = AnimalId.generate();
    private final UsuarioId vet = UsuarioId.generate();
    private final LocalDate hoy = LocalDate.of(2026, 11, 10);

    @Test
    @DisplayName("Todos los registros clínicos se tratan polimórficamente como RegistroClinico")
    void polimorfismo() {
        List<RegistroClinico> historial = List.of(
                EvaluacionClinica.create(animal, vet, hoy, "Mucosas pálidas", "Anemia leve", null),
                Vacunacion.create(animal, vet, hoy, "Rabia", null),
                Desparasitacion.create(animal, vet, hoy, "Ivermectina", null),
                Tratamiento.create(animal, vet, hoy, "Suplemento de hierro", "1 vez al día", null),
                Esterilizacion.create(animal, vet, hoy, "Sin complicaciones"));

        assertEquals(List.of(TipoRegistroClinico.EVALUACION_CLINICA, TipoRegistroClinico.VACUNACION,
                        TipoRegistroClinico.DESPARASITACION, TipoRegistroClinico.TRATAMIENTO,
                        TipoRegistroClinico.ESTERILIZACION),
                historial.stream().map(RegistroClinico::getTipo).toList());
        assertTrue(historial.stream().allMatch(r -> r.getAnimalId().equals(animal)));
        assertEquals("Vacunación: Rabia", historial.get(1).resumen());
        assertTrue(historial.get(0).resumen().contains("Anemia leve"));
    }

    @Test
    @DisplayName("Los datos mínimos de cada registro son obligatorios")
    void datosObligatorios() {
        assertThrows(InvalidDomainDataException.class, () -> EvaluacionClinica.create(animal, vet, hoy, " ", null, null));
        assertThrows(InvalidDomainDataException.class, () -> Vacunacion.create(animal, vet, hoy, null, null));
        assertThrows(InvalidDomainDataException.class, () -> Tratamiento.create(animal, null, hoy, "x", null, null));
        assertThrows(InvalidDomainDataException.class, () -> Esterilizacion.create(animal, vet, null, null));
    }
}
