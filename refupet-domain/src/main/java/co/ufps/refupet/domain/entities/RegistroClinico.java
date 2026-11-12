package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.Identificador;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Abstracción de cualquier acto veterinario que forma parte del historial clínico.
 *
 * Abstracción/herencia: evaluación clínica, vacunación, desparasitación, tratamiento y
 * esterilización comparten animal atendido, veterinario responsable, fecha y observaciones.
 * Polimorfismo: cada subclase define su identificador, su tipo y su resumen(), lo que permite
 * construir el historial clínico (regla 10) como una sola lista ordenada de RegistroClinico.
 *
 * Los registros clínicos son inmutables una vez creados: el historial no se reescribe.
 */
public abstract class RegistroClinico {

    private final AnimalId animalId;
    private final UsuarioId veterinarioId;
    private final LocalDate fecha;
    private final String observaciones;

    protected RegistroClinico(AnimalId animalId, UsuarioId veterinarioId, LocalDate fecha, String observaciones) {
        this.animalId = Validaciones.obligatorio(animalId, "animalId");
        this.veterinarioId = Validaciones.obligatorio(veterinarioId, "veterinarioId");
        this.fecha = Validaciones.obligatorio(fecha, "fecha");
        this.observaciones = Validaciones.textoOpcional(observaciones);
    }

    /** Identificador propio del registro (cada subclase devuelve su tipo de Id). */
    public abstract Identificador getId();

    public abstract TipoRegistroClinico getTipo();

    /** Descripción breve del registro para mostrar en el historial clínico. */
    public abstract String resumen();

    public AnimalId getAnimalId() { return animalId; }
    public UsuarioId getVeterinarioId() { return veterinarioId; }
    public LocalDate getFecha() { return fecha; }
    public String getObservaciones() { return observaciones; }
}
