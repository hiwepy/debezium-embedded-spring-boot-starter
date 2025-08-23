package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.sqlserver.SqlServerConnector;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * SQL Server 连接器配置器。
 */
public class SqlServerConnectorConfigurer extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return SqlServerConnector.class.getName();
    }

    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {

        // SQL Server 特定配置
        if (properties.getSqlServer() != null) {
            DebeziumConnectorProperties.SqlServer sqlServer = properties.getSqlServer();
            
            map.from(sqlServer::getDatabase).whenHasText().to(value -> builder.with("database.dbname", value));
            map.from(sqlServer::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(sqlServer::getSnapshotIsolationMode).whenHasText().to(value -> builder.with("snapshot.isolation.mode", value));

        }
    }
}
