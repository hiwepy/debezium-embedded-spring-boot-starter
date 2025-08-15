package io.debezium.embedded.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Kafka 型 Offset 存储配置。
 */
public class KafkaOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Kafka kafka = properties.getKafka();
        
        builder.with("offset.storage", "org.apache.kafka.connect.storage.KafkaOffsetBackingStore");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with("offset.storage.kafka.bootstrap.servers", value));
        map.from(kafka::getTopic).whenHasText().to(value -> builder.with("offset.storage.kafka.topic", value));
        map.from(kafka::getPartitions).to(value -> builder.with("offset.storage.kafka.partitions", value));
        map.from(kafka::getReplicationFactor).to(value -> builder.with("offset.storage.kafka.replication.factor", value));
    }
}


