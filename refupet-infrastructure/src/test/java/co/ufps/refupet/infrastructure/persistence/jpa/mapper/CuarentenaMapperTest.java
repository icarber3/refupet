package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.entities.Veterinario;
import co.ufps.refupet.domain.enums.EstadoCuarentena;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.InvalidQuarantineStateException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.CuarentenaJpa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias (sin base de datos) del Data Mapper de Cuarentena en sus tres estados. */
class CuarentenaMapperTest {

    private static final CuarentenaId ID = CuarentenaId.of("c0000000-0000-4000-8000-000000000001");
    private static final AnimalId ANIMAL = AnimalId.of("a1b2c3d4-0000-4000-8000-000000000001");
    private static final UsuarioId VET = UsuarioId.of("u0000000-0000-4000-8000-000000000002");
    private static final LocalDate INICIO = LocalDate.of(2026, 3, 1);

    private static void assertMismaCuarentena(Cuarentena e, Cuarentena o) {
        assertEquals(e.getCuarentenaId(), o.getCuarentenaId());
        assertEquals(e.getAnimalId(), o.getAnimalId());
        assertEquals(e.getPeriodo(), o.getPeriodo());
        assertEquals(e.getIniciadaPor(), o.getIniciadaPor());
        assertEquals(e.getEstado(), o.getEstado());
        assertEquals(e.getSalidaAutorizadaPor(), o.getSalidaAutorizadaPor());
        assertEquals(e.getFechaAutorizacionSalida(), o.getFechaAutorizacionSalida());
        assertEquals(e.getFechaFinalizacion(), o.getFechaFinalizacion());
    }

    @Test
    void enCurso() {
        Cuarentena original = Cuarentena.of(ID, ANIMAL, new PeriodoCuarentena(INICIO, 15), VET,
                EstadoCuarentena.EN_CURSO, null, null, null);
        CuarentenaJpa jpa = CuarentenaMapper.toJpa(original);
        assertEquals(INICIO, jpa.getFechaInicio());
        assertEquals(15, jpa.getDiasMinimos());
        assertNull(jpa.getSalidaAutorizadaPor());
        assertNull(jpa.getFechaAutorizacionSalida());
        assertNull(jpa.getFechaFinalizacion());
        assertMismaCuarentena(original, CuarentenaMapper.toDomain(jpa));
    }

    @Test
    void salidaAutorizada() {
        Cuarentena original = Cuarentena.of(ID, ANIMAL, new PeriodoCuarentena(INICIO, 15), VET,
                EstadoCuarentena.SALIDA_AUTORIZADA, VET, INICIO.plusDays(15), null);
        CuarentenaJpa jpa = CuarentenaMapper.toJpa(original);
        assertEquals(VET.value(), jpa.getSalidaAutorizadaPor());
        assertMismaCuarentena(original, CuarentenaMapper.toDomain(jpa));
    }

    @Test
    void finalizada() {
        Cuarentena original = Cuarentena.of(ID, ANIMAL, new PeriodoCuarentena(INICIO, 15), VET,
                EstadoCuarentena.FINALIZADA, VET, INICIO.plusDays(15), INICIO.plusDays(16));
        Cuarentena reconstruida = CuarentenaMapper.toDomain(CuarentenaMapper.toJpa(original));
        assertMismaCuarentena(original, reconstruida);
        assertFalse(reconstruida.estaActiva());
    }

    @Test
    void cicloDeVidaDelDominioSobreviveAlMapeo() {
        Cuarentena cuarentena = Cuarentena.iniciar(ANIMAL, new PeriodoCuarentena(INICIO, 10), VET);
        Cuarentena reconstruida = CuarentenaMapper.toDomain(CuarentenaMapper.toJpa(cuarentena));
        assertTrue(reconstruida.periodoMinimoCumplido(INICIO.plusDays(10)));
        Veterinario veterinario = Veterinario.of(VET, "Vet", Email.of("vet@refupet.co"),
                EstadoCuenta.ACTIVA, LocalDateTime.of(2026, 1, 1, 0, 0));
        reconstruida.autorizarSalida(veterinario, INICIO.plusDays(10));
        Cuarentena trasAutorizar = CuarentenaMapper.toDomain(CuarentenaMapper.toJpa(reconstruida));
        assertEquals(EstadoCuarentena.SALIDA_AUTORIZADA, trasAutorizar.getEstado());
        trasAutorizar.finalizar(INICIO.plusDays(11));
        Cuarentena trasFinalizar = CuarentenaMapper.toDomain(CuarentenaMapper.toJpa(trasAutorizar));
        assertEquals(EstadoCuarentena.FINALIZADA, trasFinalizar.getEstado());
        assertThrows(InvalidQuarantineStateException.class, () -> trasFinalizar.finalizar(INICIO.plusDays(12)));
    }

    @Test
    void aplicaLasInvariantesDelPeriodoAlReconstruir() {
        CuarentenaJpa jpa = CuarentenaMapper.toJpa(Cuarentena.of(ID, ANIMAL, new PeriodoCuarentena(INICIO, 15), VET,
                EstadoCuarentena.EN_CURSO, null, null, null));
        jpa.setDiasMinimos(0);
        assertThrows(InvalidDomainDataException.class, () -> CuarentenaMapper.toDomain(jpa));
    }
}
