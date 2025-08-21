package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MySQL 连接器配置器。
 */
public class MySqlConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
               .with("database.hostname", properties.getHost())
               .with("database.port", properties.getPort())
               .with("database.user", properties.getUsername())
               .with("database.password", properties.getPassword())
               .with("database.server.id", properties.getServerId())
               .with("database.server.name", properties.getServerName());

        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));

        // MySQL 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
            
            // 快照配置
            map.from(mySql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(mySql::getSnapshotLockingMode).whenHasText().to(value -> builder.with("snapshot.locking.mode", value));
            
            // 连接配置
            map.from(mySql::getConnectTimeoutMs).to(value -> builder.with("connect.timeout.ms", value));
            
            // GTID 配置
            map.from(mySql::getGtidSourceFilterDmlEvents).to(value -> builder.with("gtid.source.filter.dml.events", value));
            
            // 数据库连接配置
            map.from(mySql::getAllowPublicKeyRetrieval).to(value -> builder.with("database.allowPublicKeyRetrieval", value));
            map.from(mySql::getUseSSL).to(value -> builder.with("database.useSSL", value));
            map.from(mySql::getAutoReconnect).to(value -> builder.with("database.autoReconnect", value));
            map.from(mySql::getAllowMultiQueries).to(value -> builder.with("database.allowMultiQueries", value));
            map.from(mySql::getZeroDateTimeBehavior).whenHasText().to(value -> builder.with("database.zeroDateTimeBehavior", value));
            map.from(mySql::getCharacterEncoding).whenHasText().to(value -> builder.with("database.characterEncoding", value));
            map.from(mySql::getUseUnicode).to(value -> builder.with("database.useUnicode", value));
            
            // 事件处理配置
            map.from(mySql::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(mySql::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // 性能优化配置
            map.from(mySql::getMinRowCountToStreamResults).to(value -> builder.with("min.row.count.to.stream.results", value));
            map.from(mySql::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(mySql::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(mySql::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}


