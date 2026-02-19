package io.github.akbarrizky.dto.tiket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateTicketDto {

    @NotBlank(message = "Judul wajib diisi")
    @Size(max = 255)
    public String title;

    @NotBlank(message = "Deskripsi wajib diisi")
    @Size(max = 2000)
    public String description;

    @NotNull(message = "Prioritas wajib diisi")
    public Long priorityId;

    @NotNull(message = "Status wajib diisi")
    public Long statusId;

    @NotNull(message = "Kategori wajib diisi")
    public Long categoryId;

    public UUID assignedTo;

    public UUID reportedId;

    public String assignedName;

    public String reportedName;

    public String caseOwner;

    public String caseComplaint;

    public java.util.List<String> attachmentIds;
}
