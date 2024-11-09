package ru.mdm.files.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("mdm.s3")
public class S3Properties {

    private String accessKey;

    private String secretKey;

    private String region;

    private String endpoint;

    private String bucketName;

    private String prefix;
}
