package io.github.akbarrizky.dto.attachment;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttachmentDTO {

    public UUID id;
    public UUID ticketId;
    public String fileName;
    public String fileType;
    public Long fileSize;
    public LocalDateTime uploadedAt;
    public UUID uploadedBy;
    public String uploadedName;

}
