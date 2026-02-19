package io.github.akbarrizky.resource.dashboard;

import io.github.akbarrizky.service.dashboard.DashboardService;
import io.github.akbarrizky.util.ApiResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class DashboardResource {

    @Inject
    DashboardService dashboardService;

    @GET
    @Path("/summary")
    public Response getSummary() {
        return Response.ok(
                ApiResponse.success(dashboardService.getSummary()))
                .build();
    }
}
