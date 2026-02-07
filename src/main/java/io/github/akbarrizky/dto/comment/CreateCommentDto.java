package io.github.akbarrizky.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateCommentDto {

    @NotNull
    public UUID ticketId;

    @NotBlank
    public String comment;
}
