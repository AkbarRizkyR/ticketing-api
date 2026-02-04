package io.github.akbarrizky.repository.attachment;

import io.github.akbarrizky.entity.attachment.AttachmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AttachmentRepository implements PanacheRepository<AttachmentEntity> {

    public List<AttachmentEntity> findByTicketId(Long ticketId) {
        return list("ticketId", ticketId);
    }

}
