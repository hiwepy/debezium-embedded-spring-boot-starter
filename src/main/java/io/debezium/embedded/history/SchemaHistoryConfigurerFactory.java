package io.debezium.embedded.history;

import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;

/**
 * 数据库历史记录配置器工厂。
 */
public class SchemaHistoryConfigurerFactory {
    
    /**
     * 根据历史记录类型创建对应的配置器。
     *
     * @param historyProperties 历史记录类型
     * @return 历史记录配置器
     */
    public static SchemaHistoryConfigurer from(DebeziumSchemaHistoryProperties historyProperties) {
        return switch (historyProperties.getType()) {
            case FILE -> new FileSchemaHistoryConfigurer();
            case KAFKA -> new KafkaSchemaHistoryConfigurer();
            case JDBC -> new JdbcSchemaHistoryConfigurer();
            case REDIS -> new RedisSchemaHistoryConfigurer();
            case S3 -> new AmazonS3SchemaHistoryConfigurer();
            case ROCKETMQ -> new RocketMqSchemaHistoryConfigurer();
            case AZURE_BLOB -> new AzureBlobSchemaHistoryConfigurer();
            case CUSTOM -> new CustomSchemaHistoryConfigurer();
        };
    }
}
