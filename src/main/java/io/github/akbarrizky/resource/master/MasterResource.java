package io.github.akbarrizky.resource.master;

import io.github.akbarrizky.util.ApiResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.github.akbarrizky.repository.tiket.TicketCategoryRepository;
import io.github.akbarrizky.repository.tiket.TicketPriorityRepository;
import io.github.akbarrizky.repository.tiket.TicketStatusRepository;
import io.github.akbarrizky.service.user.UserMasterService;

@Path("/master")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class MasterResource {

    @Inject
    TicketCategoryRepository categoryRepository;
    @Inject
    TicketPriorityRepository priorityRepository;
    @Inject
    TicketStatusRepository statusRepository;

    @Inject
    UserMasterService userMasterService;

    @GET
    @Path("/categories")
    public Response categories() {
        return Response.ok(
                ApiResponse.success(categoryRepository.listAll())).build();
    }

    @GET
    @Path("/priorities")
    public Response priorities() {
        return Response.ok(
                ApiResponse.success(priorityRepository.listAll())).build();
    }

    @GET
    @Path("/statuses")
    public Response statuses() {
        return Response.ok(
                ApiResponse.success(statusRepository.listAll())).build();
    }

    // ============ USER MASTER ============

    @GET
    @Path("/users")
    public Response users() {
        return Response.ok(
                ApiResponse.success(userMasterService.getAll())).build();
    }

    @GET
    @Path("/users/{id}")
    public Response userById(@PathParam("id") java.util.UUID id) {
        return Response.ok(
                ApiResponse.success(userMasterService.getById(id))).build();
    }

    @GET
    @Path("/users/search")
    public Response searchUser(@QueryParam("keyword") String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return Response.ok(
                    ApiResponse.success(userMasterService.getAll())).build();
        }

        return Response.ok(
                ApiResponse.success(userMasterService.search(keyword))).build();
    }

}
