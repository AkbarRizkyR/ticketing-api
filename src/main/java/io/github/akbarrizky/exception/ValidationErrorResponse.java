package io.github.akbarrizky.exception;

import java.util.Map;

public class ValidationErrorResponse {

    public Map<String, String> errors;

    public ValidationErrorResponse(Map<String, String> errors) {
        this.errors = errors;
    }
}
