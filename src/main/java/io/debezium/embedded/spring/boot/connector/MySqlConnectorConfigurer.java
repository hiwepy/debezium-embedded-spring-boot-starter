package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * MySQL 连接器配置器。
 */
public class MySqlConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
               .with("database.hostname", properties.getHost())
               .with("database.port", properties.getPort())
               .with("database.user", properties.getUsername())
               .with("database.password", properties.getPassword())
               .with("database.server.id", properties.getServerId())
               .with("database.server.name", properties.getServerName());

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

        // MySQL 特定配置
        if (properties.getMySql() != null) {
            DebeziumEmbeddedProperties.MySql mySql = properties.getMySql();
            
            // 快照配置
            if (mySql.getSnapshotMode() != null) {
                builder.with("snapshot.mode", mySql.getSnapshotMode());
            }
            if (mySql.getSnapshotLockingMode() != null) {
                builder.with("snapshot.locking.mode", mySql.getSnapshotLockingMode());
            }
            
            // 连接配置
            if (mySql.getConnectTimeoutMs() != null) {
                builder.with("connect.timeout.ms", mySql.getConnectTimeoutMs());
            }
            
            // GTID 配置
            if (mySql.getGtidSourceFilterDmlEvents() != null) {
                builder.with("gtid.source.filter.dml.events", mySql.getGtidSourceFilterDmlEvents());
            }
            
            // 数据库连接配置
            if (mySql.getAllowPublicKeyRetrieval() != null) {
                builder.with("database.allowPublicKeyRetrieval", mySql.getAllowPublicKeyRetrieval());
            }
            if (mySql.getUseSSL() != null) {
                builder.with("database.useSSL", mySql.getUseSSL());
            }
            if (mySql.getAutoReconnect() != null) {
                builder.with("database.autoReconnect", mySql.getAutoReconnect());
            }
            if (mySql.getAllowMultiQueries() != null) {
                builder.with("database.allowMultiQueries", mySql.getAllowMultiQueries());
            }
            if (mySql.getZeroDateTimeBehavior() != null) {
                builder.with("database.zeroDateTimeBehavior", mySql.getZeroDateTimeBehavior());
            }
            if (mySql.getCharacterEncoding() != null) {
                builder.with("database.characterEncoding", mySql.getCharacterEncoding());
            }
            if (mySql.getUseUnicode() != null) {
                builder.with("database.useUnicode", mySql.getUseUnicode());
            }
            
            // 事件处理配置
            if (mySql.getTombstonesOnDelete() != null) {
                builder.with("tombstones.on.delete", mySql.getTombstonesOnDelete());
            }
            if (mySql.getIncludeQuery() != null) {
                builder.with("include.query", mySql.getIncludeQuery());
            }
            
            // 性能优化配置
            if (mySql.getMinRowCountToStreamResults() != null) {
                builder.with("min.row.count.to.stream.results", mySql.getMinRowCountToStreamResults());
            }
            if (mySql.getPollIntervalMs() != null) {
                builder.with("poll.interval.ms", mySql.getPollIntervalMs());
            }
            if (mySql.getMaxQueueSize() != null) {
                builder.with("max.queue.size", mySql.getMaxQueueSize());
            }
            if (mySql.getMaxBatchSize() != null) {
                builder.with("max.batch.size", mySql.getMaxBatchSize());
            }
        }
    }
}


