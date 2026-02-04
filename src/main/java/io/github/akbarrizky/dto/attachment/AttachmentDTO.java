package io.github.akbarrizky.dto.attachment;

import java.time.LocalDateTime;

public class AttachmentDTO {

    public Long id;
    public Long ticketId;
    public String fileName;
    public String fileType;
    public Long fileSize;
    public LocalDateTime uploadedAt;
    public Long uploadedBy;
    public String uploadedName;

}
