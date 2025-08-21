package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * SQL Server 连接器配置器。
 */
public class SqlServerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {

        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 基础配置
        builder.with("connector.class", "io.debezium.connector.sqlserver.SqlServerConnector");
        map.from(properties::getDestination).whenHasText().to(value -> builder.with("name", value));
        map.from(properties::getType).to(value -> builder.with("database.dbType", value.name().toLowerCase()));

        // 数据库连接配置
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

        // SQL Server 特定配置
        if (properties.getSqlServer() != null) {
            DebeziumConnectorProperties.SqlServer sqlServer = properties.getSqlServer();
            
            map.from(sqlServer::getDatabase).whenHasText().to(value -> builder.with("database.dbname", value));
            map.from(sqlServer::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(sqlServer::getSnapshotIsolationMode).whenHasText().to(value -> builder.with("snapshot.isolation.mode", value));

        }
    }
}
