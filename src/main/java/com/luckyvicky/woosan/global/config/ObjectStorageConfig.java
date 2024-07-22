package com.luckyvicky.woosan.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectStorageConfig {
    @Value("${cloud.ncp.s3.endpoint}")
    private String endPoint;

    @Value("${cloud.ncp.region.static}")
    private String regionName;

    @Value("${cloud.ncp.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.ncp.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.ncp.s3.bucket-name}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }
    @Bean
    public String bucketName() {
        return bucketName;
    }
}

