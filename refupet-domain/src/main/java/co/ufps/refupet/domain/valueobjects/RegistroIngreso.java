package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.ids.UsuarioId;

import java.time.LocalDate;

/**
 * Trazabilidad básica del ingreso de un animal (regla 5).
 *
 * Decisión: Value Object inmutable dentro del agregado Animal, no entidad.
 * El ingreso ocurre una sola vez, nunca se modifica y no tiene ciclo de vida
 * propio, por lo que no necesita identidad ni repositorio (no se crea IRegistroIngresoRepository).
 *
 * @param fechaIngreso     fecha en que el animal ingresó al refugio
 * @param registradoPor    voluntario que registró el ingreso
 * @param procedencia      lugar o circunstancia del rescate (opcional)
 * @param condicionInicial condición observada al ingresar (opcional)
 */
public record RegistroIngreso(LocalDate fechaIngreso,
                              UsuarioId registradoPor,
                              String procedencia,
                              String condicionInicial) {

    public RegistroIngreso {
        Validaciones.obligatorio(fechaIngreso, "fechaIngreso");
        Validaciones.obligatorio(registradoPor, "registradoPor");
        procedencia = Validaciones.textoOpcional(procedencia);
        condicionInicial = Validaciones.textoOpcional(condicionInicial);
    }
}
