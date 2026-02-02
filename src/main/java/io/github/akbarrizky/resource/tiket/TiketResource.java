package io.github.akbarrizky.resource.tiket;

import io.github.akbarrizky.dto.tiket.CreateTicketDto;
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
    public Response get(@PathParam("id") Long id) {
        return Response.ok(
                ApiResponse.success(ticketService.findById(id))).build();
    }

    @POST
    @Path("/create")
    public Response create(@Valid CreateTicketDto dto) {

        // Ambil userId dari JWT dengan aman
        Long userId = getUserIdFromJwt();

        return Response.ok(
                ApiResponse.success(
                        ticketService.create(dto, userId)))
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

        Long userId = getUserIdFromJwt();

        return Response.ok(
                ApiResponse.success(
                        ticketService.getByReporter(userId)))
                .build();
    }

    // ==========================
    // HELPER METHOD
    // ==========================

    private Long getUserIdFromJwt() {
        Object claim = jwt.getClaim("userId");

        if (claim == null) {
            throw new WebApplicationException("UserId tidak ditemukan di token", 401);
        }

        try {
            return Long.parseLong(claim.toString());
        } catch (Exception e) {
            throw new WebApplicationException("Format userId di token tidak valid", 401);
        }
    }
}
