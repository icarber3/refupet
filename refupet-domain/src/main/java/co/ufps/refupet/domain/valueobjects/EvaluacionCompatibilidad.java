package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDateTime;

/**
 * Resultado de evaluar aspectos básicos de compatibilidad de una solicitud (regla 21).
 *
 * El enunciado no define criterios concretos de compatibilidad, así que no se inventan:
 * se registra el resultado (compatible o no) y las observaciones del evaluador.
 * Es un Value Object dentro del agregado SolicitudAdopcion.
 */
public record EvaluacionCompatibilidad(boolean compatible,
                                       String observaciones,
                                       UsuarioId evaluadoPor,
                                       LocalDateTime fechaEvaluacion) {

    public EvaluacionCompatibilidad {
        observaciones = Validaciones.textoOpcional(observaciones);
        Validaciones.obligatorio(evaluadoPor, "evaluadoPor");
        Validaciones.obligatorio(fechaEvaluacion, "fechaEvaluacion");
    }
}
