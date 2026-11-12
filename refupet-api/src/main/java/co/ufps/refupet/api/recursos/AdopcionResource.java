package co.ufps.refupet.api.recursos;

import co.ufps.refupet.api.dto.FormalizarAdopcionRequest;
import co.ufps.refupet.api.dto.RegistrarDevolucionRequest;
import co.ufps.refupet.api.dto.RegistrarSeguimientoRequest;
import co.ufps.refupet.application.dto.AdopcionDTO;
import co.ufps.refupet.application.dto.SeguimientoAdopcionDTO;
import co.ufps.refupet.application.handlers.adopcion.FormalizeAdoptionHandler;
import co.ufps.refupet.application.handlers.adopcion.GetAdoptionHandler;
import co.ufps.refupet.application.handlers.adopcion.RegisterAdoptionFollowUpHandler;
import co.ufps.refupet.application.handlers.adopcion.RegisterAdoptionReturnHandler;
import co.ufps.refupet.application.queries.adopcion.GetAdoptionQuery;
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

/** Adopciones formalizadas, sus seguimientos y la eventual devolución (reglas 23 a 25). */
@Path("/api/adopciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdopcionResource {

    @Inject FormalizeAdoptionHandler formalizeAdoption;
    @Inject GetAdoptionHandler getAdoption;
    @Inject RegisterAdoptionFollowUpHandler registerAdoptionFollowUp;
    @Inject RegisterAdoptionReturnHandler registerAdoptionReturn;

    /** Formaliza la adopción de una solicitud aprobada (regla 23). 201 + Location. */
    @POST
    public Response formalizar(@HeaderParam(CABECERA_ACTOR) String actorId, FormalizarAdopcionRequest peticion) {
        AdopcionDTO adopcion = formalizeAdoption.execute(cuerpo(peticion).toCommand(actor(actorId)));
        return Response.created(URI.create("/api/adopciones/" + adopcion.adopcionId())).entity(adopcion).build();
    }

    @GET
    @Path("/{adopcionId}")
    public AdopcionDTO obtener(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("adopcionId") String adopcionId) {
        return getAdoption.execute(new GetAdoptionQuery(actor(actorId), adopcionId));
    }

    /** Seguimiento posterior a la adopción (regla 24). 201. */
    @POST
    @Path("/{adopcionId}/seguimientos")
    public Response registrarSeguimiento(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("adopcionId") String adopcionId,
                                         RegistrarSeguimientoRequest peticion) {
        SeguimientoAdopcionDTO seguimiento = registerAdoptionFollowUp.execute(cuerpo(peticion).toCommand(actor(actorId), adopcionId));
        return Response.status(Response.Status.CREATED).entity(seguimiento).build();
    }

    /** Devolución del animal (regla 25): una sola vez; el animal vuelve a estar disponible. */
    @POST
    @Path("/{adopcionId}/devolucion")
    public AdopcionDTO registrarDevolucion(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("adopcionId") String adopcionId,
                                           RegistrarDevolucionRequest peticion) {
        return registerAdoptionReturn.execute(cuerpo(peticion).toCommand(actor(actorId), adopcionId));
    }
}
