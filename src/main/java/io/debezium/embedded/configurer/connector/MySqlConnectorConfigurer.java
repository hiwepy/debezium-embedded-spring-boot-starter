package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MySQL 连接器配置器。
 * 
 * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium MySQL 连接器配置，
 * 严格按照官方文档中的参数名称进行映射。</p>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/connectors/mysql.html">MySQL Connector Documentation</a>
 */
public class MySqlConnectorConfigurer implements ConnectorConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 基础配置
        builder.with("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        map.from(properties::getDestination).whenHasText().to(value -> builder.with("name", value));
        map.from(properties::getType).to(value -> builder.with("database.dbType", value.name().toLowerCase()));

        // 数据库连接配置
        map.from(properties::getHost).whenHasText().to(value -> builder.with("database.hostname", value));
        map.from(properties::getPort).whenNonNull().to(value -> builder.with("database.port", value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("database.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("database.password", value));
        map.from(properties::getServerId).whenHasText().to(value -> builder.with("database.server.id", value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));

        // 数据库和表过滤配置
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with("database.exclude.list", value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with("table.exclude.list", value));

        // MySQL 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
        }
    }


}


