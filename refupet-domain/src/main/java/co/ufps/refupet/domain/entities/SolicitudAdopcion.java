package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.EstadoSolicitudAdopcion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.EvaluacionCompatibilidad;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Solicitud de adopción presentada por un solicitante para un animal (reglas 19 a 22).
 *
 * Ciclo de vida: PENDIENTE -> APROBADA | RECHAZADA, decisión que solo toma un administrador.
 * Mientras está pendiente puede registrarse la evaluación básica de compatibilidad.
 */
public class SolicitudAdopcion {

    private final SolicitudAdopcionId solicitudId;
    private final AnimalId animalId;
    private final UsuarioId solicitanteId;
    private final String comentarios;
    private LocalDateTime fechaSolicitud;
    private EstadoSolicitudAdopcion estado;
    private EvaluacionCompatibilidad evaluacionCompatibilidad;
    private UsuarioId decididaPor;
    private LocalDateTime fechaDecision;
    private String motivoRechazo;

    private SolicitudAdopcion(SolicitudAdopcionId solicitudId, AnimalId animalId,
                              UsuarioId solicitanteId, String comentarios) {
        this.solicitudId = Validaciones.obligatorio(solicitudId, "solicitudId");
        this.animalId = Validaciones.obligatorio(animalId, "animalId");
        this.solicitanteId = Validaciones.obligatorio(solicitanteId, "solicitanteId");
        this.comentarios = Validaciones.textoOpcional(comentarios);
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = EstadoSolicitudAdopcion.PENDIENTE;
    }

    /** Regla 19: el solicitante presenta una solicitud de adopción. */
    public static SolicitudAdopcion create(AnimalId animalId, UsuarioId solicitanteId, String comentarios) {
        return new SolicitudAdopcion(SolicitudAdopcionId.generate(), animalId, solicitanteId, comentarios);
    }

    /** Reconstrucción desde persistencia. */
    public static SolicitudAdopcion of(SolicitudAdopcionId solicitudId, AnimalId animalId, UsuarioId solicitanteId,
                                       String comentarios, LocalDateTime fechaSolicitud,
                                       EstadoSolicitudAdopcion estado, EvaluacionCompatibilidad evaluacion,
                                       UsuarioId decididaPor, LocalDateTime fechaDecision, String motivoRechazo) {
        SolicitudAdopcion solicitud = new SolicitudAdopcion(solicitudId, animalId, solicitanteId, comentarios);
        solicitud.fechaSolicitud = Validaciones.obligatorio(fechaSolicitud, "fechaSolicitud");
        solicitud.estado = Validaciones.obligatorio(estado, "estado");
        solicitud.evaluacionCompatibilidad = evaluacion;
        solicitud.decididaPor = decididaPor;
        solicitud.fechaDecision = fechaDecision;
        solicitud.motivoRechazo = motivoRechazo;
        return solicitud;
    }

    // ── Comandos de negocio ──

    /** Regla 21: registrar la evaluación básica de compatibilidad (solo con la solicitud pendiente). */
    public void evaluarCompatibilidad(boolean compatible, String observaciones, UsuarioId evaluadoPor) {
        exigirPendiente("evaluar la compatibilidad");
        this.evaluacionCompatibilidad =
                new EvaluacionCompatibilidad(compatible, observaciones, evaluadoPor, LocalDateTime.now());
    }

    /** Regla 22: solo el administrador aprueba. */
    public void aprobar(Usuario administrador) {
        exigirAdministrador(administrador);
        exigirPendiente("aprobar");
        this.estado = EstadoSolicitudAdopcion.APROBADA;
        registrarDecision(administrador);
    }

    /** Regla 22: solo el administrador rechaza. */
    public void rechazar(Usuario administrador, String motivo) {
        exigirAdministrador(administrador);
        exigirPendiente("rechazar");
        this.estado = EstadoSolicitudAdopcion.RECHAZADA;
        this.motivoRechazo = Validaciones.textoOpcional(motivo);
        registrarDecision(administrador);
    }

    public boolean estaPendiente() {
        return estado == EstadoSolicitudAdopcion.PENDIENTE;
    }

    public boolean estaAprobada() {
        return estado == EstadoSolicitudAdopcion.APROBADA;
    }

    private void exigirPendiente(String accion) {
        if (!estaPendiente()) {
            throw new InvalidAdoptionRequestStateException(
                    "No se puede " + accion + " una solicitud en estado " + estado);
        }
    }

    private void exigirAdministrador(Usuario usuario) {
        Validaciones.obligatorio(usuario, "administrador");
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new UnauthorizedRoleOperationException(
                    "Solo un administrador puede aprobar o rechazar solicitudes de adopción");
        }
    }

    private void registrarDecision(Usuario administrador) {
        this.decididaPor = administrador.getUsuarioId();
        this.fechaDecision = LocalDateTime.now();
    }

    // ── Getters ──

    public SolicitudAdopcionId getSolicitudId() { return solicitudId; }
    public AnimalId getAnimalId() { return animalId; }
    public UsuarioId getSolicitanteId() { return solicitanteId; }
    public String getComentarios() { return comentarios; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public EstadoSolicitudAdopcion getEstado() { return estado; }
    public Optional<EvaluacionCompatibilidad> getEvaluacionCompatibilidad() {
        return Optional.ofNullable(evaluacionCompatibilidad);
    }
    public UsuarioId getDecididaPor() { return decididaPor; }
    public LocalDateTime getFechaDecision() { return fechaDecision; }
    public String getMotivoRechazo() { return motivoRechazo; }
}
