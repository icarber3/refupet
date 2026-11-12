package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.TratamientoId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Tratamiento médico indicado a un animal (regla 8).
 * La fecha heredada representa la fecha de inicio del tratamiento.
 */
public class Tratamiento extends RegistroClinico {

    private final TratamientoId tratamientoId;
    private final String descripcion;
    private final String indicaciones;

    private Tratamiento(TratamientoId id, AnimalId animalId, UsuarioId veterinarioId, LocalDate fechaInicio,
                        String descripcion, String indicaciones, String observaciones) {
        super(animalId, veterinarioId, fechaInicio, observaciones);
        this.tratamientoId = Validaciones.obligatorio(id, "tratamientoId");
        this.descripcion = Validaciones.textoObligatorio(descripcion, "descripcion");
        this.indicaciones = Validaciones.textoOpcional(indicaciones);
    }

    public static Tratamiento create(AnimalId animalId, UsuarioId veterinarioId, LocalDate fechaInicio,
                                     String descripcion, String indicaciones, String observaciones) {
        return new Tratamiento(TratamientoId.generate(), animalId, veterinarioId, fechaInicio,
                descripcion, indicaciones, observaciones);
    }

    public static Tratamiento of(TratamientoId id, AnimalId animalId, UsuarioId veterinarioId, LocalDate fechaInicio,
                                 String descripcion, String indicaciones, String observaciones) {
        return new Tratamiento(id, animalId, veterinarioId, fechaInicio, descripcion, indicaciones, observaciones);
    }

    @Override
    public TratamientoId getId() {
        return tratamientoId;
    }

    @Override
    public TipoRegistroClinico getTipo() {
        return TipoRegistroClinico.TRATAMIENTO;
    }

    @Override
    public String resumen() {
        return "Tratamiento: " + descripcion + (indicaciones != null ? " | Indicaciones: " + indicaciones : "");
    }

    public LocalDate getFechaInicio() { return getFecha(); }
    public String getDescripcion() { return descripcion; }
    public String getIndicaciones() { return indicaciones; }
}
