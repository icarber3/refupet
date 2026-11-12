package co.ufps.refupet.domain.enums;

/**
 * Tipos de registro que componen el historial clínico de un animal.
 * Cada subclase de RegistroClinico devuelve su tipo (polimorfismo).
 */
public enum TipoRegistroClinico {
    EVALUACION_CLINICA,
    VACUNACION,
    DESPARASITACION,
    TRATAMIENTO,
    ESTERILIZACION
}
