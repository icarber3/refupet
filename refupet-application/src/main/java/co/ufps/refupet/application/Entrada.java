package co.ufps.refupet.application;

/**
 * Validaciones de forma de los datos de entrada de Commands y Queries
 * (como en Streamflix, los records rechazan entradas vacías con IllegalArgumentException
 * antes de llegar al dominio).
 */
public final class Entrada {

    private Entrada() {
    }

    public static void requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
    }

    public static void requerido(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
    }
}
