package co.ufps.refupet.domain.enums;

/**
 * Estado de una adopción formalizada:
 * FORMALIZADA -> CON_SEGUIMIENTO (al registrar el primer seguimiento)
 * FORMALIZADA | CON_SEGUIMIENTO -> CON_DEVOLUCION (al registrar una devolución).
 */
public enum EstadoAdopcion {
    FORMALIZADA,
    CON_SEGUIMIENTO,
    CON_DEVOLUCION
}
