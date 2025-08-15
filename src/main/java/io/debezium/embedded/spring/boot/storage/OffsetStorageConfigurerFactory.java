package io.debezium.embedded.spring.boot.storage;

import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * 根据配置选择合适的 OffsetStorageConfigurer。
 */
public final class OffsetStorageConfigurerFactory {

    private OffsetStorageConfigurerFactory() {}

    public static OffsetStorageConfigurer from(DebeziumOffsetStorageProperties properties) {
        OffsetStorageType type = properties.getType();
        if (type == null) {
            type = OffsetStorageType.FILE;
        }
        return switch (type) {
            case FILE -> new FileOffsetStorageConfigurer();
            case KAFKA -> new KafkaOffsetStorageConfigurer();
            case JDBC -> new JdbcOffsetStorageConfigurer();
            case REDIS -> new RedisOffsetStorageConfigurer();
            case S3 -> new S3OffsetStorageConfigurer();
            case CUSTOM -> new CustomOffsetStorageConfigurer();
        };
    }
}


