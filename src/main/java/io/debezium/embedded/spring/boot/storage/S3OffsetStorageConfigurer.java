package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * S3 型 Offset 存储配置。
 */
public class S3OffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.S3 s = properties.getS3();
        builder
                .with("offset.storage", "io.debezium.storage.s3.S3OffsetBackingStore")
                .with("offset.storage.s3.bucket.name", s.getBucketName())
                .with("offset.storage.s3.region", s.getRegion())
                .with("offset.storage.s3.access.key.id", s.getAccessKeyId())
                .with("offset.storage.s3.secret.access.key", s.getSecretAccessKey())
                .with("offset.storage.s3.endpoint", s.getEndpoint())
                .with("offset.storage.s3.key.prefix", s.getKeyPrefix())
                .with("offset.flush.interval.ms", s.getFlushIntervalMs());
    }
}
