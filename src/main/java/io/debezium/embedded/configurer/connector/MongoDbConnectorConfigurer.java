package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MongoDB 连接器配置器。
 */
public class MongoDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {

        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 基础配置
        builder.with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector");
        map.from(properties::getDestination).whenHasText().to(value -> builder.with("name", value));
        map.from(properties::getType).to(value -> builder.with("database.dbType", value.name().toLowerCase()));

        // 基础连接配置
        map.from(properties::getServerName).whenHasText().to(value -> builder.with("database.server.name", value));

        // 如果没有连接字符串，使用传统的连接方式
        map.from(properties::getHost).whenHasText().to(host ->
                map.from(properties::getPort).whenNonNull().to(port ->
                        builder.with("mongodb.hosts", host + ":" + port)
                )
        );
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("mongodb.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("mongodb.password", value));

        // MongoDB 特定配置
        if (properties.getMongoDb() != null) {
            DebeziumConnectorProperties.MongoDb mongoDb = properties.getMongoDb();

        }
    }
}
