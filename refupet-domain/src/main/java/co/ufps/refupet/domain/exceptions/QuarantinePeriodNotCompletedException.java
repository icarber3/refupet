package co.ufps.refupet.domain.exceptions;

import java.time.LocalDate;

/**
 * Se intentó autorizar la salida de cuarentena antes de cumplir el periodo mínimo establecido.
 */
public class QuarantinePeriodNotCompletedException extends DomainException {
    public QuarantinePeriodNotCompletedException(LocalDate fechaMinimaSalida) {
        super("No se ha cumplido el periodo mínimo de cuarentena. Fecha mínima de salida: " + fechaMinimaSalida);
    }
}
