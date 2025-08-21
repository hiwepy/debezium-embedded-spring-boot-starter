package io.debezium.embedded.configurer.history;

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
        switch (historyProperties.getType()) {
            case MEMORY:
                return new MemorySchemaHistoryConfigurer();
            case FILE:
                return new FileSchemaHistoryConfigurer();
            case KAFKA:
                return new KafkaSchemaHistoryConfigurer();
            case CUSTOM:
                return new CustomSchemaHistoryConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported schema history type: " + historyProperties.getType());
        }
    }
}
