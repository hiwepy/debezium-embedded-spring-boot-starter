package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * PostgreSQL 连接器配置器。
 */
public class PostgreSqlConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.dbname", properties.getDatabaseName())
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName())
                .with("include.schema.changes", "false");

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

        // PostgreSQL 特定配置
        if (properties.getPostgreSql() != null) {
            DebeziumEmbeddedProperties.PostgreSql postgreSql = properties.getPostgreSql();
            if (postgreSql.getPluginName() != null) {
                builder.with("plugin.name", postgreSql.getPluginName());
            }
            if (postgreSql.getSlotName() != null) {
                builder.with("slot.name", postgreSql.getSlotName());
            }
            if (postgreSql.getPublicationName() != null) {
                builder.with("publication.name", postgreSql.getPublicationName());
            }
            if (postgreSql.getSnapshotMode() != null) {
                builder.with("snapshot.mode", postgreSql.getSnapshotMode());
            }
        }
    }
}
