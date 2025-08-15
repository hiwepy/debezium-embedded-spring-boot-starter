package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * MariaDB 连接器配置器。
 */
public class MariaDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.id", properties.getServerId())
                .with("database.server.name", properties.getServerName())
                .with("include.schema.changes", "false");

        // 数据库和表过滤
        if (properties.getDatabaseIncludeList() != null) {
            builder.with("database.include.list", properties.getDatabaseIncludeList());
        }
        if (properties.getTableIncludeList() != null) {
            builder.with("table.include.list", properties.getTableIncludeList());
        }
        if (properties.getDatabaseExcludeList() != null) {
            builder.with("database.exclude.list", properties.getDatabaseExcludeList());
        }
        if (properties.getTableExcludeList() != null) {
            builder.with("table.exclude.list", properties.getTableExcludeList());
        }

        // MariaDB 特定配置
        if (properties.getMySql() != null) {
            DebeziumEmbeddedProperties.MySql mySql = properties.getMySql();
            if (mySql.getSnapshotMode() != null) {
                builder.with("snapshot.mode", mySql.getSnapshotMode());
            }
            if (mySql.getSnapshotLockingMode() != null) {
                builder.with("snapshot.locking.mode", mySql.getSnapshotLockingMode());
            }
            if (mySql.getConnectTimeoutMs() != null) {
                builder.with("connect.timeout.ms", mySql.getConnectTimeoutMs());
            }
            if (mySql.getGtidSourceFilterDmlEvents() != null) {
                builder.with("gtid.source.filter.dml.events", mySql.getGtidSourceFilterDmlEvents());
            }
        }
    }
}
