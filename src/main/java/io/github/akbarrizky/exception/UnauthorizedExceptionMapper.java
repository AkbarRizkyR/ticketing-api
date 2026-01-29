package io.github.akbarrizky.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;

@Provider
public class UnauthorizedExceptionMapper
        implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException e) {

        ErrorResponse response = new ErrorResponse(
                401,
                e.getMessage(),
                Instant.now().toString());

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(response)
                .build();
    }
}
