package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * 文件数据库历史记录配置器。
 */
public class FileDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {

        DebeziumDatabaseHistoryProperties.File file = properties.getFile();

        // Internal schema history store
        builder.with("database.history", "io.debezium.relational.history.FileDatabaseHistory");
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(file::getFileName).whenHasText().to(value -> builder.with("database.history.file.filename", value));

    }
}
