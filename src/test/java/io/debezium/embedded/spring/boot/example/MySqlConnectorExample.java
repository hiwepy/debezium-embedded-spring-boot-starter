package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.configurer.connector.ConnectorType;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MySQL 连接器配置示例
 */
@SpringBootApplication
public class MySqlConnectorExample {

    public static void main(String[] args) {
        SpringApplication.run(MySqlConnectorExample.class, args);
    }

    @Bean
    public DebeziumConnectorProperties mySqlConnectorProperties() {


        DebeziumConnectorProperties properties = new DebeziumConnectorProperties();
        
        // 基础配置
        properties.setType(ConnectorType.MYSQL);
        properties.setDestination("mysql-connector");
        properties.setHost("localhost");
        properties.setPort(3306);
        properties.setUsername("root");
        properties.setPassword("password");
        properties.setServerId("1");
        properties.setServerName("mysql-server");
        
        // 数据库过滤
        properties.setDatabaseIncludeList("test_db,user_db");
        properties.setTableIncludeList("test_db.users,test_db.orders");
        properties.setDatabaseExcludeList("mysql,information_schema");
        
        // MySQL 特定配置
        DebeziumConnectorProperties.MySql mySql = properties.getMySql();

        
        return properties;
    }
}
