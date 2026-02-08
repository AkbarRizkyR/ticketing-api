package io.github.akbarrizky.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateCommentDto {

    @NotNull
    public String ticketCode;

    @NotBlank
    public String comment;

    public List<String> attachmentIds;
}
