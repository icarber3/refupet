package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Adopcion;
import co.ufps.refupet.domain.entities.SeguimientoAdopcion;
import co.ufps.refupet.domain.enums.EstadoAdopcion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionStateException;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SeguimientoAdopcionId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Devolucion;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.AdopcionJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.SeguimientoAdopcionJpa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias (sin base de datos) del Data Mapper del agregado Adopcion. */
class AdopcionMapperTest {

    private static final AdopcionId ID = AdopcionId.of("d0000000-0000-4000-8000-000000000001");
    private static final SolicitudAdopcionId SOLICITUD = SolicitudAdopcionId.of("s0000000-0000-4000-8000-000000000001");
    private static final AnimalId ANIMAL = AnimalId.of("a1b2c3d4-0000-4000-8000-000000000001");
    private static final UsuarioId SOLICITANTE = UsuarioId.of("u0000000-0000-4000-8000-000000000004");
    private static final UsuarioId ADMIN = UsuarioId.of("u0000000-0000-4000-8000-000000000001");
    private static final LocalDate FORMALIZADA = LocalDate.of(2026, 5, 1);

    private static SeguimientoAdopcion seguimiento(String sufijo, LocalDate fecha, String texto) {
        return SeguimientoAdopcion.of(SeguimientoAdopcionId.of("f0000000-0000-4000-8000-00000000000" + sufijo),
                fecha, texto, ADMIN);
    }

    private static void assertMismaAdopcion(Adopcion e, Adopcion o) {
        assertEquals(e.getAdopcionId(), o.getAdopcionId());
        assertEquals(e.getSolicitudId(), o.getSolicitudId());
        assertEquals(e.getAnimalId(), o.getAnimalId());
        assertEquals(e.getSolicitanteId(), o.getSolicitanteId());
        assertEquals(e.getFechaFormalizacion(), o.getFechaFormalizacion());
        assertEquals(e.getFormalizadaPor(), o.getFormalizadaPor());
        assertEquals(e.getEstado(), o.getEstado());
        assertEquals(e.getDevolucion(), o.getDevolucion());
        assertEquals(e.getSeguimientos().size(), o.getSeguimientos().size());
        for (int i = 0; i < e.getSeguimientos().size(); i++) {
            SeguimientoAdopcion se = e.getSeguimientos().get(i);
            SeguimientoAdopcion so = o.getSeguimientos().get(i);
            assertEquals(se.getSeguimientoId(), so.getSeguimientoId());
            assertEquals(se.getFecha(), so.getFecha());
            assertEquals(se.getObservaciones(), so.getObservaciones());
            assertEquals(se.getRegistradoPor(), so.getRegistradoPor());
        }
    }

    private static Adopcion idaYVuelta(Adopcion adopcion) {
        return AdopcionMapper.toDomain(AdopcionMapper.toJpa(adopcion));
    }

    @Test
    void formalizadaSinSeguimientosNiDevolucion() {
        Adopcion original = Adopcion.of(ID, SOLICITUD, ANIMAL, SOLICITANTE, FORMALIZADA, ADMIN,
                EstadoAdopcion.FORMALIZADA, List.of(), null);
        AdopcionJpa jpa = AdopcionMapper.toJpa(original);
        assertNull(jpa.getDevolucionFecha());
        assertNull(jpa.getDevolucionMotivo());
        assertNull(jpa.getDevolucionRegistradaPor());
        assertTrue(jpa.getSeguimientos().isEmpty());
        Adopcion reconstruida = AdopcionMapper.toDomain(jpa);
        assertMismaAdopcion(original, reconstruida);
        assertTrue(reconstruida.estaVigente());
    }

    @Test
    void conSeguimientos() {
        Adopcion original = Adopcion.of(ID, SOLICITUD, ANIMAL, SOLICITANTE, FORMALIZADA, ADMIN,
                EstadoAdopcion.CON_SEGUIMIENTO,
                List.of(seguimiento("1", FORMALIZADA.plusDays(30), "Se adapta bien"),
                        seguimiento("2", FORMALIZADA.plusDays(60), "Sano")), null);
        AdopcionJpa jpa = AdopcionMapper.toJpa(original);
        assertEquals(2, jpa.getSeguimientos().size());
        for (SeguimientoAdopcionJpa s : jpa.getSeguimientos()) {
            assertSame(jpa, s.getAdopcion());
            assertEquals(ADMIN.value(), s.getRegistradoPor());
        }
        assertMismaAdopcion(original, AdopcionMapper.toDomain(jpa));
    }

    @Test
    void conDevolucion() {
        Adopcion original = Adopcion.of(ID, SOLICITUD, ANIMAL, SOLICITANTE, FORMALIZADA, ADMIN,
                EstadoAdopcion.CON_DEVOLUCION,
                List.of(seguimiento("1", FORMALIZADA.plusDays(10), "Problemas de convivencia")),
                new Devolucion(FORMALIZADA.plusDays(20), "Alergias en la familia", ADMIN));
        AdopcionJpa jpa = AdopcionMapper.toJpa(original);
        assertEquals(FORMALIZADA.plusDays(20), jpa.getDevolucionFecha());
        assertEquals("Alergias en la familia", jpa.getDevolucionMotivo());
        assertEquals(ADMIN.value(), jpa.getDevolucionRegistradaPor());
        Adopcion reconstruida = AdopcionMapper.toDomain(jpa);
        assertMismaAdopcion(original, reconstruida);
        assertFalse(reconstruida.estaVigente());
    }

    @Test
    void cicloDeVidaDelDominioSobreviveAlMapeo() {
        Adopcion adopcion = idaYVuelta(Adopcion.of(ID, SOLICITUD, ANIMAL, SOLICITANTE, FORMALIZADA, ADMIN,
                EstadoAdopcion.FORMALIZADA, List.of(), null));
        SeguimientoAdopcion nuevo = adopcion.registrarSeguimiento(FORMALIZADA.plusDays(15), "Visita", ADMIN);
        Adopcion conSeguimiento = idaYVuelta(adopcion);
        assertEquals(EstadoAdopcion.CON_SEGUIMIENTO, conSeguimiento.getEstado());
        assertEquals(nuevo.getSeguimientoId(), conSeguimiento.getSeguimientos().get(0).getSeguimientoId());
        conSeguimiento.registrarDevolucion(FORMALIZADA.plusDays(40), "Mudanza", ADMIN);
        Adopcion devuelta = idaYVuelta(conSeguimiento);
        assertEquals(EstadoAdopcion.CON_DEVOLUCION, devuelta.getEstado());
        assertThrows(InvalidAdoptionStateException.class,
                () -> devuelta.registrarSeguimiento(FORMALIZADA.plusDays(50), "x", ADMIN));
    }
}
