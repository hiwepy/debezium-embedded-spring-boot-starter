package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Spanner 连接器配置器。
 */
public class SpannerConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.spanner.SpannerConnector")
                .with("database.server.name", properties.getServerName());

        // Spanner 特定配置
        if (properties.getMongoDb() != null) {
            DebeziumEmbeddedProperties.MongoDb spanner = properties.getMongoDb();
            if (spanner.getConnectionString() != null) {
                builder.with("spanner.connection.string", spanner.getConnectionString());
            }
            if (spanner.getDatabaseList() != null) {
                builder.with("database.include.list", spanner.getDatabaseList());
            }
            if (spanner.getCollectionList() != null) {
                builder.with("table.include.list", spanner.getCollectionList());
            }
        }
    }
}
