package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * PostgreSQL 连接器配置器。
 */
public class PostgreSqlConnectorConfigurer implements ConnectorConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder.with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
               .with("database.hostname", properties.getHost())
               .with("database.port", properties.getPort())
               .with("database.user", properties.getUsername())
               .with("database.password", properties.getPassword())
               .with("database.dbname", properties.getDatabaseName())
               .with("database.server.name", properties.getServerName());

        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

        // PostgreSQL 特定配置
        if (properties.getPostgreSql() != null) {
            DebeziumConnectorProperties.PostgreSql postgreSql = properties.getPostgreSql();
            
            // 插件和槽配置
            map.from(postgreSql::getPluginName).whenHasText().to(value -> builder.with("plugin.name", value));
            map.from(postgreSql::getSlotName).whenHasText().to(value -> builder.with("slot.name", value));
            map.from(postgreSql::getPublicationName).whenHasText().to(value -> builder.with("publication.name", value));
            map.from(postgreSql::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            
            // SSL 配置
            map.from(postgreSql::getSslMode).whenHasText().to(value -> builder.with("database.ssl.mode", value));
            map.from(postgreSql::getSslCert).whenHasText().to(value -> builder.with("database.ssl.cert", value));
            map.from(postgreSql::getSslKey).whenHasText().to(value -> builder.with("database.ssl.key", value));
            map.from(postgreSql::getSslRootCert).whenHasText().to(value -> builder.with("database.ssl.rootcert", value));
            map.from(postgreSql::getSslPassword).whenHasText().to(value -> builder.with("database.ssl.password", value));
            
            // 事件处理配置
            map.from(postgreSql::getTombstonesOnDelete).to(value -> builder.with("tombstones.on.delete", value));
            map.from(postgreSql::getIncludeQuery).to(value -> builder.with("include.query", value));
            
            // 性能优化配置
            map.from(postgreSql::getPollIntervalMs).to(value -> builder.with("poll.interval.ms", value));
            map.from(postgreSql::getMaxQueueSize).to(value -> builder.with("max.queue.size", value));
            map.from(postgreSql::getMaxBatchSize).to(value -> builder.with("max.batch.size", value));
        }
    }
}
