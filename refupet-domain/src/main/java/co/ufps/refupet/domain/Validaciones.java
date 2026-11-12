package co.ufps.refupet.domain;

import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;

import java.util.Locale;

/**
 * Validaciones reutilizables por entidades y Value Objects del dominio.
 * Evita duplicar la misma comprobación de invariantes en cada clase.
 */
public final class Validaciones {

    private Validaciones() {
    }

    /** Exige un texto no nulo ni vacío y lo devuelve sin espacios sobrantes. */
    public static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new InvalidDomainDataException("El campo '" + campo + "' es obligatorio");
        }
        return valor.strip();
    }

    /** Normaliza un texto opcional: un texto vacío se guarda como null. */
    public static String textoOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.strip();
    }

    /** Exige que un valor no sea null. */
    public static <T> T obligatorio(T valor, String campo) {
        if (valor == null) {
            throw new InvalidDomainDataException("El campo '" + campo + "' es obligatorio");
        }
        return valor;
    }

    /** Convierte un texto en un valor de enum, aceptando minúsculas, guiones y espacios. */
    public static <E extends Enum<E>> E enumDesdeTexto(Class<E> tipo, String texto, String campo) {
        String normalizado = textoObligatorio(texto, campo)
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        try {
            return Enum.valueOf(tipo, normalizado);
        } catch (IllegalArgumentException e) {
            throw new InvalidDomainDataException("Valor no válido para '" + campo + "': " + texto);
        }
    }
}
