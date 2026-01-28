package io.github.akbarrizky.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Map;

@Provider
public class JaxRsNotFoundMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                        "timestamp", Instant.now(),
                        "status", 404,
                        "message", "Endpoint not found"))
                .build();
    }
}
