package io.github.akbarrizky.resource.user;

import io.github.akbarrizky.dto.user.CreateUserDto;
import io.github.akbarrizky.dto.user.UserDto;
import io.github.akbarrizky.service.user.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Response create(@Valid CreateUserDto dto) {
        UserDto result = userService.create(dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Path("/{id}")
    public UserDto get(@PathParam("id") Long id) {
        return userService.getById(id);
    }
}
