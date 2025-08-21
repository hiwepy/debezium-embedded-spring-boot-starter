package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Kafka 数据库历史记录配置器。
 */
public class KafkaDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        DebeziumDatabaseHistoryProperties.Kafka kafka = properties.getKafka();
        
        builder.with("database.history", "io.debezium.relational.history.KafkaDatabaseHistory");
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础配置
        map.from(kafka::getTopic).whenHasText().to(value -> builder.with("database.history.kafka.topic", value));
        map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with("database.history.kafka.bootstrap.servers", value));
    }

}
