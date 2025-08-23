package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.postgresql.PostgresConnector;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * PostgreSQL 连接器配置器。
 */
public class PostgreSqlConnectorConfigurer extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return PostgresConnector.class.getName();
    }
    
    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {
        // PostgreSQL 特定配置
        if (properties.getPostgreSql() != null) {
            DebeziumConnectorProperties.PostgreSql postgreSql = properties.getPostgreSql();
        }
    }
}
