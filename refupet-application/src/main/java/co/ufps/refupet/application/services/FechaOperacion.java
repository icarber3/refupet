package co.ufps.refupet.application.services;

import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Resuelve la fecha de un registro: si no se indica se usa la fecha actual del reloj
 * inyectado, y nunca se aceptan fechas futuras (evita estados inconsistentes).
 * El reloj se inyecta (java.time.Clock) para poder probar reglas que dependen del tiempo,
 * como el periodo mínimo de cuarentena.
 */
public final class FechaOperacion {

    private FechaOperacion() {
    }

    public static LocalDate resolver(LocalDate fechaIndicada, Clock clock, String campo) {
        LocalDate hoy = LocalDate.now(clock);
        if (fechaIndicada == null) {
            return hoy;
        }
        if (fechaIndicada.isAfter(hoy)) {
            throw new InvalidDomainDataException("La " + campo + " no puede ser una fecha futura");
        }
        return fechaIndicada;
    }
}
