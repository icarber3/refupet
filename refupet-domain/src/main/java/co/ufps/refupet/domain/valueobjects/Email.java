package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value Object de correo electrónico.
 * Aporta valor porque concentra la validación de formato y la normalización
 * (minúsculas), necesaria para garantizar que no haya dos cuentas con el mismo email.
 */
public final class Email {

    private static final Pattern FORMATO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        String limpio = Validaciones.textoObligatorio(value, "email").toLowerCase(Locale.ROOT);
        if (!FORMATO.matcher(limpio).matches()) {
            throw new InvalidDomainDataException("El email no tiene un formato válido: " + value);
        }
        this.value = limpio;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((Email) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
