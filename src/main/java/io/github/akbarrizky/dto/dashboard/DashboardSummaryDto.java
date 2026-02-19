package io.github.akbarrizky.dto.dashboard;

import java.util.List;

public class DashboardSummaryDto {

    public long totalTickets;
    public List<StatusCountDto> byStatus;
    public List<CategoryCountDto> byCategory;

    public static class StatusCountDto {
        public String status;
        public long count;

        public StatusCountDto() {
        }

        public StatusCountDto(String status, long count) {
            this.status = status;
            this.count = count;
        }
    }

    public static class CategoryCountDto {
        public String category;
        public long count;

        public CategoryCountDto() {
        }

        public CategoryCountDto(String category, long count) {
            this.category = category;
            this.count = count;
        }
    }
}
