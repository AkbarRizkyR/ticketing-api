package io.github.akbarrizky.dto.tiket;

import java.util.UUID;

public class TicketHistoryDto {
    public UUID id;
    public String ticketCode;
    public String action;
    public String field;
    public String oldValue;
    public String newValue;
    public String changedBy;
    public String changedAt;
}
