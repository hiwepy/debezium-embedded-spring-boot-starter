package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * JDBC 型 Offset 存储配置。
 */
public class JdbcOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Jdbc jdbc = properties.getJdbc();
        
        builder.with("offset.storage", "io.debezium.storage.jdbc.JdbcOffsetBackingStore");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(jdbc::getUrl).whenHasText().to(value -> builder.with("offset.storage.jdbc.url", value));
        map.from(jdbc::getUsername).whenHasText().to(value -> builder.with("offset.storage.jdbc.user", value));
        map.from(jdbc::getPassword).whenHasText().to(value -> builder.with("offset.storage.jdbc.password", value));
        map.from(jdbc::getDriverClassName).whenHasText().to(value -> builder.with("offset.storage.jdbc.driver", value));
        map.from(jdbc::getTableName).whenHasText().to(value -> builder.with("offset.storage.jdbc.table", value));
        map.from(jdbc::getFlushIntervalMs).to(value -> builder.with("offset.flush.interval.ms", value));
    }
}
