package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Administrador;
import co.ufps.refupet.domain.entities.SolicitudAdopcion;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.domain.valueobjects.EvaluacionCompatibilidad;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.SolicitudAdopcionJpa;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias (sin base de datos) del Data Mapper de SolicitudAdopcion. */
class SolicitudAdopcionMapperTest {

    private static final SolicitudAdopcionId ID = SolicitudAdopcionId.of("s0000000-0000-4000-8000-000000000001");
    private static final AnimalId ANIMAL = AnimalId.of("a1b2c3d4-0000-4000-8000-000000000001");
    private static final UsuarioId SOLICITANTE = UsuarioId.of("u0000000-0000-4000-8000-000000000004");
    private static final UsuarioId ADMIN = UsuarioId.of("u0000000-0000-4000-8000-000000000001");
    private static final LocalDateTime PRESENTADA = LocalDateTime.of(2026, 4, 1, 8, 0, 0);
    private static final LocalDateTime EVALUADA = PRESENTADA.plusDays(1);
    private static final LocalDateTime DECIDIDA = PRESENTADA.plusDays(2);

    private static void assertMismaSolicitud(SolicitudAdopcion e, SolicitudAdopcion o) {
        assertEquals(e.getSolicitudId(), o.getSolicitudId());
        assertEquals(e.getAnimalId(), o.getAnimalId());
        assertEquals(e.getSolicitanteId(), o.getSolicitanteId());
        assertEquals(e.getComentarios(), o.getComentarios());
        assertEquals(e.getFechaSolicitud(), o.getFechaSolicitud());
        assertEquals(e.getEstado(), o.getEstado());
        assertEquals(e.getEvaluacionCompatibilidad(), o.getEvaluacionCompatibilidad());
        assertEquals(e.getDecididaPor(), o.getDecididaPor());
        assertEquals(e.getFechaDecision(), o.getFechaDecision());
        assertEquals(e.getMotivoRechazo(), o.getMotivoRechazo());
    }

    private static SolicitudAdopcion idaYVuelta(SolicitudAdopcion solicitud) {
        return SolicitudAdopcionMapper.toDomain(SolicitudAdopcionMapper.toJpa(solicitud));
    }

    @Test
    void pendienteSinEvaluacionNiComentarios() {
        SolicitudAdopcion original = SolicitudAdopcion.of(ID, ANIMAL, SOLICITANTE, null, PRESENTADA,
                EstadoSolicitudAdopcion.PENDIENTE, null, null, null, null);
        SolicitudAdopcionJpa jpa = SolicitudAdopcionMapper.toJpa(original);
        assertNull(jpa.getCompatibilidadCompatible());
        assertNull(jpa.getCompatibilidadObservaciones());
        assertNull(jpa.getCompatibilidadEvaluadoPor());
        assertNull(jpa.getCompatibilidadFecha());
        assertNull(jpa.getDecididaPor());
        assertNull(jpa.getFechaDecision());
        SolicitudAdopcion reconstruida = SolicitudAdopcionMapper.toDomain(jpa);
        assertMismaSolicitud(original, reconstruida);
        assertTrue(reconstruida.getEvaluacionCompatibilidad().isEmpty());
    }

    @Test
    void pendienteConEvaluacionCompatible() {
        SolicitudAdopcion original = SolicitudAdopcion.of(ID, ANIMAL, SOLICITANTE, "Tengo patio", PRESENTADA,
                EstadoSolicitudAdopcion.PENDIENTE,
                new EvaluacionCompatibilidad(true, "Hogar adecuado", ADMIN, EVALUADA), null, null, null);
        SolicitudAdopcionJpa jpa = SolicitudAdopcionMapper.toJpa(original);
        assertEquals(Boolean.TRUE, jpa.getCompatibilidadCompatible());
        assertEquals(ADMIN.value(), jpa.getCompatibilidadEvaluadoPor());
        assertEquals(EVALUADA, jpa.getCompatibilidadFecha());
        assertMismaSolicitud(original, SolicitudAdopcionMapper.toDomain(jpa));
    }

    /** compatible = false no debe confundirse con "sin evaluación"; observaciones es opcional. */
    @Test
    void evaluacionNoCompatibleSinObservaciones() {
        SolicitudAdopcion original = SolicitudAdopcion.of(ID, ANIMAL, SOLICITANTE, null, PRESENTADA,
                EstadoSolicitudAdopcion.PENDIENTE,
                new EvaluacionCompatibilidad(false, null, ADMIN, EVALUADA), null, null, null);
        SolicitudAdopcion reconstruida = idaYVuelta(original);
        EvaluacionCompatibilidad evaluacion = reconstruida.getEvaluacionCompatibilidad().orElseThrow();
        assertFalse(evaluacion.compatible());
        assertNull(evaluacion.observaciones());
        assertMismaSolicitud(original, reconstruida);
    }

    @Test
    void aprobada() {
        SolicitudAdopcion original = SolicitudAdopcion.of(ID, ANIMAL, SOLICITANTE, null, PRESENTADA,
                EstadoSolicitudAdopcion.APROBADA, null, ADMIN, DECIDIDA, null);
        SolicitudAdopcionJpa jpa = SolicitudAdopcionMapper.toJpa(original);
        assertEquals(ADMIN.value(), jpa.getDecididaPor());
        assertEquals(DECIDIDA, jpa.getFechaDecision());
        assertMismaSolicitud(original, SolicitudAdopcionMapper.toDomain(jpa));
    }

    @Test
    void rechazadaConMotivo() {
        SolicitudAdopcion original = SolicitudAdopcion.of(ID, ANIMAL, SOLICITANTE, null, PRESENTADA,
                EstadoSolicitudAdopcion.RECHAZADA, new EvaluacionCompatibilidad(false, "Sin espacio", ADMIN, EVALUADA),
                ADMIN, DECIDIDA, "No cumple requisitos");
        SolicitudAdopcion reconstruida = idaYVuelta(original);
        assertMismaSolicitud(original, reconstruida);
        assertEquals("No cumple requisitos", reconstruida.getMotivoRechazo());
    }

    @Test
    void cicloDeVidaDelDominioSobreviveAlMapeo() {
        Administrador admin = Administrador.of(ADMIN, "Admin", Email.of("admin@refupet.co"),
                EstadoCuenta.ACTIVA, PRESENTADA);
        SolicitudAdopcion solicitud = idaYVuelta(SolicitudAdopcion.create(ANIMAL, SOLICITANTE, "Hola"));
        solicitud.evaluarCompatibilidad(true, null, ADMIN);
        SolicitudAdopcion evaluada = idaYVuelta(solicitud);
        assertTrue(evaluada.getEvaluacionCompatibilidad().isPresent());
        evaluada.aprobar(admin);
        SolicitudAdopcion aprobada = idaYVuelta(evaluada);
        assertTrue(aprobada.estaAprobada());
        assertEquals(ADMIN, aprobada.getDecididaPor());
        assertThrows(InvalidAdoptionRequestStateException.class, () -> aprobada.rechazar(admin, "x"));
    }
}
