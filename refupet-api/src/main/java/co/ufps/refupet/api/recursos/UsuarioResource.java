package co.ufps.refupet.api.recursos;

import co.ufps.refupet.api.dto.ActualizarUsuarioRequest;
import co.ufps.refupet.api.dto.CrearUsuarioRequest;
import co.ufps.refupet.application.commands.usuario.ActivateUserCommand;
import co.ufps.refupet.application.commands.usuario.ApprovePendingUserCommand;
import co.ufps.refupet.application.commands.usuario.SuspendUserCommand;
import co.ufps.refupet.application.dto.UsuarioDTO;
import co.ufps.refupet.application.handlers.usuario.ActivateUserHandler;
import co.ufps.refupet.application.handlers.usuario.ApprovePendingUserHandler;
import co.ufps.refupet.application.handlers.usuario.CreateUserHandler;
import co.ufps.refupet.application.handlers.usuario.GetUserHandler;
import co.ufps.refupet.application.handlers.usuario.ListPendingUsersHandler;
import co.ufps.refupet.application.handlers.usuario.SuspendUserHandler;
import co.ufps.refupet.application.handlers.usuario.UpdateUserHandler;
import co.ufps.refupet.application.queries.usuario.GetUserQuery;
import co.ufps.refupet.application.queries.usuario.ListPendingUsersQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static co.ufps.refupet.api.Peticiones.CABECERA_ACTOR;
import static co.ufps.refupet.api.Peticiones.actor;
import static co.ufps.refupet.api.Peticiones.cuerpo;

/**
 * Gestión de cuentas de usuario por el administrador (reglas 26 a 32).
 * Solo traduce HTTP ↔ Commands/Queries; las reglas y permisos los aplican los casos de uso.
 */
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject CreateUserHandler createUser;
    @Inject UpdateUserHandler updateUser;
    @Inject GetUserHandler getUser;
    @Inject ListPendingUsersHandler listPendingUsers;
    @Inject ApprovePendingUserHandler approvePendingUser;
    @Inject SuspendUserHandler suspendUser;
    @Inject ActivateUserHandler activateUser;

    /** Crea una cuenta (nace PENDIENTE_APROBACION). 201 + Location. */
    @POST
    public Response crear(@HeaderParam(CABECERA_ACTOR) String actorId, CrearUsuarioRequest peticion) {
        UsuarioDTO creado = createUser.execute(cuerpo(peticion).toCommand(actor(actorId)));
        return Response.created(URI.create("/api/usuarios/" + creado.usuarioId())).entity(creado).build();
    }

    @GET
    @Path("/pendientes")
    public List<UsuarioDTO> pendientes(@HeaderParam(CABECERA_ACTOR) String actorId) {
        return listPendingUsers.execute(new ListPendingUsersQuery(actor(actorId)));
    }

    @GET
    @Path("/{usuarioId}")
    public UsuarioDTO obtener(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("usuarioId") String usuarioId) {
        return getUser.execute(new GetUserQuery(actor(actorId), usuarioId));
    }

    @PUT
    @Path("/{usuarioId}")
    public UsuarioDTO actualizar(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("usuarioId") String usuarioId,
                                 ActualizarUsuarioRequest peticion) {
        return updateUser.execute(cuerpo(peticion).toCommand(actor(actorId), usuarioId));
    }

    /** Aprueba una cuenta pendiente (regla 30). */
    @POST
    @Path("/{usuarioId}/aprobacion")
    public UsuarioDTO aprobar(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("usuarioId") String usuarioId) {
        return approvePendingUser.execute(new ApprovePendingUserCommand(actor(actorId), usuarioId));
    }

    /** Suspende una cuenta activa (regla 28). */
    @POST
    @Path("/{usuarioId}/suspension")
    public UsuarioDTO suspender(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("usuarioId") String usuarioId) {
        return suspendUser.execute(new SuspendUserCommand(actor(actorId), usuarioId));
    }

    /** Reactiva una cuenta suspendida (regla 29). */
    @POST
    @Path("/{usuarioId}/activacion")
    public UsuarioDTO activar(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("usuarioId") String usuarioId) {
        return activateUser.execute(new ActivateUserCommand(actor(actorId), usuarioId));
    }
}
