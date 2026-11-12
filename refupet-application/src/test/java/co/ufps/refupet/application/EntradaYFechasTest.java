package co.ufps.refupet.application;

import co.ufps.refupet.application.commands.animal.RegisterAnimalCommand;
import co.ufps.refupet.application.commands.cuarentena.StartQuarantineCommand;
import co.ufps.refupet.application.commands.usuario.CreateUserCommand;
import co.ufps.refupet.application.commands.usuario.RegisterApplicantCommand;
import co.ufps.refupet.application.queries.adopcion.ListAdoptionRequestsQuery;
import co.ufps.refupet.application.services.FechaOperacion;
import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reglas de Application independientes de los repositorios: validación de forma de Commands y Queries
 * (IllegalArgumentException → HTTP 400) y resolución de fechas de operación con el reloj inyectado.
 */
class EntradaYFechasTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-06-15T15:00:00Z"), ZONA);
    private static final LocalDate HOY = LocalDate.of(2026, 6, 15);

    @Test
    void sinFechaSeUsaLaDelReloj() {
        assertEquals(HOY, FechaOperacion.resolver(null, RELOJ, "fecha"));
    }

    @Test
    void seAceptanHoyYFechasPasadas() {
        assertEquals(HOY, FechaOperacion.resolver(HOY, RELOJ, "fecha"));
        assertEquals(HOY.minusYears(1), FechaOperacion.resolver(HOY.minusYears(1), RELOJ, "fecha"));
    }

    @Test
    void noSeAceptanFechasFuturas() {
        InvalidDomainDataException error = assertThrows(InvalidDomainDataException.class,
                () -> FechaOperacion.resolver(HOY.plusDays(1), RELOJ, "fecha de ingreso"));
        assertTrue(error.getMessage().contains("fecha de ingreso"));
    }

    /** "Hoy" depende de la zona del reloj: a las 23:30 en Bogotá ya es el día siguiente en UTC. */
    @Test
    void elDiaActualSeCalculaEnLaZonaDelReloj() {
        Clock nocheEnBogota = Clock.fixed(Instant.parse("2026-06-16T04:30:00Z"), ZONA);
        assertEquals(HOY, FechaOperacion.resolver(null, nocheEnBogota, "fecha"));
        assertThrows(InvalidDomainDataException.class,
                () -> FechaOperacion.resolver(HOY.plusDays(1), nocheEnBogota, "fecha"));
    }

    @Test
    void losCommandsRechazanCamposObligatoriosVacios() {
        assertThrows(IllegalArgumentException.class,
                () -> new CreateUserCommand(null, "Ana", "a@b.co", "VOLUNTARIO", null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new CreateUserCommand("actor", " ", "a@b.co", "VOLUNTARIO", null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterAnimalCommand("actor", "Luna", null, null, null, null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterApplicantCommand("Sara", null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new StartQuarantineCommand("actor", null, null, 5));
    }

    @Test
    void lasConversionesDelegaEnElDominio() {
        assertEquals(RolUsuario.VETERINARIO,
                new CreateUserCommand("actor", "Ana", "a@b.co", "veterinario", null, null).toRol());
        assertEquals(Especie.PERRO,
                new RegisterAnimalCommand("actor", "Luna", "perro", null, null, null, null, null).toEspecie());
        assertThrows(InvalidDomainDataException.class,
                () -> new CreateUserCommand("actor", "Ana", "no-es-email", "VOLUNTARIO", null, null).toEmail());
    }

    @Test
    void elFiltroDeEstadoDeSolicitudesEsOpcional() {
        assertTrue(new ListAdoptionRequestsQuery("actor", null).toEstado().isEmpty());
        assertTrue(new ListAdoptionRequestsQuery("actor", "  ").toEstado().isEmpty());
        assertEquals(EstadoSolicitudAdopcion.PENDIENTE, new ListAdoptionRequestsQuery("actor", "pendiente").toEstado().orElseThrow());
        assertThrows(InvalidDomainDataException.class, () -> new ListAdoptionRequestsQuery("actor", "otro").toEstado());
    }
}
