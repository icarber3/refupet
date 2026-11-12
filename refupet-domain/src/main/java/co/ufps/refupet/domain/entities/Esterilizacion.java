package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.TipoRegistroClinico;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.EsterilizacionId;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Esterilización realizada a un animal (regla 9).
 * Además de quedar en el historial, marca al animal como esterilizado (Animal.marcarEsterilizado),
 * lo que impide registrarla dos veces.
 */
public class Esterilizacion extends RegistroClinico {

    private final EsterilizacionId esterilizacionId;

    private Esterilizacion(EsterilizacionId id, AnimalId animalId, UsuarioId veterinarioId,
                           LocalDate fecha, String observaciones) {
        super(animalId, veterinarioId, fecha, observaciones);
        this.esterilizacionId = Validaciones.obligatorio(id, "esterilizacionId");
    }

    public static Esterilizacion create(AnimalId animalId, UsuarioId veterinarioId, LocalDate fecha,
                                        String observaciones) {
        return new Esterilizacion(EsterilizacionId.generate(), animalId, veterinarioId, fecha, observaciones);
    }

    public static Esterilizacion of(EsterilizacionId id, AnimalId animalId, UsuarioId veterinarioId,
                                    LocalDate fecha, String observaciones) {
        return new Esterilizacion(id, animalId, veterinarioId, fecha, observaciones);
    }

    @Override
    public EsterilizacionId getId() {
        return esterilizacionId;
    }

    @Override
    public TipoRegistroClinico getTipo() {
        return TipoRegistroClinico.ESTERILIZACION;
    }

    @Override
    public String resumen() {
        return "Esterilización realizada";
    }
}
