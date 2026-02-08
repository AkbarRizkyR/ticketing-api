package io.github.akbarrizky.util;

import java.util.List;

public class PaginationResponse<T> {
    public List<T> items;
    public int page;
    public int size;
    public long totalItems;
    public int totalPages;

    public PaginationResponse(List<T> items, int page, int size, long totalItems, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }
}
