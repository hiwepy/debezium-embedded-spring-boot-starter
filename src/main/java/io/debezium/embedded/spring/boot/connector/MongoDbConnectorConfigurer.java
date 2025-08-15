package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * MongoDB 连接器配置器。
 */
public class MongoDbConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
                .with("database.server.name", properties.getServerName())
                .with("database.history", "io.debezium.connector.mongodb.MongoDbDatabaseHistory")
                .with("database.history.file.filename", properties.getHistoryFileName());

        // MongoDB 特定配置
        if (properties.getMongoDb() != null) {
            DebeziumEmbeddedProperties.MongoDb mongoDb = properties.getMongoDb();
            if (mongoDb.getConnectionString() != null) {
                builder.with("mongodb.connection.string", mongoDb.getConnectionString());
            } else {
                // 如果没有连接字符串，使用传统的连接方式
                builder.with("mongodb.hosts", properties.getHost() + ":" + properties.getPort());
                if (properties.getUsername() != null) {
                    builder.with("mongodb.user", properties.getUsername());
                }
                if (properties.getPassword() != null) {
                    builder.with("mongodb.password", properties.getPassword());
                }
            }
            if (mongoDb.getDatabaseList() != null) {
                builder.with("database.include.list", mongoDb.getDatabaseList());
            }
            if (mongoDb.getCollectionList() != null) {
                builder.with("collection.include.list", mongoDb.getCollectionList());
            }
            if (mongoDb.getSnapshotMode() != null) {
                builder.with("snapshot.mode", mongoDb.getSnapshotMode());
            }
        }
    }
}
