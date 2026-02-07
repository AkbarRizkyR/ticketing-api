package io.github.akbarrizky.resource.tiket;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;

import io.github.akbarrizky.service.attachment.AttachmentService;
import io.quarkus.security.Authenticated;
import io.github.akbarrizky.dto.attachment.AttachmentDTO;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/attachments")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class AttachmentResource {

    @Inject
    AttachmentService attachmentService;

    @Inject
    io.minio.MinioClient minioClient;

    @Inject
    JsonWebToken jwt;

    private static final org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(AttachmentResource.class);

    public static class UploadForm {
        @RestForm("files")
        public java.util.List<org.jboss.resteasy.reactive.multipart.FileUpload> files;
    }

    @ConfigProperty(name = "upload.max-files", defaultValue = "5")
    int maxFiles;

    @ConfigProperty(name = "upload.max-size", defaultValue = "20971520") // 20MB
    long maxFileSize;

    @ConfigProperty(name = "upload.allowed-extensions", defaultValue = "jpg,jpeg,png,pdf,doc,docx,xls,xlsx")
    java.util.List<String> allowedExtensions;

    @POST
    @Path("/upload")
    @Authenticated
    public Response upload(@MultipartForm UploadForm form) throws Exception {

        if (form.files == null || form.files.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Files are required").build();
        }

        // 1. Validate Max Files
        if (form.files.size() > maxFiles) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Maksimal jumlah file yang diupload adalah " + maxFiles).build();
        }

        java.util.List<AttachmentDTO> results = new java.util.ArrayList<>();

        for (org.jboss.resteasy.reactive.multipart.FileUpload file : form.files) {
            String originalFileName = file.fileName();
            String contentType = file.contentType();
            if (file.size() > maxFileSize) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Ukuran file " + originalFileName + " melebihi batas " + (maxFileSize / 1024 / 1024)
                                + "MB")
                        .build();
            }
            String extension = getExtension(originalFileName);
            if (!allowedExtensions.contains(extension.toLowerCase())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Format file " + originalFileName + " tidak diizinkan. Hanya: "
                                + String.join(", ", allowedExtensions))
                        .build();
            }
            if (!isValidMimeType(extension, contentType)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Tipe file " + originalFileName + " tidak valid atau berpotensi tidak aman.").build();
            }

            try (InputStream is = java.nio.file.Files.newInputStream(file.uploadedFile())) {
                AttachmentDTO result = attachmentService.uploadFile(
                        is,
                        originalFileName,
                        contentType,
                        jwt);
                results.add(result);
            }
        }

        return Response.ok(results).build();
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        }
        return "";
    }

    private boolean isValidMimeType(String extension, String contentType) {
        // Basic map of common allowed types
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return contentType.equals("image/jpeg");
            case "png":
                return contentType.equals("image/png");
            case "pdf":
                return contentType.equals("application/pdf");
            case "doc":
                return contentType.equals("application/msword");
            case "docx":
                return contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xls":
                return contentType.equals("application/vnd.ms-excel");
            case "xlsx":
                return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default:
                return true;
        }
    }

    @GET
    @Path("/getDataUpload")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@QueryParam("id_file") String idFile) {
        // Try to parse ID as UUID
        try {
            java.util.UUID id = java.util.UUID.fromString(idFile);

            // Get Entity for Metadata (Filename, ContentType)
            io.github.akbarrizky.entity.attachment.AttachmentEntity entity = attachmentService.findById(id);
            if (entity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("File tidak ditemukan")
                        .build();
            }

            InputStream fileStream = attachmentService.getFileById(id);
            if (fileStream == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("File tidak ditemukan")
                        .build();
            }

            return Response.ok(fileStream)
                    .header("Content-Disposition", "attachment; filename=\"" + entity.getFileName() + "\"")
                    .header("Content-Type", entity.getFileType())
                    .build();

        } catch (IllegalArgumentException e) {
            // Fallback for legacy calls (if any)
            InputStream fileStream = attachmentService.getFileByName(idFile);
            if (fileStream == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("File tidak ditemukan")
                        .build();
            }
            return Response.ok(fileStream)
                    .header("Content-Disposition", "attachment; filename=\"" + idFile + "\"")
                    .build();
        }
    }

    @GET
    @Path("/list-buckets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListBuckets() {
        logger.info("List buckets...");
        try {
            java.util.List<io.minio.messages.Bucket> buckets = minioClient.listBuckets();
            return Response.ok(
                    buckets.stream().map(io.minio.messages.Bucket::name).collect(java.util.stream.Collectors.toList()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
