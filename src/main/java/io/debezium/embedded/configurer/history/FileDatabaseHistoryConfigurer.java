package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import io.debezium.relational.history.FileDatabaseHistory;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * 文件数据库历史记录配置器。
 */
public class FileDatabaseHistoryConfigurer extends AbstractDatabaseHistoryConfigurer {

    @Override
    public String getDatabaseHistory() {
        return FileDatabaseHistory.class.getName();
    }

    /**
     * 应用数据库历史记录配置。
     *
     * @param map PropertyMapper 实例
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {

        DebeziumDatabaseHistoryProperties.File file = properties.getFile();

        // FileDatabaseHistory 只有一个必需的配置字段：FILE_PATH
        map.from(file::getFileName).whenHasText().to(value -> builder.with(FileDatabaseHistory.FILE_PATH.name(), value));
    }
}
