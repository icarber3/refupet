package co.ufps.refupet.api;

/**
 * Validaciones de forma de las peticiones HTTP, previas a construir los Commands/Queries.
 *
 * <h2>Identificación del actor (SOLUCIÓN PROVISIONAL)</h2>
 * Quien ejecuta cada operación se identifica con la cabecera {@value #CABECERA_ACTOR}, que contiene el id
 * de un usuario existente. Los casos de uso verifican con ese id que la cuenta está activa y que su rol
 * tiene permiso para la operación.
 * <p>
 * <strong>Esto NO es autenticación</strong>: el servidor no comprueba que quien envía la petición sea
 * realmente ese usuario, así que cualquiera que conozca un id puede suplantarlo. Es un mecanismo temporal
 * del alcance académico y debe sustituirse por autenticación real antes de cualquier uso fuera de ese entorno.
 */
public final class Peticiones {

    /** Cabecera HTTP con el id del usuario que ejecuta la operación (no autenticada). */
    public static final String CABECERA_ACTOR = "X-Actor-Id";

    private Peticiones() {
    }

    /** Exige la cabecera del actor; su ausencia es una petición inválida (400). */
    public static String actor(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new IllegalArgumentException("Falta la cabecera " + CABECERA_ACTOR + " con el id del usuario que realiza la operación");
        }
        return actorId;
    }

    /** Exige el cuerpo JSON de la petición (400 si falta). */
    public static <T> T cuerpo(T cuerpo) {
        if (cuerpo == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        return cuerpo;
    }
}
