package io.github.akbarrizky.dto.comment;

import io.github.akbarrizky.dto.attachment.AttachmentDTO;

import java.util.List;

public class CommentResponseDto {

    public Long id;

    public Long ticketId;

    public Long userId;

    public String userFullName;

    public String comment;

    public String createdAt;

    public List<AttachmentDTO> attachments;
}
