package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Cassandra 连接器配置器。
 */
public class CassandraConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.cassandra.CassandraConnector")
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.cassandra.CassandraDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

        // Cassandra 特定配置
        builder.with("cassandra.hosts", properties.getHost() + ":" + properties.getPort());
        if (properties.getUsername() != null) {
            builder.with("cassandra.username", properties.getUsername());
        }
        if (properties.getPassword() != null) {
            builder.with("cassandra.password", properties.getPassword());
        }

        // 数据库和表过滤
        if (properties.getDatabaseIncludeList() != null) {
            builder.with("keyspace.include.list", properties.getDatabaseIncludeList());
        }
        if (properties.getTableIncludeList() != null) {
            builder.with("table.include.list", properties.getTableIncludeList());
        }
    }
}
