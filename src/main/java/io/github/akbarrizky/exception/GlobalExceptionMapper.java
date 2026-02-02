package io.github.akbarrizky.exception;

import io.github.akbarrizky.util.LogUtil;
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
                        // Log domain exceptions as warning or info if needed, usually less critical
                        LogUtil.warn("AppException: " + app.getMessage());

                        return Response.status(app.getStatus())
                                        .entity(Map.of(
                                                        "timestamp", Instant.now(),
                                                        "status", app.getStatus(),
                                                        "message", app.getMessage()))
                                        .build();
                }

                // Log unexpected exceptions with full stack trace
                LogUtil.error("Unexpected error occurred", exception);

                return Response.status(500)
                                .entity(Map.of(
                                                "timestamp", Instant.now(),
                                                "status", 500,
                                                "message", "Internal server error: " + exception.getMessage()))
                                .build();
        }
}
