package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.Validaciones;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.enums.Especie;
import co.ufps.refupet.domain.exceptions.InvalidAnimalStateException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.EstadoSalud;
import co.ufps.refupet.domain.valueobjects.RegistroIngreso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Agregado raíz que representa a un animal del refugio.
 *
 * Contiene: datos generales (regla 3), trazabilidad del ingreso (regla 5), observaciones de
 * comportamiento (regla 4), estado de salud vigente (regla 11), si está esterilizado (regla 9)
 * y su ubicación (cuarentena / población general, reglas 12-16).
 *
 * Los registros clínicos (evaluaciones, vacunas, etc.) NO están dentro del agregado: son
 * agregados propios que referencian al animal por AnimalId, para que el historial pueda
 * crecer sin cargar todo en el animal.
 */
public class Animal {

    private final AnimalId animalId;
    private final String nombre;
    private final Especie especie;
    private final String raza;
    private final String descripcion;
    private final RegistroIngreso registroIngreso;
    private EstadoUbicacionAnimal ubicacion;   // null: aún no se ha asignado ubicación
    private EstadoSalud estadoSalud;           // null: aún no se ha registrado
    private boolean esterilizado;
    private final List<ObservacionComportamiento> observaciones;

    private Animal(AnimalId animalId, String nombre, Especie especie, String raza,
                   String descripcion, RegistroIngreso registroIngreso) {
        this.animalId = Validaciones.obligatorio(animalId, "animalId");
        this.nombre = Validaciones.textoObligatorio(nombre, "nombre");
        this.especie = Validaciones.obligatorio(especie, "especie");
        this.raza = Validaciones.textoOpcional(raza);
        this.descripcion = Validaciones.textoOpcional(descripcion);
        this.registroIngreso = Validaciones.obligatorio(registroIngreso, "registroIngreso");
        this.esterilizado = false;
        this.observaciones = new ArrayList<>();
    }

    /** Regla 1: registrar un animal que ingresa al refugio. */
    public static Animal create(String nombre, Especie especie, String raza,
                                String descripcion, RegistroIngreso registroIngreso) {
        return new Animal(AnimalId.generate(), nombre, especie, raza, descripcion, registroIngreso);
    }

    /** Reconstrucción desde persistencia. */
    public static Animal of(AnimalId animalId, String nombre, Especie especie, String raza,
                            String descripcion, RegistroIngreso registroIngreso,
                            EstadoUbicacionAnimal ubicacion, EstadoSalud estadoSalud,
                            boolean esterilizado, List<ObservacionComportamiento> observaciones) {
        Animal animal = new Animal(animalId, nombre, especie, raza, descripcion, registroIngreso);
        animal.ubicacion = ubicacion;
        animal.estadoSalud = estadoSalud;
        animal.esterilizado = esterilizado;
        if (observaciones != null) {
            animal.observaciones.addAll(observaciones);
        }
        return animal;
    }

    // ── Comandos de negocio ──

    /** Regla 4: registrar una observación de comportamiento. */
    public ObservacionComportamiento registrarObservacion(String descripcion, UsuarioId registradoPor) {
        ObservacionComportamiento observacion = ObservacionComportamiento.create(descripcion, registradoPor);
        observaciones.add(observacion);
        return observacion;
    }

    /** Regla 11: actualizar el estado de salud del animal. */
    public void actualizarEstadoSalud(String descripcionEstado, UsuarioId veterinarioId) {
        this.estadoSalud = new EstadoSalud(descripcionEstado, LocalDateTime.now(), veterinarioId);
    }

    /** Regla 9: un animal solo puede quedar registrado como esterilizado una vez. */
    public void marcarEsterilizado() {
        if (esterilizado) {
            throw new InvalidAnimalStateException("El animal " + nombre + " ya está registrado como esterilizado");
        }
        this.esterilizado = true;
    }

    /** Regla 12: el animal pasa a cuarentena. */
    public void ingresarACuarentena() {
        if (ubicacion == EstadoUbicacionAnimal.CUARENTENA) {
            throw new InvalidAnimalStateException("El animal " + nombre + " ya se encuentra en cuarentena");
        }
        this.ubicacion = EstadoUbicacionAnimal.CUARENTENA;
    }

    /** Regla 16: paso a población general, solo desde cuarentena. */
    public void trasladarAPoblacionGeneral() {
        if (ubicacion != EstadoUbicacionAnimal.CUARENTENA) {
            throw new InvalidAnimalStateException(
                    "Solo un animal en cuarentena puede pasar a población general");
        }
        this.ubicacion = EstadoUbicacionAnimal.POBLACION_GENERAL;
    }

    public boolean estaEnCuarentena() {
        return ubicacion == EstadoUbicacionAnimal.CUARENTENA;
    }

    public boolean estaEnPoblacionGeneral() {
        return ubicacion == EstadoUbicacionAnimal.POBLACION_GENERAL;
    }

    // ── Getters ──

    public AnimalId getAnimalId() { return animalId; }
    public String getNombre() { return nombre; }
    public Especie getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public String getDescripcion() { return descripcion; }
    public RegistroIngreso getRegistroIngreso() { return registroIngreso; }
    public Optional<EstadoUbicacionAnimal> getUbicacion() { return Optional.ofNullable(ubicacion); }
    public Optional<EstadoSalud> getEstadoSalud() { return Optional.ofNullable(estadoSalud); }
    public boolean isEsterilizado() { return esterilizado; }
    public List<ObservacionComportamiento> getObservaciones() { return List.copyOf(observaciones); }
}
