package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.EstadoAdopcion;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionStateException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.AdopcionId;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.SolicitudAdopcionId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Devolucion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adopción formalizada a partir de una solicitud aprobada (reglas 23 a 25).
 *
 * Agrupa los seguimientos posteriores (entidades internas) y la eventual devolución
 * (Value Object). Una adopción con devolución deja de estar vigente y el animal
 * vuelve a estar disponible para adopción.
 */
public class Adopcion {

    private final AdopcionId adopcionId;
    private final SolicitudAdopcionId solicitudId;
    private final AnimalId animalId;
    private final UsuarioId solicitanteId;
    private final LocalDate fechaFormalizacion;
    private final UsuarioId formalizadaPor;
    private EstadoAdopcion estado;
    private final List<SeguimientoAdopcion> seguimientos;
    private Devolucion devolucion;

    private Adopcion(AdopcionId adopcionId, SolicitudAdopcionId solicitudId, AnimalId animalId,
                     UsuarioId solicitanteId, LocalDate fechaFormalizacion, UsuarioId formalizadaPor) {
        this.adopcionId = Validaciones.obligatorio(adopcionId, "adopcionId");
        this.solicitudId = Validaciones.obligatorio(solicitudId, "solicitudId");
        this.animalId = Validaciones.obligatorio(animalId, "animalId");
        this.solicitanteId = Validaciones.obligatorio(solicitanteId, "solicitanteId");
        this.fechaFormalizacion = Validaciones.obligatorio(fechaFormalizacion, "fechaFormalizacion");
        this.formalizadaPor = Validaciones.obligatorio(formalizadaPor, "formalizadaPor");
        this.estado = EstadoAdopcion.FORMALIZADA;
        this.seguimientos = new ArrayList<>();
    }

    /** Regla 23: solo una solicitud aprobada puede formalizarse como adopción. */
    public static Adopcion formalizar(SolicitudAdopcion solicitud, LocalDate fecha, UsuarioId formalizadaPor) {
        Validaciones.obligatorio(solicitud, "solicitud");
        if (!solicitud.estaAprobada()) {
            throw new InvalidAdoptionRequestStateException(
                    "Solo se puede formalizar una adopción a partir de una solicitud aprobada; estado: "
                            + solicitud.getEstado());
        }
        return new Adopcion(AdopcionId.generate(), solicitud.getSolicitudId(), solicitud.getAnimalId(),
                solicitud.getSolicitanteId(), fecha, formalizadaPor);
    }

    /** Reconstrucción desde persistencia. */
    public static Adopcion of(AdopcionId adopcionId, SolicitudAdopcionId solicitudId, AnimalId animalId,
                              UsuarioId solicitanteId, LocalDate fechaFormalizacion, UsuarioId formalizadaPor,
                              EstadoAdopcion estado, List<SeguimientoAdopcion> seguimientos, Devolucion devolucion) {
        Adopcion adopcion = new Adopcion(adopcionId, solicitudId, animalId, solicitanteId,
                fechaFormalizacion, formalizadaPor);
        adopcion.estado = Validaciones.obligatorio(estado, "estado");
        if (seguimientos != null) {
            adopcion.seguimientos.addAll(seguimientos);
        }
        adopcion.devolucion = devolucion;
        return adopcion;
    }

    // ── Comandos de negocio ──

    /** Regla 24: seguimiento posterior a la adopción (no admitido tras una devolución). */
    public SeguimientoAdopcion registrarSeguimiento(LocalDate fecha, String observaciones, UsuarioId registradoPor) {
        exigirVigente("registrar un seguimiento");
        exigirFechaPosteriorAFormalizacion(fecha);
        SeguimientoAdopcion seguimiento = SeguimientoAdopcion.create(fecha, observaciones, registradoPor);
        seguimientos.add(seguimiento);
        this.estado = EstadoAdopcion.CON_SEGUIMIENTO;
        return seguimiento;
    }

    /** Regla 25: registrar la devolución del animal (una sola vez). */
    public void registrarDevolucion(LocalDate fecha, String motivo, UsuarioId registradoPor) {
        exigirVigente("registrar una devolución");
        exigirFechaPosteriorAFormalizacion(fecha);
        this.devolucion = new Devolucion(fecha, motivo, registradoPor);
        this.estado = EstadoAdopcion.CON_DEVOLUCION;
    }

    /** Una adopción está vigente mientras el animal no haya sido devuelto. */
    public boolean estaVigente() {
        return estado != EstadoAdopcion.CON_DEVOLUCION;
    }

    private void exigirVigente(String accion) {
        if (!estaVigente()) {
            throw new InvalidAdoptionStateException(
                    "No se puede " + accion + ": la adopción ya registra una devolución");
        }
    }

    private void exigirFechaPosteriorAFormalizacion(LocalDate fecha) {
        Validaciones.obligatorio(fecha, "fecha");
        if (fecha.isBefore(fechaFormalizacion)) {
            throw new InvalidDomainDataException(
                    "La fecha no puede ser anterior a la formalización de la adopción (" + fechaFormalizacion + ")");
        }
    }

    // ── Getters ──

    public AdopcionId getAdopcionId() { return adopcionId; }
    public SolicitudAdopcionId getSolicitudId() { return solicitudId; }
    public AnimalId getAnimalId() { return animalId; }
    public UsuarioId getSolicitanteId() { return solicitanteId; }
    public LocalDate getFechaFormalizacion() { return fechaFormalizacion; }
    public UsuarioId getFormalizadaPor() { return formalizadaPor; }
    public EstadoAdopcion getEstado() { return estado; }
    public List<SeguimientoAdopcion> getSeguimientos() { return List.copyOf(seguimientos); }
    public Optional<Devolucion> getDevolucion() { return Optional.ofNullable(devolucion); }
}
