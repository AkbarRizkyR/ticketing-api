package io.github.akbarrizky.dto.tiket;

import jakarta.validation.constraints.NotBlank;

public class UpdateTicketStatusDto {

    @NotBlank
    public String status;
}
