package io.debezium.embedded.spring.boot.storage;

/**
 * Offset 存储类型。
 * 支持 File、Kafka、JDBC、Redis、S3 内置实现，以及自定义类型。
 */
public enum OffsetStorageType {
    FILE,
    KAFKA,
    JDBC,
    REDIS,
    S3,
    CUSTOM
}


