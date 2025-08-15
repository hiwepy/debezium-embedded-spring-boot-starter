package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import io.debezium.embedded.spring.boot.storage.OffsetStorageConfigurer;

/**
 * MySQL 型 Connector 配置。
 * @see <a href="https://debezium.io/documentation/reference/3.2/development/engine.html">engine.html</a>
 */
public class MySQLConnectorConfigurer implements ConnectorConfigurer {

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


