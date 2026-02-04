package io.github.akbarrizky.util;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MinioConfig {

    @ConfigProperty(name = "minio.url")
    String url;

    @ConfigProperty(name = "minio.access-key")
    String accessKey;

    @ConfigProperty(name = "minio.secret-key")
    String secretKey;

    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                // Use simple credentials directly
                .credentials(accessKey, secretKey)
                .build();
    }
}
