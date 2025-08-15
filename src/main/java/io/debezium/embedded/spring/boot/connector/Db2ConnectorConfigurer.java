package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * DB2 连接器配置器。
 */
public class Db2ConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.db2.Db2Connector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.db2.Db2DatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

        // 数据库和表过滤
        if (properties.getDatabaseIncludeList() != null) {
            builder.with("database.include.list", properties.getDatabaseIncludeList());
        }
        if (properties.getTableIncludeList() != null) {
            builder.with("table.include.list", properties.getTableIncludeList());
        }
        if (properties.getSchemaIncludeList() != null) {
            builder.with("schema.include.list", properties.getSchemaIncludeList());
        }
    }
}
