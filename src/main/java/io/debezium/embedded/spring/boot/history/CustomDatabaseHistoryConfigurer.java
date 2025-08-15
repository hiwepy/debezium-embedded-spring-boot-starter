package io.debezium.embedded.spring.boot.history;

import io.debezium.config.Configuration;

/**
 * 自定义数据库历史记录配置器。
 */
public class CustomDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        DebeziumDatabaseHistoryProperties.Custom custom = properties.getCustom();
        
        if (custom.getHistoryClass() != null) {
            builder.with("database.history", custom.getHistoryClass());
            
            // 添加自定义配置属性
            if (custom.getProps() != null) {
                custom.getProps().forEach((key, value) -> {
                    if (key.startsWith("database.history.")) {
                        builder.with(key, value);
                    } else {
                        builder.with("database.history." + key, value);
                    }
                });
            }
        }
    }
}
