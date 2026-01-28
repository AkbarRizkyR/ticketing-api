package io.github.akbarrizky.exception;

import jakarta.ws.rs.core.Response;

public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, Response.Status.BAD_REQUEST.getStatusCode());
    }
}
