package io.github.akbarrizky.exception;

import jakarta.ws.rs.core.Response;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND.getStatusCode());
    }
}
