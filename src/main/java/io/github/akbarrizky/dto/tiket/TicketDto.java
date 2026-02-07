package io.github.akbarrizky.dto.tiket;

import io.github.akbarrizky.dto.attachment.AttachmentDTO;
import java.util.List;

import java.util.UUID;

public class TicketDto {

    public UUID id;
    public String ticketCode;
    public String title;
    public String description;

    public String priority;
    public String status;
    public String category;

    public String createdBy;
    public String createdAt;
    public String updatedAt;

    public UUID assignedTo;
    public UUID reportedId;

    public String assignedName;
    public String reportedName;

    public List<AttachmentDTO> attachments;
}
