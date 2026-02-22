package com.cy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "s3")
@Data
public class S3Config {
    private String accessKey;
    private String secretKey;
    private String endpoint = "http://localhost:9000";
    private String region = "cn-north-1";

    @Bean
    public S3Client s3Client() {
        if (accessKey == null || accessKey.trim().isEmpty() || secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("S3 access key and secret key must be configured and cannot be blank");
        }
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .region(Region.of(region))
                .build();
    }
}
