package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.ObservacionId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDateTime;

/**
 * Observación sobre el comportamiento de un animal (regla 4).
 *
 * Decisión: entidad interna del agregado Animal (como Profile dentro de Account en Streamflix).
 * Tiene identidad propia (ObservacionId) para distinguir observaciones con el mismo texto y
 * conservar la trazabilidad de quién y cuándo la registró, pero no existe fuera de su animal:
 * se crea a través de Animal.registrarObservacion y se guarda junto con el animal.
 * Por eso no se define un IObservacionComportamientoRepository independiente.
 */
public class ObservacionComportamiento {

    private final ObservacionId observacionId;
    private final String descripcion;
    private final UsuarioId registradoPor;
    private final LocalDateTime fechaRegistro;

    private ObservacionComportamiento(ObservacionId observacionId, String descripcion,
                                      UsuarioId registradoPor, LocalDateTime fechaRegistro) {
        this.observacionId = Validaciones.obligatorio(observacionId, "observacionId");
        this.descripcion = Validaciones.textoObligatorio(descripcion, "descripcion");
        this.registradoPor = Validaciones.obligatorio(registradoPor, "registradoPor");
        this.fechaRegistro = Validaciones.obligatorio(fechaRegistro, "fechaRegistro");
    }

    public static ObservacionComportamiento create(String descripcion, UsuarioId registradoPor) {
        return new ObservacionComportamiento(ObservacionId.generate(), descripcion, registradoPor, LocalDateTime.now());
    }

    public static ObservacionComportamiento of(ObservacionId observacionId, String descripcion,
                                               UsuarioId registradoPor, LocalDateTime fechaRegistro) {
        return new ObservacionComportamiento(observacionId, descripcion, registradoPor, fechaRegistro);
    }

    public ObservacionId getObservacionId() { return observacionId; }
    public String getDescripcion() { return descripcion; }
    public UsuarioId getRegistradoPor() { return registradoPor; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
