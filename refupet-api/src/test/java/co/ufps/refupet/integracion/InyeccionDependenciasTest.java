package co.ufps.refupet.integracion;

import co.ufps.refupet.domain.repositories.IAdopcionRepository;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import co.ufps.refupet.domain.repositories.IDesparasitacionRepository;
import co.ufps.refupet.domain.repositories.IEsterilizacionRepository;
import co.ufps.refupet.domain.repositories.IEvaluacionClinicaRepository;
import co.ufps.refupet.domain.repositories.ISolicitudAdopcionRepository;
import co.ufps.refupet.domain.repositories.ITratamientoRepository;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.repositories.IVacunacionRepository;
import co.ufps.refupet.infrastructure.config.RefuPetAplicacion;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.AdopcionRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.AnimalRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.CuarentenaRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.DesparasitacionRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.EsterilizacionRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.EvaluacionClinicaRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.SolicitudAdopcionRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.TratamientoRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.UsuarioRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.jpa.repository.VacunacionRepositoryJpa;
import co.ufps.refupet.infrastructure.persistence.memory.AdopcionRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.AnimalRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.CuarentenaRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.DesparasitacionRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.EsterilizacionRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.EvaluacionClinicaRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.SolicitudAdopcionRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.TratamientoRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.UsuarioRepositoryEnMemoria;
import co.ufps.refupet.infrastructure.persistence.memory.VacunacionRepositoryEnMemoria;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ClientProxy;
import io.quarkus.arc.Subclass;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica el grafo CDI de PRODUCCIÓN (sin alternativas de prueba): qué implementación resuelve cada
 * contrato del dominio, que los adaptadores en memoria no son beans, que todos los handlers y servicios
 * se construyen por inyección, sus proxies y qué casos de uso son transaccionales.
 *
 * No ejecuta ninguna operación de los repositorios: solo resuelve beans. La base de datos solo se usa
 * en el arranque (generation=validate, lectura de metadatos).
 *
 * El perfil desactiva la eliminación de beans sin uso, porque todavía no hay recursos REST que inyecten
 * los handlers (Etapa 5); sin esto ArC los descartaría y no podrían verificarse.
 */
@QuarkusTest
@TestProfile(InyeccionDependenciasTest.SinEliminarBeansSinUso.class)
class InyeccionDependenciasTest {

    public static class SinEliminarBeansSinUso implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.arc.remove-unused-beans", "false");
        }
    }

    private static final Map<Class<?>, Class<?>> CONTRATOS = Map.of(
            IUsuarioRepository.class, UsuarioRepositoryJpa.class,
            IAnimalRepository.class, AnimalRepositoryJpa.class,
            IEvaluacionClinicaRepository.class, EvaluacionClinicaRepositoryJpa.class,
            IVacunacionRepository.class, VacunacionRepositoryJpa.class,
            IDesparasitacionRepository.class, DesparasitacionRepositoryJpa.class,
            ITratamientoRepository.class, TratamientoRepositoryJpa.class,
            IEsterilizacionRepository.class, EsterilizacionRepositoryJpa.class,
            ICuarentenaRepository.class, CuarentenaRepositoryJpa.class,
            ISolicitudAdopcionRepository.class, SolicitudAdopcionRepositoryJpa.class,
            IAdopcionRepository.class, AdopcionRepositoryJpa.class);

    /** Los 24 casos de uso que escriben (al menos una llamada a save). */
    private static final Set<String> HANDLERS_DE_ESCRITURA = Set.of(
            "ApproveAdoptionRequestHandler", "EvaluateBasicCompatibilityHandler", "FormalizeAdoptionHandler",
            "RegisterAdoptionFollowUpHandler", "RegisterAdoptionReturnHandler", "RejectAdoptionRequestHandler",
            "SubmitAdoptionRequestHandler", "RegisterAnimalHandler", "RegisterBehaviorObservationHandler",
            "RegisterClinicalEvaluationHandler", "RegisterDewormingHandler", "RegisterSterilizationHandler",
            "RegisterTreatmentHandler", "RegisterVaccinationHandler", "UpdateAnimalHealthHandler",
            "AuthorizeQuarantineExitHandler", "MoveAnimalToGeneralPopulationHandler", "StartQuarantineHandler",
            "ActivateUserHandler", "ApprovePendingUserHandler", "CreateUserHandler", "RegisterApplicantHandler",
            "SuspendUserHandler", "UpdateUserHandler");

    private static BeanManager beanManager() {
        return Arc.container().beanManager();
    }

    private static List<Bean<?>> beansDeApplication() {
        return beanManager().getBeans(Object.class, Any.Literal.INSTANCE).stream()
                .filter(b -> b.getBeanClass().getName().startsWith("co.ufps.refupet.application."))
                .collect(Collectors.toList());
    }

    private static Method execute(Class<?> handler) {
        return Arrays.stream(handler.getDeclaredMethods())
                .filter(m -> m.getName().equals("execute"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void cadaContratoDelDominioSeResuelveConSuAdaptadorJpa() {
        CONTRATOS.forEach((contrato, adaptador) -> {
            assertEquals(1, beanManager().getBeans(contrato).size(), "beans para " + contrato.getSimpleName());
            Object instancia = ClientProxy.unwrap(Arc.container().instance(contrato).get());
            assertTrue(adaptador.isInstance(instancia),
                    contrato.getSimpleName() + " debe resolverse con " + adaptador.getSimpleName()
                            + " pero se obtuvo " + instancia.getClass().getName());
        });
    }

    @Test
    void losAdaptadoresEnMemoriaYElEnsamblajeManualNoSonBeans() {
        for (Class<?> clase : List.of(UsuarioRepositoryEnMemoria.class, AnimalRepositoryEnMemoria.class,
                EvaluacionClinicaRepositoryEnMemoria.class, VacunacionRepositoryEnMemoria.class,
                DesparasitacionRepositoryEnMemoria.class, TratamientoRepositoryEnMemoria.class,
                EsterilizacionRepositoryEnMemoria.class, CuarentenaRepositoryEnMemoria.class,
                SolicitudAdopcionRepositoryEnMemoria.class, AdopcionRepositoryEnMemoria.class,
                RefuPetAplicacion.class)) {
            assertTrue(beanManager().getBeans(clase).isEmpty(), clase.getSimpleName() + " no debe ser un bean");
        }
    }

    /** 41 handlers + 4 servicios, todos @ApplicationScoped y construidos por inyección por constructor. */
    @Test
    void todosLosHandlersYServiciosSeConstruyenPorInyeccion() {
        List<Bean<?>> beans = beansDeApplication();
        Set<String> nombres = beans.stream().map(b -> b.getBeanClass().getSimpleName()).collect(Collectors.toCollection(TreeSet::new));
        assertEquals(45, beans.size(), "beans de application: " + nombres);
        assertEquals(41, nombres.stream().filter(n -> n.endsWith("Handler")).count());
        assertTrue(nombres.containsAll(Set.of("AdopcionService", "AtencionVeterinariaService",
                "CuarentenaService", "DisponibilidadAdopcionService")));
        for (Bean<?> bean : beans) {
            assertEquals(ApplicationScoped.class, bean.getScope(), bean.getBeanClass().getSimpleName());
            Object proxy = Arc.container().instance(bean.getBeanClass()).get();
            assertInstanceOf(ClientProxy.class, proxy, "proxy de cliente para " + bean.getBeanClass().getSimpleName());
            // unwrap fuerza la creación de la instancia real: falla si alguna dependencia no se puede inyectar
            assertNotNull(ClientProxy.unwrap(proxy));
        }
    }

    /** Exactamente los 24 handlers de escritura son @Transactional y se instancian con interceptor. */
    @Test
    void soloLosHandlersDeEscrituraSonTransaccionales() {
        Set<String> transaccionales = new TreeSet<>();
        for (Bean<?> bean : beansDeApplication()) {
            Class<?> clase = bean.getBeanClass();
            if (!clase.getSimpleName().endsWith("Handler")) {
                continue;
            }
            boolean anotado = execute(clase).isAnnotationPresent(Transactional.class);
            boolean interceptado = ClientProxy.unwrap(Arc.container().instance(clase).get()) instanceof Subclass;
            assertEquals(anotado, interceptado, "anotación e interceptor coinciden en " + clase.getSimpleName());
            if (anotado) {
                transaccionales.add(clase.getSimpleName());
            }
        }
        assertEquals(new TreeSet<>(HANDLERS_DE_ESCRITURA), transaccionales);
    }

    /**
     * Las 19 consultas con nombre de los adaptadores existen y declaran exactamente los parámetros que
     * usan los repositorios. Hibernate ya las analizó contra el modelo al arrancar; aquí solo se crean
     * los objetos de consulta: NO se ejecutan ni se envía SQL a las tablas.
     */
    @Test
    void lasConsultasConNombreExistenConLosParametrosEsperados() {
        Map<String, Set<String>> consultas = new java.util.HashMap<>(Map.of(
                "UsuarioJpa.porEmail", Set.of("email"),
                "UsuarioJpa.contarPorEmail", Set.of("email"),
                "UsuarioJpa.porEstadoCuenta", Set.of("estado"),
                "AnimalJpa.porId", Set.of("id"),
                "AnimalJpa.contarPorId", Set.of("id"),
                "AnimalJpa.todos", Set.of(),
                "AnimalJpa.porUbicacion", Set.of("ubicacion"),
                "CuarentenaJpa.porAnimal", Set.of("animalId")));
        consultas.putAll(Map.of(
                "SolicitudAdopcionJpa.todas", Set.of(),
                "SolicitudAdopcionJpa.porEstado", Set.of("estado"),
                "SolicitudAdopcionJpa.porSolicitante", Set.of("solicitanteId"),
                "AdopcionJpa.porId", Set.of("id"),
                "AdopcionJpa.porSolicitud", Set.of("solicitudId"),
                "AdopcionJpa.porAnimal", Set.of("animalId")));
        for (String clinico : List.of("EvaluacionClinicaJpa", "VacunacionJpa", "DesparasitacionJpa",
                "TratamientoJpa", "EsterilizacionJpa")) {
            consultas.put(clinico + ".porAnimal", Set.of("animalId"));
        }
        assertEquals(19, consultas.size());

        jakarta.persistence.EntityManager em = Arc.container().instance(jakarta.persistence.EntityManager.class).get();
        consultas.forEach((nombre, parametros) -> {
            Set<String> declarados = em.createNamedQuery(nombre).getParameters().stream()
                    .map(jakarta.persistence.Parameter::getName)
                    .collect(Collectors.toSet());
            assertEquals(parametros, declarados, "parámetros de " + nombre);
        });
    }

    @Test
    void elRelojDeProduccionEsElDelSistema() {
        Clock reloj = Arc.container().instance(Clock.class).get();
        assertEquals(ZoneId.systemDefault(), reloj.getZone());
        assertEquals(1, beanManager().getBeans(Clock.class).size());
    }
}
