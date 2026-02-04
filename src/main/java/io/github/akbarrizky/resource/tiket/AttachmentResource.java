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
import io.github.akbarrizky.dto.attachment.AttachmentDTO;

@Path("/attachments")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class AttachmentResource {

    @Inject
    AttachmentService attachmentService;

    @Inject
    JsonWebToken jwt;

    public static class UploadForm {
        @RestForm("file")
        public InputStream file;

        @RestForm("fileName")
        public String fileName;

        @RestForm("contentType")
        public String contentType;
    }

    @POST
    @Path("/{ticketId}")
    public Response upload(
            @PathParam("ticketId") Long ticketId,
            @MultipartForm UploadForm form) throws Exception {

        AttachmentDTO result = attachmentService.uploadFile(
                ticketId,
                form.file,
                form.fileName,
                form.contentType,
                jwt);

        return Response.ok(result).build();
    }

    @POST
    @Path("/upload")
    public Response upload(@MultipartForm UploadForm form) throws Exception {

        AttachmentDTO result = attachmentService.uploadWithoutTicket(
                form.file,
                form.fileName,
                form.contentType,
                jwt);

        return Response.ok(result).build();
    }

    @GET
    @Path("/getDataUpload")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@QueryParam("id_file") String fileName) {

        InputStream fileStream = attachmentService.getFileByName(fileName);

        if (fileStream == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File tidak ditemukan")
                    .build();
        }

        return Response.ok(fileStream)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }

}
