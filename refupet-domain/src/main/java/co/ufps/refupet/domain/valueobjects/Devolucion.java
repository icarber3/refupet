package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Devolución de un animal adoptado (regla 25).
 *
 * Decisión: Value Object dentro del agregado Adopcion, no entidad independiente.
 * Una adopción puede tener como máximo una devolución, que no cambia después de
 * registrarse; no necesita identidad propia (no se crea DevolucionId ni IDevolucionRepository).
 */
public record Devolucion(LocalDate fecha, String motivo, UsuarioId registradoPor) {

    public Devolucion {
        Validaciones.obligatorio(fecha, "fechaDevolucion");
        motivo = Validaciones.textoObligatorio(motivo, "motivoDevolucion");
        Validaciones.obligatorio(registradoPor, "registradoPor");
    }
}
