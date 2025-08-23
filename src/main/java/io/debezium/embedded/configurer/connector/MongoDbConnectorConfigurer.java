package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.mongodb.MongoDbConnector;
import io.debezium.connector.postgresql.PostgresConnector;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * MongoDB 连接器配置器。
 */
public class MongoDbConnectorConfigurer extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return MongoDbConnector.class.getName();
    }

    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {

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
