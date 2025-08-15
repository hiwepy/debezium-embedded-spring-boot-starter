package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Oracle 连接器配置器。
 */
public class OracleConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.oracle.OracleConnector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.name", properties.getServerName());

        // Oracle 特定配置
        if (properties.getOracle() != null) {
            DebeziumEmbeddedProperties.Oracle oracle = properties.getOracle();
            
            if (oracle.getDatabase() != null) {
                builder.with("database.dbname", oracle.getDatabase());
            }
            if (oracle.getPdbName() != null) {
                builder.with("database.pdb.name", oracle.getPdbName());
            }
            if (oracle.getSnapshotMode() != null) {
                builder.with("snapshot.mode", oracle.getSnapshotMode());
            }
            if (oracle.getLogMiningStrategy() != null) {
                builder.with("log.mining.strategy", oracle.getLogMiningStrategy());
            }
            
            // 其他重要配置
            builder.with("database.connection.adapter", "logminer")
                   .with("database.oracle.version", "19")
                   .with("database.oracle.connection.pool.size", "20")
                   .with("database.oracle.connection.pool.increment", "5")
                   .with("database.oracle.connection.pool.max", "100")
                   .with("database.oracle.connection.pool.min", "5")
                   .with("database.oracle.connection.pool.timeout", "300")
                   .with("database.oracle.connection.pool.validate", "true");
            
            // 事件处理配置
            builder.with("tombstones.on.delete", "false")
                   .with("include.query", "false")
                   .with("database.initial.statements", "ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
            
            // 性能优化配置
            builder.with("poll.interval.ms", "1000")
                   .with("max.queue.size", "8192")
                   .with("max.batch.size", "2048")
                   .with("log.mining.batch.size.min", "1")
                   .with("log.mining.batch.size.max", "1000")
                   .with("log.mining.batch.size.default", "250");
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
