package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.mysql.MySqlConnector;
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
public class MySqlConnectorConfigurer extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return MySqlConnector.class.getName();
    }

    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {

        // MySQL 特定配置
        if (properties.getMySql() != null) {
            DebeziumConnectorProperties.MySql mySql = properties.getMySql();
        }
    }


}


