package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.EstadoCuarentena;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.InvalidQuarantineStateException;
import co.ufps.refupet.domain.exceptions.QuarantineExitNotAuthorizedException;
import co.ufps.refupet.domain.exceptions.QuarantinePeriodNotCompletedException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.PeriodoCuarentena;

import java.time.LocalDate;

/**
 * Agregado que representa una cuarentena de un animal (reglas 12 a 16).
 *
 * Ciclo de vida: iniciar -> autorizarSalida (solo veterinario, periodo mínimo cumplido)
 * -> finalizar (paso a población general, solo con salida autorizada).
 * Es un agregado propio (y no parte de Animal) porque un animal puede pasar por varias
 * cuarentenas a lo largo del tiempo y cada una conserva su propia trazabilidad.
 */
public class Cuarentena {

    private final CuarentenaId cuarentenaId;
    private final AnimalId animalId;
    private final PeriodoCuarentena periodo;
    private final UsuarioId iniciadaPor;
    private EstadoCuarentena estado;
    private UsuarioId salidaAutorizadaPor;
    private LocalDate fechaAutorizacionSalida;
    private LocalDate fechaFinalizacion;

    private Cuarentena(CuarentenaId cuarentenaId, AnimalId animalId, PeriodoCuarentena periodo, UsuarioId iniciadaPor) {
        this.cuarentenaId = Validaciones.obligatorio(cuarentenaId, "cuarentenaId");
        this.animalId = Validaciones.obligatorio(animalId, "animalId");
        this.periodo = Validaciones.obligatorio(periodo, "periodo");
        this.iniciadaPor = Validaciones.obligatorio(iniciadaPor, "iniciadaPor");
        this.estado = EstadoCuarentena.EN_CURSO;
    }

    /** Regla 12: registrar el inicio de la cuarentena de un animal. */
    public static Cuarentena iniciar(AnimalId animalId, PeriodoCuarentena periodo, UsuarioId iniciadaPor) {
        return new Cuarentena(CuarentenaId.generate(), animalId, periodo, iniciadaPor);
    }

    /** Reconstrucción desde persistencia. */
    public static Cuarentena of(CuarentenaId cuarentenaId, AnimalId animalId, PeriodoCuarentena periodo,
                                UsuarioId iniciadaPor, EstadoCuarentena estado, UsuarioId salidaAutorizadaPor,
                                LocalDate fechaAutorizacionSalida, LocalDate fechaFinalizacion) {
        Cuarentena cuarentena = new Cuarentena(cuarentenaId, animalId, periodo, iniciadaPor);
        cuarentena.estado = Validaciones.obligatorio(estado, "estado");
        cuarentena.salidaAutorizadaPor = salidaAutorizadaPor;
        cuarentena.fechaAutorizacionSalida = fechaAutorizacionSalida;
        cuarentena.fechaFinalizacion = fechaFinalizacion;
        return cuarentena;
    }

    // ── Comandos de negocio ──

    /** Regla 14: controla si se cumplió el periodo mínimo establecido. */
    public boolean periodoMinimoCumplido(LocalDate fecha) {
        return periodo.estaCumplido(fecha);
    }

    /**
     * Reglas 14 y 15: la salida requiere autorización de un veterinario y
     * solo puede darse cuando se cumplió el periodo mínimo.
     */
    public void autorizarSalida(Usuario autorizador, LocalDate fecha) {
        Validaciones.obligatorio(autorizador, "autorizador");
        Validaciones.obligatorio(fecha, "fecha");
        if (autorizador.getRol() != RolUsuario.VETERINARIO) {
            throw new QuarantineExitNotAuthorizedException(
                    "Solo un veterinario puede autorizar la salida de cuarentena");
        }
        if (estado != EstadoCuarentena.EN_CURSO) {
            throw new InvalidQuarantineStateException(
                    "Solo se puede autorizar la salida de una cuarentena en curso; estado actual: " + estado);
        }
        if (!periodo.estaCumplido(fecha)) {
            throw new QuarantinePeriodNotCompletedException(periodo.fechaMinimaSalida());
        }
        this.estado = EstadoCuarentena.SALIDA_AUTORIZADA;
        this.salidaAutorizadaPor = autorizador.getUsuarioId();
        this.fechaAutorizacionSalida = fecha;
    }

    /** Regla 16: finaliza la cuarentena al registrar el paso a población general. */
    public void finalizar(LocalDate fecha) {
        Validaciones.obligatorio(fecha, "fecha");
        if (estado == EstadoCuarentena.EN_CURSO) {
            throw new QuarantineExitNotAuthorizedException(
                    "El animal no puede pasar a población general sin la autorización de salida del veterinario");
        }
        if (estado == EstadoCuarentena.FINALIZADA) {
            throw new InvalidQuarantineStateException("La cuarentena ya fue finalizada");
        }
        this.estado = EstadoCuarentena.FINALIZADA;
        this.fechaFinalizacion = fecha;
    }

    public boolean estaActiva() {
        return estado.esActiva();
    }

    // ── Getters ──

    public CuarentenaId getCuarentenaId() { return cuarentenaId; }
    public AnimalId getAnimalId() { return animalId; }
    public PeriodoCuarentena getPeriodo() { return periodo; }
    public UsuarioId getIniciadaPor() { return iniciadaPor; }
    public EstadoCuarentena getEstado() { return estado; }
    public UsuarioId getSalidaAutorizadaPor() { return salidaAutorizadaPor; }
    public LocalDate getFechaAutorizacionSalida() { return fechaAutorizacionSalida; }
    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
}
