package co.ufps.refupet.domain.enums;

/**
 * Ciclo de vida de una cuarentena:
 * EN_CURSO -> SALIDA_AUTORIZADA (el veterinario autoriza tras cumplir el periodo mínimo)
 * SALIDA_AUTORIZADA -> FINALIZADA (se registra el paso a población general).
 */
public enum EstadoCuarentena {
    EN_CURSO,
    SALIDA_AUTORIZADA,
    FINALIZADA;

    /** Una cuarentena está activa mientras el animal no haya pasado a población general. */
    public boolean esActiva() {
        return this != FINALIZADA;
    }
}
