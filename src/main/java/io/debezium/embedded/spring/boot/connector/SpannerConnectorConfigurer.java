package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Spanner 连接器配置器。
 */
public class SpannerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.spanner.SpannerConnector")
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.spanner.SpannerDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

        // Spanner 特定配置
        if (properties.getDatabaseName() != null) {
            builder.with("spanner.database", properties.getDatabaseName());
        }
        if (properties.getHost() != null) {
            builder.with("spanner.project", properties.getHost());
        }
        if (properties.getPort() != null) {
            builder.with("spanner.instance", properties.getPort().toString());
        }

        // 数据库和表过滤
        if (properties.getDatabaseIncludeList() != null) {
            builder.with("database.include.list", properties.getDatabaseIncludeList());
        }
        if (properties.getTableIncludeList() != null) {
            builder.with("table.include.list", properties.getTableIncludeList());
        }
    }
}
