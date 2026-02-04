package io.github.akbarrizky.service.tiket;

import java.time.LocalDateTime;
import java.util.List;
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
import io.github.akbarrizky.util.DateUtil;

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
                ticket.categoryName = category.name;

                ticket.createdBy = user.fullName;

                ticket.ticketCode = "TCK-" + System.currentTimeMillis();

                ticketRepository.persist(ticket);

                return toDto(ticket);
        }

        @Transactional
        public TicketDto update(UpdateTicketDto dto, Long userId) {

                Ticket ticket = ticketRepository.findByIdOptional(dto.id)
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan"));

                if (dto.title != null)
                        ticket.title = dto.title;

                if (dto.description != null)
                        ticket.description = dto.description;

                if (dto.categoryId != null) {
                        TicketCategory category = categoryRepository.findByIdOptional(dto.categoryId)
                                        .orElseThrow(() -> new NotFoundException("Category tidak ditemukan"));

                        ticket.category = category;
                        ticket.categoryName = category.name;
                }

                if (dto.priorityId != null) {
                        TicketPriority priority = priorityRepository.findByIdOptional(dto.priorityId)
                                        .orElseThrow(() -> new NotFoundException("Priority tidak ditemukan"));

                        ticket.priority = priority;
                        ticket.priorityName = priority.name;
                }

                if (dto.statusId != null) {
                        TicketStatus status = statusRepository.findByIdOptional(dto.statusId)
                                        .orElseThrow(() -> new NotFoundException("Status tidak ditemukan"));

                        ticket.status = status;
                        ticket.statusName = status.name;
                }

                if (dto.assignedTo != null) {
                        User user = userRepository.findByIdOptional(dto.assignedTo)
                                        .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                        ticket.assignedTo = user;
                        ticket.assignedName = user.fullName;
                }

                if (dto.reportedId != null) {
                        User reporter = userRepository.findByIdOptional(dto.reportedId)
                                        .orElseThrow(() -> new NotFoundException("User reporter tidak ditemukan"));

                        ticket.reportedBy = reporter;
                        ticket.reportedName = reporter.fullName;
                }

                return toDto(ticket);
        }

        @Transactional
        public CommentResponseDto createComment(CreateCommentDto dto, Long userId) {

                Ticket ticket = ticketRepository.findByIdOptional(dto.ticketId)
                                .orElseThrow(() -> new NotFoundException("Ticket tidak ditemukan"));

                User user = userRepository.findByIdOptional(userId)
                                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));

                TicketComment comment = new TicketComment();

                comment.ticket = ticket;
                comment.user = user;
                comment.comment = dto.comment;
                comment.createdAt = LocalDateTime.now();

                commentRepository.persist(comment);

                return toCommentDto(comment);
        }

        public List<CommentResponseDto> getByTicket(Long ticketId) {
                return commentRepository.findByTicketId(ticketId)
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

                // Comment TIDAK perlu attachment
                dto.attachments = List.of();

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

                dto.createdAt = DateUtil.format(t.createdAt);
                dto.updatedAt = DateUtil.format(t.updatedAt);

                // ===== BAGIAN PENTING =====
                List<AttachmentDTO> attachments = attachmentService.getByTicket(t.id);

                dto.attachments = attachments;
                // ===========================

                return dto;
        }
}
