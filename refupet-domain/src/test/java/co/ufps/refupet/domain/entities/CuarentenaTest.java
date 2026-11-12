package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.EstadoCuarentena;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.InvalidQuarantineStateException;
import co.ufps.refupet.domain.exceptions.QuarantineExitNotAuthorizedException;
import co.ufps.refupet.domain.exceptions.QuarantinePeriodNotCompletedException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CuarentenaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 11, 1);

    private Cuarentena nuevaCuarentena(int dias) {
        return Cuarentena.iniciar(AnimalId.generate(), new PeriodoCuarentena(INICIO, dias), UsuarioId.generate());
    }

    private Veterinario veterinario() {
        Veterinario vet = Veterinario.create("Dra. Ruiz", Email.of("ruiz@refupet.co"));
        vet.aprobar();
        return vet;
    }

    @Test
    @DisplayName("El periodo mínimo calcula la fecha mínima de salida y si está cumplido")
    void periodoMinimo() {
        PeriodoCuarentena periodo = new PeriodoCuarentena(INICIO, 10);
        assertEquals(LocalDate.of(2026, 11, 11), periodo.fechaMinimaSalida());
        assertFalse(periodo.estaCumplido(LocalDate.of(2026, 11, 10)));
        assertTrue(periodo.estaCumplido(LocalDate.of(2026, 11, 11)));
        assertEquals(1, periodo.diasRestantes(LocalDate.of(2026, 11, 10)));
        assertThrows(InvalidDomainDataException.class, () -> new PeriodoCuarentena(INICIO, 0));
    }

    @Test
    @DisplayName("No se autoriza la salida antes de cumplir el periodo mínimo")
    void salidaAntesDeTiempo() {
        Cuarentena cuarentena = nuevaCuarentena(10);
        assertThrows(QuarantinePeriodNotCompletedException.class,
                () -> cuarentena.autorizarSalida(veterinario(), INICIO.plusDays(5)));
        assertEquals(EstadoCuarentena.EN_CURSO, cuarentena.getEstado());
    }

    @Test
    @DisplayName("Solo un veterinario autoriza la salida")
    void soloVeterinario() {
        Cuarentena cuarentena = nuevaCuarentena(10);
        Administrador admin = Administrador.create("Admin", Email.of("admin@refupet.co"));
        assertThrows(QuarantineExitNotAuthorizedException.class,
                () -> cuarentena.autorizarSalida(admin, INICIO.plusDays(10)));
    }

    @Test
    @DisplayName("Ciclo completo: en curso -> salida autorizada -> finalizada")
    void cicloCompleto() {
        Cuarentena cuarentena = nuevaCuarentena(10);
        Veterinario vet = veterinario();
        cuarentena.autorizarSalida(vet, INICIO.plusDays(10));
        assertEquals(EstadoCuarentena.SALIDA_AUTORIZADA, cuarentena.getEstado());
        assertEquals(vet.getUsuarioId(), cuarentena.getSalidaAutorizadaPor());

        cuarentena.finalizar(INICIO.plusDays(11));
        assertEquals(EstadoCuarentena.FINALIZADA, cuarentena.getEstado());
        assertFalse(cuarentena.estaActiva());
        assertThrows(InvalidQuarantineStateException.class, () -> cuarentena.finalizar(INICIO.plusDays(12)));
    }

    @Test
    @DisplayName("No se pasa a población general sin autorización de salida")
    void finalizarSinAutorizacion() {
        Cuarentena cuarentena = nuevaCuarentena(3);
        assertThrows(QuarantineExitNotAuthorizedException.class, () -> cuarentena.finalizar(INICIO.plusDays(5)));
    }
}
