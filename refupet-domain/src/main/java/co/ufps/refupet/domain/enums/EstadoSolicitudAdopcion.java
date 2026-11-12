package co.ufps.refupet.domain.enums;

import co.ufps.refupet.domain.Validaciones;

/**
 * Estado de una solicitud de adopción. Solo una solicitud PENDIENTE puede
 * ser aprobada o rechazada por el administrador.
 */
public enum EstadoSolicitudAdopcion {
    PENDIENTE,
    APROBADA,
    RECHAZADA;

    public static EstadoSolicitudAdopcion fromString(String dato) {
        return Validaciones.enumDesdeTexto(EstadoSolicitudAdopcion.class, dato, "estadoSolicitud");
    }
}
