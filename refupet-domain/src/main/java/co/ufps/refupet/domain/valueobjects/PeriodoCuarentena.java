package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Periodo mínimo de una cuarentena (regla 14).
 *
 * Aporta reglas propias: calcula la fecha mínima de salida y si el periodo se ha cumplido.
 * El enunciado no fija un número de días, por eso el periodo mínimo NO está codificado:
 * se establece al iniciar cada cuarentena.
 *
 * @param fechaInicio fecha de inicio de la cuarentena
 * @param diasMinimos duración mínima establecida, en días (mayor que cero)
 */
public record PeriodoCuarentena(LocalDate fechaInicio, int diasMinimos) {

    public PeriodoCuarentena {
        Validaciones.obligatorio(fechaInicio, "fechaInicio");
        if (diasMinimos <= 0) {
            throw new InvalidDomainDataException("El periodo mínimo de cuarentena debe ser mayor que cero días");
        }
    }

    public LocalDate fechaMinimaSalida() {
        return fechaInicio.plusDays(diasMinimos);
    }

    public boolean estaCumplido(LocalDate fecha) {
        return !Validaciones.obligatorio(fecha, "fecha").isBefore(fechaMinimaSalida());
    }

    public long diasRestantes(LocalDate fecha) {
        return Math.max(0, ChronoUnit.DAYS.between(fecha, fechaMinimaSalida()));
    }
}
