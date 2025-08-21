package io.debezium.embedded.configurer.history;

import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;

/**
 * 数据库历史记录配置器工厂。
 */
public class DatabaseHistoryConfigurerFactory {
    
    /**
     * 根据历史记录类型创建对应的配置器。
     *
     * @param historyProperties 历史记录类型
     * @return 历史记录配置器
     */
    public static DatabaseHistoryConfigurer from(DebeziumDatabaseHistoryProperties historyProperties) {
        switch (historyProperties.getType()) {
            case MEMORY:
                return new MemoryDatabaseHistoryConfigurer();
            case FILE:
                return new FileDatabaseHistoryConfigurer();
            case KAFKA:
                return new KafkaDatabaseHistoryConfigurer();
            case CUSTOM:
                return new CustomDatabaseHistoryConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported schema history type: " + historyProperties.getType());
        }
    }
}
