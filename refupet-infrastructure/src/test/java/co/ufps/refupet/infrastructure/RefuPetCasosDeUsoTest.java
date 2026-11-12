package co.ufps.refupet.infrastructure;

import co.ufps.refupet.application.commands.adopcion.ApproveAdoptionRequestCommand;
import co.ufps.refupet.application.commands.adopcion.EvaluateBasicCompatibilityCommand;
import co.ufps.refupet.application.commands.adopcion.FormalizeAdoptionCommand;
import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionFollowUpCommand;
import co.ufps.refupet.application.commands.adopcion.RegisterAdoptionReturnCommand;
import co.ufps.refupet.application.commands.adopcion.RejectAdoptionRequestCommand;
import co.ufps.refupet.application.commands.adopcion.SubmitAdoptionRequestCommand;
import co.ufps.refupet.application.commands.animal.RegisterAnimalCommand;
import co.ufps.refupet.application.commands.animal.RegisterBehaviorObservationCommand;
import co.ufps.refupet.application.commands.clinica.RegisterClinicalEvaluationCommand;
import co.ufps.refupet.application.commands.clinica.RegisterDewormingCommand;
import co.ufps.refupet.application.commands.clinica.RegisterSterilizationCommand;
import co.ufps.refupet.application.commands.clinica.RegisterTreatmentCommand;
import co.ufps.refupet.application.commands.clinica.RegisterVaccinationCommand;
import co.ufps.refupet.application.commands.clinica.UpdateAnimalHealthCommand;
import co.ufps.refupet.application.commands.cuarentena.AuthorizeQuarantineExitCommand;
import co.ufps.refupet.application.commands.cuarentena.MoveAnimalToGeneralPopulationCommand;
import co.ufps.refupet.application.commands.cuarentena.StartQuarantineCommand;
import co.ufps.refupet.application.commands.usuario.ActivateUserCommand;
import co.ufps.refupet.application.commands.usuario.ApprovePendingUserCommand;
import co.ufps.refupet.application.commands.usuario.CreateUserCommand;
import co.ufps.refupet.application.commands.usuario.RegisterApplicantCommand;
import co.ufps.refupet.application.commands.usuario.SuspendUserCommand;
import co.ufps.refupet.application.commands.usuario.UpdateUserCommand;
import co.ufps.refupet.application.dto.AdopcionDTO;
import co.ufps.refupet.application.dto.AnimalDTO;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.dto.HistorialClinicoDTO;
import co.ufps.refupet.application.dto.ObservacionComportamientoDTO;
import co.ufps.refupet.application.dto.RegistroIngresoDTO;
import co.ufps.refupet.application.dto.SolicitanteDTO;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.dto.SolicitudAdopcionDetalleDTO;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.queries.adopcion.GetAdoptionQuery;
import co.ufps.refupet.application.queries.adopcion.GetAdoptionRequestQuery;
import co.ufps.refupet.application.queries.adopcion.GetAvailableAnimalsQuery;
import co.ufps.refupet.application.queries.adopcion.ListAdoptionRequestsQuery;
import co.ufps.refupet.application.queries.animal.GetAnimalIngressTraceQuery;
import co.ufps.refupet.application.queries.animal.GetAnimalObservationsQuery;
import co.ufps.refupet.application.queries.animal.GetAnimalQuery;
import co.ufps.refupet.application.queries.animal.ListAnimalsQuery;
import co.ufps.refupet.application.queries.clinica.GetClinicalHistoryQuery;
import co.ufps.refupet.application.queries.cuarentena.CheckMinimumQuarantinePeriodQuery;
import co.ufps.refupet.application.queries.cuarentena.GetAnimalQuarantineStatusQuery;
import co.ufps.refupet.application.queries.cuarentena.GetAnimalsInQuarantineQuery;
import co.ufps.refupet.application.queries.cuarentena.GetQuarantineQuery;
import co.ufps.refupet.application.queries.usuario.GetApplicantQuery;
import co.ufps.refupet.application.queries.usuario.GetUserQuery;
import co.ufps.refupet.application.queries.usuario.ListPendingUsersQuery;
import co.ufps.refupet.domain.exceptions.AccountApprovalNotAllowedException;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.exceptions.EmailAlreadyRegisteredException;
import co.ufps.refupet.domain.exceptions.InvalidAdoptionRequestStateException;
import co.ufps.refupet.domain.exceptions.InvalidAnimalStateException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.InvalidQuarantineStateException;
import co.ufps.refupet.domain.exceptions.InvalidUserStateException;
import co.ufps.refupet.domain.exceptions.QuarantineExitNotAuthorizedException;
import co.ufps.refupet.domain.exceptions.QuarantinePeriodNotCompletedException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.infrastructure.config.RefuPetAplicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de los casos de uso de RefuPet de extremo a extremo:
 * handlers de application + entidades de domain + repositorios en memoria de infrastructure.
 */
class RefuPetCasosDeUsoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 10);

    private RelojControlado reloj;
    private RefuPetAplicacion app;
    private String adminId;
    private String vetId;
    private String voluntarioId;
    private String solicitanteId;

    @BeforeEach
    void setUp() {
        reloj = new RelojControlado(HOY);
        app = new RefuPetAplicacion(reloj);
        adminId = app.inicializarAdministrador("Admin Refugio", "admin@refupet.co").usuarioId();
        vetId = crearUsuarioActivo("Dra. Gómez", "vet@refupet.co", "VETERINARIO");
        voluntarioId = crearUsuarioActivo("Carlos Voluntario", "voluntario@refupet.co", "VOLUNTARIO");
        solicitanteId = app.registerApplicant.execute(
                new RegisterApplicantCommand("Laura Pérez", "laura@correo.co", "3001234567", "Calle 1 # 2-3")).usuarioId();
        app.approvePendingUser.execute(new ApprovePendingUserCommand(adminId, solicitanteId));
    }

    // ── Utilidades de los escenarios ──

    private String crearUsuarioActivo(String nombre, String email, String rol) {
        String id = app.createUser.execute(new CreateUserCommand(adminId, nombre, email, rol, null, null)).usuarioId();
        app.approvePendingUser.execute(new ApprovePendingUserCommand(adminId, id));
        return id;
    }

    private String registrarAnimal(String nombre) {
        return app.registerAnimal.execute(new RegisterAnimalCommand(voluntarioId, nombre, "perro", "Criollo",
                "Manchas negras", null, "Rescatado en el parque", "Herida en pata")).animalId();
    }

    private String iniciarCuarentena(String animalId, int dias) {
        return app.startQuarantine.execute(new StartQuarantineCommand(vetId, animalId, null, dias)).cuarentenaId();
    }

    /** Lleva un animal recién registrado hasta población general (disponible para adopción). */
    private String animalDisponible(String nombre) {
        String animalId = registrarAnimal(nombre);
        String cuarentenaId = iniciarCuarentena(animalId, 7);
        reloj.avanzarDias(7);
        app.authorizeQuarantineExit.execute(new AuthorizeQuarantineExitCommand(vetId, cuarentenaId));
        app.moveAnimalToGeneralPopulation.execute(new MoveAnimalToGeneralPopulationCommand(vetId, cuarentenaId));
        return animalId;
    }

    private String solicitudAprobada(String animalId) {
        String solicitudId = app.submitAdoptionRequest.execute(
                new SubmitAdoptionRequestCommand(solicitanteId, animalId, "Vivo en casa con patio")).solicitudId();
        app.approveAdoptionRequest.execute(new ApproveAdoptionRequestCommand(adminId, solicitudId));
        return solicitudId;
    }

    // ═════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Administración de usuarios")
    class Usuarios {

        @Test
        @DisplayName("Creación: la cuenta queda pendiente y aparece en la lista de pendientes")
        void creacion() {
            UsuarioDTO creado = app.createUser.execute(
                    new CreateUserCommand(adminId, "Pedro", "pedro@refupet.co", "voluntario", null, null));
            assertEquals("VOLUNTARIO", creado.rol());
            assertEquals("PENDIENTE_APROBACION", creado.estadoCuenta());
            assertTrue(app.listPendingUsers.execute(new ListPendingUsersQuery(adminId)).stream()
                    .anyMatch(u -> u.usuarioId().equals(creado.usuarioId())));
        }

        @Test
        @DisplayName("Aprobación: solo una cuenta pendiente puede aprobarse")
        void aprobacion() {
            String id = app.createUser.execute(
                    new CreateUserCommand(adminId, "Pedro", "pedro@refupet.co", "VETERINARIO", null, null)).usuarioId();
            assertEquals("ACTIVA", app.approvePendingUser.execute(new ApprovePendingUserCommand(adminId, id)).estadoCuenta());
            assertThrows(AccountApprovalNotAllowedException.class,
                    () -> app.approvePendingUser.execute(new ApprovePendingUserCommand(adminId, id)));
        }

        @Test
        @DisplayName("Modificación de datos, incluido el contacto del solicitante")
        void modificacion() {
            app.updateUser.execute(new UpdateUserCommand(adminId, solicitanteId, "Laura P. Gómez",
                    "laura.gomez@correo.co", "3109998888", null));
            SolicitanteDTO solicitante = app.getApplicant.execute(new GetApplicantQuery(adminId, solicitanteId));
            assertEquals("Laura P. Gómez", solicitante.nombre());
            assertEquals("laura.gomez@correo.co", solicitante.email());
            assertEquals("3109998888", solicitante.telefono());
        }

        @Test
        @DisplayName("El email debe ser único al crear y al modificar")
        void emailUnico() {
            assertThrows(EmailAlreadyRegisteredException.class, () -> app.createUser.execute(
                    new CreateUserCommand(adminId, "Otro", "VET@refupet.co", "VOLUNTARIO", null, null)));
            assertThrows(EmailAlreadyRegisteredException.class, () -> app.updateUser.execute(
                    new UpdateUserCommand(adminId, voluntarioId, "Carlos", "vet@refupet.co", null, null)));
        }

        @Test
        @DisplayName("Suspensión y activación; una cuenta suspendida no puede operar")
        void suspensionYActivacion() {
            assertEquals("SUSPENDIDA",
                    app.suspendUser.execute(new SuspendUserCommand(adminId, voluntarioId)).estadoCuenta());
            assertThrows(InvalidUserStateException.class, () -> registrarAnimal("Toby"));

            assertEquals("ACTIVA",
                    app.activateUser.execute(new ActivateUserCommand(adminId, voluntarioId)).estadoCuenta());
            assertNotNull(registrarAnimal("Toby"));
        }

        @Test
        @DisplayName("Solo el administrador gestiona usuarios")
        void soloAdministrador() {
            assertThrows(UnauthorizedRoleOperationException.class, () -> app.createUser.execute(
                    new CreateUserCommand(voluntarioId, "X", "x@refupet.co", "VOLUNTARIO", null, null)));
            assertThrows(UnauthorizedRoleOperationException.class,
                    () -> app.getUser.execute(new GetUserQuery(vetId, adminId)));
        }

        @Test
        @DisplayName("Una cuenta pendiente no puede operar hasta ser aprobada")
        void pendienteNoOpera() {
            String nuevoId = app.registerApplicant.execute(
                    new RegisterApplicantCommand("Mario", "mario@correo.co", null, null)).usuarioId();
            assertThrows(InvalidUserStateException.class,
                    () -> app.getAvailableAnimals.execute(new GetAvailableAnimalsQuery(nuevoId)));
        }
    }

    // ═════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Ingreso y registro de animales")
    class Animales {

        @Test
        @DisplayName("Registro y consulta de animales")
        void registroYConsulta() {
            String firulais = registrarAnimal("Firulais");
            registrarAnimal("Luna");
            List<AnimalDTO> animales = app.listAnimals.execute(new ListAnimalsQuery(vetId));
            assertEquals(2, animales.size());
            assertTrue(animales.stream().anyMatch(a -> a.animalId().equals(firulais)));
        }

        @Test
        @DisplayName("Visualización de la información general")
        void informacionGeneral() {
            String id = registrarAnimal("Firulais");
            AnimalDTO animal = app.getAnimal.execute(new GetAnimalQuery(adminId, id));
            assertEquals("Firulais", animal.nombre());
            assertEquals("PERRO", animal.especie());
            assertEquals("Criollo", animal.raza());
            assertEquals(HOY, animal.fechaIngreso());
            assertNull(animal.ubicacion());
            assertFalse(animal.esterilizado());
        }

        @Test
        @DisplayName("Registro de observaciones de comportamiento")
        void observaciones() {
            String id = registrarAnimal("Firulais");
            app.registerBehaviorObservation.execute(
                    new RegisterBehaviorObservationCommand(voluntarioId, id, "Juega bien con otros perros"));
            app.registerBehaviorObservation.execute(
                    new RegisterBehaviorObservationCommand(voluntarioId, id, "Se asusta con ruidos fuertes"));
            List<ObservacionComportamientoDTO> obs =
                    app.getAnimalObservations.execute(new GetAnimalObservationsQuery(vetId, id));
            assertEquals(2, obs.size());
            assertEquals(voluntarioId, obs.get(0).registradoPorId());
        }

        @Test
        @DisplayName("Trazabilidad básica del ingreso")
        void trazabilidadIngreso() {
            String id = registrarAnimal("Firulais");
            RegistroIngresoDTO ingreso = app.getAnimalIngressTrace.execute(new GetAnimalIngressTraceQuery(adminId, id));
            assertEquals(HOY, ingreso.fechaIngreso());
            assertEquals(voluntarioId, ingreso.registradoPorId());
            assertEquals("Rescatado en el parque", ingreso.procedencia());
            assertEquals("Herida en pata", ingreso.condicionInicial());
        }

        @Test
        @DisplayName("Solo el voluntario registra ingresos y observaciones; no se aceptan fechas futuras")
        void restricciones() {
            assertThrows(UnauthorizedRoleOperationException.class, () -> app.registerAnimal.execute(
                    new RegisterAnimalCommand(vetId, "X", "GATO", null, null, null, null, null)));
            assertThrows(InvalidDomainDataException.class, () -> app.registerAnimal.execute(
                    new RegisterAnimalCommand(voluntarioId, "X", "GATO", null, null, HOY.plusDays(1), null, null)));
            assertThrows(UnauthorizedRoleOperationException.class,
                    () -> app.listAnimals.execute(new ListAnimalsQuery(solicitanteId)));
            assertThrows(AnimalNotFoundException.class,
                    () -> app.getAnimal.execute(new GetAnimalQuery(adminId, "no-existe")));
        }
    }

    // ═════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Atención veterinaria")
    class AtencionVeterinaria {

        @Test
        @DisplayName("Registro de evaluaciones, vacunaciones, desparasitaciones, tratamientos y esterilización en el historial clínico")
        void historialClinico() {
            String id = registrarAnimal("Firulais");
            app.registerClinicalEvaluation.execute(new RegisterClinicalEvaluationCommand(
                    vetId, id, HOY.minusDays(3), "Herida superficial en pata", "Laceración leve", null));
            app.registerVaccination.execute(new RegisterVaccinationCommand(vetId, id, HOY.minusDays(2), "Rabia", null));
            app.registerDeworming.execute(new RegisterDewormingCommand(vetId, id, HOY.minusDays(2), "Albendazol", null));
            app.registerTreatment.execute(new RegisterTreatmentCommand(
                    vetId, id, HOY.minusDays(1), "Antibiótico", "Cada 12 horas por 5 días", null));
            app.registerSterilization.execute(new RegisterSterilizationCommand(vetId, id, null, "Sin complicaciones"));

            HistorialClinicoDTO historial = app.getClinicalHistory.execute(new GetClinicalHistoryQuery(adminId, id));
            assertEquals(5, historial.registros().size());
            assertEquals("EVALUACION_CLINICA", historial.registros().get(0).tipo(), "Ordenado por fecha");
            assertEquals("ESTERILIZACION", historial.registros().get(4).tipo());
            assertTrue(historial.esterilizado());
            assertTrue(historial.registros().stream().allMatch(r -> r.veterinarioId().equals(vetId)));
        }

        @Test
        @DisplayName("Una esterilización no puede registrarse dos veces")
        void esterilizacionUnica() {
            String id = registrarAnimal("Luna");
            app.registerSterilization.execute(new RegisterSterilizationCommand(vetId, id, null, null));
            assertThrows(InvalidAnimalStateException.class,
                    () -> app.registerSterilization.execute(new RegisterSterilizationCommand(vetId, id, null, null)));
        }

        @Test
        @DisplayName("Actualización del estado de salud")
        void estadoSalud() {
            String id = registrarAnimal("Firulais");
            AnimalDTO animal = app.updateAnimalHealth.execute(new UpdateAnimalHealthCommand(vetId, id, "Recuperado"));
            assertEquals("Recuperado", animal.estadoSalud());
            assertEquals("Recuperado",
                    app.getClinicalHistory.execute(new GetClinicalHistoryQuery(vetId, id)).estadoSalud());
        }

        @Test
        @DisplayName("Solo el veterinario gestiona la información clínica")
        void soloVeterinario() {
            String id = registrarAnimal("Firulais");
            assertThrows(UnauthorizedRoleOperationException.class, () -> app.registerVaccination.execute(
                    new RegisterVaccinationCommand(voluntarioId, id, null, "Rabia", null)));
            assertThrows(UnauthorizedRoleOperationException.class, () -> app.updateAnimalHealth.execute(
                    new UpdateAnimalHealthCommand(adminId, id, "Bien")));
            assertThrows(AnimalNotFoundException.class, () -> app.registerTreatment.execute(
                    new RegisterTreatmentCommand(vetId, "no-existe", null, "x", null, null)));
        }
    }

    // ═════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Cuarentena")
    class Cuarentenas {

        @Test
        @DisplayName("Inicio y consulta de cuarentena")
        void inicioYConsulta() {
            String id = registrarAnimal("Firulais");
            registrarAnimal("Luna");
            String cuarentenaId = iniciarCuarentena(id, 10);

            List<CuarentenaDTO> enCuarentena =
                    app.getAnimalsInQuarantine.execute(new GetAnimalsInQuarantineQuery(adminId));
            assertEquals(1, enCuarentena.size());
            assertEquals("Firulais", enCuarentena.get(0).nombreAnimal());

            CuarentenaDTO cuarentena = app.getQuarantine.execute(new GetQuarantineQuery(vetId, cuarentenaId));
            assertEquals("EN_CURSO", cuarentena.estado());
            assertEquals("CUARENTENA", app.getAnimal.execute(new GetAnimalQuery(vetId, id)).ubicacion());
            assertEquals(cuarentenaId, app.getAnimalQuarantineStatus.execute(
                    new GetAnimalQuarantineStatusQuery(vetId, id)).orElseThrow().cuarentenaId());
        }

        @Test
        @DisplayName("Un animal no puede tener dos cuarentenas activas")
        void unaSolaActiva() {
            String id = registrarAnimal("Firulais");
            iniciarCuarentena(id, 10);
            assertThrows(InvalidQuarantineStateException.class, () -> iniciarCuarentena(id, 5));
        }

        @Test
        @DisplayName("Control del periodo mínimo de cuarentena")
        void periodoMinimo() {
            String cuarentenaId = iniciarCuarentena(registrarAnimal("Firulais"), 10);

            CuarentenaDTO antes = app.checkMinimumQuarantinePeriod.execute(
                    new CheckMinimumQuarantinePeriodQuery(vetId, cuarentenaId));
            assertFalse(antes.periodoMinimoCumplido());
            assertEquals(10, antes.diasRestantes());
            assertEquals(HOY.plusDays(10), antes.fechaMinimaSalida());
            assertThrows(QuarantinePeriodNotCompletedException.class, () ->
                    app.authorizeQuarantineExit.execute(new AuthorizeQuarantineExitCommand(vetId, cuarentenaId)));

            reloj.avanzarDias(10);
            assertTrue(app.checkMinimumQuarantinePeriod.execute(
                    new CheckMinimumQuarantinePeriodQuery(vetId, cuarentenaId)).periodoMinimoCumplido());
        }

        @Test
        @DisplayName("La salida de cuarentena la autoriza el veterinario y luego pasa a población general")
        void salidaYPoblacionGeneral() {
            String id = registrarAnimal("Firulais");
            String cuarentenaId = iniciarCuarentena(id, 5);
            reloj.avanzarDias(5);

            assertThrows(UnauthorizedRoleOperationException.class, () ->
                    app.authorizeQuarantineExit.execute(new AuthorizeQuarantineExitCommand(adminId, cuarentenaId)));
            assertThrows(QuarantineExitNotAuthorizedException.class, () -> app.moveAnimalToGeneralPopulation
                    .execute(new MoveAnimalToGeneralPopulationCommand(vetId, cuarentenaId)));

            CuarentenaDTO autorizada = app.authorizeQuarantineExit.execute(
                    new AuthorizeQuarantineExitCommand(vetId, cuarentenaId));
            assertEquals("SALIDA_AUTORIZADA", autorizada.estado());
            assertEquals(vetId, autorizada.salidaAutorizadaPorId());

            AnimalDTO animal = app.moveAnimalToGeneralPopulation.execute(
                    new MoveAnimalToGeneralPopulationCommand(vetId, cuarentenaId));
            assertEquals("POBLACION_GENERAL", animal.ubicacion());
            assertEquals("FINALIZADA",
                    app.getQuarantine.execute(new GetQuarantineQuery(vetId, cuarentenaId)).estado());
            assertTrue(app.getAnimalsInQuarantine.execute(new GetAnimalsInQuarantineQuery(vetId)).isEmpty());
        }
    }

    // ═════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Adopción responsable")
    class Adopciones {

        @Test
        @DisplayName("Consulta de animales disponibles: solo los que están en población general sin adopción vigente")
        void disponibles() {
            String disponible = animalDisponible("Firulais");
            String enCuarentena = registrarAnimal("Luna");
            iniciarCuarentena(enCuarentena, 30);
            registrarAnimal("Max");

            List<AnimalDTO> disponibles = app.getAvailableAnimals.execute(new GetAvailableAnimalsQuery(solicitanteId));
            assertEquals(List.of(disponible), disponibles.stream().map(AnimalDTO::animalId).toList());
        }

        @Test
        @DisplayName("Registro de solicitante y presentación de solicitud")
        void presentarSolicitud() {
            String animalId = animalDisponible("Firulais");
            SolicitudAdopcionDTO solicitud = app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(solicitanteId, animalId, "Tengo experiencia con perros"));
            assertEquals("PENDIENTE", solicitud.estado());

            SolicitudAdopcionDetalleDTO detalle = app.getAdoptionRequest.execute(
                    new GetAdoptionRequestQuery(adminId, solicitud.solicitudId()));
            assertEquals("Laura Pérez", detalle.solicitante().nombre());
            assertEquals("Firulais", detalle.animal().nombre());
            assertNotNull(app.getAdoptionRequest.execute(
                    new GetAdoptionRequestQuery(solicitanteId, solicitud.solicitudId())), "El solicitante ve su solicitud");

            assertThrows(InvalidAdoptionRequestStateException.class, () -> app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(solicitanteId, animalId, "Otra vez")));
        }

        @Test
        @DisplayName("No se solicita un animal no disponible ni lo hace un perfil distinto del solicitante")
        void restriccionesSolicitud() {
            String enCuarentena = registrarAnimal("Luna");
            iniciarCuarentena(enCuarentena, 30);
            assertThrows(InvalidAnimalStateException.class, () -> app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(solicitanteId, enCuarentena, null)));

            String disponible = animalDisponible("Firulais");
            assertThrows(UnauthorizedRoleOperationException.class, () -> app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(voluntarioId, disponible, null)));
        }

        @Test
        @DisplayName("Evaluación de compatibilidad, aprobación y rechazo por el administrador")
        void aprobacionYRechazo() {
            String animalId = animalDisponible("Firulais");
            String solicitudId = app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(solicitanteId, animalId, null)).solicitudId();

            SolicitudAdopcionDTO evaluada = app.evaluateBasicCompatibility.execute(
                    new EvaluateBasicCompatibilityCommand(adminId, solicitudId, true, "Espacio y tiempo adecuados"));
            assertTrue(evaluada.compatible());

            assertThrows(UnauthorizedRoleOperationException.class, () -> app.approveAdoptionRequest.execute(
                    new ApproveAdoptionRequestCommand(solicitanteId, solicitudId)));
            assertEquals("APROBADA", app.approveAdoptionRequest.execute(
                    new ApproveAdoptionRequestCommand(adminId, solicitudId)).estado());

            String otroSolicitante = app.registerApplicant.execute(
                    new RegisterApplicantCommand("Mario", "mario@correo.co", null, null)).usuarioId();
            app.approvePendingUser.execute(new ApprovePendingUserCommand(adminId, otroSolicitante));
            String otraSolicitud = app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(otroSolicitante, animalId, null)).solicitudId();
            SolicitudAdopcionDTO rechazada = app.rejectAdoptionRequest.execute(
                    new RejectAdoptionRequestCommand(adminId, otraSolicitud, "Ya existe una solicitud aprobada"));
            assertEquals("RECHAZADA", rechazada.estado());
            assertEquals(1, app.listAdoptionRequests.execute(new ListAdoptionRequestsQuery(adminId, "rechazada")).size());
        }

        @Test
        @DisplayName("Formalización y seguimiento de la adopción")
        void formalizacionYSeguimiento() {
            String animalId = animalDisponible("Firulais");
            String solicitudId = solicitudAprobada(animalId);

            AdopcionDTO adopcion = app.formalizeAdoption.execute(new FormalizeAdoptionCommand(adminId, solicitudId, null));
            assertEquals("FORMALIZADA", adopcion.estado());
            assertTrue(app.getAvailableAnimals.execute(new GetAvailableAnimalsQuery(solicitanteId)).isEmpty(),
                    "Un animal adoptado deja de estar disponible");
            assertThrows(InvalidAdoptionRequestStateException.class, () ->
                    app.formalizeAdoption.execute(new FormalizeAdoptionCommand(adminId, solicitudId, null)));

            reloj.avanzarDias(15);
            app.registerAdoptionFollowUp.execute(new RegisterAdoptionFollowUpCommand(
                    adminId, adopcion.adopcionId(), null, "El perro se adaptó bien al hogar"));
            AdopcionDTO conSeguimiento = app.getAdoption.execute(new GetAdoptionQuery(adminId, adopcion.adopcionId()));
            assertEquals("CON_SEGUIMIENTO", conSeguimiento.estado());
            assertEquals(1, conSeguimiento.seguimientos().size());
        }

        @Test
        @DisplayName("No se formaliza una solicitud que no fue aprobada")
        void formalizarSinAprobar() {
            String animalId = animalDisponible("Firulais");
            String solicitudId = app.submitAdoptionRequest.execute(
                    new SubmitAdoptionRequestCommand(solicitanteId, animalId, null)).solicitudId();
            assertThrows(InvalidAdoptionRequestStateException.class, () ->
                    app.formalizeAdoption.execute(new FormalizeAdoptionCommand(adminId, solicitudId, null)));
        }

        @Test
        @DisplayName("Registro de devoluciones: el animal vuelve a estar disponible")
        void devolucion() {
            String animalId = animalDisponible("Firulais");
            String adopcionId = app.formalizeAdoption.execute(
                    new FormalizeAdoptionCommand(adminId, solicitudAprobada(animalId), null)).adopcionId();

            reloj.avanzarDias(20);
            AdopcionDTO devuelta = app.registerAdoptionReturn.execute(
                    new RegisterAdoptionReturnCommand(adminId, adopcionId, null, "Cambio de residencia"));
            assertEquals("CON_DEVOLUCION", devuelta.estado());
            assertEquals("Cambio de residencia", devuelta.motivoDevolucion());
            assertEquals(List.of(animalId), app.getAvailableAnimals.execute(
                    new GetAvailableAnimalsQuery(solicitanteId)).stream().map(AnimalDTO::animalId).toList());
        }
    }
}
