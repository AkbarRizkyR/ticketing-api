package io.github.akbarrizky.util;

public class ApiResponse<T> {

    public String status;
    public T data;

    private ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("Berhasil / Success", data);
    }
}
