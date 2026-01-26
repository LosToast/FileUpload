package com.example.fileupload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${app.aws.region}")
    String region;
    @Bean
    public S3Client s3Client(@Value("${app.aws.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                // IMPORTANT: do not set credentials here.
                // AWS SDK will use your SSO profile via default credential chain.
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region)) // Region from your application properties
                .credentialsProvider(DefaultCredentialsProvider.builder().build())  // AWS credentials provider
                .build();
    }
}
