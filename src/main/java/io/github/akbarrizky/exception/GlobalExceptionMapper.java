package io.github.akbarrizky.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof AppException app) {
            return Response.status(app.getStatus())
                    .entity(Map.of(
                            "timestamp", Instant.now(),
                            "status", app.getStatus(),
                            "message", app.getMessage()))
                    .build();
        }

        return Response.status(500)
                .entity(Map.of(
                        "timestamp", Instant.now(),
                        "status", 500,
                        "message", "Internal server error"))
                .build();
    }
}
