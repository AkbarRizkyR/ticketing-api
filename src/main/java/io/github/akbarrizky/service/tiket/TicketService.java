package io.github.akbarrizky.service.tiket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.akbarrizky.dto.comment.CommentResponseDto;
import io.github.akbarrizky.dto.attachment.AttachmentDTO;
import io.github.akbarrizky.dto.comment.CreateCommentDto;
import io.github.akbarrizky.dto.tiket.CreateTicketDto;
import io.github.akbarrizky.dto.tiket.TicketDto;
import io.github.akbarrizky.dto.tiket.UpdateTicketDto;
import io.github.akbarrizky.entity.tiket.*;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.exception.NotFoundException;
import io.github.akbarrizky.repository.tiket.*;
import io.github.akbarrizky.repository.user.UserRepository;
import io.github.akbarrizky.service.attachment.AttachmentService;
import io.github.akbarrizky.entity.attachment.AttachmentEntity;
import io.github.akbarrizky.util.DateUtil;
import io.github.akbarrizky.util.PaginationResponse;
import io.github.akbarrizky.dto.tiket.TicketHistoryDto;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
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

        @Inject
        TicketCommentRepository commentRepository;

        // ===== YANG BARU =====
        @Inject
        AttachmentService attachmentService;
        // =====================

        @Transactional
        public TicketDto create(CreateTicketDto dto, String userIdStr) {

                UUID userId = UUID.fromString(userIdStr);
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
                ticket.categoryName = category.name;

                ticket.createdBy = user.fullName;
                ticket.ticketCode = "TCK-" + System.currentTimeMillis();

                // HANDLE ASSIGNED TO
                if (dto.assignedTo != null) {
                        try {
                                User assignedUser = userRepository.findByIdOptional(dto.assignedTo).orElse(null);
                                if (assignedUser != null) {
                                        ticket.assignedTo = assignedUser;
                                        ticket.assignedName = assignedUser.fullName;
                                }
                        } catch (Exception e) {
                                // ignore
                        }
                } else if (dto.assignedName != null) {
                        ticket.assignedName = dto.assignedName;
                }

                // HANDLE REPORTED BY
                if (dto.reportedId != null) {
                        try {
                                User reportedUser = userRepository.findByIdOptional(dto.reportedId).orElse(null);
                                if (reportedUser != null) {
                                        ticket.reportedBy = reportedUser;
                                        ticket.reportedName = reportedUser.fullName;
                                }
                        } catch (Exception e) {
                                // ignore
                        }

                } else if (dto.reportedName != null) {
                        ticket.reportedName = dto.reportedName;
                }

                // HANDLE CASE OWNER
                ticket.caseOwner = dto.caseOwner;

                // HANDLE CASE COMPLAINT
                ticket.caseComplaint = dto.caseComplaint;

                ticketRepository.persist(ticket);

                // HANDLE ATTACHMENTS
                if (dto.attachmentIds != null && !dto.attachmentIds.isEmpty()) {
                        for (String attachIdStr : dto.attachmentIds) {
                                try {
                                        UUID attachId = UUID.fromString(attachIdStr);
                                        AttachmentEntity attachment = attachmentService.findById(attachId);
                                        if (attachment != null) {
                                                attachment.setTicketId(ticket.id);
                                                attachmentService.persistAttachment(attachment);
                                        }
                                } catch (Exception e) {
                                        // ignore invalid IDs
                                }
                        }
                }

                // LOG HISTORY
                logHistory(ticket, "CREATED", "Ticket Created", "-", "-", user.fullName);

                return toDto(ticket);
        }

        @Transactional
        public TicketDto update(UpdateTicketDto dto, String userIdStr) {

                UUID userId = UUID.fromString(userIdStr);
                User userModifier = userRepository.findByIdOptional(userId)
                                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                Ticket ticket = ticketRepository.findByIdOptional(dto.id)
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan"));

                if (dto.title != null && !dto.title.equals(ticket.title)) {
                        logHistory(ticket, "UPDATED", "Title", ticket.title, dto.title, userModifier.fullName);
                        ticket.title = dto.title;
                }

                if (dto.description != null && !dto.description.equals(ticket.description)) {
                        logHistory(ticket, "UPDATED", "Description", ticket.description, dto.description,
                                        userModifier.fullName);
                        ticket.description = dto.description;
                }

                if (dto.categoryId != null) {
                        TicketCategory category = categoryRepository.findByIdOptional(dto.categoryId)
                                        .orElseThrow(() -> new NotFoundException("Category tidak ditemukan"));

                        if (ticket.category == null || !ticket.category.id.equals(category.id)) {
                                logHistory(ticket, "UPDATED", "Category",
                                                ticket.category != null ? ticket.category.name : "-",
                                                category.name, userModifier.fullName);

                                ticket.category = category;
                                ticket.categoryName = category.name;
                        }
                }

                if (dto.priorityId != null) {
                        TicketPriority priority = priorityRepository.findByIdOptional(dto.priorityId)
                                        .orElseThrow(() -> new NotFoundException("Priority tidak ditemukan"));

                        if (ticket.priority == null || !ticket.priority.id.equals(priority.id)) {
                                logHistory(ticket, "UPDATED", "Priority",
                                                ticket.priority != null ? ticket.priority.name : "-",
                                                priority.name, userModifier.fullName);

                                ticket.priority = priority;
                                ticket.priorityName = priority.name;
                        }
                }

                if (dto.statusId != null) {
                        TicketStatus status = statusRepository.findByIdOptional(dto.statusId)
                                        .orElseThrow(() -> new NotFoundException("Status tidak ditemukan"));

                        if (ticket.status == null || !ticket.status.id.equals(status.id)) {
                                logHistory(ticket, "UPDATED", "Status",
                                                ticket.status != null ? ticket.status.name : "-",
                                                status.name, userModifier.fullName);

                                ticket.status = status;
                                ticket.statusName = status.name;
                        }
                }

                if (dto.assignedTo != null) {
                        User user = userRepository.findByIdOptional(dto.assignedTo)
                                        .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                        if (ticket.assignedTo == null || !ticket.assignedTo.id.equals(user.id)) {
                                logHistory(ticket, "UPDATED", "Assigned To",
                                                ticket.assignedName != null ? ticket.assignedName : "-",
                                                user.fullName, userModifier.fullName);

                                ticket.assignedTo = user;
                                ticket.assignedName = user.fullName;
                        }
                }

                if (dto.reportedId != null) {
                        User reporter = userRepository.findByIdOptional(dto.reportedId)
                                        .orElseThrow(() -> new NotFoundException("User reporter tidak ditemukan"));

                        ticket.reportedBy = reporter;
                        ticket.reportedName = reporter.fullName;
                }

                // HANDLE CASE OWNER
                if (dto.caseOwner != null && !dto.caseOwner.equals(ticket.caseOwner)) {
                        logHistory(ticket, "UPDATED", "Case Owner",
                                        ticket.caseOwner != null ? ticket.caseOwner : "-",
                                        dto.caseOwner, userModifier.fullName);
                        ticket.caseOwner = dto.caseOwner;
                }

                // HANDLE CASE COMPLAINT
                if (dto.caseComplaint != null && !dto.caseComplaint.equals(ticket.caseComplaint)) {
                        logHistory(ticket, "UPDATED", "Case Complaint",
                                        ticket.caseComplaint != null ? ticket.caseComplaint : "-",
                                        dto.caseComplaint, userModifier.fullName);
                        ticket.caseComplaint = dto.caseComplaint;
                }

                // HANDLE ATTACHMENTS
                if (dto.attachmentIds != null && !dto.attachmentIds.isEmpty()) {
                        for (String attachIdStr : dto.attachmentIds) {
                                try {
                                        UUID attachId = UUID.fromString(attachIdStr);
                                        AttachmentEntity attachment = attachmentService.findById(attachId);
                                        if (attachment != null) {
                                                attachment.setTicketId(ticket.id);
                                                attachmentService.persistAttachment(attachment);
                                        }
                                } catch (Exception e) {
                                        // ignore invalid IDs
                                }
                        }
                        logHistory(ticket, "UPDATED", "Attachments", "-",
                                        "Added " + dto.attachmentIds.size() + " files", userModifier.fullName);
                }

                return toDto(ticket);
        }

        // ... existing methods ...

        @Inject
        TicketHistoryRepository historyRepository;

        private void logHistory(Ticket ticket, String action, String field, String oldValue, String newValue,
                        String changedBy) {
                TicketHistory history = new TicketHistory();
                history.ticket = ticket;
                history.action = action;
                history.field = field;
                history.oldValue = oldValue;
                history.newValue = newValue;
                history.changedBy = changedBy;
                historyRepository.persist(history);
        }

        public io.github.akbarrizky.util.PaginationResponse<io.github.akbarrizky.dto.tiket.TicketHistoryDto> getHistory(
                        String ticketCode, int page, int size) {
                Ticket ticket = ticketRepository.find("ticketCode", ticketCode).firstResultOptional()
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan: " + ticketCode));

                io.quarkus.hibernate.orm.panache.PanacheQuery<TicketHistory> query = historyRepository.find(
                                "ticket.ticketCode", io.quarkus.panache.common.Sort.descending("changedAt"),
                                ticketCode);

                List<io.github.akbarrizky.dto.tiket.TicketHistoryDto> items = query
                                .page(io.quarkus.panache.common.Page.of(page, size))
                                .list().stream().map(h -> {
                                        io.github.akbarrizky.dto.tiket.TicketHistoryDto dto = new io.github.akbarrizky.dto.tiket.TicketHistoryDto();
                                        dto.id = h.id;
                                        dto.action = h.action;
                                        dto.field = h.field;
                                        dto.oldValue = h.oldValue;
                                        dto.newValue = h.newValue;
                                        dto.changedBy = h.changedBy;
                                        dto.changedAt = DateUtil.format(h.changedAt);
                                        return dto;
                                }).collect(Collectors.toList());

                long totalItems = query.count();
                int totalPages = (int) Math.ceil((double) totalItems / size);

                return new PaginationResponse<>(items, page, size, totalItems, totalPages);
        }

        public PaginationResponse<TicketHistoryDto> getAllHistory(
                        int page, int size) {
                PanacheQuery<TicketHistory> query = historyRepository
                                .findAll(io.quarkus.panache.common.Sort.descending("changedAt"));

                List<TicketHistoryDto> items = query
                                .page(io.quarkus.panache.common.Page.of(page, size))
                                .list().stream().map(h -> {
                                        TicketHistoryDto dto = new TicketHistoryDto();
                                        dto.id = h.id;
                                        dto.ticketCode = h.ticket.ticketCode;
                                        dto.action = h.action;
                                        dto.field = h.field;
                                        dto.oldValue = h.oldValue;
                                        dto.newValue = h.newValue;
                                        dto.changedBy = h.changedBy;
                                        dto.changedAt = DateUtil.format(h.changedAt);
                                        return dto;
                                }).collect(Collectors.toList());

                long totalItems = query.count();
                int totalPages = (int) Math.ceil((double) totalItems / size);

                return new io.github.akbarrizky.util.PaginationResponse<>(items, page, size, totalItems, totalPages);
        }

        @Transactional
        public CommentResponseDto createComment(CreateCommentDto dto, String userIdStr) {

                UUID userId = UUID.fromString(userIdStr); // Parse from JWT subject/claim

                Ticket ticket = ticketRepository.find("ticketCode", dto.ticketCode).firstResultOptional()
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan: " + dto.ticketCode));

                User user = userRepository.findByIdOptional(userId)
                                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                TicketComment comment = new TicketComment();

                comment.ticket = ticket;
                comment.user = user;
                comment.comment = dto.comment;
                comment.createdAt = LocalDateTime.now();

                commentRepository.persist(comment);

                if (dto.attachmentIds != null && !dto.attachmentIds.isEmpty()) {
                        List<AttachmentEntity> attachments = new java.util.ArrayList<>();
                        for (String attachmentIdStr : dto.attachmentIds) {
                                try {
                                        UUID attachmentId = UUID.fromString(attachmentIdStr);
                                        AttachmentEntity attachment = attachmentService
                                                        .findById(attachmentId);
                                        if (attachment != null) {
                                                attachments.add(attachment);
                                        }
                                } catch (Exception e) {
                                        // log error
                                }
                        }
                        comment.attachments = attachments;
                        commentRepository.persist(comment);
                }

                return toCommentDto(comment);
        }

        public List<CommentResponseDto> getByTicket(String ticketCode) {
                Ticket ticket = ticketRepository.find("ticketCode", ticketCode).firstResultOptional()
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan: " + ticketCode));

                return commentRepository.findByTicketId(ticket.id)
                                .stream()
                                .map(this::toCommentDto)
                                .collect(Collectors.toList());
        }

        private CommentResponseDto toCommentDto(TicketComment c) {

                CommentResponseDto dto = new CommentResponseDto();

                dto.id = c.id;
                dto.ticketId = c.ticket.id;
                dto.userId = c.user.id;
                dto.userFullName = c.user.fullName;
                dto.comment = c.comment;

                dto.createdAt = c.createdAt != null
                                ? c.createdAt.toString()
                                : null;

                dto.updatedAt = c.updatedAt != null
                                ? c.updatedAt.toString()
                                : null;

                // Populate attachments
                if (c.attachments != null) {
                        dto.attachments = c.attachments.stream()
                                        .map(att -> attachmentService.toDTO(att))
                                        .collect(Collectors.toList());
                } else {
                        dto.attachments = java.util.Collections.emptyList();
                }

                return dto;
        }

        public TicketDto findById(UUID id) {

                Ticket ticket = ticketRepository.findByIdOptional(id)
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan"));

                return toDto(ticket);
        }

        public List<TicketDto> getAll() {
                return ticketRepository.listAll().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        public List<TicketDto> getByReporter(String userIdStr) {

                UUID userId = UUID.fromString(userIdStr);
                User user = userRepository.findByIdOptional(userId)
                                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                return ticketRepository.list("createdBy", user.fullName).stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        private TicketDto toDto(Ticket t) {

                TicketDto dto = new TicketDto();

                dto.id = t.id;
                dto.ticketCode = t.ticketCode;
                dto.title = t.title;
                dto.description = t.description;

                dto.priority = t.priorityName;
                dto.status = t.statusName;
                dto.category = t.categoryName;

                dto.createdBy = t.createdBy;

                dto.assignedTo = t.assignedTo != null ? t.assignedTo.id : null;
                dto.assignedName = t.assignedName;

                dto.reportedName = t.reportedName;
                dto.reportedId = t.reportedBy != null ? t.reportedBy.id : null;

                dto.caseOwner = t.caseOwner;
                dto.caseComplaint = t.caseComplaint;

                dto.createdAt = DateUtil.format(t.createdAt);
                dto.updatedAt = DateUtil.format(t.updatedAt);

                // ===== BAGIAN PENTING =====
                List<AttachmentDTO> attachments = attachmentService.getByTicket(t.id);

                dto.attachments = attachments;
                // ===========================

                return dto;
        }
}
