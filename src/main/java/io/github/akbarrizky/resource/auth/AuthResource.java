package io.github.akbarrizky.resource.auth;

import io.github.akbarrizky.dto.auth.LoginDto;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.repository.user.UserRepository;
import io.github.akbarrizky.service.auth.AuthService;
import io.github.akbarrizky.util.ApiResponse;
import io.github.akbarrizky.util.PasswordUtil;
import io.github.akbarrizky.exception.UnauthorizedException;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserRepository userRepository;

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginDto dto) {

        User user = userRepository.findByEmail(dto.email)
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));

        if (!PasswordUtil.verify(dto.password, user.passwordHash)) {
            throw new UnauthorizedException("Email atau password salah");
        }

        String token = authService.generateToken(
                user.id,
                user.email,
                user.fullName,
                user.roles
                        .stream()
                        .map(r -> r.name)
                        .collect(java.util.stream.Collectors.toSet()));

        return Response.ok(ApiResponse.success(token)).build();
    }
}
