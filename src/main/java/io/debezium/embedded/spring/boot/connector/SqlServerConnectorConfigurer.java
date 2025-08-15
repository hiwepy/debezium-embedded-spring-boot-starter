package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * SQL Server 连接器配置器。
 */
public class SqlServerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.sqlserver.SqlServerConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.sqlserver.SqlServerDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

        // SQL Server 特定配置
        if (properties.getSqlServer() != null) {
            DebeziumEmbeddedProperties.SqlServer sqlServer = properties.getSqlServer();
            if (sqlServer.getDatabase() != null) {
                builder.with("database.dbname", sqlServer.getDatabase());
            }
            if (sqlServer.getSnapshotMode() != null) {
                builder.with("snapshot.mode", sqlServer.getSnapshotMode());
            }
            if (sqlServer.getSnapshotIsolationMode() != null) {
                builder.with("snapshot.isolation.mode", sqlServer.getSnapshotIsolationMode());
            }
        }

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
