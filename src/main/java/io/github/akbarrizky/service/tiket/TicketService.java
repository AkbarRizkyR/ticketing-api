package io.github.akbarrizky.service.tiket;

import java.util.List;
import java.util.stream.Collectors;
import io.github.akbarrizky.dto.tiket.CreateTicketDto;
import io.github.akbarrizky.dto.tiket.TicketDto;
import io.github.akbarrizky.entity.tiket.Ticket;
import io.github.akbarrizky.entity.tiket.TicketCategory;
import io.github.akbarrizky.entity.tiket.TicketPriority;
import io.github.akbarrizky.entity.tiket.TicketStatus;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.exception.NotFoundException;
import io.github.akbarrizky.repository.tiket.TicketRepository;
import io.github.akbarrizky.repository.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import io.github.akbarrizky.repository.tiket.TicketCategoryRepository;
import io.github.akbarrizky.repository.tiket.TicketPriorityRepository;
import io.github.akbarrizky.repository.tiket.TicketStatusRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TicketService {

    @Inject
    TicketRepository ticketRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TicketPriorityRepository priorityRepository;

    @Inject
    TicketStatusRepository statusRepository;

    @Inject
    TicketCategoryRepository categoryRepository;

    @Transactional
    public TicketDto create(CreateTicketDto dto, Long userId) {

        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

        TicketPriority priority = priorityRepository.findByIdOptional(dto.priorityId)
                .orElseThrow(() -> new NotFoundException("Priority tidak ditemukan"));

        TicketStatus status = statusRepository.findByIdOptional(dto.statusId)
                .orElseThrow(() -> new NotFoundException("Status tidak ditemukan"));

        TicketCategory category = categoryRepository.findByIdOptional(dto.categoryId)
                .orElseThrow(() -> new NotFoundException("Category tidak ditemukan"));

        Ticket ticket = new Ticket();

        ticket.title = dto.title;
        ticket.description = dto.description;

        ticket.priority = priority;
        ticket.priorityName = priority.name;

        ticket.status = status;
        ticket.statusName = status.name;

        ticket.category = category;
        // ticket.categoryName = category.name; // Skipped

        ticket.createdBy = user;

        // Generate Ticket Code (TCK-{timestamp}) - total < 20 chars
        // System.currentTimeMillis() is 13 digits. TCK- + 13 = 17 chars.
        ticket.ticketCode = "TCK-" + System.currentTimeMillis();

        ticketRepository.persist(ticket);

        return toDto(ticket);
    }

    private TicketDto toDto(Ticket t) {

        TicketDto dto = new TicketDto();

        dto.id = t.id;
        dto.ticketCode = t.ticketCode;
        dto.title = t.title;
        dto.description = t.description;

        dto.priority = (t.priority != null) ? t.priority.name : null;
        dto.status = (t.status != null) ? t.status.name : null;
        dto.category = (t.category != null) ? t.category.name : null;

        dto.createdBy = (t.createdBy != null) ? t.createdBy.email : null;

        return dto;
    }

    public TicketDto findById(Long id) {

        Ticket ticket = ticketRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan"));

        return toDto(ticket);
    }

    public List<TicketDto> getAll() {
        return ticketRepository.listAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TicketDto> getByReporter(Long userId) {
        return ticketRepository.list("createdBy.id", userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
