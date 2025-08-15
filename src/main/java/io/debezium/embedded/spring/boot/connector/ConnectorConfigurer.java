package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * 抽象的数据库连接器配置器，将数据库特定的配置写入 Debezium Configuration.Builder。
 */
public interface ConnectorConfigurer {
    void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties);
}


