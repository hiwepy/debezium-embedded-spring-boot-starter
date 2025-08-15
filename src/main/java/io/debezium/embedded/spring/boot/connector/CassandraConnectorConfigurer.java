package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * Cassandra 连接器配置器。
 */
public class CassandraConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.cassandra.CassandraConnector")
                .with("database.server.name", properties.getServerName());

        // Cassandra 特定配置
        if (properties.getMongoDb() != null) {
            DebeziumEmbeddedProperties.MongoDb cassandra = properties.getMongoDb();
            if (cassandra.getConnectionString() != null) {
                builder.with("cassandra.connection.string", cassandra.getConnectionString());
            } else {
                builder.with("cassandra.hosts", properties.getHost() + ":" + properties.getPort());
                if (properties.getUsername() != null) {
                    builder.with("cassandra.user", properties.getUsername());
                }
                if (properties.getPassword() != null) {
                    builder.with("cassandra.password", properties.getPassword());
                }
            }
            if (cassandra.getDatabaseList() != null) {
                builder.with("keyspace.include.list", cassandra.getDatabaseList());
            }
            if (cassandra.getCollectionList() != null) {
                builder.with("table.include.list", cassandra.getCollectionList());
            }
        }
    }
}
