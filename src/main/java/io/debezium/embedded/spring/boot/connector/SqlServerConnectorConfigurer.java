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
                .with("database.server.name", properties.getServerName());

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
            
            // 其他重要配置
            builder.with("database.encrypt", "false")
                   .with("database.trustServerCertificate", "true")
                   .with("database.applicationName", "DebeziumConnector")
                   .with("database.connectionTimeout", "30000")
                   .with("database.commandTimeout", "30000")
                   .with("database.loginTimeout", "30000");
            
            // 事件处理配置
            builder.with("tombstones.on.delete", "false")
                   .with("include.query", "false")
                   .with("database.initial.statements", "SET ARITHABORT ON; SET NUMERIC_ROUNDABORT OFF; SET CONCAT_NULL_YIELDS_NULL ON; SET ANSI_WARNINGS ON; SET ANSI_PADDING ON; SET ANSI_NULLS ON; SET QUOTED_IDENTIFIER ON;");
            
            // 性能优化配置
            builder.with("poll.interval.ms", "1000")
                   .with("max.queue.size", "8192")
                   .with("max.batch.size", "2048")
                   .with("database.history.skip.unparseable.ddl", "true")
                   .with("database.history.store.only.monitored.tables.ddl", "true")
                   .with("database.history.store.only.captured.tables.ddl", "true");
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
