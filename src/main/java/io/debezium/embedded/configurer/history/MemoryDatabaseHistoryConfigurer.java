package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import io.debezium.relational.history.MemoryDatabaseHistory;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Memory 数据库历史记录配置器。
 */
public class MemoryDatabaseHistoryConfigurer extends AbstractDatabaseHistoryConfigurer {


    @Override
    public String getDatabaseHistory() {
        return MemoryDatabaseHistory.class.getName();
    }
    
    /**
     * Memory 历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {

    }

}
