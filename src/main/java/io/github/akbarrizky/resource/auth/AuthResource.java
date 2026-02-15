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
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;

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
    public Response login(@Valid LoginDto dto, @Context HttpHeaders headers) {
        // In JAX-RS/Quarkus, getting remote IP can be tricky without specific reactive
        // extensions or Undertow.
        // For now, let's try getting it from headers like X-Forwarded-For or use a
        // placeholder if complex.
        // Or if we want to extract it safely without new deps:
        String ipAddress = headers.getHeaderString("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = "unknown";
        }
        String userAgent = headers.getHeaderString("User-Agent");

        User user = userRepository.findByEmail(dto.email)
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));

        if (!PasswordUtil.verify(dto.password, user.passwordHash)) {
            throw new UnauthorizedException("Email atau password salah");
        }

        // Delegating session creation to service
        String token = authService.login(dto, ipAddress, userAgent);

        return Response.ok(ApiResponse.success(token)).build();
    }

    @POST
    @Path("/logout")
    @Authenticated
    public Response logout(@Context SecurityContext ctx) {
        String sessionId = null;
        try {
            Object claim = jwt.getClaim("sessionId");
            if (claim != null) {
                sessionId = claim.toString();
            }
        } catch (Exception e) {
            // ignore
        }
        authService.logout(sessionId);
        return Response.ok(ApiResponse.success("Logout berhasil")).build();
    }

    @Inject
    org.eclipse.microprofile.jwt.JsonWebToken jwt;
}
