package io.github.akbarrizky.dto.tiket;

public class TicketDto {

    public Long id;
    public String ticketCode;
    public String title;
    public String description;

    public String priority;
    public String status;
    public String category;

    public String createdBy;
    public String createdAt;
    public String updatedAt;

    public Long assignedTo;
    public Long reportedId;

    public String assignedName;
    public String reportedName;
}
