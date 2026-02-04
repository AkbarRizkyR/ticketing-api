package io.github.akbarrizky.dto.tiket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketDto {

    @NotNull
    public Long id;

    @NotBlank(message = "Judul wajib diisi")
    public String title;

    @NotBlank(message = "Deskripsi wajib diisi")
    public String description;

    public Long categoryId;

    public Long priorityId;

    public Long statusId;

    public Long assignedTo;

    public Long reportedId;

    public String assignedName;

    public String reportedName;
}
