package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * JDBC 型 Offset 存储配置。
 */
public class JdbcOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Jdbc j = properties.getJdbc();
        builder
                .with("offset.storage", "io.debezium.storage.jdbc.JdbcOffsetBackingStore")
                .with("offset.storage.jdbc.url", j.getUrl())
                .with("offset.storage.jdbc.user", j.getUsername())
                .with("offset.storage.jdbc.password", j.getPassword())
                .with("offset.storage.jdbc.driver", j.getDriverClassName())
                .with("offset.storage.jdbc.table", j.getTableName())
                .with("offset.flush.interval.ms", j.getFlushIntervalMs());
    }
}
