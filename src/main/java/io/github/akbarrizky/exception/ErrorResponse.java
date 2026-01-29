package io.github.akbarrizky.exception;

public class ErrorResponse {

    public int status;
    public String message;
    public String timestamp;

    public ErrorResponse(int status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
