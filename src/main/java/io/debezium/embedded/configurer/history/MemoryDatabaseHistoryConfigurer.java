package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;

/**
 * Memory 数据库历史记录配置器。
 */
public class MemoryDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    /**
     * Memory 历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        // Internal schema history store
        builder.with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory");
    }

}
