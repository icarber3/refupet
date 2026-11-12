package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Desparasitacion;
import co.ufps.refupet.domain.entities.Esterilizacion;
import co.ufps.refupet.domain.entities.EvaluacionClinica;
import co.ufps.refupet.domain.entities.RegistroClinico;
import co.ufps.refupet.domain.entities.Tratamiento;
import co.ufps.refupet.domain.entities.Vacunacion;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.DesparasitacionId;
import co.ufps.refupet.domain.ids.EsterilizacionId;
import co.ufps.refupet.domain.ids.EvaluacionClinicaId;
import co.ufps.refupet.domain.ids.TratamientoId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.ids.VacunacionId;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.EvaluacionClinicaJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.RegistroClinicoJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.VacunacionJpa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias (sin base de datos) del Data Mapper de los cinco registros clínicos. */
class RegistroClinicoMapperTest {

    private static final AnimalId ANIMAL = AnimalId.of("a1b2c3d4-0000-4000-8000-000000000001");
    private static final UsuarioId VET = UsuarioId.of("u0000000-0000-4000-8000-000000000002");
    private static final LocalDate FECHA = LocalDate.of(2026, 2, 20);
    private static final String RID = "r0000000-0000-4000-8000-000000000001";

    private static void assertComunes(RegistroClinico esperado, RegistroClinico obtenido) {
        assertEquals(esperado.getClass(), obtenido.getClass());
        assertEquals(esperado.getId(), obtenido.getId());
        assertEquals(esperado.getAnimalId(), obtenido.getAnimalId());
        assertEquals(esperado.getVeterinarioId(), obtenido.getVeterinarioId());
        assertEquals(esperado.getFecha(), obtenido.getFecha());
        assertEquals(esperado.getObservaciones(), obtenido.getObservaciones());
        assertEquals(esperado.getTipo(), obtenido.getTipo());
        assertEquals(esperado.resumen(), obtenido.resumen());
    }

    private static void assertColumnasComunes(RegistroClinico registro, RegistroClinicoJpa jpa) {
        assertEquals(registro.getId().value(), jpa.getId());
        assertEquals(ANIMAL.value(), jpa.getAnimalId());
        assertEquals(VET.value(), jpa.getVeterinarioId());
        assertEquals(FECHA, jpa.getFecha());
        assertEquals(registro.getObservaciones(), jpa.getObservaciones());
    }

    @Test
    void evaluacionClinicaCompleta() {
        EvaluacionClinica original = EvaluacionClinica.of(EvaluacionClinicaId.of(RID), ANIMAL, VET, FECHA,
                "Mucosas pálidas", "Anemia leve", "Repetir en 15 días");
        EvaluacionClinicaJpa jpa = RegistroClinicoMapper.toJpa(original);
        assertColumnasComunes(original, jpa);
        assertEquals("Mucosas pálidas", jpa.getHallazgos());
        assertEquals("Anemia leve", jpa.getDiagnostico());
        EvaluacionClinica reconstruida = RegistroClinicoMapper.toDomain(jpa);
        assertComunes(original, reconstruida);
        assertEquals(original.getHallazgos(), reconstruida.getHallazgos());
        assertEquals(original.getDiagnostico(), reconstruida.getDiagnostico());
    }

    @Test
    void evaluacionClinicaSinOpcionales() {
        EvaluacionClinica original = EvaluacionClinica.of(EvaluacionClinicaId.of(RID), ANIMAL, VET, FECHA,
                "Sin hallazgos", null, null);
        EvaluacionClinica reconstruida = RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(original));
        assertComunes(original, reconstruida);
        assertNull(reconstruida.getDiagnostico());
        assertNull(reconstruida.getObservaciones());
    }

    @Test
    void vacunacion() {
        Vacunacion original = Vacunacion.of(VacunacionId.of(RID), ANIMAL, VET, FECHA, "Rabia", "Lote 123");
        VacunacionJpa jpa = RegistroClinicoMapper.toJpa(original);
        assertColumnasComunes(original, jpa);
        assertEquals("Rabia", jpa.getVacuna());
        Vacunacion reconstruida = RegistroClinicoMapper.toDomain(jpa);
        assertComunes(original, reconstruida);
        assertEquals("Rabia", reconstruida.getVacuna());
    }

    @Test
    void desparasitacion() {
        Desparasitacion original = Desparasitacion.of(DesparasitacionId.of(RID), ANIMAL, VET, FECHA, "Ivermectina", null);
        Desparasitacion reconstruida = RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(original));
        assertComunes(original, reconstruida);
        assertEquals("Ivermectina", reconstruida.getProducto());
        assertNull(reconstruida.getObservaciones());
    }

    @Test
    void tratamientoCompletoYSinIndicaciones() {
        Tratamiento completo = Tratamiento.of(TratamientoId.of(RID), ANIMAL, VET, FECHA,
                "Antibiótico", "Cada 12 horas por 7 días", "Con comida");
        Tratamiento reconstruido = RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(completo));
        assertComunes(completo, reconstruido);
        assertEquals(FECHA, reconstruido.getFechaInicio());
        assertEquals("Antibiótico", reconstruido.getDescripcion());
        assertEquals("Cada 12 horas por 7 días", reconstruido.getIndicaciones());

        Tratamiento minimo = Tratamiento.of(TratamientoId.of(RID), ANIMAL, VET, FECHA, "Reposo", null, null);
        assertNull(RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(minimo)).getIndicaciones());
    }

    @Test
    void esterilizacion() {
        Esterilizacion original = Esterilizacion.of(EsterilizacionId.of(RID), ANIMAL, VET, FECHA, "Sin complicaciones");
        Esterilizacion reconstruida = RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(original));
        assertComunes(original, reconstruida);
        assertEquals(TipoRegistroClinico.ESTERILIZACION, reconstruida.getTipo());
    }

    @Test
    void registrosCreadosConCreateConservanSuId() {
        Vacunacion nueva = Vacunacion.create(ANIMAL, VET, FECHA, "Parvovirus", null);
        assertEquals(nueva.getId(), RegistroClinicoMapper.toDomain(RegistroClinicoMapper.toJpa(nueva)).getId());
    }

    @Test
    void aplicaLasInvariantesDelDominioAlReconstruir() {
        VacunacionJpa jpa = RegistroClinicoMapper.toJpa(
                Vacunacion.of(VacunacionId.of(RID), ANIMAL, VET, FECHA, "Rabia", null));
        jpa.setVacuna("  ");
        assertThrows(InvalidDomainDataException.class, () -> RegistroClinicoMapper.toDomain(jpa));
    }
}
