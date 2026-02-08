package io.github.akbarrizky.dto.comment;

import io.github.akbarrizky.dto.attachment.AttachmentDTO;

import java.util.List;

import java.util.UUID;

public class CommentResponseDto {

    public UUID id;

    public UUID ticketId;

    public UUID userId;

    public String userFullName;

    public String comment;

    public String createdAt;

    public String updatedAt;

    public List<AttachmentDTO> attachments;
}
