package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Vitess 连接器配置器。
 */
public class VitessConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.vitess.VitessConnector")
                .with("database.server.name", properties.getServerName());

        // Vitess 特定配置
        builder.with("vitess.hosts", properties.getHost() + ":" + properties.getPort());
        if (properties.getUsername() != null) {
            builder.with("vitess.user", properties.getUsername());
        }
        if (properties.getPassword() != null) {
            builder.with("vitess.password", properties.getPassword());
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
