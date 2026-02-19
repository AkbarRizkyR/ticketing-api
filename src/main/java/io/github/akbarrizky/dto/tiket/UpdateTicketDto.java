package io.github.akbarrizky.dto.tiket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateTicketDto {

    @NotNull
    public UUID id;

    @NotBlank(message = "Judul wajib diisi")
    public String title;

    @NotBlank(message = "Deskripsi wajib diisi")
    public String description;

    public Long categoryId;

    public Long priorityId;

    public Long statusId;

    public UUID assignedTo;

    public UUID reportedId;

    public String assignedName;

    public String reportedName;

    public String caseOwner;

    public String caseComplaint;

    public java.util.List<String> attachmentIds;
}
