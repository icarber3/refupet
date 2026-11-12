package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.EstadoAdopcion;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionStateException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AdopcionTest {

    private final Administrador admin = Administrador.create("Admin", Email.of("admin@refupet.co"));
    private final UsuarioId solicitanteId = UsuarioId.generate();

    private SolicitudAdopcion nuevaSolicitud() {
        return SolicitudAdopcion.create(AnimalId.generate(), solicitanteId, "Tengo patio amplio");
    }

    @Nested
    @DisplayName("Solicitud de adopción")
    class Solicitud {

        @Test
        @DisplayName("Nace pendiente y el administrador la aprueba")
        void aprobar() {
            SolicitudAdopcion solicitud = nuevaSolicitud();
            assertEquals(EstadoSolicitudAdopcion.PENDIENTE, solicitud.getEstado());
            solicitud.aprobar(admin);
            assertEquals(EstadoSolicitudAdopcion.APROBADA, solicitud.getEstado());
            assertEquals(admin.getUsuarioId(), solicitud.getDecididaPor());
        }

        @Test
        @DisplayName("El administrador la rechaza con motivo")
        void rechazar() {
            SolicitudAdopcion solicitud = nuevaSolicitud();
            solicitud.rechazar(admin, "Vivienda no adecuada");
            assertEquals(EstadoSolicitudAdopcion.RECHAZADA, solicitud.getEstado());
            assertEquals("Vivienda no adecuada", solicitud.getMotivoRechazo());
        }

        @Test
        @DisplayName("Solo un administrador decide")
        void soloAdministrador() {
            Veterinario vet = Veterinario.create("Vet", Email.of("vet@refupet.co"));
            assertThrows(UnauthorizedRoleOperationException.class, () -> nuevaSolicitud().aprobar(vet));
        }

        @Test
        @DisplayName("No se decide dos veces ni se evalúa una solicitud ya decidida")
        void decisionUnica() {
            SolicitudAdopcion solicitud = nuevaSolicitud();
            solicitud.evaluarCompatibilidad(true, "Buen entorno", admin.getUsuarioId());
            assertTrue(solicitud.getEvaluacionCompatibilidad().orElseThrow().compatible());
            solicitud.rechazar(admin, null);
            assertThrows(InvalidAdoptionRequestStateException.class, () -> solicitud.aprobar(admin));
            assertThrows(InvalidAdoptionRequestStateException.class,
                    () -> solicitud.evaluarCompatibilidad(false, null, admin.getUsuarioId()));
        }
    }

    @Nested
    @DisplayName("Adopción formalizada")
    class Formalizada {

        private final LocalDate fecha = LocalDate.of(2026, 11, 5);

        @Test
        @DisplayName("Solo se formaliza una solicitud aprobada")
        void soloAprobada() {
            assertThrows(InvalidAdoptionRequestStateException.class,
                    () -> Adopcion.formalizar(nuevaSolicitud(), fecha, admin.getUsuarioId()));
        }

        @Test
        @DisplayName("Seguimiento y devolución cambian el estado; tras la devolución no hay más registros")
        void seguimientoYDevolucion() {
            SolicitudAdopcion solicitud = nuevaSolicitud();
            solicitud.aprobar(admin);
            Adopcion adopcion = Adopcion.formalizar(solicitud, fecha, admin.getUsuarioId());
            assertEquals(EstadoAdopcion.FORMALIZADA, adopcion.getEstado());
            assertTrue(adopcion.estaVigente());

            adopcion.registrarSeguimiento(fecha.plusDays(15), "Se adapta bien", admin.getUsuarioId());
            assertEquals(EstadoAdopcion.CON_SEGUIMIENTO, adopcion.getEstado());
            assertEquals(1, adopcion.getSeguimientos().size());

            adopcion.registrarDevolucion(fecha.plusDays(30), "Alergia en la familia", admin.getUsuarioId());
            assertEquals(EstadoAdopcion.CON_DEVOLUCION, adopcion.getEstado());
            assertFalse(adopcion.estaVigente());
            assertThrows(InvalidAdoptionStateException.class,
                    () -> adopcion.registrarSeguimiento(fecha.plusDays(31), "x", admin.getUsuarioId()));
            assertThrows(InvalidAdoptionStateException.class,
                    () -> adopcion.registrarDevolucion(fecha.plusDays(31), "x", admin.getUsuarioId()));
        }

        @Test
        @DisplayName("No se registran seguimientos con fecha anterior a la formalización")
        void fechaAnterior() {
            SolicitudAdopcion solicitud = nuevaSolicitud();
            solicitud.aprobar(admin);
            Adopcion adopcion = Adopcion.formalizar(solicitud, fecha, admin.getUsuarioId());
            assertThrows(InvalidDomainDataException.class,
                    () -> adopcion.registrarSeguimiento(fecha.minusDays(1), "x", admin.getUsuarioId()));
        }
    }
}
