package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MariaDB 连接器配置器。
 */
public class MariaDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.id", properties.getServerId())
                .with("database.server.name", properties.getServerName())
                .with("include.schema.changes", "false");

        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));

        // MariaDB 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
            map.from(mySql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(mySql::getSnapshotLockingMode).whenHasText().to(value -> builder.with("snapshot.locking.mode", value));
            map.from(mySql::getConnectTimeoutMs).to(value -> builder.with("connect.timeout.ms", value));
            map.from(mySql::getGtidSourceFilterDmlEvents).to(value -> builder.with("gtid.source.filter.dml.events", value));
        }
    }
}
