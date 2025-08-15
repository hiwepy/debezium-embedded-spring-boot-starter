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
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.oracle.OracleDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

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
