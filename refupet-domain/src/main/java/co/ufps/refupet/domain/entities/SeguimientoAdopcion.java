package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.SeguimientoAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Seguimiento posterior a una adopción (regla 24).
 *
 * Decisión: entidad interna del agregado Adopcion. Una adopción puede tener varios
 * seguimientos, cada uno con datos propios (fecha, observaciones, responsable), por lo que
 * tienen identidad; pero solo existen dentro de su adopción y se guardan con ella.
 */
public class SeguimientoAdopcion {

    private final SeguimientoAdopcionId seguimientoId;
    private final LocalDate fecha;
    private final String observaciones;
    private final UsuarioId registradoPor;

    private SeguimientoAdopcion(SeguimientoAdopcionId seguimientoId, LocalDate fecha,
                                String observaciones, UsuarioId registradoPor) {
        this.seguimientoId = Validaciones.obligatorio(seguimientoId, "seguimientoId");
        this.fecha = Validaciones.obligatorio(fecha, "fecha");
        this.observaciones = Validaciones.textoObligatorio(observaciones, "observaciones");
        this.registradoPor = Validaciones.obligatorio(registradoPor, "registradoPor");
    }

    public static SeguimientoAdopcion create(LocalDate fecha, String observaciones, UsuarioId registradoPor) {
        return new SeguimientoAdopcion(SeguimientoAdopcionId.generate(), fecha, observaciones, registradoPor);
    }

    public static SeguimientoAdopcion of(SeguimientoAdopcionId seguimientoId, LocalDate fecha,
                                         String observaciones, UsuarioId registradoPor) {
        return new SeguimientoAdopcion(seguimientoId, fecha, observaciones, registradoPor);
    }

    public SeguimientoAdopcionId getSeguimientoId() { return seguimientoId; }
    public LocalDate getFecha() { return fecha; }
    public String getObservaciones() { return observaciones; }
    public UsuarioId getRegistradoPor() { return registradoPor; }
}
