package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Kafka 型 Offset 存储配置。
 */
public class KafkaOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Kafka k = properties.getKafka();
        builder
                .with("offset.storage", "org.apache.kafka.connect.storage.KafkaOffsetBackingStore")
                .with("bootstrap.servers", k.getBootstrapServers())
                .with("offset.storage.topic", k.getTopic())
                .with("offset.storage.partitions", k.getPartitions())
                .with("offset.storage.replication.factor", k.getReplicationFactor());
    }
}


