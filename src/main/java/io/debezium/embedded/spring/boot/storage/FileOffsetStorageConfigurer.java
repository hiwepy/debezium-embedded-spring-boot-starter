package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * File 型 Offset 存储配置。
 */
public class FileOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.File f = properties.getFile();
        builder.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", f.getFileName())
                .with("offset.flush.interval.ms", f.getFlushIntervalMs())
                .with("offset.flush.timeout.ms", f.getFlushTimeoutMs())
        ;
    }
}


