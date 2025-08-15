package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * 抽象的 Offset 存储配置器，将 storage 相关配置写入 Debezium Configuration.Builder。
 */
public interface OffsetStorageConfigurer {
    void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties);
}


