package io.github.akbarrizky.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCommentDto {

    @NotNull
    public Long ticketId;

    @NotBlank
    public String comment;
}
