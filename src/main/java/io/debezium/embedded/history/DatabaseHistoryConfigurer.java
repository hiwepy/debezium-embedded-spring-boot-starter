package io.debezium.embedded.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;

/**
 * 数据库历史记录配置器接口。
 */
public interface DatabaseHistoryConfigurer {
    
    /**
     * 应用历史记录配置到 Debezium 配置构建器。
     *
     * @param builder Debezium 配置构建器
     * @param properties 历史记录配置属性
     */
    void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties);
}
