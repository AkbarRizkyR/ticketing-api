package io.github.akbarrizky.service.attachment;

import io.github.akbarrizky.dto.attachment.AttachmentDTO;
import io.github.akbarrizky.entity.attachment.AttachmentEntity;
import io.github.akbarrizky.repository.attachment.AttachmentRepository;
import io.github.akbarrizky.util.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AttachmentService {

    @Inject
    AttachmentRepository attachmentRepository;

    @Inject
    MinioConfig minioConfig;

    @ConfigProperty(name = "minio.bucket")
    String bucketName;

    @Transactional
    public AttachmentDTO uploadFile(
            Long ticketId,
            InputStream fileInputStream,
            String fileName,
            String contentType,
            JsonWebToken jwt) throws Exception {

        String storedFileName = UUID.randomUUID() + "_" + fileName;

        // Ensure bucket exists
        boolean found = minioConfig.minioClient().bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioConfig.minioClient().makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        // Upload to MinIO
        try (InputStream stream = fileInputStream) {
            minioConfig.minioClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedFileName)
                            .stream(stream, -1, 10485760) // 10MB part size
                            .contentType(contentType != null ? contentType : "application/octet-stream") // default
                            .build());
        }

        AttachmentEntity entity = new AttachmentEntity();
        entity.setTicketId(ticketId);
        entity.setFileName(fileName);
        entity.setFileType(contentType);
        entity.setFileSize(0L);
        entity.setFilePath(storedFileName); // Store object name as path

        // Use Subject for ID (safer)
        if (jwt.getSubject() != null) {
            entity.setUploadedBy(Long.parseLong(jwt.getSubject()));
        } else if (jwt.getClaim("userId") != null) {
            entity.setUploadedBy(Long.parseLong(jwt.getClaim("userId").toString()));
        }

        // Use fullName claim or fallback to getName() (email)
        Object fullNameClaim = jwt.getClaim("fullName");
        if (fullNameClaim != null) {
            entity.setUploadedName(fullNameClaim.toString());
        } else {
            entity.setUploadedName(jwt.getName());
        }

        attachmentRepository.persist(entity);

        // Update file size after upload
        try {
            long size = minioConfig.minioClient().statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedFileName)
                            .build())
                    .size();
            entity.setFileSize(size);
        } catch (Exception e) {
            e.printStackTrace(); // log stacktrace
            throw new RuntimeException("Upload ke MinIO gagal: " + e.getMessage(), e);
        }

        return toDTO(entity);
    }

    public List<AttachmentDTO> getByTicket(Long ticketId) {
        return attachmentRepository.findByTicketId(ticketId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttachmentDTO toDTO(AttachmentEntity e) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.id = e.getId();
        dto.ticketId = e.getTicketId();
        dto.fileName = e.getFileName();
        dto.fileType = e.getFileType();
        dto.fileSize = e.getFileSize();
        dto.uploadedAt = e.getUploadedAt();
        dto.uploadedBy = e.getUploadedBy();
        dto.uploadedName = e.getUploadedName();
        return dto;
    }

    @Transactional
    public void deleteByTicket(Long ticketId) {

        List<AttachmentEntity> list = attachmentRepository.findByTicketId(ticketId);

        for (AttachmentEntity entity : list) {
            try {
                minioConfig.minioClient().removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(entity.getFilePath())
                                .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            attachmentRepository.delete(entity);
        }
    }

    @Transactional
    public AttachmentDTO uploadWithoutTicket(
            InputStream fileInputStream,
            String fileName,
            String contentType,
            JsonWebToken jwt) throws Exception {

        String storedFileName = UUID.randomUUID() + "_" + fileName;

        // Upload to MinIO
        try (InputStream stream = fileInputStream) {
            minioConfig.minioClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedFileName)
                            .stream(stream, -1, 10485760)
                            .contentType(contentType)
                            .build());
        }

        AttachmentEntity entity = new AttachmentEntity();

        // TANPA ticketId
        entity.setFileName(fileName);
        entity.setFileType(contentType);
        entity.setFileSize(0L);
        entity.setFilePath(storedFileName);

        // Use Subject for ID (safer)
        if (jwt.getSubject() != null) {
            entity.setUploadedBy(Long.parseLong(jwt.getSubject()));
        } else if (jwt.getClaim("userId") != null) {
            entity.setUploadedBy(Long.parseLong(jwt.getClaim("userId").toString()));
        }

        // Use fullName claim or fallback to getName() (email)
        Object fullNameClaim = jwt.getClaim("fullName");
        if (fullNameClaim != null) {
            entity.setUploadedName(fullNameClaim.toString());
        } else {
            entity.setUploadedName(jwt.getName());
        }

        attachmentRepository.persist(entity);

        // Update file size after upload
        try {
            long size = minioConfig.minioClient().statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storedFileName)
                            .build())
                    .size();
            entity.setFileSize(size);
        } catch (Exception e) {
            // Ignore if stat fails
        }

        return toDTO(entity);
    }

    public InputStream getFileByName(String fileName) {
        try {
            // Use find by file path which is object name
            AttachmentEntity entity = attachmentRepository.find("fileName", fileName)
                    .firstResult();

            if (entity == null)
                return null;

            return minioConfig.minioClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(entity.getFilePath())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
