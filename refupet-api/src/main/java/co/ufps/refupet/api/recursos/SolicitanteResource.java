package co.ufps.refupet.api.recursos;

import co.ufps.refupet.api.dto.RegistrarSolicitanteRequest;
import co.ufps.refupet.application.dto.SolicitanteDTO;
import co.ufps.refupet.application.handlers.usuario.GetApplicantHandler;
import co.ufps.refupet.application.handlers.usuario.RegisterApplicantHandler;
import co.ufps.refupet.application.queries.usuario.GetApplicantQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import static co.ufps.refupet.api.Peticiones.CABECERA_ACTOR;
import static co.ufps.refupet.api.Peticiones.actor;
import static co.ufps.refupet.api.Peticiones.cuerpo;

/** Solicitantes de adopción: autorregistro (regla 26) y consulta de su información (regla 20). */
@Path("/api/solicitantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitanteResource {

    @Inject RegisterApplicantHandler registerApplicant;
    @Inject GetApplicantHandler getApplicant;

    /**
     * Registro del propio solicitante: es la única operación sin actor (el caso de uso no lo requiere).
     * La cuenta nace PENDIENTE_APROBACION hasta que un administrador la apruebe. 201 + Location.
     */
    @POST
    public Response registrar(RegistrarSolicitanteRequest peticion) {
        SolicitanteDTO registrado = registerApplicant.execute(cuerpo(peticion).toCommand());
        return Response.created(URI.create("/api/solicitantes/" + registrado.usuarioId())).entity(registrado).build();
    }

    @GET
    @Path("/{solicitanteId}")
    public SolicitanteDTO obtener(@HeaderParam(CABECERA_ACTOR) String actorId,
                                  @PathParam("solicitanteId") String solicitanteId) {
        return getApplicant.execute(new GetApplicantQuery(actor(actorId), solicitanteId));
    }
}
