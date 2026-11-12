package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.ids.VacunacionId;

import java.time.LocalDate;

/**
 * Vacunación aplicada a un animal (regla 7).
 * Decisión: registro propio, porque un animal recibe varias vacunas a lo largo del tiempo
 * y cada aplicación debe quedar en el historial.
 */
public class Vacunacion extends RegistroClinico {

    private final VacunacionId vacunacionId;
    private final String vacuna;

    private Vacunacion(VacunacionId id, AnimalId animalId, UsuarioId veterinarioId,
                       LocalDate fecha, String vacuna, String observaciones) {
        super(animalId, veterinarioId, fecha, observaciones);
        this.vacunacionId = Validaciones.obligatorio(id, "vacunacionId");
        this.vacuna = Validaciones.textoObligatorio(vacuna, "vacuna");
    }

    public static Vacunacion create(AnimalId animalId, UsuarioId veterinarioId, LocalDate fecha,
                                    String vacuna, String observaciones) {
        return new Vacunacion(VacunacionId.generate(), animalId, veterinarioId, fecha, vacuna, observaciones);
    }

    public static Vacunacion of(VacunacionId id, AnimalId animalId, UsuarioId veterinarioId,
                                LocalDate fecha, String vacuna, String observaciones) {
        return new Vacunacion(id, animalId, veterinarioId, fecha, vacuna, observaciones);
    }

    @Override
    public VacunacionId getId() {
        return vacunacionId;
    }

    @Override
    public TipoRegistroClinico getTipo() {
        return TipoRegistroClinico.VACUNACION;
    }

    @Override
    public String resumen() {
        return "Vacunación: " + vacuna;
    }

    public String getVacuna() { return vacuna; }
}
