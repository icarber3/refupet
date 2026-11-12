package co.ufps.refupet.api.recursos;

import co.ufps.refupet.api.dto.EvaluarCompatibilidadRequest;
import co.ufps.refupet.api.dto.PresentarSolicitudRequest;
import co.ufps.refupet.api.dto.RechazarSolicitudRequest;
import co.ufps.refupet.application.commands.adopcion.ApproveAdoptionRequestCommand;
import co.ufps.refupet.application.dto.SolicitudAdopcionDTO;
import co.ufps.refupet.application.dto.SolicitudAdopcionDetalleDTO;
import co.ufps.refupet.application.handlers.adopcion.ApproveAdoptionRequestHandler;
import co.ufps.refupet.application.handlers.adopcion.EvaluateBasicCompatibilityHandler;
import co.ufps.refupet.application.handlers.adopcion.GetAdoptionRequestHandler;
import co.ufps.refupet.application.handlers.adopcion.ListAdoptionRequestsHandler;
import co.ufps.refupet.application.handlers.adopcion.RejectAdoptionRequestHandler;
import co.ufps.refupet.application.handlers.adopcion.SubmitAdoptionRequestHandler;
import co.ufps.refupet.application.queries.adopcion.GetAdoptionRequestQuery;
import co.ufps.refupet.application.queries.adopcion.ListAdoptionRequestsQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static co.ufps.refupet.api.Peticiones.CABECERA_ACTOR;
import static co.ufps.refupet.api.Peticiones.actor;
import static co.ufps.refupet.api.Peticiones.cuerpo;

/** Solicitudes de adopción: presentación, evaluación y decisión (reglas 19 a 22, 37 y 38). */
@Path("/api/solicitudes-adopcion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudAdopcionResource {

    @Inject SubmitAdoptionRequestHandler submitAdoptionRequest;
    @Inject ListAdoptionRequestsHandler listAdoptionRequests;
    @Inject GetAdoptionRequestHandler getAdoptionRequest;
    @Inject EvaluateBasicCompatibilityHandler evaluateBasicCompatibility;
    @Inject ApproveAdoptionRequestHandler approveAdoptionRequest;
    @Inject RejectAdoptionRequestHandler rejectAdoptionRequest;

    /** El solicitante presenta una solicitud para un animal disponible. 201 + Location. */
    @POST
    public Response presentar(@HeaderParam(CABECERA_ACTOR) String actorId, PresentarSolicitudRequest peticion) {
        SolicitudAdopcionDTO solicitud = submitAdoptionRequest.execute(cuerpo(peticion).toCommand(actor(actorId)));
        return Response.created(URI.create("/api/solicitudes-adopcion/" + solicitud.solicitudId())).entity(solicitud).build();
    }

    /** Lista todas las solicitudes o, con {@code ?estado=PENDIENTE|APROBADA|RECHAZADA}, las de ese estado. */
    @GET
    public List<SolicitudAdopcionDTO> listar(@HeaderParam(CABECERA_ACTOR) String actorId, @QueryParam("estado") String estado) {
        return listAdoptionRequests.execute(new ListAdoptionRequestsQuery(actor(actorId), estado));
    }

    /** Solicitud con la información del solicitante y del animal (regla 20). */
    @GET
    @Path("/{solicitudId}")
    public SolicitudAdopcionDetalleDTO obtener(@HeaderParam(CABECERA_ACTOR) String actorId,
                                               @PathParam("solicitudId") String solicitudId) {
        return getAdoptionRequest.execute(new GetAdoptionRequestQuery(actor(actorId), solicitudId));
    }

    /** Evaluación básica de compatibilidad (regla 21). */
    @POST
    @Path("/{solicitudId}/evaluacion-compatibilidad")
    public SolicitudAdopcionDTO evaluarCompatibilidad(@HeaderParam(CABECERA_ACTOR) String actorId,
                                                      @PathParam("solicitudId") String solicitudId,
                                                      EvaluarCompatibilidadRequest peticion) {
        return evaluateBasicCompatibility.execute(cuerpo(peticion).toCommand(actor(actorId), solicitudId));
    }

    /** Aprobación por el administrador (regla 22). */
    @POST
    @Path("/{solicitudId}/aprobacion")
    public SolicitudAdopcionDTO aprobar(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("solicitudId") String solicitudId) {
        return approveAdoptionRequest.execute(new ApproveAdoptionRequestCommand(actor(actorId), solicitudId));
    }

    /** Rechazo por el administrador (regla 22); el motivo es opcional. */
    @POST
    @Path("/{solicitudId}/rechazo")
    public SolicitudAdopcionDTO rechazar(@HeaderParam(CABECERA_ACTOR) String actorId, @PathParam("solicitudId") String solicitudId,
                                         RechazarSolicitudRequest peticion) {
        return rejectAdoptionRequest.execute(RechazarSolicitudRequest.toCommand(peticion, actor(actorId), solicitudId));
    }
}
