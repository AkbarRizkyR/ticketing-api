package io.github.akbarrizky.resource.tiket;

import io.github.akbarrizky.dto.comment.CommentResponseDto;
import io.github.akbarrizky.dto.comment.CreateCommentDto;
import io.github.akbarrizky.dto.tiket.CreateTicketDto;
import io.github.akbarrizky.dto.tiket.UpdateTicketDto;
import io.github.akbarrizky.service.tiket.TicketService;
import io.github.akbarrizky.util.ApiResponse;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ticket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class TiketResource {

    @Inject
    TicketService ticketService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") java.util.UUID id) {
        return Response.ok(
                ApiResponse.success(ticketService.findById(id))).build();
    }

    @POST
    @Path("/create")
    public Response create(@Valid CreateTicketDto dto) {

        String userId = getUserIdFromJwt();

        return Response.ok(
                ApiResponse.success(
                        ticketService.create(dto, userId)))
                .build();
    }

    @POST
    @Path("/update")
    public Response update(@Valid UpdateTicketDto dto) {

        String userId = getUserIdFromJwt();

        return Response.ok(
                ApiResponse.success(
                        ticketService.update(dto, userId)))
                .build();
    }

    @POST
    @Path("/list")
    public Response list() {
        return Response.ok(
                ApiResponse.success(
                        ticketService.getAll()))
                .build();
    }

    @POST
    @Path("/my-tickets")
    public Response myTickets() {

        String userId = getUserIdFromJwt();

        return Response.ok(
                ApiResponse.success(
                        ticketService.getByReporter(userId)))
                .build();
    }

    @POST
    @Path("/comment")
    public CommentResponseDto create(CreateCommentDto dto) {

        Object claim = jwt.getClaim("userId");
        String userId = claim != null ? claim.toString() : jwt.getSubject();

        return ticketService.createComment(dto, userId);
    }

    @GET
    @Path("/ticket/{ticketId}")
    public Response getByTicket(@PathParam("ticketId") java.util.UUID ticketId) {
        return Response.ok(
                ApiResponse.success(
                        ticketService.getByTicket(ticketId)))
                .build();
    }

    // ==========================
    // HELPER METHOD
    // ==========================

    private String getUserIdFromJwt() {
        Object claim = jwt.getClaim("userId");

        if (claim == null) {
            // Check subject
            if (jwt.getSubject() != null)
                return jwt.getSubject();
            throw new WebApplicationException("UserId tidak ditemukan di token", 401);
        }

        return claim.toString();
    }
}
