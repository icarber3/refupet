package co.ufps.refupet.integracion;

import co.ufps.refupet.application.commands.animal.RegisterAnimalCommand;
import co.ufps.refupet.application.commands.clinica.RegisterSterilizationCommand;
import co.ufps.refupet.application.commands.cuarentena.AuthorizeQuarantineExitCommand;
import co.ufps.refupet.application.commands.cuarentena.MoveAnimalToGeneralPopulationCommand;
import co.ufps.refupet.application.commands.cuarentena.StartQuarantineCommand;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.animal.GetAnimalHandler;
import co.ufps.refupet.application.handlers.animal.RegisterAnimalHandler;
import co.ufps.refupet.application.handlers.clinica.RegisterSterilizationHandler;
import co.ufps.refupet.application.handlers.cuarentena.AuthorizeQuarantineExitHandler;
import co.ufps.refupet.application.handlers.cuarentena.MoveAnimalToGeneralPopulationHandler;
import co.ufps.refupet.application.handlers.cuarentena.StartQuarantineHandler;
import co.ufps.refupet.application.queries.animal.GetAnimalQuery;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.entities.Veterinario;
import co.ufps.refupet.domain.entities.Voluntario;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.EstadoUbicacionAnimal;
import co.ufps.refupet.domain.exceptions.InvalidAnimalStateException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.infrastructure.persistence.memory.AnimalRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.UsuarioRepositoryEnMemoria;
import co.ufps.refupet.integracion.RegistroTransacciones.Operacion;
import io.quarkus.arc.ClientProxy;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Transacciones de los casos de uso dentro del contenedor de Quarkus (Narayana JTA + interceptor
 * {@code @Transactional}), con los repositorios en memoria seleccionados como alternativa de prueba.
 * No se ejecuta ninguna operación sobre las tablas de la base de datos.
 */
@QuarkusTest
@TestProfile(PerfilRepositoriosEnMemoria.class)
class TransaccionesCasosDeUsoTest {

    @Inject RegistroTransacciones registro;
    @Inject IUsuarioRepository usuarios;
    @Inject IAnimalRepository animales;
    @Inject RegisterAnimalHandler registerAnimal;
    @Inject GetAnimalHandler getAnimal;
    @Inject StartQuarantineHandler startQuarantine;
    @Inject AuthorizeQuarantineExitHandler authorizeQuarantineExit;
    @Inject MoveAnimalToGeneralPopulationHandler moveToGeneralPopulation;
    @Inject RegisterSterilizationHandler registerSterilization;

    private String voluntarioId;
    private String veterinarioId;

    private static String email(String prefijo) {
        return prefijo + "-" + UUID.randomUUID() + "@refupet.co";
    }

    @BeforeEach
    void preparar() {
        registro.reiniciar();
        Usuario voluntario = Voluntario.of(UsuarioId.generate(), "Vol", Email.of(email("vol")),
                EstadoCuenta.ACTIVA, LocalDateTime.now());
        Usuario veterinario = Veterinario.of(UsuarioId.generate(), "Vet", Email.of(email("vet")),
                EstadoCuenta.ACTIVA, LocalDateTime.now());
        usuarios.save(voluntario);
        usuarios.save(veterinario);
        voluntarioId = voluntario.getUsuarioId().value();
        veterinarioId = veterinario.getUsuarioId().value();
        registro.reiniciar();
    }

    private String registrarAnimal() {
        return registerAnimal.execute(new RegisterAnimalCommand(voluntarioId, "Toby", "PERRO",
                null, null, null, null, null)).animalId();
    }

    private List<Operacion> operaciones(String... nombres) {
        Set<String> filtro = Set.of(nombres);
        return registro.operaciones().stream().filter(o -> filtro.contains(o.nombre())).toList();
    }

    /** Todas las operaciones indicadas ocurrieron en UNA misma transacción activa, que terminó con el estado esperado. */
    private void assertMismaTransaccion(int estadoEsperado, String... nombres) {
        List<Operacion> ops = operaciones(nombres);
        assertEquals(Set.of(nombres), ops.stream().map(Operacion::nombre).collect(Collectors.toSet()),
                "operaciones registradas: " + registro.operaciones());
        Set<Object> transacciones = ops.stream().map(Operacion::transaccion).collect(Collectors.toSet());
        assertEquals(1, transacciones.size(), "se esperaba una sola transacción: " + ops);
        Object transaccion = transacciones.iterator().next();
        assertNotNull(transaccion, "las operaciones deben ejecutarse dentro de una transacción");
        assertEquals(estadoEsperado, registro.estadoFinal(transaccion));
    }

    @Test
    void losRepositoriosEnMemoriaSoloSeSeleccionanEnEstePerfil() {
        assertInstanceOf(AnimalRepositoryEnMemoria.class, ClientProxy.unwrap(animales));
        assertInstanceOf(UsuarioRepositoryEnMemoria.class, ClientProxy.unwrap(usuarios));
    }

    @Test
    void unCasoDeUsoDeEscrituraSeEjecutaEnUnaTransaccionConfirmada() {
        registrarAnimal();
        assertMismaTransaccion(Status.STATUS_COMMITTED, "usuario.findById", "animal.save");
    }

    @Test
    void cadaEjecucionUsaSuPropiaTransaccion() {
        registrarAnimal();
        registrarAnimal();
        List<Operacion> saves = operaciones("animal.save");
        assertEquals(2, saves.size());
        assertNotEquals(saves.get(0).transaccion(), saves.get(1).transaccion());
    }

    @Test
    void unaConsultaNoAbreTransaccion() {
        String animalId = registrarAnimal();
        registro.reiniciar();
        AnimalDTO animal = getAnimal.execute(new GetAnimalQuery(voluntarioId, animalId));
        assertEquals(animalId, animal.animalId());
        List<Operacion> lecturas = operaciones("animal.findById");
        assertFalse(lecturas.isEmpty());
        lecturas.forEach(o -> assertNull(o.transaccion(), "las consultas no deben abrir transacción"));
    }

    /** Dos agregados (Animal + Cuarentena) en una sola transacción. */
    @Test
    void iniciarCuarentenaEscribeAnimalYCuarentenaEnLaMismaTransaccion() {
        String animalId = registrarAnimal();
        registro.reiniciar();
        startQuarantine.execute(new StartQuarantineCommand(veterinarioId, animalId, null, 5));
        assertMismaTransaccion(Status.STATUS_COMMITTED, "animal.save", "cuarentena.save");
    }

    /** Si falla la segunda escritura, la transacción del caso de uso completo se revierte. */
    @Test
    void siFallaLaSegundaEscrituraSeRevierteTodoElCasoDeUso() {
        String animalId = registrarAnimal();
        registro.reiniciar();
        registro.fallarEn("cuarentena.save");
        assertThrows(IllegalStateException.class,
                () -> startQuarantine.execute(new StartQuarantineCommand(veterinarioId, animalId, null, 5)));
        assertMismaTransaccion(Status.STATUS_ROLLEDBACK, "animal.save", "cuarentena.save");
    }

    /** Dos agregados (Cuarentena + Animal) en una sola transacción. */
    @Test
    void pasarAPoblacionGeneralEscribeCuarentenaYAnimalEnLaMismaTransaccion() {
        String animalId = registrarAnimal();
        CuarentenaDTO cuarentena = startQuarantine.execute(new StartQuarantineCommand(
                veterinarioId, animalId, LocalDate.now().minusDays(3), 1));
        authorizeQuarantineExit.execute(new AuthorizeQuarantineExitCommand(veterinarioId, cuarentena.cuarentenaId()));
        registro.reiniciar();
        AnimalDTO resultado = moveToGeneralPopulation.execute(
                new MoveAnimalToGeneralPopulationCommand(veterinarioId, cuarentena.cuarentenaId()));
        assertEquals(EstadoUbicacionAnimal.POBLACION_GENERAL.name(), resultado.ubicacion());
        assertMismaTransaccion(Status.STATUS_COMMITTED, "cuarentena.save", "animal.save");
    }

    /** Dos agregados (Animal + Esterilizacion) en una sola transacción. */
    @Test
    void esterilizarEscribeAnimalYRegistroClinicoEnLaMismaTransaccion() {
        String animalId = registrarAnimal();
        registro.reiniciar();
        registerSterilization.execute(new RegisterSterilizationCommand(veterinarioId, animalId, null, null));
        assertMismaTransaccion(Status.STATUS_COMMITTED, "animal.save", "esterilizacion.save");
        assertTrue(animales.findById(AnimalId.of(animalId)).orElseThrow().isEsterilizado());
    }

    /** Una excepción de dominio (RuntimeException) también revierte la transacción del caso de uso. */
    @Test
    void unaExcepcionDeDominioRevierteLaTransaccion() {
        String animalId = registrarAnimal();
        registerSterilization.execute(new RegisterSterilizationCommand(veterinarioId, animalId, null, null));
        registro.reiniciar();
        assertThrows(InvalidAnimalStateException.class,
                () -> registerSterilization.execute(new RegisterSterilizationCommand(veterinarioId, animalId, null, null)));
        List<Operacion> ops = operaciones("usuario.findById");
        assertEquals(1, ops.size());
        assertNotNull(ops.get(0).transaccion());
        assertEquals(Status.STATUS_ROLLEDBACK, registro.estadoFinal(ops.get(0).transaccion()));
        assertTrue(operaciones("animal.save", "esterilizacion.save").isEmpty());
    }

    /**
     * Esterilización: si falla el registro clínico (segunda escritura), se revierte también la marca del
     * animal (primera escritura). Nota: los repositorios en memoria no deshacen sus datos; lo que se
     * verifica es que ambas escrituras pertenecen a la misma transacción JTA y que esta termina revertida,
     * que es lo que hace que la base de datos descarte las dos.
     */
    @Test
    void siFallaElRegistroDeEsterilizacionSeRevierteLaMarcaDelAnimal() {
        String animalId = registrarAnimal();
        registro.reiniciar();
        registro.fallarEn("esterilizacion.save");
        assertThrows(IllegalStateException.class,
                () -> registerSterilization.execute(new RegisterSterilizationCommand(veterinarioId, animalId, null, null)));
        assertMismaTransaccion(Status.STATUS_ROLLEDBACK, "animal.save", "esterilizacion.save");
    }

    /** Paso a población general: si falla la actualización del animal, se revierte la finalización de la cuarentena. */
    @Test
    void siFallaElTrasladoDelAnimalSeRevierteLaFinalizacionDeLaCuarentena() {
        String animalId = registrarAnimal();
        CuarentenaDTO cuarentena = startQuarantine.execute(new StartQuarantineCommand(
                veterinarioId, animalId, LocalDate.now().minusDays(3), 1));
        authorizeQuarantineExit.execute(new AuthorizeQuarantineExitCommand(veterinarioId, cuarentena.cuarentenaId()));
        registro.reiniciar();
        registro.fallarEn("animal.save");
        assertThrows(IllegalStateException.class, () -> moveToGeneralPopulation.execute(
                new MoveAnimalToGeneralPopulationCommand(veterinarioId, cuarentena.cuarentenaId())));
        assertMismaTransaccion(Status.STATUS_ROLLEDBACK, "cuarentena.save", "animal.save");
    }
}
