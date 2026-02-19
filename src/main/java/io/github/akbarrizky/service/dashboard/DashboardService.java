package io.github.akbarrizky.service.dashboard;

import io.github.akbarrizky.dto.dashboard.DashboardSummaryDto;
import io.github.akbarrizky.dto.dashboard.DashboardSummaryDto.CategoryCountDto;
import io.github.akbarrizky.dto.dashboard.DashboardSummaryDto.StatusCountDto;
import io.github.akbarrizky.repository.tiket.TicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardService {

    @Inject
    TicketRepository ticketRepository;

    public DashboardSummaryDto getSummary() {

        DashboardSummaryDto dto = new DashboardSummaryDto();

        // Total tickets
        dto.totalTickets = ticketRepository.count();

        // Count by status
        List<Object[]> statusCounts = ticketRepository
                .getEntityManager()
                .createQuery(
                        "SELECT t.statusName, COUNT(t) FROM Ticket t GROUP BY t.statusName ORDER BY t.statusName",
                        Object[].class)
                .getResultList();

        dto.byStatus = statusCounts.stream()
                .map(row -> new StatusCountDto(
                        row[0] != null ? (String) row[0] : "Unknown",
                        (Long) row[1]))
                .collect(Collectors.toList());

        // Count by category
        List<Object[]> categoryCounts = ticketRepository
                .getEntityManager()
                .createQuery(
                        "SELECT t.categoryName, COUNT(t) FROM Ticket t GROUP BY t.categoryName ORDER BY t.categoryName",
                        Object[].class)
                .getResultList();

        dto.byCategory = categoryCounts.stream()
                .map(row -> new CategoryCountDto(
                        row[0] != null ? (String) row[0] : "Unknown",
                        (Long) row[1]))
                .collect(Collectors.toList());

        return dto;
    }
}
