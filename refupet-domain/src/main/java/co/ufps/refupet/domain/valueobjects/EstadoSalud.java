package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDateTime;

/**
 * Estado de salud vigente de un animal (regla 11).
 *
 * Decisión: Value Object y no enum ni entidad. El enunciado no define un
 * catálogo cerrado de estados de salud, así que no se inventan valores fijos:
 * el veterinario describe el estado y queda registrado quién y cuándo lo actualizó.
 * Cada actualización reemplaza el valor completo (inmutabilidad).
 */
public record EstadoSalud(String descripcion,
                          LocalDateTime fechaActualizacion,
                          UsuarioId actualizadoPor) {

    public EstadoSalud {
        descripcion = Validaciones.textoObligatorio(descripcion, "estadoSalud");
        Validaciones.obligatorio(fechaActualizacion, "fechaActualizacion");
        Validaciones.obligatorio(actualizadoPor, "actualizadoPor");
    }
}
